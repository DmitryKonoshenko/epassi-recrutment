package fi.epassi.recruitment.book;

import fi.epassi.recruitment.book.dto.BookCountAuthorDto;
import fi.epassi.recruitment.book.dto.BookCountDto;
import fi.epassi.recruitment.book.dto.BookCountTitleDto;
import fi.epassi.recruitment.book.dto.BookDto;
import fi.epassi.recruitment.book.model.BookModel;
import fi.epassi.recruitment.book.repo.BookRepository;
import fi.epassi.recruitment.exception.BookNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookCountMapper bookCountMapper;

    public UUID createBook(BookDto bookDto) {
        BookModel bookModel = toBookModel(bookDto);
        bookModel.setCount(0L);
        var savedBook = bookRepository.save(bookModel);
        return savedBook.getIsbn();
    }

    public void deleteBookWithIsbn(@NonNull UUID isbn) {
        bookRepository.deleteById(isbn);
    }

    public BookDto getBookByIsbn(@NonNull UUID isbn) throws BookNotFoundException {
        return bookRepository.findByIsbn(isbn)
                .map(BookService::toBookDto)
                .orElseThrow(() -> new BookNotFoundException(isbn.toString()));
    }

    public BookCountDto getBookCountByIsbn(@NonNull UUID isbn) throws BookNotFoundException {
        return bookRepository.findByIsbn(isbn)
                .map(bookCountMapper::toBookDto)
                .orElseThrow(() -> new BookNotFoundException(isbn.toString()));
    }


    public List<BookDto> getBooks(String author, String title) {
        if (StringUtils.isNotBlank(author) && StringUtils.isNotBlank(title)) {
            return bookRepository.findByAuthorAndTitle(author, title).stream().map(BookService::toBookDto).toList();
        } else if (StringUtils.isNotBlank(author) && StringUtils.isBlank(title)) {
            return bookRepository.findByAuthor(author).stream().map(BookService::toBookDto).toList();
        } else if (StringUtils.isNotBlank(title) && StringUtils.isBlank(author)) {
            return bookRepository.findByTitle(title).stream().map(BookService::toBookDto).toList();
        }

        return bookRepository.findAll().stream().map(BookService::toBookDto).toList();
    }

    public UUID updateBook(BookDto bookDto) {
        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            var bookModel = toBookModel(bookDto);
            var savedBook = bookRepository.save(bookModel);
            return savedBook.getIsbn();
        }

        throw new BookNotFoundException(bookDto.getIsbn().toString());
    }

    private static BookModel toBookModel(BookDto bookDto) {
        return BookModel.builder()
                .isbn(bookDto.getIsbn())
                .author(bookDto.getAuthor())
                .title(bookDto.getTitle())
                .price(bookDto.getPrice())
                .build();
    }

    public static BookDto toBookDto(BookModel bookModel) {
        return BookDto.builder()
                .isbn(bookModel.getIsbn())
                .author(bookModel.getAuthor())
                .title(bookModel.getTitle())
                .price(bookModel.getPrice())
                .build();
    }

    public UUID addBookCount(BookCountDto bookDto) {
        Optional<BookModel> op = bookRepository.findByIsbn(bookDto.getIsbn());
        if (op.isPresent()) {
            var bookModel = op.get();
            bookModel.setCount(bookDto.getCount());
            var savedBook = bookRepository.save(bookModel);
            return savedBook.getIsbn();
        }

        throw new BookNotFoundException(bookDto.getIsbn().toString());
    }

    public List<BookCountAuthorDto> getBooksCountByAuthor(String author) {
        return bookRepository.findByAuthor(author).stream().map(bookCountMapper::toBookCountAuthorDto).toList();
    }

    public List<BookCountTitleDto> getBooksCountByTitle(String title) {
        return bookRepository.findByTitle(title).stream().map(bookCountMapper::toBookCountTitleDto).toList();
    }
}
