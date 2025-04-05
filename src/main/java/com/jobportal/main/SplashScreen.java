package com.jobportal.main;
import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Label label = new Label("Welcome to Job Portal!");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        StackPane root = new StackPane(label);
        root.setStyle("-fx-background-color: #3498db;");
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setResizable(false); // Prevent resizing issues
        primaryStage.setTitle("Job Portal - Splash Screen"); // Add a title

        primaryStage.setScene(scene);
        primaryStage.show();
        // Delay before switching to main app
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            try {
                new JobPortal().start(new Stage()); // Open main app
                primaryStage.close(); // Close splash screen
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception
            }
        });
        delay.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
