package com.tritonkor.persistence.repository.impl.jdbc;


import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.filter.TestFilterDto;
import com.tritonkor.persistence.entity.filter.UserFilterDto;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.persistence.repository.mapper.impl.UserRowMapper;
import com.tritonkor.persistence.util.ConnectionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl extends GenericJdbcRepository<User> implements UserRepository {

    public UserRepositoryImpl(ConnectionManager connectionManager, UserRowMapper rowMapper) {
        super(connectionManager, rowMapper, TableNames.USERS.getName());
    }

    @Override
    protected Map<String, Object> tableValues(User user) {
        Map<String, Object> values = new LinkedHashMap<>();

        if (!user.getUsername().isBlank()) {
            values.put("username", user.getUsername());
        }
        if (!user.getEmail().isBlank()) {
            values.put("email", user.getEmail());
        }
        if (!user.getPassword().isBlank()) {
            values.put("password", user.getPassword());
        }
        if (Objects.nonNull(user.getAvatar())) {
            values.put("avatar", user.getAvatar());
        }
        if (Objects.nonNull(user.getBirthday())) {
            values.put("birthday", user.getBirthday());
        }
        if (Objects.nonNull(user.getRole())) {
            values.put("role", user.getRole());
        }
        return values;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findBy("username", username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findBy("email", email);
    }

    @Override
    public Set<User> findAll(int offset, int limit, String sortColumn, boolean ascending,
            UserFilterDto userFilterDto) {
        return findAll(offset, limit, sortColumn, ascending, userFilterDto, "");
    }

    private Set<User> findAll(
            int offset,
            int limit,
            String sortColumn,
            boolean ascending,
            UserFilterDto userFilterDto,  String wherePrefix) {
        StringBuilder where = new StringBuilder(STR."\{wherePrefix} ");
        HashMap<String, Object> filters = new HashMap<>();

        if (!userFilterDto.username().isBlank()) {
            filters.put("username", userFilterDto.username());
        }
        if (!userFilterDto.email().isBlank()) {
            filters.put("email", userFilterDto.email());
        }
        if (Objects.nonNull(userFilterDto.role())) {
            filters.put("role", userFilterDto.role());
        }

        // Фільтр по create_date
        if (Objects.nonNull(userFilterDto.birthdayFrom())
                && Objects.nonNull(userFilterDto.birthdayTo())) {
            where.append(
                    STR."birthday BETWEEN '\{userFilterDto.birthdayFrom()}' AND '\{userFilterDto.birthdayTo()}' ");
        } else if (Objects.nonNull(userFilterDto.birthdayFrom())) {
            where.append(STR."birthday >= '\{userFilterDto.birthdayFrom()}' ");
        } else if (Objects.nonNull(userFilterDto.birthdayTo())) {
            where.append(STR."birthday <= '\{userFilterDto.birthdayTo()}' ");
        }

        return findAll(offset, limit, sortColumn, ascending, filters, where.toString());
    }

}
