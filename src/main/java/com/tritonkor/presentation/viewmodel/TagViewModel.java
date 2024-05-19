package com.tritonkor.presentation.viewmodel;

import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TagViewModel {
    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();

    public TagViewModel(UUID id, String name) {
        this.id.set(id);
        this.name.set(name);
    }

    public UUID getId() {
        return id.get();
    }

    public ObjectProperty<UUID> idProperty() {
        return id;
    }

    public void setId(UUID id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TagViewModel.class.getSimpleName() + "[", "]")
                .add("id=" + id.get())
                .add("name=" + name.get())
                .toString();
    }
}
