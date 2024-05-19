package com.tritonkor.presentation.viewmodel;

import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class QuestionViewModel {
    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private final StringProperty text = new SimpleStringProperty();

    public QuestionViewModel(UUID id, String title) {
        this.id.set(id);
        this.text.set(title);
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

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", QuestionViewModel.class.getSimpleName() + "[", "]")
                .add("id=" + id.get())
                .add("text=" + text.get())
                .toString();
    }
}
