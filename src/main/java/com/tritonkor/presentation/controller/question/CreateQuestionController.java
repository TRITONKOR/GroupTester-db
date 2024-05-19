package com.tritonkor.presentation.controller.question;

import com.tritonkor.domain.dto.QuestionStoreDto;
import com.tritonkor.domain.dto.QuestionUpdateDto;
import com.tritonkor.domain.service.impl.QuestionService;
import com.tritonkor.persistence.entity.Question;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.presentation.viewmodel.QuestionViewModel;
import java.io.IOException;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateQuestionController {
    @Autowired
    private QuestionListController questionListController;
    @Autowired
    private QuestionService questionService;
    @FXML
    private Label questionLabel;
    @FXML
    private TextField textField;
    @FXML
    private Button saveButton;

    private QuestionViewModel questionViewModel;

    @FXML
    public void initialize() {
        // Створення користувача з пустими даними як приклад
        questionViewModel = new QuestionViewModel(
                UUID.randomUUID(), "Text");

        // Зв'язування властивостей ViewModel з View
        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
        textField.textProperty().bindBidirectional(questionViewModel.textProperty());
    }

    @FXML
    public void onSave() throws IOException {
        System.out.println("Saving Question Data: " + questionViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Question Information");
        alert.setHeaderText("Question Data Saved Successfully");
        alert.setContentText(questionViewModel.toString());
        alert.showAndWait();

        String text = questionViewModel.getText();

        try {
            questionService.findByText(text);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Текст вже використовується");
            return;
        } catch (EntityNotFoundException e) {
            QuestionStoreDto questionStoreDto = new QuestionStoreDto(
                    text,
                    questionListController.getCurrentTest().getId()
            );

            questionService.create(questionStoreDto);

            int currentPage = questionListController.pagination.getCurrentPageIndex();
            int totalPages = questionListController.getTotalPages();

            questionListController.pagination.setPageCount(totalPages);
            questionListController.updateQuestionList(currentPage);
        }
    }

    @FXML
    public void onUpdate() throws IOException {
        System.out.println("Updating Question Data: " + questionViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Question Information");
        alert.setHeaderText("Question Data Updating");
        alert.setContentText(questionViewModel.toString());
        alert.showAndWait();

        try {
            String text = questionViewModel.getText();

            QuestionUpdateDto questionUpdateDto = new QuestionUpdateDto(
                    questionViewModel.getId(),
                    text,
                    questionListController.getCurrentTest().getId()
            );

            questionService.update(questionUpdateDto);

            int currentPage = questionListController.pagination.getCurrentPageIndex();
            int totalPages = questionListController.getTotalPages();

            questionListController.pagination.setPageCount(totalPages);
            questionListController.updateQuestionList(currentPage);

        } catch (EntityNotFoundException e) {

            showAlert(Alert.AlertType.ERROR, "Помилка", "Питання з цим текстом не існує");
        }
    }

    public void stealFields(Question question) {
        questionViewModel = new QuestionViewModel(
                question.getId(),
                question.getText()
        );

        bindFieldsToViewModel();
        saveButton.setDisable(true);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
