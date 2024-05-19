package com.tritonkor.presentation.controller.question;

import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.filter.QuestionFilterDto;
import com.tritonkor.persistence.entity.filter.UserFilterDto;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import com.tritonkor.presentation.controller.MainController;
import com.tritonkor.presentation.controller.answer.AnswerListController;
import java.nio.file.Path;
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
public class QuestionListController {
    @Autowired
    private MainController mainController;
    @Autowired
    private AnswerListController answerListController;
    @Autowired
    private CreateQuestionController createQuestionController;

    @FXML
    private VBox questionListContainer;
    @FXML
    public Pagination pagination;
    private final QuestionRepository questionRepository;

    private static final int PAGE_SIZE = 5;
    private String sortColumn = "text";
    private boolean ascending = true;
    private Test currentTest;
    private QuestionFilterDto currentFilters;

    public QuestionListController( QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @FXML
    public void initialize() {
        pagination.setPageCount(getTotalPages());
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            handlePageChange(newIndex.intValue());
        });
        updateQuestionList(0);
    }

    public int getTotalPages() {
        long totalQuestions = questionRepository.findAllByTestId(currentTest.getId()).size();
        return (int) Math.ceil((double) totalQuestions / PAGE_SIZE);
    }

    public void updateQuestionList(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;
        currentFilters = new QuestionFilterDto(currentTest.getId(), "");

        Set<Question> questions = questionRepository.findAll(
                offset, PAGE_SIZE, sortColumn, ascending, currentFilters
        );

        questionListContainer.getChildren().clear();
        questionListContainer.getChildren().addAll(questions.stream().map(this::createQuestionCard).toList());
    }

    private VBox createQuestionCard(Question question) {
        Label textLabel = new Label(question.getText());
        textLabel.setStyle("-fx-font-weight: bold;");

        Label answerCount = new Label("Answer Count: " + question.getAnswersLazy().size());

        Button deleteButton = new Button("Видалити");
        Button answersButton = new Button("Відповіді");
        Button updateButton = new Button("Оновити");

        HBox hBox = new HBox(deleteButton, answersButton, updateButton);

        deleteButton.setOnAction(event -> handleDeleteQuestion(question));
        answersButton.setOnAction(event -> manageAnswers(question));
        updateButton.setOnAction(event -> handleUpdateQuestion(question));

        VBox userCard = new VBox(textLabel, answerCount, hBox);
        userCard.setSpacing(5);
        userCard.setStyle(
                "-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 5;");

        return userCard;
    }

    @FXML
    private void handlePageChange(int pageIndex) {
        updateQuestionList(pageIndex);
    }

    @FXML
    public void handleSortAscending(ActionEvent actionEvent) {
        ascending = true;
        updateQuestionList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleSortDescending(ActionEvent actionEvent) {
        ascending = false;
        updateQuestionList(pagination.getCurrentPageIndex());
    }

    public Test getCurrentTest() {
        return currentTest;
    }

    public void setCurrentTest(Test currentTest) {
        this.currentTest = currentTest;
    }

    @FXML
    public void handleDeleteQuestion(Question question) {
        questionRepository.delete(question.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Question Information");
        alert.setHeaderText("Question Deletion");
        alert.setContentText("Question Has Been Deleted Successfully");
        alert.showAndWait();

        pagination.setPageCount(getTotalPages());
        updateQuestionList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void manageAnswers(Question question) {
        answerListController.setCurrentQuestion(question);

        String fxmlFile = Path.of("view", "answer", "AnswerList.fxml").toString();

        mainController.switchPage(fxmlFile);
    }

    @FXML
    public void handleUpdateQuestion(Question question) {
        createQuestionController.stealFields(question);
    }
}
