package com.tritonkor.presentation.controller.user;

import static com.tritonkor.presentation.Runner.springContext;

import com.tritonkor.domain.exception.AuthenticationException;
import com.tritonkor.domain.exception.UserAlreadyAuthenticatedException;
import com.tritonkor.domain.service.impl.AuthenticationService;
import com.tritonkor.presentation.Runner;
import com.tritonkor.presentation.util.SpringFXMLLoader;
import java.io.IOException;
import java.nio.file.Path;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class SignInController {

    private final AuthenticationService authenticationService;
    @FXML
    public PasswordField passwordField;
    @FXML
    private TextField usernameField;

    private Stage currentStage;
    private Scene mainScene;

    public SignInController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @FXML
    public void initialize() {

    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            boolean authenticated = authenticationService.authenticate(username, password);
            System.out.println(authenticated);

            if (authenticated) {
                showAlert(Alert.AlertType.INFORMATION, "Успіх", "Вхід виконано успішно!");
                loadMain();
            } else {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Невірний логін або пароль.");
            }
        } catch (UserAlreadyAuthenticatedException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", e.getMessage());
        } catch (AuthenticationException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Невірний логін або пароль.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMain() throws IOException {
        currentStage = (Stage) passwordField.getScene().getWindow();
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
