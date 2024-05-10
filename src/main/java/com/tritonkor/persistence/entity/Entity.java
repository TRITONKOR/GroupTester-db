package com.tritonkor.persistence.entity;

import java.util.Objects;
import java.util.UUID;

public class Entity {
    protected final UUID id;

    protected transient boolean isValid;

    /**
     * Constructs a new {@code Entity} with the specified identifier.
     *
     * @param id The universally unique identifier (UUID) associated with the entity.
     */
    public Entity(UUID id) {
        this.id = id;
    }

    /**
     * Gets the universally unique identifier (UUID) associated with the entity.
     *
     * @return The UUID of the entity.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Equality is based on the equality of the identifiers.
     *
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the o argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    /**
     * Returns a hash code value for the entity.
     *
     * @return A hash code value based on the identifier.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
