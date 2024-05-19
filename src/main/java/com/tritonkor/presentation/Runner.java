package com.tritonkor.presentation;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.tritonkor.persistence.AppConfig;
import com.tritonkor.persistence.util.ConnectionManager;
import com.tritonkor.persistence.util.DatabaseInitializer;
import com.tritonkor.presentation.util.SpringFXMLLoader;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Runner extends Application {

    public static AnnotationConfigApplicationContext springContext;

    @Override
    public void start(Stage stage) throws Exception {
        var fxmlLoader = new SpringFXMLLoader(springContext);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        var mainFxmlResource = Runner.class.getResource(Path.of("view", "authorise.fxml").toString());
        Parent parent = (Parent) fxmlLoader.load(mainFxmlResource);
        Scene scene = new Scene(parent, 1700, 1000);
        stage.setTitle("GroupTester");
        stage.setScene(scene);

        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        stage.show();
    }

    public static void main(String[] args) {
        springContext = new AnnotationConfigApplicationContext(AppConfig.class);
        var connectionManager = springContext.getBean(ConnectionManager.class);
        var databaseInitializer = springContext.getBean(DatabaseInitializer.class);

        try {
            databaseInitializer.init();
            launch(args);
        } finally {
            connectionManager.closePool();
        }
    }
}
