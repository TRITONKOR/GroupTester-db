package com.tritonkor.persistence.entity.proxy.contract;

import com.tritonkor.persistence.entity.Result;
import java.util.Set;
import java.util.UUID;

@FunctionalInterface
public interface Results {
    Set<Result> get(UUID reportId);
}
