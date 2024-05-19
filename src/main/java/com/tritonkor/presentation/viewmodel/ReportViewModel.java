package com.tritonkor.presentation.viewmodel;

import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReportViewModel {
    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private final StringProperty testTitle = new SimpleStringProperty();
    private final StringProperty ownerUsername = new SimpleStringProperty();

    public ReportViewModel(UUID id, String testTitle, String ownerUsername) {
        this.id.set(id);
        this.testTitle.set(testTitle);
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

    public String getOwnerUsername() {
        return ownerUsername.get();
    }

    public StringProperty ownerUsernameProperty() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername.set(ownerUsername);
    }

    public String getTestTitle() {
        return testTitle.get();
    }

    public StringProperty testTitleProperty() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle.set(testTitle);
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", ReportViewModel.class.getSimpleName() + "[", "]")
                .add("id=" + id.get())
                .add("ownerUsername=" + ownerUsername.get())
                .add("testTitle=" + testTitle.get())
                .toString();
    }
}
