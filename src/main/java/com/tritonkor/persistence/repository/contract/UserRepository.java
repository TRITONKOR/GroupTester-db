package com.tritonkor.persistence.repository.contract;

import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.filter.UserFilterDto;
import com.tritonkor.persistence.repository.Repository;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends Repository<User> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Set<User> findAll(int offset, int limit, String sortColumn, boolean ascending, UserFilterDto userFilterDto);
}
