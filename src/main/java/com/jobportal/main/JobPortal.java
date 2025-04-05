package com.jobportal.main;

import com.jobportal.controllers.*;
import com.jobportal.models.User;
import com.jobportal.utils.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class JobPortal extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        loadScene("/com/jobportal/jobportal/login.fxml", "Job Portal - Login");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(800);
        primaryStage.show();
    }

    public static void loadScene(String fxmlPath, String title) {
        try {
            String path = fxmlPath;
            if (!fxmlPath.startsWith("/com/jobportal/jobportal/")) {
                path = "/com/jobportal/jobportal/" + fxmlPath;
            }
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(JobPortal.class.getResource(path)));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            
            // Ensure the window resizes properly
            primaryStage.setResizable(true);
            primaryStage.sizeToScene();

            // Center the window on the screen
            primaryStage.centerOnScreen();
            
            // Set current user for controller if applicable
            Object controller = loader.getController();
            User currentUser = SessionManager.getInstance().getCurrentUser();
            
            if (currentUser != null) {
                if (controller instanceof JobSeekerDashboardController) {
                    ((JobSeekerDashboardController) controller).setCurrentUser(currentUser);
                } else if (controller instanceof ProfileController) {
                    ProfileController.setCurrentUser(currentUser);
                } else if (controller instanceof MessagesController) {
                    MessagesController.setCurrentUser(currentUser);
                } else if (controller instanceof MyApplicationsController) {
                    MyApplicationsController.setCurrentUser(currentUser);
                } else if (controller instanceof DashboardController) {
                    DashboardController.setCurrentUser(currentUser);
                } else if (controller instanceof PostJobController) {
                    ((PostJobController) controller).setCurrentUser(currentUser);
                } else if (controller instanceof UpdateApplicationStatusController) {
                    UpdateApplicationStatusController.setCurrentUser(currentUser);
                }
            }
            
            primaryStage.show();
            primaryStage.getScene().getStylesheets().add("dashboard.css");
        } catch (IOException e) {
            System.err.println("Failed to load scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
