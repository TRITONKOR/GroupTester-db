package com.tritonkor.presentation.viewmodel;

import com.tritonkor.persistence.entity.User;
import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TestViewModel {
    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty ownerUsername = new SimpleStringProperty();

    public TestViewModel(UUID id, String title, String ownerUsername) {
        this.id.set(id);
        this.title.set(title);
        this.ownerUsername.set(ownerUsername);
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

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getOwnerUsername() {
        return ownerUsername.get();
    }

    public StringProperty ownerUsernameProperty() {
        return ownerUsername;
    }

    public void setOwner(String ownerUsername) {
        this.ownerUsername.set(ownerUsername);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TestViewModel.class.getSimpleName() + "[", "]")
                .add("id=" + id.get())
                .add("ownerUsername=" + ownerUsername.get())
                .add("title=" + title.get())
                .toString();
    }
}
