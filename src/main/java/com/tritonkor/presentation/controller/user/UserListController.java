package com.tritonkor.presentation.controller.user;

import com.tritonkor.persistence.entity.User;
import com.tritonkor.persistence.entity.User.Role;
import com.tritonkor.persistence.entity.filter.UserFilterDto;
import com.tritonkor.persistence.repository.contract.UserRepository;
import com.tritonkor.presentation.viewmodel.UserViewModel;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
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

// Потрібно було використовувати UserService замість UserRepository
@Component
public class UserListController {
    @Autowired
    private CreateUserController createUserController;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Role> roleComboBoxFilter;
    @FXML
    private DatePicker birthdayFromPicker;
    @FXML
    private DatePicker birthdayToPicker;
    @FXML
    private VBox userListContainer;

    @FXML
    public Pagination pagination;

    private final UserRepository userRepository;

    private static final int PAGE_SIZE = 4;
    private String sortColumn = "username";
    private boolean ascending = true;
    private UserViewModel userViewModel;
    private UserFilterDto currentFilters = new UserFilterDto("", "", null, null, null);

    public UserListController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @FXML
    public void initialize() {
        roleComboBoxFilter.getItems().addAll(Role.values());
        roleComboBoxFilter.setPromptText("Виберіть роль");

        pagination.setPageCount(getTotalPages());
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            handlePageChange(newIndex.intValue());
        });

        updateUserList(0);
    }

    public int getTotalPages() {
        long totalUsers = userRepository.count();
        return (int) Math.ceil((double) totalUsers / PAGE_SIZE);
    }

    public void updateUserList(int pageIndex) {
        int offset = pageIndex * PAGE_SIZE;

        Set<User> users = userRepository.findAll(
            offset, PAGE_SIZE, sortColumn, ascending, currentFilters
        );

        userListContainer.getChildren().clear();
        userListContainer.getChildren().addAll(users.stream().map(this::createUserCard).toList());
    }

    private VBox createUserCard(User user) {
        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.setStyle("-fx-font-weight: bold;");

        Label emailLabel = new Label("Email: " + user.getEmail());
        Label birthdayLabel = new Label("День народження: " + user.getBirthday().toString());
        Label roleLabel = new Label("Роль: " + user.getRole().getName());

        Button deleteButton = new Button("Видалити");
        Button updateButton = new Button("Оновити");

        HBox hBox = new HBox(deleteButton, updateButton);

        deleteButton.setOnAction(event -> handleDeleteUser(user));
        updateButton.setOnAction(event -> {
            try {
                handleUpdateUser(user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        VBox userCard = new VBox(usernameLabel, emailLabel, birthdayLabel, roleLabel, hBox);
        userCard.setSpacing(5);
        userCard.setStyle(
            "-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1; -fx-background-radius: 5;");

        return userCard;
    }

    @FXML
    private void handlePageChange(int pageIndex) {
        updateUserList(pageIndex);
    }

    @FXML
    public void handleSortAscending(ActionEvent actionEvent) {
        ascending = true;
        updateUserList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleSortDescending(ActionEvent actionEvent) {
        ascending = false;
        updateUserList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleApplyFilters(ActionEvent actionEvent) {
        String username = searchField.getText().trim();
        Role role = roleComboBoxFilter.getValue();
        LocalDate birthdayFrom = birthdayFromPicker.getValue();
        LocalDate birthdayTo = birthdayToPicker.getValue();

        // не дороблено, щоб фільтрувати по діапазону дат (приклад в коментарях є)
        currentFilters = new UserFilterDto(username, "", birthdayFrom, birthdayTo, role);

        pagination.setPageCount(getTotalPages());
        updateUserList(0);
    }


    @FXML
    public void handleDeleteUser(User user) {
        userRepository.delete(user.getId());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Information");
        alert.setHeaderText("User Deletion");
        alert.setContentText(user.getUsername() + " Has Been Deleted Successfully");
        alert.showAndWait();

        pagination.setPageCount(getTotalPages());
        updateUserList(pagination.getCurrentPageIndex());
    }

    @FXML
    public void handleUpdateUser(User user) throws IOException {
        createUserController.stealFields(user);
    }
}
