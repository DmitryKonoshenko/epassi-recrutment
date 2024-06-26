package fi.epassi.recruitment.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCountDto {

    @NotNull
    private UUID isbn;

    @Min(value = 0, message = "Book count must be higher than 0")
    private Long count;
}
