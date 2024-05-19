package com.tritonkor.persistence.entity.filter;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportFilterDto(
        UUID ownerId,
        UUID testId,
        LocalDateTime createdAtStart,
        LocalDateTime createdAtEnd
) {

}
