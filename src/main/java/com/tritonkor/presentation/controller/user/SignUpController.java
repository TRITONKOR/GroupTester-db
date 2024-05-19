package com.tritonkor.presentation.controller.user;

import static com.tritonkor.presentation.Runner.springContext;

import com.tritonkor.domain.dto.UserStoreDto;
import com.tritonkor.domain.service.impl.AuthenticationService;
import com.tritonkor.domain.service.impl.FileService;
import com.tritonkor.domain.service.impl.UserService;
import com.tritonkor.persistence.entity.User.Role;
import com.tritonkor.persistence.exception.EntityNotFoundException;
import com.tritonkor.presentation.Runner;
import com.tritonkor.presentation.util.SpringFXMLLoader;
import com.tritonkor.presentation.viewmodel.UserViewModel;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SignUpController {
    @Autowired
    private UserListController userListController;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private AuthenticationService authenticationService;
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
    private Stage currentStage;
    private Scene mainScene;

    @FXML
    public void initialize() throws IOException {
        // Ініціалізація ролей у ComboBox
        roleComboBox.getItems().addAll(Role.values());
        roleComboBox.setValue(Role.STUDENT);

        // Створення користувача з пустими даними як приклад
        userViewModel = new UserViewModel(
            UUID.randomUUID(),
            "JohnDoe",
            "john.doe@example.com",
            "password",
            new Image(fileService.getPathFromResource("default-avatar.png").toUri().toString()),
            fileService.getPathFromResource("default-avatar.png"),
            LocalDate.of(1990, 1, 1),
            Role.STUDENT
        );

        // Зв'язування властивостей ViewModel з View
        bindFieldsToViewModel();
    }

    private void bindFieldsToViewModel() {
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
    public void onSave() throws IOException {
        System.out.println("Saving User Data: " + userViewModel);

        // Відображення інформації через Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Information");
        alert.setHeaderText("User Data Saved Successfully");
        alert.setContentText(userViewModel.toString());
        alert.showAndWait();

        String username = userViewModel.getUsername();
        String email = userViewModel.getEmail();

        try {
            userService.findByUsername(username);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Логін вже використовується");
            return;
        } catch (EntityNotFoundException e) {
            // Логін ще не використовується
        }

        try {
            userService.findByEmail(email);
            showAlert(Alert.AlertType.ERROR, "Помилка", "Email вже використовується");
        } catch (EntityNotFoundException e) {
            // Email ще не використовується, можна створити користувача
            UserStoreDto userStoreDto = new UserStoreDto(
                    username,
                    email,
                    userViewModel.getPassword(),
                    userViewModel.getAvatarPath(),
                    userViewModel.getBirthday(),
                    userViewModel.getRole()
            );

            userService.create(userStoreDto);

            authenticationService.authenticate(username, userViewModel.getPassword());
            loadMain();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMain() throws IOException {
        currentStage = (Stage) usernameField.getScene().getWindow();
        var fxmlLoader = new SpringFXMLLoader(springContext);
        var mainFxmlResource = Runner.class.getResource(Path.of("view", "main.fxml").toString());
        Parent parent = (Parent) fxmlLoader.load(mainFxmlResource);
        mainScene = new Scene(parent, 1700, 1000);

        // встановлення нової Scene для поточного Stage
        currentStage.setScene(mainScene);

        // показ нової Scene
        currentStage.show();
    }
}
