package fi.epassi.recruitment.book;

import fi.epassi.recruitment.BaseIntegrationTest;
import fi.epassi.recruitment.book.dto.BookCountDto;
import fi.epassi.recruitment.book.model.BookModel;
import fi.epassi.recruitment.book.repo.BookRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.math.BigDecimal;
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
    private static final String BASE_PATH_V1_BOOK_BY_AUTHOR = BASE_PATH_V1_BOOK + "/author";
    private static final String BASE_PATH_V1_BOOK_BY_TITLE = BASE_PATH_V1_BOOK + "/title";
    private static final String BASE_PATH_V1_BOOK_BY_ISBN = BASE_PATH_V1_BOOK + "/{isbn}";

    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String PAGE = "page";
    private static final String SIZE = "size";
    private static final String SORT = "sort";

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
            .count(350L)
            .build();

    private static final BookModel BOOK_FELLOWSHIP_COUNT = BookModel.builder()
            .isbn(UUID.fromString("556aa37d-ef9c-45d3-ba4a-a792c123208a"))
            .title("The Fellowship of the Rings")
            .author("J.R.R Tolkien")
            .price(TEN)
            .count(300L)
            .build();

    private static final BookModel F_BOOK_COUNT = BookModel.builder()
            .isbn(UUID.fromString("556aa37d-ef9c-45d3-ba4a-a792c1232055"))
            .title("1 book")
            .author("J.R.R Tolkien")
            .price(new BigDecimal(9))
            .count(240L)
            .build();

    private static final BookModel S_BOOK_COUNT = BookModel.builder()
            .isbn(UUID.fromString("556aa37d-ef9c-45d3-ba4a-a792c1232011"))
            .title("2 book")
            .author("J.R.R Tolkien")
            .price(new BigDecimal(8))
            .count(110L)
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
                .andExpect(jsonPath("$.response.count", is(350)));
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
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_AUTHOR);

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON)
                .queryParam(AUTHOR, "J.R.R Tolkien");
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
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_AUTHOR);

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON)
                .queryParam(AUTHOR, "J.R.R Tolkien");
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
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_TITLE);

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON)
                .queryParam(TITLE, "The Hobbit");
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
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_TITLE);

        var request = get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON)
                .queryParam(TITLE, "The Hobbit");
        var response = mvc.perform(request);

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithBookWhenSearchingByAuthorAndPage() {
        // Given
        bookRepository.save(BOOK_HOBBIT_COUNT);
        bookRepository.save(BOOK_FELLOWSHIP_COUNT);
        bookRepository.save(F_BOOK_COUNT);
        bookRepository.save(S_BOOK_COUNT);

        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_BOOK_BY_AUTHOR);
        var request = get(requestUrl)
                .queryParam(AUTHOR, "J.R.R Tolkien")
                .queryParam(PAGE, "0")
                .queryParam(SIZE, "3")
                .queryParam(SORT, "count")
                .contentType(APPLICATION_JSON);
        var response = mvc.perform(request);

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response[0].author", is("J.R.R Tolkien")))
                .andExpect(jsonPath("$.response[0].count", is(notNullValue())))
                .andExpect(jsonPath("$.response[0].count", is(110)))
                .andExpect(jsonPath("$.response[1].count", is(240)))
                .andExpect(jsonPath("$.response[2].count", is(300)));
    }
}
