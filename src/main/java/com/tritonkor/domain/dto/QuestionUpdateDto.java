package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;

public record QuestionUpdateDto(
        @NotNull(message = "Відсутній іденитфікатор питання")
        UUID id,
        @Length(min = 10, max = 64, message = "Текст питання повинний відповідати діапазону символів від 10 до 64")
        String text,
        UUID testId
) {

}
