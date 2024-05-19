package com.tritonkor.presentation.viewmodel;

import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AnswerViewModel {
    private final ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private final StringProperty text = new SimpleStringProperty();
    private final BooleanProperty isCorrect = new SimpleBooleanProperty();

    public AnswerViewModel(UUID id, String title, Boolean isCorrect) {
        this.id.set(id);
        this.text.set(title);
        this.isCorrect.set(isCorrect);
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

    public Boolean getIsCorrect() { return isCorrect.get(); }

    public BooleanProperty isCorrectProperty() { return isCorrect;}

    public void setIsCorrect(Boolean isCorrect) { this.isCorrect.set(isCorrect);}

    @Override
    public String toString() {
        return new StringJoiner(", ", AnswerViewModel.class.getSimpleName() + "[", "]")
                .add("id=" + id.get())
                .add("text=" + text.get())
                .toString();
    }
}
