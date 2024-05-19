package com.tritonkor.presentation.controller.test;

import com.tritonkor.domain.dto.TestStoreDto;
import com.tritonkor.domain.dto.TestUpdateDto;
import com.tritonkor.domain.service.impl.AuthenticationService;
import com.tritonkor.domain.service.impl.TestService;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.presentation.viewmodel.TestViewModel;
import com.tritonkor.presentation.viewmodel.UserViewModel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateTestController {
    @Autowired
    private TestListController testListController;

    @Autowired
    private TestService testService;
    @FXML
    private Label testLabel;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> ownerComboBox;
    @FXML
    private Button saveButton;

    private TestViewModel testViewModel;

    private final UserRepository userRepository;

    public CreateTestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @FXML
    public void initialize() {
        testViewModel = new TestViewModel(
                UUID.randomUUID(), "Title", null);

        Set<User> teachers = userRepository.findAllWhere(STR."role = 'TEACHER'");
        Set<String> usernames = new HashSet<>();
        for (User user : teachers) {
            usernames.add(user.getUsername());
        }
        ownerComboBox.getItems().addAll(usernames);

        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
        titleField.textProperty().bindBidirectional(testViewModel.titleProperty());
        ownerComboBox.valueProperty().bindBidirectional(testViewModel.ownerUsernameProperty());
    }

    @FXML
    public void onSave() throws IOException {
        System.out.println("Saving Test Data: " + testViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test Information");
        alert.setHeaderText("Test Data Saved Successfully");
        alert.setContentText(testViewModel.toString());
        alert.showAndWait();

        String title = testViewModel.getTitle();
        User owner = userRepository.findByUsername(testViewModel.getOwnerUsername()).orElseThrow();

        try {
            testService.findByTitle(title);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Назва вже використовується");
            return;
        } catch (EntityNotFoundException e) {
            if (Objects.isNull(owner)) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Користувача не обрано");
                return;
            }

            TestStoreDto testStoreDto = new TestStoreDto(
                    title,
                    owner.getId()
            );

            testService.create(testStoreDto);

            int currentPage = testListController.pagination.getCurrentPageIndex();
            int totalPages = testListController.getTotalPages();

            testListController.pagination.setPageCount(totalPages);
            testListController.updateTestList(currentPage);
        }
    }

    @FXML
    public void onUpdate() throws IOException {
        System.out.println("Updating Test Data: " + testViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test Information");
        alert.setHeaderText("Test Data Updating");
        alert.setContentText(testViewModel.toString());
        alert.showAndWait();

        try {
            testService.findById(testViewModel.getId());

            String title = testViewModel.getTitle();
            User owner = userRepository.findByUsername(testViewModel.getOwnerUsername()).orElseThrow();

            TestUpdateDto testUpdateDto = new TestUpdateDto(
                    testViewModel.getId(),
                    title,
                    owner.getId()
            );

            testService.update(testUpdateDto);
            saveButton.setDisable(false);

            int currentPage = testListController.pagination.getCurrentPageIndex();
            int totalPages = testListController.getTotalPages();

            testListController.pagination.setPageCount(totalPages);
            testListController.updateTestList(currentPage);
        } catch (EntityNotFoundException e) {

            showAlert(Alert.AlertType.ERROR, "Помилка", "Назва вже використовується");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void stealFields(Test test) {
        User owner = userRepository.findById(test.getOwnerId()).orElseThrow();
        testViewModel = new TestViewModel(
                test.getId(),
                test.getTitle(),
                owner.getUsername()
        );

        bindFieldsToViewModel();
        saveButton.setDisable(true);
    }
}


