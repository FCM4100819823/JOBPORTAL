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
import com.jobportal.models.User;
import com.jobportal.utils.NotificationManager;

public class JobPortal extends Application {
    private static Stage primaryStage;
    private static User currentUser;

    @Override
    public void start(Stage stage) throws IOException {
        if (stage == null) {
            throw new IllegalArgumentException("Stage cannot be null");
        }
        primaryStage = stage;
        loadLoginScene();
    }

    // Helper to load login scene - ensures primaryStage is set first
    private void loadLoginScene() {
        try {
            loadScene("login.fxml", "Job Portal - Login");
            if (primaryStage != null) {
                primaryStage.setMaximized(true);
                primaryStage.show();
            }
        } catch (Exception e) {
            System.err.println("Failed to load login scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Optional: Method to show a splash screen first
    private void showSplashScreenAndThenLogin() {
        try {
            Label label = new Label("Welcome to Job Portal!");
            label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
            StackPane splashRoot = new StackPane(label);
            splashRoot.setStyle("-fx-background-color: #3498db;");
            Scene splashScene = new Scene(splashRoot, 400, 300);
            
            if (primaryStage != null) {
                primaryStage.setScene(splashScene);
                primaryStage.setTitle("Loading Job Portal...");
                primaryStage.setMaximized(false);
                primaryStage.setWidth(400);
                primaryStage.setHeight(300);
                primaryStage.centerOnScreen();
                primaryStage.show();

                PauseTransition delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(event -> loadLoginScene());
                delay.play();
            }
        } catch (Exception e) {
            System.err.println("Failed to show splash screen: " + e.getMessage());
            e.printStackTrace();
            loadLoginScene(); // Fallback to direct login
        }
    }

    /**
     * Loads the specified FXML scene into the primary Stage.
     *
     * @param fxmlFileName The name of the .fxml file (e.g., "login.fxml").
     * @param title The title for the window.
     */
    public static void loadScene(String fxmlFileName, String title) {
        if (fxmlFileName == null || fxmlFileName.isEmpty()) {
            NotificationManager.showError("Error", "FXML file name cannot be null or empty.");
            return;
        }
        
        if (title == null || title.isEmpty()) {
            NotificationManager.showError("Error", "Scene title cannot be null or empty.");
            return;
        }

        try {
            if (primaryStage == null) {
                NotificationManager.showError("Error", "Primary stage is not initialized.");
                return;
            }

            String fxmlPath = "/com/jobportal/jobportal/" + fxmlFileName;
            FXMLLoader loader = new FXMLLoader(JobPortal.class.getResource(fxmlPath));
            
            if (loader.getLocation() == null) {
                NotificationManager.showError("Error", "Cannot find FXML file at path: " + fxmlPath);
                return;
            }
            
            Parent root = loader.load();
            if (root == null) {
                NotificationManager.showError("Error", "Failed to load FXML root element.");
                return;
            }

            primaryStage.setTitle(title);
            Scene scene = primaryStage.getScene();
            
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
            
            if (!primaryStage.isMaximized()) {
                primaryStage.setMaximized(true);
                primaryStage.centerOnScreen();
            }
            
            primaryStage.show();

        } catch (IOException e) {
            NotificationManager.showError("Error", "Failed to load FXML file: " + fxmlFileName);
            e.printStackTrace();
        } catch (Exception e) {
            NotificationManager.showError("Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setRoot(String fxmlFileName) throws IOException {
        if (fxmlFileName == null || fxmlFileName.isEmpty()) {
            throw new IllegalArgumentException("FXML file name cannot be null or empty.");
        }

        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage is not initialized.");
        }

        String fxmlPath = "/com/jobportal/jobportal/" + fxmlFileName + ".fxml";
        FXMLLoader loader = new FXMLLoader(JobPortal.class.getResource(fxmlPath));
        
        if (loader.getLocation() == null) {
            throw new IOException("Cannot find FXML file at path: " + fxmlPath);
        }
        
        Parent root = loader.load();
        if (root == null) {
            throw new IOException("Failed to load FXML root element.");
        }

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

    public static void setPrimaryStage(Stage stage) {
        if (stage == null) {
            throw new IllegalArgumentException("Stage cannot be null");
        }
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}
