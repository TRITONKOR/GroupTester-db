package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReportStoreDto(
        @NotNull(message = "Задайте тест, до якого належить звіт")
        UUID testId,

        @NotNull(message = "Задайте користувача, якому належить звіт")
        UUID ownerId
) {

}
