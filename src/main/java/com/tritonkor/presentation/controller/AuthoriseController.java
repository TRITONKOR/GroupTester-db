package com.tritonkor.presentation.controller;

import static com.tritonkor.presentation.Runner.springContext;

import com.tritonkor.presentation.Runner;
import com.tritonkor.presentation.util.SpringFXMLLoader;
import java.io.IOException;
import java.nio.file.Path;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

@Component
public class AuthoriseController {

    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private BorderPane root;
    @FXML
    private void handleMenuSelection(ActionEvent actionEvent) {
        ToggleButton selectedButton = (ToggleButton) toggleGroup.getSelectedToggle();
        if (selectedButton != null) {
            switch (selectedButton.getText()) {
                case "Авторизація" -> switchPage(Path.of("view", "user", "SignIn.fxml").toString());
                case "Реєстрація" -> switchPage(Path.of("view", "user", "SignUp.fxml").toString());
            }
        }
    }

    private void switchPage(String fxmlFile) {
        try {
            var fxmlLoader = new SpringFXMLLoader(springContext);
            Pane newPage = (Pane) fxmlLoader.load(Runner.class.getResource(fxmlFile));
            root.setCenter(newPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
