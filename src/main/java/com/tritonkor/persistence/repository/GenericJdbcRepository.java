package com.tritonkor.persistence.repository;

import com.tritonkor.persistence.entity.Entity;
import com.tritonkor.persistence.exception.EntityDeleteException;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.exception.EntityUpdateException;
import com.tritonkor.persistence.repository.mapper.RowMapper;
import com.tritonkor.persistence.util.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GenericJdbcRepository<T extends Entity> implements Repository<T> {

    private final ConnectionManager connectionManager;
    private final RowMapper<T> rowMapper;
    private final String tableName;

    public GenericJdbcRepository(
            ConnectionManager connectionManager, RowMapper<T> rowMapper, String tableName) {
        this.connectionManager = connectionManager;
        this.rowMapper = rowMapper;
        this.tableName = tableName;
    }

    // Переписати на аспекти spring Context
    @Override
    public Optional<T> findById(UUID id) {
        return findBy("id", id);
    }

    @Override
    public Optional<T> findBy(String column, Object value) {
        final String sql =
                STR."""
            SELECT *
              FROM \{
                        tableName}
             WHERE \{
                        column} = ?
        """;

        UUID id = (UUID) value;
        try (Connection connection = connectionManager.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id, Types.OTHER);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return Optional.ofNullable(rowMapper.mapRow(resultSet));
        } catch (SQLException throwables) {
            return Optional.empty();
        }
    }
    @Override
    public Set<T> findAllWhere(String whereQuery) {
        try (Connection connection = connectionManager.get()) {
            return findAllWhere(whereQuery, connection);
        } catch (SQLException throwables) {
            throw new EntityNotFoundException(
                    STR."Помилка при отриманні всіх запитів з таблиці: \{tableName}");
        }
    }

    private Set<T> findAllWhere(String whereQuery, Connection connection) {
        final String sql = STR."""
            SELECT *
              FROM \{tableName}
             WHERE \{whereQuery}
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            ResultSet resultSet = statement.executeQuery();
            Set<T> entities = new LinkedHashSet<>();
            while (resultSet.next()) {
                entities.add(rowMapper.mapRow(resultSet));
            }
            // LOGGER.info("found all users - [%s]".formatted(users));
            return entities;
        } catch (SQLException throwables) {
            throw new EntityNotFoundException(
                    STR."Помилка при отриманні всіх запитів з таблиці: \{tableName}");
        }
    }

    @Override
    public Set<T> findAll() {
        final String sql = STR."""
            SELECT *
              FROM \{tableName}
        """;

        try (Connection connection = connectionManager.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            Set<T> entities = new LinkedHashSet<>();
            while (resultSet.next()) {
                entities.add(rowMapper.mapRow(resultSet));
            }
            // LOGGER.info("found all users - [%s]".formatted(users));
            return entities;
        } catch (SQLException throwables) {
            throw new EntityNotFoundException(
                    STR."Помилка при отриманні всіх запитів з таблиці: \{tableName}");
        }
    }

    @Override
    public Set<T> findAll(int offset, int limit) {
        return findAll(offset, limit, "id", true);
    }

    @Override
    public Set<T> findAll(int offset, int limit, String sortColumn, boolean ascending) {
        return findAll(offset, limit, sortColumn, ascending, new HashMap<>());
    }

    public Set<T> findAll(int offset, int limit, String sortColumn, boolean ascending,
            Map<String, Object> filters) {
        return findAll(offset, limit, sortColumn, ascending, filters, "");
    }

    @Override
    public Set<T> findAll(int offset, int limit, String sortColumn, boolean ascending,
            Map<String, Object> filters, String where) {
        StringBuilder whereClause = new StringBuilder(!where.isBlank() ? where : "1 = 1");
        List<Object> values = new ArrayList<>();
        filters.forEach((column, value) -> {

            if (value instanceof String) {
                whereClause.append(STR." AND \{column} LIKE ?");
                values.add(STR."%\{value}%");
            } else {
                whereClause.append(STR." AND \{column} = ?");
                values.add(value);
            }
        });

        String sortDirection = ascending ? "ASC" : "DESC";
        String sql = STR."""
              SELECT *
                FROM \{tableName}
                WHERE \{whereClause}
             ORDER BY \{sortColumn} \{sortDirection}
                LIMIT ?
               OFFSET ?
        """;

        try (Connection connection = connectionManager.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i), Types.OTHER);
            }
            statement.setInt(values.size() + 1, limit);
            statement.setInt(values.size() + 2, offset);

            ResultSet resultSet = statement.executeQuery();
            Set<T> entities = new LinkedHashSet<>();
            while (resultSet.next()) {
                entities.add(rowMapper.mapRow(resultSet));
            }
            return entities;
        } catch (SQLException e) {
            throw new EntityNotFoundException(
                    "Помилка при отриманні даних з таблиці: %s".formatted(tableName));
        }
    }

    @Override
    public long count() {
        final String sql = STR."""
            SELECT COUNT(ID)
              FROM \{tableName}
        """;

        try (Connection connection = connectionManager.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            long count = resultSet.getLong(1);
            // LOGGER.info("count of users - {}", result);
            return count;
        } catch (SQLException throwables) {
            throw new EntityNotFoundException(
                    STR."Помилка при отриманні кількості записів з таблиці: \{tableName}");
        }
    }

    @Override
    public T save(final T entity) {
        var values = tableValues(entity);

        T newEntity;
        if (Objects.isNull(entity.getId())) {
            UUID newId = UUID.randomUUID();
            values.put("id", newId);
            newEntity = insert(values);
        } else {
            values.put("id", entity.getId());
            newEntity = update(values);
        }

        return newEntity;
    }

    protected T insert(Map<String, Object> values) {
        var attributes = values.keySet();
        String attributesString = String.join(", ", attributes);
        String placeholders =
                Stream.generate(() -> "?")
                        .limit(attributes.size())
                        .collect(Collectors.joining(", "));
        String sql = STR."""
                        INSERT INTO \{tableName} (\{attributesString})
                        VALUES (\{placeholders})
                        """;

        if (attributes.stream().anyMatch(a -> a.equals("create_date"))) {
            values.put("create_date", LocalDateTime.now()); // create_date
        }

        int idIndex = 0;

        return updateExecute(values.values(), sql, "Помилка при додаванні нового запису в таблицю");
    }

    protected T update(Map<String, Object> values) {
        var attributes = values.keySet();
        String attributesString =
                attributes.stream()
                        .filter(a -> !a.equals("create_date") && !a.equals("id"))
                        .map(a -> STR."\{a} = ?")
                        .collect(Collectors.joining(", "));
        String sql = STR."""
                          UPDATE \{tableName}
                             SET \{attributesString}
                           WHERE id = ?
                         """;

        values.remove("create_date"); // create_date


        return updateExecute(values.values(), sql,
                "Помилка при оновленні існуючого запису в таблиці");
    }

    private T updateExecute(Collection<Object> rawValues, String sql, String exceptionMessage) {
        List<Object> values = new ArrayList<>(rawValues);
        try (Connection connection = connectionManager.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.size(); i++) {
                statementSetter(values, i, statement);
            }
            statement.executeUpdate();
            return findById((UUID) values.getLast()).orElseThrow();
        } catch (SQLException | NoSuchElementException e) {
            throw new EntityUpdateException(e.getMessage());
        }
    }

    @Override
    public Set<T> save(Collection<T> entities) {
        Set<T> results;
        List<Map<String, Object>> listOfValues = new ArrayList<>(new LinkedHashSet<>());

        for (var entity : entities) {
            var values = tableValues(entity);
            values.put("id", Objects.isNull(entity.getId()) ? UUID.randomUUID() : entity.getId());
            listOfValues.add(values);
        }

        if (entities.stream().allMatch(e -> Objects.isNull(e.getId()))) {
            results = batchInsert(listOfValues);
        } else {
            results = batchUpdate(listOfValues);
        }
        return results;
    }

    protected Set<T> batchInsert(List<Map<String, Object>> listOfValues) {
        var attributes = listOfValues.getFirst().keySet();
        String attributesString = String.join(", ", attributes);
        String placeholders =
                Stream.generate(() -> "?")
                        .limit(attributes.size())
                        .collect(Collectors.joining(", "));
        String sql =
                STR."""
                INSERT INTO \{
                        tableName} (\{
                        attributesString})
                VALUES (\{
                        placeholders})
        """;

        if (attributes.stream().anyMatch(a -> a.equals("created_at"))) {
            listOfValues.forEach(values -> {
                values.put("created_at", LocalDateTime.now()); // created_at
                values.put("updated_at", LocalDateTime.now()); // updated_at
            });
        }

        return batchExecute(listOfValues, sql, "Помилка при додаванні нового запису в таблицю");
    }

    protected Set<T> batchUpdate(List<Map<String, Object>> listOfValues) {
        var attributes = listOfValues.getFirst().keySet();
        String attributesString =
                attributes.stream()
                        .filter(a -> !a.equals("create_date") && !a.equals("id"))
                        .map(a -> STR."\{a} = ?")
                        .collect(Collectors.joining(", "));
        String sql = STR."""
                          UPDATE \{tableName}
                             SET \{attributesString}
                           WHERE id = ?
                         """;

        return batchExecute(listOfValues, sql, "Помилка при оновленні існуючого запису в таблиці");
    }

    private Set<T> batchExecute(List<Map<String, Object>> listOfRawValues, String sql,
            String exceptionMessage) {
        Set<T> results;
        var listOfValues = listOfRawValues.stream()
                .map(values -> new ArrayList<>(values.values())).toList();
        try (Connection connection = connectionManager.get()) {
            // Відключаємо автоматичний фікс транзакцій
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (var values : listOfValues) {
                    for (int i = 0; i < values.size(); i++) {
                        statementSetter(values, i, statement);
                    }
                    statement.addBatch();
                }

                statement.executeBatch();

                // Отримуємо результати після виконання пакету
                results = getEntitiesAfterBatchExecute(listOfValues, connection);

                // Перевіряємо, чи всі записи були оновлені або додані
                if (results.isEmpty() || listOfValues.size() != results.size()) {
                    // Якщо є помилка, робимо ролбек
                    connection.rollback();
                    throw new EntityUpdateException(exceptionMessage);
                } else {
                    // Якщо все пройшло успішно, комітуємо транзакцію
                    connection.commit();
                }
            } catch (SQLException throwables) {
                // В разі виникнення помилки, робимо ролбек транзакції
                connection.rollback();
                throw new EntityUpdateException(exceptionMessage);
            }
        } catch (SQLException e) {
            throw new EntityUpdateException("Помилка при роботі з підключенням до бази даних");
        }

        return results;
    }

    private static void statementSetter(List<Object> values, int i, PreparedStatement statement)
            throws SQLException {
        if (values.get(i) instanceof Enum) {
            statement.setString(i + 1, values.get(i).toString());
        } else if (values.get(i) instanceof byte[] && Objects.nonNull(values.get(i))) {
            statement.setBytes(i + 1, (byte[]) values.get(i));
        } else {
            statement.setObject(i + 1, values.get(i), Types.OTHER);
        }
    }

    private Set<T> getEntitiesAfterBatchExecute(List<ArrayList<Object>> listOfValues,
            Connection connection) {
        Set<T> results;
        List<String> ids = listOfValues.stream().map(values -> {
            UUID id = (UUID) values.stream()
                    .filter(UUID.class::isInstance)
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("UUID not found"));

            return STR."'\{id.toString()}'";
        }).toList();

        results = findAllWhere(STR."id IN(\{String.join(", ", ids)})", connection);
        return results;
    }

    @Override
    public boolean delete(UUID id) {
        final String sql =
                STR."""
                    DELETE FROM \{
                        tableName}
                          WHERE id = ?
                """;

        try (Connection connection = connectionManager.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id, Types.OTHER);

            return statement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new EntityDeleteException(
                    STR."Помилка при видаленні запису з таблиці по id: \{id.toString()}");
        }
    }

    public boolean delete(Collection<UUID> ids) {
        final String sql =
                STR."""
                    DELETE FROM \{tableName}
                          WHERE id = ?
                """;

        try (Connection connection = connectionManager.get();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            for (var id : ids) {
                statement.setObject(1, id, Types.OTHER);
                statement.addBatch();
            }
            statement.executeBatch();

            return statement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new EntityDeleteException(
                    STR."Помилка при видаленні записів з таблиці по ids: \{ids.toString()}");
        }
    }

    protected abstract Map<String, Object> tableValues(T entity);
}
