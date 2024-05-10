package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;

public record TestUpdateDto (
        @NotNull(message = "Відсутній іденитфікатор тесту")
        UUID id,
        @Length(min = 6, max = 64, message = "Назва тесту повинна відповідати діапазону символів від 6 до 64")
        String title,
        UUID ownerId
)
{
}
