package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record TagStoreDto(
        @NotBlank(message = "Назва тегу не може бути порожнім")
        @Length(min = 6, max = 64, message = "Назва має містити від 6 до 64 символів")
        String name
) {

}
