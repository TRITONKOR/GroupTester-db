package com.tritonkor.presentation.viewmodel;

import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ResultViewModel {
    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private final StringProperty testTitle = new SimpleStringProperty();
    private final StringProperty ownerUsername = new SimpleStringProperty();
    private final ObjectProperty<Integer> mark = new SimpleObjectProperty<>();

    public ResultViewModel(UUID id, String testTitle, String ownerUsername, Integer mark) {
        this.id.set(id);
        this.testTitle.set(testTitle);
        this.ownerUsername.set(ownerUsername);
        this.mark.set(mark);
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

    public int getMark() {
        return mark.get();
    }

    public ObjectProperty<Integer> markProperty() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark.set(mark);
    }



    @Override
    public String toString() {
        return new StringJoiner(", ", ResultViewModel.class.getSimpleName() + "[", "]")
                .add("id=" + id.get())
                .add("ownerUsername=" + ownerUsername.get())
                .add("testTitle=" + testTitle.get())
                .add("mark=" + mark.get())
                .toString();
    }
}
