package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Application;
import com.jobportal.models.User;
import com.jobportal.services.ApplicationService;
import com.jobportal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.control.Label;

import java.util.List;

public class MyApplicationsController {
    @FXML private ListView<String> applicationsListView;
    @FXML private Text noApplicationsText;

    private final ApplicationService applicationService = new ApplicationService();
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML
    private void initialize() {
        // Get user from session if not directly set
        if (currentUser == null) {
            currentUser = SessionManager.getInstance().getCurrentUser();
        }
        
        loadApplications();
    }
    
    private void loadApplications() {
        applicationsListView.getItems().clear();
        if (currentUser != null) {
            try {
                List<Application> applications = applicationService.getApplicationsByJobSeeker(currentUser.getEmail());
                if (applications.isEmpty()) {
                    applicationsListView.getItems().add("No applications found.");
                } else {
                    for (Application application : applications) {
                        applicationsListView.getItems().add(
                                "Job ID: " + application.getJobId() +
                                ", Status: " + application.getStatus() +
                                ", Applied On: " + application.getApplicationDate()
                        );
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading applications: " + e.getMessage());
                e.printStackTrace();
                showAlert("Error", "Failed to load applications: " + e.getMessage());
            }
        } else {
            System.err.println("Current user is null. Cannot load applications.");
            showAlert("Error", "You must be logged in to view applications");
            JobPortal.loadScene("login.fxml", "Login");
        }
    }

    @FXML
    private void goToDashboard() {
        JobPortal.loadScene("jobseeker_dashboard.fxml", "Job Seeker Dashboard");
    }
    
    @FXML
    private void refreshApplications() {
        applicationsListView.getItems().clear();
        loadApplications();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
