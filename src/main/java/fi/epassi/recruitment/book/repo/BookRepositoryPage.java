package fi.epassi.recruitment.book.repo;

import fi.epassi.recruitment.book.model.BookModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface BookRepositoryPage extends PagingAndSortingRepository<BookModel, UUID> {

    List<BookModel> findByTitle(String title, Pageable pageable);

    List<BookModel> findByAuthor(String author, Pageable pageable);

    List<BookModel> findByAuthorAndTitle(String author, String title, Pageable pageable);
}