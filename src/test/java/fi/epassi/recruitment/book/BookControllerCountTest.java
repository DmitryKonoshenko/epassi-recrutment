package fi.epassi.recruitment.book;

import fi.epassi.recruitment.BaseIntegrationTest;
import fi.epassi.recruitment.book.dto.BookCountDto;
import fi.epassi.recruitment.book.model.BookModel;
import fi.epassi.recruitment.book.repo.BookRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.UUID;

import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerCountTest extends BaseIntegrationTest {

    private static final String BASE_PATH_V1_BOOK = "/api/v1/count";
    private static final String BASE_PATH_V1_BOOK_BY_AUTHOR = BASE_PATH_V1_BOOK + "/author?author=";
    private static final String BASE_PATH_V1_BOOK_BY_TITLE = BASE_PATH_V1_BOOK + "/title?title=";
    private static final String BASE_PATH_V1_BOOK_BY_ISBN = BASE_PATH_V1_BOOK + "/{isbn}";

    private static final BookModel BOOK_HOBBIT = BookModel.builder()
            .isbn(UUID.fromString("66737096-39ef-4a7c-aa4a-9fd018c14178"))
            .title("The Hobbit")
            .author("J.R.R Tolkien")
            .price(TEN)
            .build();

    private static final BookModel BOOK_HOBBIT_COUNT = BookModel.builder()
            .isbn(UUID.fromString("66737096-39ef-4a7c-aa4a-9fd018c14178"))
            .title("The Hobbit")
            .author("J.R.R Tolkien")
            .price(TEN)
            .count(450L)
            .build();

    @Autowired
    private BookRepository bookRepository;

    @Test
    @SneakyThrows
    void shouldCreateAddCountAndReturnId() {
        bookRepository.save(BOOK_HOBBIT);
        // Given
        var bookDto = BookCountDto.builder().isbn(UUID.fromString("66737096-39ef-4a7c-aa4a-9fd018c14178")).count(450L).build();
        var bookDtoJson = mapper.writeValueAsString(bookDto);
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK);
        var request = post(requestUrl).contentType(APPLICATION_JSON).content(bookDtoJson);
        var response = mvc.perform(request);
        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())));

        BookModel model = bookRepository.findByIsbn(UUID.fromString("66737096-39ef-4a7c-aa4a-9fd018c14178")).get();
        Assert.isTrue(model.getCount() == 450L, "Incorrect count");
    }

    @Test
    @SneakyThrows
    void shouldRespondWithNotFoundWhenUpdatingCountNonExistingBook() {
        // Given
        var bookDto = BookCountDto.builder().isbn(UUID.fromString("66737096-39ef-4a7c-aa4a-9fd018c14178")).count(450L).build();
        var bookDtoJson = mapper.writeValueAsString(bookDto);
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK);
        var request = post(requestUrl).contentType(APPLICATION_JSON).content(bookDtoJson);
        var response = mvc.perform(request);
        // Then
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithFoundWhenSearchingForExistingBookCountByIsbn() {
        bookRepository.save(BOOK_HOBBIT_COUNT);
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_ISBN);

        var request = get(requestUrl, UUID.fromString("66737096-39ef-4a7c-aa4a-9fd018c14178")).contentType(APPLICATION_JSON);
        var response = mvc.perform(request);

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())))
                .andExpect(jsonPath("$.response.count", is(450)));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithFoundWhenSearchingForNonExistingBookCountByIsbn() {
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_ISBN);

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON);
        var response = mvc.perform(request);

        // Then
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithFoundWhenSearchingForExistingBookCountByAuthor() {
        bookRepository.save(BOOK_HOBBIT_COUNT);
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_AUTHOR + "J.R.R Tolkien");

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON);
        var response = mvc.perform(request);

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())))
                .andExpect(jsonPath("$.response[0].author", is("J.R.R Tolkien")));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithFoundWhenSearchingForNonExistingBookCountByAuthor() {
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_AUTHOR + "J.R.R Tolkien");

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON);
        var response = mvc.perform(request);

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithFoundWhenSearchingForExistingBookCountByTitle() {
        bookRepository.save(BOOK_HOBBIT_COUNT);
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_TITLE + "The Hobbit");

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON);
        var response = mvc.perform(request);

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())))
                .andExpect(jsonPath("$.response[0].title", is("The Hobbit")));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithFoundWhenSearchingForNonExistingBookCountByTitle() {
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_TITLE + "The Hobbit");

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON);
        var response = mvc.perform(request);

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())));
    }
}
