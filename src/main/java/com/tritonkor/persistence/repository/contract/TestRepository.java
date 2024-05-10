package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.filter.TestFilterDto;
import com.tritonkor.persistence.repository.Repository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TestRepository extends Repository<Test>, ManyToMany {

    Optional<Test> findByTitle(String title);

    Set<Tag> findAllTags(UUID testId);
    Set<Test> findAllByUserId(UUID userId);

    Set<Test> findAll(int offset,
            int limit,
            String sortColumn,
            boolean ascending,
            TestFilterDto testFilterDto);

    Set<Test> findAllByUserId(UUID userId,
            int offset,
            int limit,
            String sortColumn,
            boolean ascending,
            TestFilterDto testFilterDto);
}
