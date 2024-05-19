package com.tritonkor.presentation.controller.answer;

import com.tritonkor.persistence.entity.Answer;
import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.filter.AnswerFilterDto;
import com.tritonkor.persistence.repository.contract.AnswerRepository;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnswerListController {
    @Autowired
    private CreateAnswerController createAnswerController;

    @FXML
    private VBox answerListContainer;
    @FXML
    public Pagination pagination;

    private final AnswerRepository answerRepository;

    private static final int PAGE_SIZE = 5;
    private String sortColumn = "text";
    private boolean ascending = true;

    private Question currentQuestion;
    private AnswerFilterDto currentFilters;

    public AnswerListController( AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @FXML
    public void initialize() {
        pagination.setPageCount(getTotalPages());
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            handlePageChange(newIndex.intValue());
        });
        updateAnswerList(0);
    }

    public int getTotalPages() {
        long totalUsers = answerRepository.findAllByQuestionId(currentQuestion.getId()).size();
        return (int) Math.ceil((double) totalUsers / PAGE_SIZE);
    }

    public void updateAnswerList(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;
        currentFilters = new AnswerFilterDto(currentQuestion.getId(), "");

        Set<Answer> answers = answerRepository.findAll(
                offset, PAGE_SIZE, sortColumn, ascending, currentFilters
        );

        answerListContainer.getChildren().clear();
        answerListContainer.getChildren().addAll(answers.stream().map(this::createAnswerCard).toList());
    }

    private VBox createAnswerCard(Answer answer) {
        Label textLabel = new Label(answer.getText());
        textLabel.setStyle("-fx-font-weight: bold;");

        Label isCorrectLabel = new Label(answer.getCorrect().toString());

        Button deleteButton = new Button("Видалити");
        Button updateButton = new Button("Оновити");

        HBox hBox = new HBox(deleteButton, updateButton);

        deleteButton.setOnAction(event -> handleDeleteAnswer(answer));
        updateButton.setOnAction(event -> handleUpdateAnswer(answer));

        VBox userCard = new VBox(textLabel, isCorrectLabel, hBox);
        userCard.setSpacing(5);
        userCard.setStyle(
                "-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 5;");

        return userCard;
    }

    @FXML
    private void handlePageChange(int pageIndex) {
        updateAnswerList(pageIndex);
    }

    @FXML
    public void handleSortAscending(ActionEvent actionEvent) {
        ascending = true;
        updateAnswerList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleSortDescending(ActionEvent actionEvent) {
        ascending = false;
        updateAnswerList(pagination.getCurrentPageIndex());
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    @FXML
    public void handleDeleteAnswer(Answer answer) {
        answerRepository.delete(answer.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Answer Information");
        alert.setHeaderText("Answer Deletion");
        alert.setContentText("Answer Has Been Deleted Successfully");
        alert.showAndWait();
        pagination.setPageCount(getTotalPages());
        updateAnswerList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleUpdateAnswer(Answer answer) {
        createAnswerController.stealFields(answer);
    }
}
