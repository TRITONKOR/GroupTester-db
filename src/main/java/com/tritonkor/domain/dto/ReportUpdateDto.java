package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReportUpdateDto(
        @NotNull(message = "Відсутній іденитфікатор звіту")
        UUID id,

        UUID testId,

        UUID ownerId
) {

}
