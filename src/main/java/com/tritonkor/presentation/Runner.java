package com.tritonkor.presentation;

import com.tritonkor.persistence.AppConfig;
import com.tritonkor.persistence.util.ConnectionManager;
import com.tritonkor.persistence.util.DatabaseInitializer;
import com.tritonkor.presentation.util.SpringFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Runner extends Application {

    private static AnnotationConfigApplicationContext springContext;

    @Override
    public void start(Stage stage) throws Exception {
        var fxmlLoader = new SpringFXMLLoader(springContext);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        var mainFxmlResource = Runner.class.getResource("view/main.fxml");
        Scene scene = new Scene((Parent) fxmlLoader.load(mainFxmlResource), 900, 600);
        stage.setTitle("Title");
        stage.setScene(scene);
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
