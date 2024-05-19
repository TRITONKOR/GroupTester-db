package com.tritonkor.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;

public record TagUpdateDto(
        @NotNull(message = "Відсутній іденитфікатор тегу")
        UUID id,
        @Length(min = 6, max = 64, message = "Назва тегу повинна відповідати діапазону символів від 6 до 64")
        String name
) {

}
