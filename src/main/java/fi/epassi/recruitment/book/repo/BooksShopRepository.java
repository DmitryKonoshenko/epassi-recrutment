package fi.epassi.recruitment.book.repo;

import fi.epassi.recruitment.book.model.BookShopModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BooksShopRepository extends JpaRepository<BookShopModel, Long> {

}
