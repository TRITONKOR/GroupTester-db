package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;


public interface TagRepository extends Repository<Tag>, ManyToMany{
    Set<Test> findAllTests(UUID tagId);
}
