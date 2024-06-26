package fi.epassi.recruitment.book.repo;

import fi.epassi.recruitment.book.model.BookShopBookModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksShopBookRepository extends JpaRepository<BookShopBookModel, Long> {

}
