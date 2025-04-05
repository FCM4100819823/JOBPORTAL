package com.jobportal.controllers;

import java.util.List;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Application;
import com.jobportal.models.User;
import com.jobportal.services.ApplicationService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class MyApplicationsController {
    @FXML private ListView<String> applicationsListView;
    @FXML private Text noApplicationsText;

    private final ApplicationService applicationService = new ApplicationService();

    @FXML
    private void initialize() {
        // Get user from session
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Redirect to login if session is invalid during initialization
            System.err.println("MyApplicationsController initialized without user session. Redirecting to login.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
            return;
        }
        loadApplications(currentUser);
    }
    
    private void loadApplications(User currentUser) { 
        applicationsListView.getItems().clear();
        // No need to check currentUser again, already checked in initialize/refresh
        try {
            List<Application> applications = applicationService.getApplicationsByJobSeeker(currentUser.getEmail());
            if (applications.isEmpty()) {
                noApplicationsText.setVisible(true);
                noApplicationsText.setManaged(true);
                applicationsListView.setVisible(false);
                applicationsListView.setManaged(false);
                // applicationsListView.getItems().add("No applications found."); // Or keep list empty
            } else {
                noApplicationsText.setVisible(false);
                noApplicationsText.setManaged(false);
                applicationsListView.setVisible(true);
                applicationsListView.setManaged(true);
                for (Application application : applications) {
                    // Add application details to list view
                    applicationsListView.getItems().add(
                            "Job: " + application.getJobId() + // Assuming jobId maps to a title or we need to fetch it
                            " | Status: " + application.getStatus() +
                            " | Applied On: " + application.getApplicationDate() // Format date if needed
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading applications: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load applications: " + e.getMessage());
        }
    }

    @FXML
    private void goToDashboard() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            String role = currentUser.getRole().toLowerCase();
            String targetFxml = null;
            String targetTitle = null;
            switch (role) {
                case "job seeker":
                    targetFxml = "jobseeker_dashboard.fxml";
                    targetTitle = "Job Seeker Dashboard";
                    break;
                case "employer":
                    targetFxml = "employer_dashboard.fxml";
                    targetTitle = "Employer Dashboard";
                    break;
                // Add other roles if applicable
                default:
                    System.err.println("Unknown role for dashboard navigation from MyApplications: " + currentUser.getRole());
                    targetFxml = "login.fxml";
                    targetTitle = "Job Portal - Login";
            }
             JobPortal.loadScene(targetFxml, targetTitle);
        } else {
            System.err.println("MyApplicationsController: No user session found. Redirecting to login.");
             JobPortal.loadScene("login.fxml", "Job Portal - Login");
        }
    }
    
    @FXML
    private void refreshApplications() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "Session expired. Please log in again.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
            return;
        }
        applicationsListView.getItems().clear();
        loadApplications(currentUser);
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
