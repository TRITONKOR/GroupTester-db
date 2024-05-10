package com.tritonkor.persistence.entity.proxy.contract;

import com.tritonkor.persistence.entity.Test;
import java.util.Set;
import java.util.UUID;

@FunctionalInterface
public interface Tests {
    Set<Test> get(UUID tagId);
}
