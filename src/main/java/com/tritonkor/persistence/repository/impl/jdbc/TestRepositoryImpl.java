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

/**
 * Implementation of the TestRepository interface using JDBC.
 * This class handles database operations for Test entities.
 */
@Repository
public class TestRepositoryImpl extends GenericJdbcRepository<Test> implements TestRepository {
    private final ConnectionManager connectionManager;
    private final TagRowMapper tagRowMapper;
    private final JdbcManyToMany<Tag> jdbcManyToMany;

    /**
     * Constructor for TestRepositoryImpl.
     *
     * @param connectionManager the connection manager to handle database connections
     * @param rowMapper the row mapper for Test entities
     * @param tagRowMapper the row mapper for Tag entities
     * @param jdbcManyToMany utility for many-to-many relationships
     */
    public TestRepositoryImpl(ConnectionManager connectionManager,
            TestRowMapper rowMapper, TagRowMapper tagRowMapper,
            JdbcManyToMany<Tag> jdbcManyToMany) {
        super(connectionManager, rowMapper, TableNames.TESTS.getName());
        this.connectionManager = connectionManager;
        this.tagRowMapper = tagRowMapper;
        this.jdbcManyToMany = jdbcManyToMany;
    }

    /**
     * Maps the values of a Test entity to a Map.
     *
     * @param test the Test entity
     * @return a Map of column names to values
     */
    @Override
    protected Map<String, Object> tableValues(Test test) {
        Map<String, Object> values = new LinkedHashMap<>();

        if (!test.getTitle().isBlank()) {
            values.put("title", test.getTitle());
        }
        if (Objects.nonNull(test.getOwnerId())) {
            values.put("owner_id", test.getOwnerId());
        }
        values.put("create_date", test.getCreatedAt());

        return values;
    }

    /**
     * Finds a Test entity by its title.
     *
     * @param title the title of the Test
     * @return an Optional containing the Test if found, otherwise empty
     */
    @Override
    public Optional<Test> findByTitle(String title) {
        return findBy("title", title);
    }

    /**
     * Finds all Tags associated with a given Test ID.
     *
     * @param testId the ID of the Test
     * @return a Set of Tags associated with the Test
     */
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

    /**
     * Attaches a Tag to a Test.
     *
     * @param testId the ID of the Test
     * @param tagId the ID of the Tag
     * @return true if the operation was successful, otherwise false
     */
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

    /**
     * Detaches a Tag from a Test.
     *
     * @param testId the ID of the Test
     * @param tagId the ID of the Tag
     * @return true if the operation was successful, otherwise false
     */
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

    /**
     * Finds all Test entities with pagination, sorting, and filtering options.
     *
     * @param offset the offset of the first result
     * @param limit the maximum number of results
     * @param sortColumn the column to sort by
     * @param ascending true for ascending order, false for descending
     * @param testFilterDto the filter criteria for the query
     * @return a Set of Test entities
     */
    @Override
    public Set<Test> findAll(int offset, int limit, String sortColumn, boolean ascending,
            TestFilterDto testFilterDto) {
        return findAll(offset, limit, sortColumn, ascending, testFilterDto, "");
    }

    /**
     * Finds all Test entities by User ID.
     *
     * @param userId the ID of the User
     * @return a Set of Test entities
     */
    @Override
    public Set<Test> findAllByUserId(UUID userId) {
        return findAllWhere(STR."owner_id = '\{userId}'");
    }

    /**
     * Finds all Test entities by User ID with pagination, sorting, and filtering options.
     *
     * @param userId the ID of the User
     * @param offset the offset of the first result
     * @param limit the maximum number of results
     * @param sortColumn the column to sort by
     * @param ascending true for ascending order, false for descending
     * @param testFilterDto the filter criteria for the query
     * @return a Set of Test entities
     */
    @Override
    public Set<Test> findAllByUserId(UUID userId, int offset, int limit, String sortColumn,
            boolean ascending, TestFilterDto testFilterDto) {
        return findAll(offset, limit, sortColumn, ascending, testFilterDto,
                STR."user_id = '\{userId}'");
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
            filters.put("owner_id", testFilterDto.ownerId());
        }

        // Фільтр по create_date
        if (Objects.nonNull(testFilterDto.createdAtStart())
                && Objects.nonNull(testFilterDto.createdAtEnd())) {
            where.append(
                    STR."create_date BETWEEN '\{testFilterDto.createdAtStart()}' AND '\{testFilterDto.createdAtEnd()}' ");
        } else if (Objects.nonNull(testFilterDto.createdAtStart())) {
            where.append(STR."create_date >= '\{testFilterDto.createdAtStart()}' ");
        } else if (Objects.nonNull(testFilterDto.createdAtEnd())) {
            where.append(STR."create_date <= '\{testFilterDto.createdAtEnd()}' ");
        }


        return findAll(offset, limit, sortColumn, ascending, filters, where.toString());
    }
}
