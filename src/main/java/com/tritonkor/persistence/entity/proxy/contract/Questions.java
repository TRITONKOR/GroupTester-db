package com.tritonkor.persistence.entity.proxy.contract;

import com.tritonkor.persistence.entity.Question;
import java.util.Set;
import java.util.UUID;

@FunctionalInterface
public interface Questions {
    Set<Question> get(UUID testId);
}
