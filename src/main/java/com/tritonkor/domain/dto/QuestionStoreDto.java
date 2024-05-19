package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;

public record QuestionStoreDto(
        @NotBlank(message = "Текст питання не може бути порожнім")
        @Length(min = 10, max = 256, message = "Текст питання має містити від 6 до 64 символів")
        String text,

        @NotNull(message = "Задайте тест, до якого належить питання")
        UUID testId
) {

}
