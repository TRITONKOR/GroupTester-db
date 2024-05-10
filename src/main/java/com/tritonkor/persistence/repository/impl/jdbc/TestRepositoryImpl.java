package com.tritonkor.persistence.repository.impl.jdbc;

import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.filter.TestFilterDto;
import com.tritonkor.persistence.entity.filter.UserFilterDto;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.mapper.impl.TagRowMapper;
import com.tritonkor.persistence.repository.mapper.impl.TestRowMapper;
import com.tritonkor.persistence.util.ConnectionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TestRepositoryImpl extends GenericJdbcRepository<Test> implements TestRepository {
    private final ConnectionManager connectionManager;
    private final TagRowMapper tagRowMapper;
    private final JdbcManyToMany<Tag> jdbcManyToMany;

    public TestRepositoryImpl(ConnectionManager connectionManager,
            TestRowMapper rowMapper, TagRowMapper tagRowMapper,
            JdbcManyToMany<Tag> jdbcManyToMany) {
        super(connectionManager, rowMapper, TableNames.TESTS.getName());
        this.connectionManager = connectionManager;
        this.tagRowMapper = tagRowMapper;
        this.jdbcManyToMany = jdbcManyToMany;
    }

    @Override
    protected Map<String, Object> tableValues(Test test) {
        Map<String, Object> values = new LinkedHashMap<>();

        if (!test.getTitle().isBlank()) {
            values.put("title", test.getTitle());
        }
        if (Objects.nonNull(test.getOwnerId())) {
            values.put("owner_id", test.getOwnerId());
        }
        if (Objects.nonNull(test.getCreatedAt())) {
            values.put("create_date", test.getCreatedAt());
        }

        return values;
    }

    @Override
    public Optional<Test> findByTitle(String title) {
        return findBy("title", title);
    }

    @Override
    public Set<Tag> findAllTags(UUID testId) {
        final String sql =
                """
                        SELECT *
                          FROM tags AS t
                               JOIN test_tag AS tt
                                 ON t.id = tt.tag_id
                         WHERE tt.test_id = ?;
                        """;

        return jdbcManyToMany.getByPivot(
                testId,
                sql,
                tagRowMapper,
                STR."Помилка при отриманні всіх тегів тесту по id: \{testId}");
    }

    @Override
    public boolean attach(UUID testId, UUID tagId) {
        final String sql =
                """
                        INSERT INTO test_tag(testId, tagId)
                        VALUES (?, ?);
                        """;
        return jdbcManyToMany.executeUpdate(
                testId, tagId, sql, STR."Помилка при додаванні нового тегу до тесту");
    }

    @Override
    public boolean detach(UUID testId, UUID tagId) {
        final String sql =
                """
                        DELETE FROM test_tag
                              WHERE testId = ? AND tagId = ?;
                        """;
        return jdbcManyToMany.executeUpdate(
                testId,
                tagId,
                sql,
                STR."Помилка при видаленні запису з таблиці по testId: \\{testId.toString()} і tagId: \\{tagId.toString()}");
    }

    @Override
    public Set<Test> findAll(int offset, int limit, String sortColumn, boolean ascending,
            TestFilterDto testFilterDto) {
        return findAll(offset, limit, sortColumn, ascending, testFilterDto, "");
    }

    @Override
    public Set<Test> findAllByUserId(UUID userId) {
        return findAllWhere(STR."owner_id = \{userId}");
    }

    @Override
    public Set<Test> findAllByUserId(UUID userId, int offset, int limit, String sortColumn,
            boolean ascending, TestFilterDto testFilterDto) {
        return findAll(offset, limit, sortColumn, ascending, testFilterDto,
                STR."user_id = \{userId}");
    }

    private Set<Test> findAll(int offset, int limit, String sortColumn, boolean ascending,
            TestFilterDto testFilterDto, String wherePrefix) {
        StringBuilder where = new StringBuilder(STR."\{wherePrefix} ");
        HashMap<String, Object> filters = new HashMap<>();

        // Додавання фільтрів до where-умови
        if (!testFilterDto.title().isBlank()) {
            filters.put("title", testFilterDto.title());
        }
        if (Objects.nonNull(testFilterDto.ownerId())) {
            filters.put("ownerId", testFilterDto.ownerId());
        }

        // Фільтр по created_at
        if (Objects.nonNull(testFilterDto.createdAtStart())
                && Objects.nonNull(testFilterDto.createdAtEnd())) {
            where.append(
                    STR."AND create_date BETWEEN \{testFilterDto.createdAtStart()} AND \{testFilterDto.createdAtEnd()} ");
        } else if (Objects.nonNull(testFilterDto.createdAtStart())) {
            where.append(STR."AND create_date >= \{testFilterDto.createdAtStart()} ");
        } else if (Objects.nonNull(testFilterDto.createdAtEnd())) {
            where.append(STR."AND create_date <= \{testFilterDto.createdAtEnd()} ");
        }


        return findAll(offset, limit, sortColumn, ascending, filters, where.toString());
    }
}