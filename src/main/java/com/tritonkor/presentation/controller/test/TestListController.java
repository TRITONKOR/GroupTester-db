package com.tritonkor.presentation.controller.test;

import com.tritonkor.persistence.entity.Test;
import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.filter.TestFilterDto;
import com.tritonkor.persistence.repository.contract.QuestionRepository;
import com.tritonkor.persistence.repository.contract.TagRepository;
import com.tritonkor.persistence.repository.contract.TestRepository;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.presentation.controller.MainController;
import com.tritonkor.presentation.controller.question.QuestionListController;
import com.tritonkor.presentation.controller.tag.TagListController;
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
public class TestListController {
    @Autowired
    private MainController mainController;
    @Autowired
    private QuestionListController questionListController;
    @Autowired
    private TestTagsListController testTagsListController;
    @Autowired
    private CreateTestController createTestController;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> ownerComboBoxFilter;
    @FXML
    private DatePicker createDateFromPicker;
    @FXML
    private DatePicker createDateToPicker;
    @FXML
    private VBox testListContainer;
    @FXML
    public Pagination pagination;
    private static final int PAGE_SIZE = 4;
    private String sortColumn = "title";
    private boolean ascending = true;

    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    private TestFilterDto currentFilters = new TestFilterDto("", null, null, null);

    public TestListController(TestRepository testRepository, UserRepository userRepository,
            QuestionRepository questionRepository) {
        this.testRepository = testRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }

