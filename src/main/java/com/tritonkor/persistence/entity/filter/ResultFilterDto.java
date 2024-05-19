package com.tritonkor.persistence.entity.filter;

import java.time.LocalDateTime;
import java.util.UUID;

public record ResultFilterDto(
        UUID ownerId,
        UUID testId,
        UUID reportId,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd
) {

}
