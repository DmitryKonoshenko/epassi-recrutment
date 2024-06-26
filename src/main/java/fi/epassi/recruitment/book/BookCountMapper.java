package fi.epassi.recruitment.book;

import fi.epassi.recruitment.book.dto.BookCountAuthorDto;
import fi.epassi.recruitment.book.dto.BookCountDto;
import fi.epassi.recruitment.book.dto.BookCountTitleDto;
import fi.epassi.recruitment.book.model.BookModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookCountMapper {
    BookCountDto toBookDto(BookModel book);

    BookModel toBookCountModel(BookCountDto book);

    BookCountTitleDto toBookCountTitleDto(BookModel bookModel);

    BookCountAuthorDto toBookCountAuthorDto(BookModel bookModel);
}