    /**
     * Initializes the TestListController after the FXML file has been loaded.
     * This method populates the ownerComboBoxFilter with usernames of teachers and sets up pagination.
     */
    @FXML
    public void initialize() {
        Set<User> teachers = userRepository.findAllWhere(STR."role = 'TEACHER'");
        Set<String> usernames = new HashSet<>();
        for (User user : teachers) {
            usernames.add(user.getUsername());
        }
        ownerComboBoxFilter.getItems().addAll(usernames);

        pagination.setPageCount(getTotalPages());
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            handlePageChange(newIndex.intValue());
        });
        updateTestList(0);
    }

    /**
     * Calculates the total number of pages needed for pagination based on the total number of tests.
     *
     * @return The total number of pages needed for pagination.
     */
    public int getTotalPages() {
        long totalTests = testRepository.count();
        return (int) Math.ceil((double) totalTests / PAGE_SIZE);
    }

    /**
     * Updates the list of tests displayed on the page according to the current pagination settings and filters.
     *
     * @param pageIndex The index of the page to be displayed.
     */
    public void updateTestList(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;

        Set<Test> tests = testRepository.findAll(
                offset, PAGE_SIZE, sortColumn, ascending, currentFilters
        );

        testListContainer.getChildren().clear();
        testListContainer.getChildren().addAll(tests.stream().map(this::createTestCard).toList());
    }

    /**
     * Creates a visual representation (VBox) of a test card with its details.
     *
     * @param test The test object for which the card is being created.
     * @return The VBox representing the test card.
     */
    private VBox createTestCard(Test test) {
        Label titleLabel = new Label(test.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold;");
        User owner = userRepository.findById(test.getOwnerId()).orElseThrow();
        int questionCount = questionRepository.findAllByTestId(test.getId()).size();
        int tagCount = testRepository.findAllTags(test.getId()).size();

        Label ownerUsername = new Label("Логін власника: " + owner.getUsername());
        Label questionCountLabel = new Label("Кількість питань: " + questionCount);
        Label tagCountLabel = new Label("Кількість тегів: " + tagCount);
        Label createDateLabel = new Label("Дата створення: " + test.getCreatedAt().toString());

        Button deleteButton = new Button("Видалити");
        Button questionsButton = new Button("Питання");
        Button tagsButton = new Button("Теги");
        Button updateButton = new Button("Оновити");

        HBox hBox = new HBox(deleteButton, questionsButton, tagsButton, updateButton);

        deleteButton.setOnAction(event -> handleDeleteTest(test));
        questionsButton.setOnAction(event -> manageQuestions(test));
        tagsButton.setOnAction(event -> manageTags(test));
        updateButton.setOnAction(event -> handleUpdateTest(test));

        VBox testCard = new VBox(titleLabel, ownerUsername, questionCountLabel, tagCountLabel, createDateLabel
                , hBox);
        testCard.setSpacing(5);
        testCard.setStyle(
                "-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 5;");

        return testCard;
    }

    /**
     * Handles the change of page in the pagination control.
     *
     * @param pageIndex The index of the page to be displayed.
     */
    @FXML
    private void handlePageChange(int pageIndex) {
        updateTestList(pageIndex);
    }

    /**
     * Handles the action of sorting tests in ascending order.
     *
     * @param actionEvent The ActionEvent triggered by the sortAscendingButton.
     */
    @FXML
    public void handleSortAscending(ActionEvent actionEvent) {
        ascending = true;
        updateTestList(pagination.getCurrentPageIndex());
    }

    /**
     * Handles the action of sorting tests in descending order.
     *
     * @param actionEvent The ActionEvent triggered by the sortDescendingButton.
     */
    @FXML
    public void handleSortDescending(ActionEvent actionEvent) {
        ascending = false;
        updateTestList(pagination.getCurrentPageIndex());
    }

    /**
     * Handles the action of applying filters to the tests.
     *
     * @param actionEvent The ActionEvent triggered by the applyFiltersButton.
     */
    @FXML
    public void handleApplyFilters(ActionEvent actionEvent) {
        UUID ownerId = null;
        LocalDateTime createDateFrom = null;
        LocalDateTime createDateTo = null;
        if (Objects.nonNull(ownerComboBoxFilter.getValue())) {
            User user = userRepository.findByUsername(ownerComboBoxFilter.getValue()).orElseThrow();
            ownerId = user.getId();
        }
        if (Objects.nonNull(createDateFromPicker.getValue())) {
            createDateFrom = createDateFromPicker.getValue().atTime(LocalTime.MIN);
        }
        if (Objects.nonNull(createDateToPicker.getValue())) {
            createDateTo = createDateToPicker.getValue().atTime(LocalTime.MIN);
        }
        String title = searchField.getText().trim();

        // не дороблено, щоб фільтрувати по діапазону дат (приклад в коментарях є)
        currentFilters = new TestFilterDto(title, ownerId, createDateFrom, createDateTo);

        pagination.setPageCount(getTotalPages());
        updateTestList(0);
    }

    /**
     * Handles the action of deleting a test.
     *
     * @param test The test object to be deleted.
     */
    @FXML
    public void handleDeleteTest(Test test) {
        testRepository.delete(test.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Test Information");
        alert.setHeaderText("Test Deletion");
        alert.setContentText(test.getTitle() + " Has Been Deleted Successfully");
        alert.showAndWait();

        pagination.setPageCount(getTotalPages());
        updateTestList(pagination.getCurrentPageIndex());
    }

    /**
     * Handles the action of managing questions related to a test.
     *
     * @param test The test object for which questions are being managed.
     */
    @FXML
    public void manageQuestions(Test test) {
        questionListController.setCurrentTest(test);

        String fxmlFile = Path.of("view", "question", "QuestionList.fxml").toString();

        mainController.switchPage(fxmlFile);
    }

    /**
     * Handles the action of managing tags related to a test.
     *
     * @param test The test object for which tags are being managed.
     */
    @FXML
    public void manageTags(Test test) {
        testTagsListController.setCurrentTest(test);

        String fxmlFile = Path.of("view", "test", "TestTagsList.fxml").toString();

        mainController.switchPage(fxmlFile);
    }

    /**
     * Handles the action of updating a test.
     *
     * @param test The test object to be updated.
     */
    @FXML
    public void handleUpdateTest(Test test) {
        createTestController.stealFields(test);
    }
}
