package com.jobportal.main;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration; // For splash screen if needed

public class JobPortal extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Set the static primary stage HERE
        
        // --- Option: Re-integrate Splash Screen Logic --- 
        // showSplashScreenAndThenLogin(); 
        // --- OR --- 
        // --- Direct Login Load --- 
        loadLoginScene();
    }

    // Helper to load login scene - ensures primaryStage is set first
    private void loadLoginScene() {
        loadScene("login.fxml", "Job Portal - Login");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    // Optional: Method to show a splash screen first
    private void showSplashScreenAndThenLogin() {
        // Create splash screen content (similar to SplashScreen.java)
        Label label = new Label("Welcome to Job Portal!");
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        StackPane splashRoot = new StackPane(label);
        splashRoot.setStyle("-fx-background-color: #3498db;");
        Scene splashScene = new Scene(splashRoot, 400, 300);
        
        // Configure the primary stage for the splash
        primaryStage.setScene(splashScene);
        primaryStage.setTitle("Loading Job Portal...");
        // Optional: Undecorated splash window
        // primaryStage.initStyle(StageStyle.UNDECORATED); 
        primaryStage.setMaximized(false); // Don't maximize splash
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.centerOnScreen();
        primaryStage.show();

        // Delay before switching to login scene on the SAME stage
        PauseTransition delay = new PauseTransition(Duration.seconds(2)); // Shorter delay?
        delay.setOnFinished(event -> {
            // Configure stage back for main app
            // primaryStage.initStyle(StageStyle.DECORATED); // Restore decorations if changed
             loadLoginScene(); // Load login into the same stage
        });
        delay.play();
    }

    /**
     * Loads the specified FXML scene into the primary Stage.
     *
     * @param fxmlFileName The name of the .fxml file (e.g., "login.fxml").
     * @param title The title for the window.
     */
    public static void loadScene(String fxmlFileName, String title) {
        try {
            if (primaryStage == null) {
                // This error should NOT happen now if start() runs correctly
                System.err.println("FATAL Error: Primary stage is not set when loading scene: " + fxmlFileName);
                return; 
            }
            if (fxmlFileName == null || fxmlFileName.isEmpty()) {
                throw new IllegalArgumentException("FXML file name cannot be null or empty.");
            }

            String fxmlPath = "/com/jobportal/jobportal/" + fxmlFileName;
            FXMLLoader loader = new FXMLLoader(JobPortal.class.getResource(fxmlPath));
            if (loader.getLocation() == null) {
                 throw new IOException("Cannot find FXML file at path: " + fxmlPath);
            }
            Parent root = loader.load();

            primaryStage.setTitle(title);
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root); 
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root); 
            }
            
            // Ensure stage is maximized (unless it's the login screen maybe?)
            // Let's always maximize for now, simplicity.
            if (!primaryStage.isMaximized()) {
                 primaryStage.setMaximized(true);
                 primaryStage.centerOnScreen(); // Recenter after maximize
            }
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFileName);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading scene: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) { 
            System.err.println("An unexpected error occurred loading FXML: " + fxmlFileName);
            e.printStackTrace();
        }
    }

    public static void setRoot(String fxmlFileName) throws IOException {
        if (fxmlFileName == null || fxmlFileName.isEmpty()) {
            throw new IllegalArgumentException("FXML file name cannot be null or empty.");
        }

        String fxmlPath = "/com/jobportal/jobportal/" + fxmlFileName + ".fxml";
        FXMLLoader loader = new FXMLLoader(JobPortal.class.getResource(fxmlPath));
        if (loader.getLocation() == null) {
            throw new IOException("Cannot find FXML file at path: " + fxmlPath);
        }
        Parent root = loader.load();

        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
