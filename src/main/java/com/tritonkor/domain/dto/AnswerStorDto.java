package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;

public record AnswerStorDto(
        @NotBlank(message = "Текст відповіді не може бути порожнім")
        @Length(min = 10, max = 64, message = "Текст відповіді має містити від 6 до 64 символів")
        String text,

        @NotNull(message = "Задайте питання, до якого належить відповідь")
        UUID questionId,

        @NotNull(message = "Задайте, чи відповідь є правильною")
        boolean isCorrect
) {

}
