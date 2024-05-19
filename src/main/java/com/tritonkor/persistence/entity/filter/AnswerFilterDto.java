package com.tritonkor.persistence.entity.filter;

import java.util.UUID;

public record AnswerFilterDto(
        UUID questionId, String text
) {

}
