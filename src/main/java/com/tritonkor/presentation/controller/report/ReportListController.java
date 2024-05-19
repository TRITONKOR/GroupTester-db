package com.tritonkor.presentation.controller.report;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.filter.ReportFilterDto;
import com.tritonkor.persistence.entity.filter.TestFilterDto;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import com.tritonkor.persistence.repository.contract.ReportRepository;
import com.tritonkor.persistence.repository.contract.ResultRepository;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.presentation.controller.MainController;
import com.tritonkor.presentation.controller.question.QuestionListController;
import com.tritonkor.presentation.controller.result.ResultListController;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportListController {

    @Autowired
    private MainController mainController;
    @Autowired
    private ResultListController resultListController;
    @Autowired
    private CreateReportController createReportController;

    @FXML
    private ComboBox<String> ownerComboBoxFilter;
    @FXML
    private ComboBox<String> testComboBoxFilter;
    @FXML
    private DatePicker createDateFromPicker;
    @FXML
    private DatePicker createDateToPicker;
    @FXML
    private VBox reportListContainer;
    @FXML
    public Pagination pagination;

    private static final int PAGE_SIZE = 4;
    private String sortColumn = "test_id";
    private boolean ascending = true;

    private final TestRepository testRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;

    private ReportFilterDto currentFilters = new ReportFilterDto(null, null, null, null);

    public ReportListController(TestRepository testRepository, ReportRepository reportRepository,
            UserRepository userRepository, ResultRepository resultRepository) {
        this.testRepository = testRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.resultRepository = resultRepository;
    }

    @FXML
    public void initialize() {
        Set<User> teachers = userRepository.findAllWhere(STR."role = 'TEACHER'");
        Set<String> usernames = new HashSet<>();
        for (User user : teachers) {
            usernames.add(user.getUsername());
        }
        ownerComboBoxFilter.getItems().addAll(usernames);

        Set<Test> tests = testRepository.findAll();
        Set<String> testTitles = new HashSet<>();
        for (Test test : tests) {
            testTitles.add(test.getTitle());
        }
        testComboBoxFilter.getItems().addAll(testTitles);

        pagination.setPageCount(getTotalPages());
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            handlePageChange(newIndex.intValue());
        });
        updateReportList(0);
    }


    public int getTotalPages() {
        long totalTests = reportRepository.count();
        return (int) Math.ceil((double) totalTests / PAGE_SIZE);
    }

    public void updateReportList(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;

        Set<Report> reports = reportRepository.findAll(
                offset, PAGE_SIZE, sortColumn, ascending, currentFilters
        );

        reportListContainer.getChildren().clear();
        reportListContainer.getChildren().addAll(reports.stream().map(this::createReportCard).toList());
    }

    private VBox createReportCard(Report report) {
        Test test = testRepository.findById(report.getTestId()).orElseThrow();
        User owner = userRepository.findById(report.getOwnerId()).orElseThrow();
        int resultCount =  resultRepository.findAllByReportId(report.getId()).size();

        Label testTitleLabel = new Label(test.getTitle());
        testTitleLabel.setStyle("-fx-font-weight: bold;");

        Label ownerUsername = new Label("Логін власника: " + owner.getUsername());
        Label resultCountLabel = new Label("Кількість результатів: " + resultCount);
        Label createDateLabel = new Label("Дата створення: " + report.getCreatedAt().toString());

        Button deleteButton = new Button("Видалити");
        Button resultsButton = new Button("Результати");
        Button updateButton = new Button("Оновити");

        HBox hBox = new HBox(deleteButton, resultsButton, updateButton);

        deleteButton.setOnAction(event -> handleDeleteReport(report));
        resultsButton.setOnAction(event -> manageResults(report));
        updateButton.setOnAction(event -> handleUpdateReport(report));

        VBox testCard = new VBox(testTitleLabel, ownerUsername, resultCountLabel, createDateLabel
                , hBox);
        testCard.setSpacing(5);
        testCard.setStyle(
                "-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 5;");

        return testCard;
    }

    @FXML
    private void handlePageChange(int pageIndex) {
        updateReportList(pageIndex);
    }

    @FXML
    public void handleSortAscending(ActionEvent actionEvent) {
        ascending = true;
        updateReportList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleSortDescending(ActionEvent actionEvent) {
        ascending = false;
        updateReportList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleApplyFilters(ActionEvent actionEvent) {
        UUID ownerId = null;
        UUID testId = null;
        LocalDateTime createDateFrom = null;
        LocalDateTime createDateTo = null;

        if (Objects.nonNull(ownerComboBoxFilter.getValue())) {
            User user = userRepository.findByUsername(ownerComboBoxFilter.getValue()).orElseThrow();
            ownerId = user.getId();
        }
        if (Objects.nonNull(testComboBoxFilter.getValue())) {
            Test test = testRepository.findByTitle(testComboBoxFilter.getValue()).orElseThrow();
            testId = test.getId();
        }
        if (Objects.nonNull(createDateFromPicker.getValue())) {
            createDateFrom = createDateFromPicker.getValue().atTime(LocalTime.MIN);
        }
        if (Objects.nonNull(createDateToPicker.getValue())) {
            createDateTo = createDateToPicker.getValue().atTime(LocalTime.MIN);
        }

        // не дороблено, щоб фільтрувати по діапазону дат (приклад в коментарях є)
        currentFilters = new ReportFilterDto(ownerId, testId, createDateFrom, createDateTo);

        pagination.setPageCount(getTotalPages());
        updateReportList(0);
    }

    @FXML
    public void handleDeleteReport(Report report) {
        reportRepository.delete(report.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report Information");
        alert.setHeaderText("Report Deletion");
        alert.setContentText("Report Has Been Deleted Successfully");
        alert.showAndWait();

        pagination.setPageCount(getTotalPages());
        updateReportList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void manageResults(Report report) {
        resultListController.setCurrentReport(report);

        String fxmlFile = Path.of("view", "result", "ResultList.fxml").toString();

        mainController.switchPage(fxmlFile);
    }

    @FXML
    public void handleUpdateReport(Report report) {
        createReportController.stealFields(report);
    }
}
