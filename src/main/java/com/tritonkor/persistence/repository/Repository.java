package com.tritonkor.persistence.repository;

import com.tritonkor.persistence.entity.Entity;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T extends Entity> {
    Optional<T> findById(UUID id);

    Optional<T> findBy(String column, Object value);

    Set<T> findAll();

    Set<T> findAll(int offset, int limit);

    Set<T> findAll(int offset, int limit, String sortColumn, boolean ascending);

    Set<T> findAll(int offset, int limit, String sortColumn, boolean ascending,
            Map<String, Object> filters);

    Set<T> findAll(int offset, int limit, String sortColumn, boolean ascending,
            Map<String, Object> filters, String where);

    Set<T> findAllWhere(String whereQuery);

    long count();

    T save(T entity);

    Set<T> save(Collection<T> entities);

    boolean delete(UUID id);

    boolean delete(Collection<UUID> ids);
}
