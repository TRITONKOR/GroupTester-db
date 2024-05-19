package com.tritonkor.presentation.controller;

import static com.tritonkor.presentation.Runner.springContext;

import com.tritonkor.domain.service.impl.AuthenticationService;
import com.tritonkor.presentation.Runner;
import com.tritonkor.presentation.util.SpringFXMLLoader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller class for the main application window.
 */
@Component
public class MainController {
    @Autowired
    private AuthenticationService authenticationService;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private BorderPane root;
    @FXML
    private Label username;
    @FXML
    private Label role;
    @FXML
    private ImageView avatar;

    /**
     * Initializes the main window.
     */
    @FXML
    public void initialize() {
        // Встановлюємо значення за замовчуванням
        ToggleButton initialButton = (ToggleButton) toggleGroup.getToggles().getFirst();
        initialButton.setSelected(true);

        username.setText(STR."Username: \{authenticationService.getUser().getUsername()}");
        role.setText(STR."Role: \{authenticationService.getUser().getRole().getName()}");

        InputStream inputStream = new ByteArrayInputStream(authenticationService.getUser().getAvatar());
        Image image = new Image(inputStream);
        avatar.setImage(image);
    }

    /**
     * Handles menu selection.
     *
     * @param actionEvent the event triggering the menu selection
     */
    @FXML
    private void handleMenuSelection(ActionEvent actionEvent) {
        ToggleButton selectedButton = (ToggleButton) toggleGroup.getSelectedToggle();
        if (selectedButton != null) {
            switch (selectedButton.getText()) {
                case "Користувачі" -> switchPage(Path.of("view", "user", "UserList.fxml").toString());
                case "Тести" -> switchPage(Path.of("view", "test", "TestList.fxml").toString());
                case "Звіти" -> switchPage(Path.of("view", "report", "ReportList.fxml").toString());
                case "Теги" -> switchPage(Path.of("view", "tag", "TagList.fxml").toString());
                default -> System.err.println(STR."Unknown selection: \{selectedButton.getText()}");
            }
        }
    }

    /**
     * Switches the page to the specified FXML file.
     *
     * @param fxmlFile the path to the FXML file
     */
    public void switchPage(String fxmlFile) {
        try {
            var fxmlLoader = new SpringFXMLLoader(springContext);
            Pane newPage = (Pane) fxmlLoader.load(Runner.class.getResource(fxmlFile));
            root.setCenter(newPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
