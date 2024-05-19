package com.tritonkor.presentation.controller.result;

import com.tritonkor.persistence.entity.Report;
import com.tritonkor.persistence.entity.Result;
import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.filter.AnswerFilterDto;
import com.tritonkor.persistence.entity.filter.ResultFilterDto;
import com.tritonkor.persistence.repository.contract.ResultRepository;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.contract.UserRepository;
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
public class ResultListController {
    @Autowired
    private CreateResultController createResultController;

    @FXML
    private VBox resultListContainer;
    @FXML
    public Pagination pagination;
    private final ResultRepository resultRepository;
    private static final int PAGE_SIZE = 5;
    private String sortColumn = "text";
    private boolean ascending = true;
    private Report currentReport;

    private final TestRepository testRepository;
    private final UserRepository userRepository;

    private ResultFilterDto currentFilters;

    public ResultListController(ResultRepository resultRepository, TestRepository testRepository,
            UserRepository userRepository) {
        this.resultRepository = resultRepository;
        this.testRepository = testRepository;
        this.userRepository = userRepository;
    }

    @FXML
    public void initialize() {
        pagination.setPageCount(getTotalPages());
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            handlePageChange(newIndex.intValue());
        });
        updateResultList(0);
    }

    public int getTotalPages() {
        long totalUsers = resultRepository.findAllByReportId(currentReport.getId()).size();
        return (int) Math.ceil((double) totalUsers / PAGE_SIZE);
    }

    public void updateResultList(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;
        currentFilters = new ResultFilterDto(currentReport.getOwnerId(), currentReport.getTestId(),
                currentReport.getId(), null, null);

        Set<Result> results = resultRepository.findAllByReportId(currentReport.getId());

        resultListContainer.getChildren().clear();
        resultListContainer.getChildren()
                .addAll(results.stream().map(this::createReportCard).toList());
    }

    private VBox createReportCard(Result result) {
        Test test = testRepository.findById(result.getTestId()).orElseThrow();
        User owner = userRepository.findById(result.getOwnerId()).orElseThrow();

        Label testTitleLabel = new Label(test.getTitle());
        testTitleLabel.setStyle("-fx-font-weight: bold;");

        Label usernameLabel = new Label(owner.getUsername());
        Label markLabel = new Label(result.getMark().toString());
        Label createDateLabel = new Label("Дата створення: " + result.getCreatedAt().toString());

        Button deleteButton = new Button("Видалити");
        Button updateButton = new Button("Оновити");

        HBox hBox = new HBox(deleteButton, updateButton);

        deleteButton.setOnAction(event -> handleDeleteResult(result));
        updateButton.setOnAction(event -> handleUpdateResult(result));

        VBox userCard = new VBox(testTitleLabel, usernameLabel, markLabel, createDateLabel, hBox);
        userCard.setSpacing(5);
        userCard.setStyle(
                "-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 5;");

        return userCard;
    }

    @FXML
    private void handlePageChange(int pageIndex) {
        updateResultList(pageIndex);
    }

    @FXML
    public void handleSortAscending(ActionEvent actionEvent) {
        ascending = true;
        updateResultList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleSortDescending(ActionEvent actionEvent) {
        ascending = false;
        updateResultList(pagination.getCurrentPageIndex());
    }

    public Report getCurrentReport() {
        return currentReport;
    }

    public void setCurrentReport(Report currentReport) {
        this.currentReport = currentReport;
    }

    @FXML
    public void handleDeleteResult(Result result) {
        resultRepository.delete(result.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Result Information");
        alert.setHeaderText("Result Deletion");
        alert.setContentText("Result Has Been Deleted Successfully");
        alert.showAndWait();

        pagination.setPageCount(getTotalPages());
        updateResultList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleUpdateResult(Result result) {
        createResultController.stealFields(result);
    }
}
