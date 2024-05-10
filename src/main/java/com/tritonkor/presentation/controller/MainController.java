package com.tritonkor.presentation.controller;

import com.tritonkor.domain.dto.UserStoreDto;
import com.tritonkor.domain.service.impl.FileService;
import com.tritonkor.domain.service.impl.UserService;
import com.tritonkor.persistence.entity.User.Role;
import com.tritonkor.presentation.viewmodel.UserViewModel;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MainController {

    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @FXML
    private Label idLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView photoImageView;
    @FXML
    private DatePicker birthdayPicker;
    @FXML
    private ComboBox<Role> roleComboBox;

    private UserViewModel userViewModel;

    @FXML
    public void initialize() {
        // Ініціалізація ролей у ComboBox
        roleComboBox.getItems().addAll(Role.values());
        roleComboBox.setValue(Role.STUDENT);

        // Створення користувача з пустими даними як приклад
        userViewModel = new UserViewModel(
                UUID.randomUUID(),
                "JohnDoe",
                "john.doe@example.com",
                "password123",
                new Image(fileService.getPathFromResource("default-avatar.png").toUri().toString()),
                fileService.getPathFromResource("default-avatar.png"),
                LocalDate.of(1990, 1, 1),
                Role.STUDENT
        );

        // Зв'язування властивостей ViewModel з View
        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
        idLabel.setText(userViewModel.getId().toString());
        usernameField.textProperty().bindBidirectional(userViewModel.usernameProperty());
        emailField.textProperty().bindBidirectional(userViewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(userViewModel.passwordProperty());
        photoImageView.imageProperty().bindBidirectional(userViewModel.avatarProperty());
        birthdayPicker.valueProperty().bindBidirectional(userViewModel.birthdayProperty());
        roleComboBox.valueProperty().bindBidirectional(userViewModel.roleProperty());
    }

    @FXML
    private void onUploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        var extensionFilter = new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extensionFilter);
        Path path = fileChooser.showOpenDialog(null).toPath();

        if (!path.toString().isBlank()) {
            Image image = new Image(path.toUri().toString());
            userViewModel.setAvatar(image);
            userViewModel.setAvatarPath(path);
        }
    }

    @FXML
    private void onSave() {
        System.out.println("Saving User Data: " + userViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Information");
        alert.setHeaderText("User Data Saved Successfully");
        alert.setContentText(userViewModel.toString());
        alert.showAndWait();

        UserStoreDto userStoreDto = new UserStoreDto(
                userViewModel.getUsername(),
                userViewModel.getEmail(),
                userViewModel.getPassword(),
                userViewModel.getAvatarPath(),
                userViewModel.getBirthday(),
                userViewModel.getRole()
        );
        userService.create(userStoreDto);
    }

    @FXML
    private void onCancel() {
        System.out.println("Operation Cancelled");
    }
}
