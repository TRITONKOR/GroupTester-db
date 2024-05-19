package com.tritonkor.presentation.controller.answer;

import com.tritonkor.domain.dto.AnswerStorDto;
import com.tritonkor.domain.dto.AnswerUpdateDto;
import com.tritonkor.domain.service.impl.AnswerService;
import com.tritonkor.persistence.entity.Answer;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.presentation.viewmodel.AnswerViewModel;
import java.io.IOException;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateAnswerController {

    @Autowired
    private AnswerListController answerListController;
    @Autowired
    private AnswerService answerService;
    @FXML
    private Label answerLabel;
    @FXML
    private TextField textField;
    @FXML
    private CheckBox isCorrectCheckBox;
    @FXML
    private Button saveButton;

    private AnswerViewModel answerViewModel;

    @FXML
    public void initialize() {
        // Створення користувача з пустими даними як приклад
        answerViewModel = new AnswerViewModel(
                UUID.randomUUID(), "Text", false);

        // Зв'язування властивостей ViewModel з View
        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
        textField.textProperty().bindBidirectional(answerViewModel.textProperty());
        isCorrectCheckBox.selectedProperty().bindBidirectional(answerViewModel.isCorrectProperty());
    }

    @FXML
    public void onSave() throws IOException {
        System.out.println("Saving Answer Data: " + answerViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Answer Information");
        alert.setHeaderText("Answer Data Saved Successfully");
        alert.setContentText(answerViewModel.toString());
        alert.showAndWait();

        String text = answerViewModel.getText();
        Boolean isCorrect = answerViewModel.getIsCorrect();

        try {
            answerService.findByText(text);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Текст вже використовується");
            return;
        } catch (EntityNotFoundException e) {
            AnswerStorDto answerStorDto = new AnswerStorDto(
                    text,
                    answerListController.getCurrentQuestion().getId(),
                    isCorrect
            );

            answerService.create(answerStorDto);

            int currentPage = answerListController.pagination.getCurrentPageIndex();
            int totalPages = answerListController.getTotalPages();

            answerListController.pagination.setPageCount(totalPages);
            answerListController.updateAnswerList(currentPage);
        }
    }

    @FXML
    public void onUpdate() throws IOException {
        System.out.println("Updating Answer Data: " + answerViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Answer Information");
        alert.setHeaderText("Answer Data Updating");
        alert.setContentText(answerViewModel.toString());
        alert.showAndWait();

        String text = answerViewModel.getText();
        Boolean isCorrect = answerViewModel.getIsCorrect();

        AnswerUpdateDto answerUpdateDto = new AnswerUpdateDto(
                answerViewModel.getId(),
                text,
                answerListController.getCurrentQuestion().getId(),
                isCorrect
        );

        answerService.update(answerUpdateDto);

        int currentPage = answerListController.pagination.getCurrentPageIndex();
        int totalPages = answerListController.getTotalPages();

        answerListController.pagination.setPageCount(totalPages);
        answerListController.updateAnswerList(currentPage);
    }

    public void stealFields(Answer answer) {
        answerViewModel = new AnswerViewModel(
                answer.getId(),
                answer.getText(),
                answer.getCorrect()
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
