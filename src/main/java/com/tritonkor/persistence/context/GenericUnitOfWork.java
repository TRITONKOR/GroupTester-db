package com.tritonkor.persistence.context;

import com.tritonkor.persistence.entity.Entity;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericUnitOfWork<T extends Entity> implements UnitOfWork<T> {
    final Logger LOGGER = LoggerFactory.getLogger(GenericUnitOfWork.class);
    private final Map<UnitActions, List<T>> context;
    private final Repository<T> repository;

    private Set<T> entities;

    public GenericUnitOfWork(Repository<T> repository) {
        this.repository = repository;
        context = new HashMap<>();
    }

    @Override
    public void registerNew(T entity) {
        LOGGER.info("Registering {} for insert in context.", entity.getId());
        register(entity, UnitActions.INSERT);
    }

    @Override
    public void registerModified(T entity) {
        LOGGER.info("Registering {} for modify in context.", entity.getId());
        register(entity, UnitActions.MODIFY);
    }

    @Override
    public void registerDeleted(T entity) {
        LOGGER.info("Registering {} for delete in context.", entity.getId());
        register(entity, UnitActions.DELETE);
    }

    @Override
    public void registerDeleted(UUID id) {
        LOGGER.info("Registering {} for delete in context.", id);
        Entity entity = new Entity(id);
        register((T)entity, UnitActions.DELETE);
    }

    private void register(T entity, UnitActions operation) {
        var entitiesToOperate = context.get(operation);

        if (entitiesToOperate == null) {
            entitiesToOperate = new ArrayList<>();
        }

        entitiesToOperate.add(entity);
        context.put(operation, entitiesToOperate);
    }

    /** All UnitOfWork operations are batched and executed together on commit only. */
    @Override
    public void commit() {
        if (context.isEmpty()) {
            return;
        }
        LOGGER.info("Commit started");
        if (context.containsKey(UnitActions.INSERT)) {
            commitInsert();
        }
        if (context.containsKey(UnitActions.MODIFY)) {
            commitModify();
        }
        if (context.containsKey(UnitActions.DELETE)) {
            commitDelete();
        }
        LOGGER.info("Commit finished.");
        context.clear();
    }

    private void commitInsert() {
        var entitiesToBeInserted = context.get(UnitActions.INSERT);
        repository.save(entitiesToBeInserted);
    }

    private void commitModify() {
        var modifiedEntities = context.get(UnitActions.MODIFY);
        repository.save(modifiedEntities);
/*        for (var entity : modifiedEntities) {
            LOGGER.info("Modifying {} in table.", entity.id());
            repository.save(entity);
        }*/
    }

    private void commitDelete() {
        var deletedEntities = context.get(UnitActions.DELETE);
        repository.delete(deletedEntities.stream().map(Entity::getId).toList());
    }

    public T getEntity(UUID id) {
        return entities.stream().filter(e -> e.getId().equals(id)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Спочатку потрібно зробити операцію додавання чи оновлення. Або це дивна помилка..."));
    }

    public T getEntity() {
        return entities.stream().findFirst().orElseThrow();
    }

    public Set<T> getEntities() {
        return entities;
    }
}