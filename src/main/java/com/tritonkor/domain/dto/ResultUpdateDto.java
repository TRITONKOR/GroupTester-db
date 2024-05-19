package com.tritonkor.domain.dto;

import com.tritonkor.persistence.entity.Mark;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ResultUpdateDto(
        @NotNull(message = "Відсутній іденитфікатор звіту")
        UUID id,

        UUID testId,

        UUID ownerId,
        UUID reportId,
        Mark mark
) {

}
