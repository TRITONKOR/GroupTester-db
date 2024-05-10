package com.tritonkor.persistence.repository.impl.jdbc;

import com.tritonkor.persistence.entity.Tag;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.repository.GenericJdbcRepository;
import com.tritonkor.persistence.repository.contract.TableNames;
import com.tritonkor.persistence.repository.contract.TagRepository;
import com.tritonkor.persistence.repository.mapper.impl.TagRowMapper;
import com.tritonkor.persistence.repository.mapper.impl.TestRowMapper;
import com.tritonkor.persistence.util.ConnectionManager;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class TagRepositoryImpl extends GenericJdbcRepository<Tag> implements TagRepository {

    private final TestRowMapper testRowMapper;
    private final JdbcManyToMany<Test> jdbcManyToMany;

    public TagRepositoryImpl(ConnectionManager connectionManager,
            TagRowMapper rowMapper,
            TestRowMapper testRowMapper, JdbcManyToMany<Test> jdbcManyToMany) {
        super(connectionManager, rowMapper, TableNames.TAGS.getName());
        this.testRowMapper = testRowMapper;
        this.jdbcManyToMany = jdbcManyToMany;
    }

    @Override
    protected Map<String, Object> tableValues(Tag tag) {
        Map<String, Object> values = new LinkedHashMap<>();

        if (!tag.getName().isBlank()) {
            values.put("name", tag.getName());
        }

        return values;
    }

    @Override
    public Set<Test> findAllTests(UUID tagId) {
        final String sql =
                """
                        SELECT *
                          FROM tests AS t
                               JOIN test_tag AS tt
                                 ON t.id = tt.test_id
                         WHERE tt.tag_id = ?;
                        """;

        return jdbcManyToMany.getByPivot(
                tagId,
                sql,
                testRowMapper,
                STR."Помилка при отриманні всіх тестів тега по id: \{tagId}");
    }

    @Override
    public boolean attach(UUID testId, UUID tagId) {
        final String sql =
                """
                        INSERT INTO test_tag(test_id, tag_id)
                        VALUES (?, ?);
                        """;

        return jdbcManyToMany.executeUpdate(
                testId, tagId, sql, STR."Помилка при додаванні нового тесту до тегу");
    }

    @Override
    public boolean detach(UUID testId, UUID tagId) {
        final String sql =
                """
                        DELETE FROM test_tag
                              WHERE test_id = ? AND tag_id = ?;
                        """;

        return jdbcManyToMany.executeUpdate(
                testId,
                tagId,
                sql,
                STR."Помилка при видаленні запису з таблиці по testId: \{
                        testId.toString()} і tagId: \{
                        tagId.toString()}");
    }
}
