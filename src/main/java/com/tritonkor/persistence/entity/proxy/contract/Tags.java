package com.tritonkor.persistence.entity.proxy.contract;

import com.tritonkor.persistence.entity.Tag;
import java.util.Set;
import java.util.UUID;

@FunctionalInterface
public interface Tags {
    Set<Tag> get(UUID testId);
}
