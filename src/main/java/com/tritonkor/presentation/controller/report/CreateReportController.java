package com.tritonkor.presentation.controller.report;

import com.tritonkor.domain.dto.ReportStoreDto;
import com.tritonkor.domain.dto.ReportUpdateDto;
import com.tritonkor.domain.service.impl.ReportService;
import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.presentation.viewmodel.ReportViewModel;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateReportController {
    @Autowired
    private ReportListController reportListController;

    @Autowired
    private ReportService reportService;
    @FXML
    private Label reportLabel;
    @FXML
    private ComboBox<String> testComboBox;
    @FXML
    private ComboBox<String> ownerComboBox;
    @FXML
    private Button saveButton;


    private ReportViewModel reportViewModel;

    private final UserRepository userRepository;
    private final TestRepository testRepository;

    public CreateReportController(UserRepository userRepository, TestRepository testRepository) {
        this.userRepository = userRepository;
        this.testRepository = testRepository;
    }

    @FXML
    public void initialize() {
        reportViewModel = new ReportViewModel(
                UUID.randomUUID(), null, null);

        Set<User> teachers = userRepository.findAllWhere(STR."role = 'TEACHER'");
        Set<String> usernames = new HashSet<>();
        for (User user : teachers) {
            usernames.add(user.getUsername());
        }
        ownerComboBox.getItems().addAll(usernames);

        Set<Test> tests = testRepository.findAll();
        Set<String> testTitles = new HashSet<>();
        for (Test test : tests) {
            testTitles.add(test.getTitle());
        }
        testComboBox.getItems().addAll(testTitles);

        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
        testComboBox.valueProperty().bindBidirectional(reportViewModel.testTitleProperty());
        ownerComboBox.valueProperty().bindBidirectional(reportViewModel.ownerUsernameProperty());
    }

    @FXML
    public void onSave() throws IOException {
        System.out.println("Saving Report Data: " + reportViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Information");
        alert.setHeaderText("Report Data Saving");
        alert.setContentText(reportViewModel.toString());
        alert.showAndWait();

        if (reportViewModel.getTestTitle().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Тест не обрано");
            return;
        }
        if (reportViewModel.getOwnerUsername().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Користувача не обрано");
            return;
        }

        Test test = testRepository.findByTitle(reportViewModel.getTestTitle()).orElseThrow();
        User owner = userRepository.findByUsername(reportViewModel.getOwnerUsername())
                .orElseThrow();

        ReportStoreDto reportStoreDto = new ReportStoreDto(
                test.getId(),
                owner.getId()
        );

        reportService.create(reportStoreDto);

        int currentPage = reportListController.pagination.getCurrentPageIndex();
        int totalPages = reportListController.getTotalPages();

        reportListController.pagination.setPageCount(totalPages);
        reportListController.updateReportList(currentPage);
    }

    @FXML
    public void onUpdate() throws IOException {
        System.out.println("Updating Report Data: " + reportViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Information");
        alert.setHeaderText("Report Data Updating");
        alert.setContentText(reportViewModel.toString());
        alert.showAndWait();

        Test test = testRepository.findByTitle(reportViewModel.getTestTitle()).orElseThrow();
        User owner = userRepository.findByUsername(reportViewModel.getOwnerUsername())
                .orElseThrow();

        try {
            reportService.findById(reportViewModel.getId());

            ReportUpdateDto reportUpdateDto = new ReportUpdateDto(
                    reportViewModel.getId(),
                    test.getId(),
                    owner.getId()
            );

            reportService.update(reportUpdateDto);

            int currentPage = reportListController.pagination.getCurrentPageIndex();
            int totalPages = reportListController.getTotalPages();

            reportListController.pagination.setPageCount(totalPages);
            reportListController.updateReportList(currentPage);
        } catch (EntityNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Звіт з таким айді не існує");
        }
    }

    public void stealFields(Report report) {
        Test test = testRepository.findById(report.getTestId()).orElseThrow();
        User owner = userRepository.findById(report.getOwnerId()).orElseThrow();

        reportViewModel = new ReportViewModel(
                report.getId(),
                test.getTitle(),
                owner.getUsername()
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
