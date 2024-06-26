package fi.epassi.recruitment.book.controller;

import fi.epassi.recruitment.api.ApiResponse;
import fi.epassi.recruitment.book.BookService;
import fi.epassi.recruitment.book.dto.BookCountAuthorDto;
import fi.epassi.recruitment.book.dto.BookCountDto;
import fi.epassi.recruitment.book.dto.BookCountTitleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static fi.epassi.recruitment.book.controller.BookController.getPageable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/count", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class BookCountController {

    private final BookService bookService;

    @PostMapping
    ApiResponse<UUID> addBookCount(@RequestBody @Validated BookCountDto bookDto) {
        var isbn = bookService.addBookCount(bookDto);
        return ApiResponse.ok(isbn);
    }

    @GetMapping("/{isbn}")
    ApiResponse<BookCountDto> getBookByIsbn(@PathVariable("isbn") @Validated UUID isbn) {
        return ApiResponse.ok(bookService.getBookCountByIsbn(isbn));
    }

    @GetMapping("/author")
    ApiResponse<List<BookCountAuthorDto>> getBooksCountAuthor(
            @RequestParam(value = "author") String author,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return ApiResponse.ok(bookService.getBooksCountByAuthor(author, getPage(page, size, sort)));
    }

    @GetMapping("/title")
    ApiResponse<List<BookCountTitleDto>> getBooksCountTitle(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort) {
        return ApiResponse.ok(bookService.getBooksCountByTitle(title, getPage(page, size, sort)));
    }

    private Pageable getPage(Integer page, Integer size, String sort) {
        return getPageable(page, size, sort);
    }

}
