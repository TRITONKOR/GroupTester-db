package com.tritonkor.persistence.entity.proxy.contract;

import com.tritonkor.persistence.entity.Answer;
import java.util.Set;
import java.util.UUID;

@FunctionalInterface
public interface Answers {
    Set<Answer> get(UUID questionId);
}
