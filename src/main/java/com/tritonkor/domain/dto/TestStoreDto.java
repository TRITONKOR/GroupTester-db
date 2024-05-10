package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;

public record TestStoreDto(
    @NotBlank(message = "Назва тесту не може бути порожнім")
    @Length(min = 6, max = 64, message = "Назва має містити від 6 до 64 символів")
    String title,

    @NotNull(message = "Задайте автора тесту")
    UUID ownerId
)
{}
