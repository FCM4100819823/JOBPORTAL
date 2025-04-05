package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Job;
import com.jobportal.models.User;
import com.jobportal.services.JobService;
import com.jobportal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PostJobController {
    @FXML private TextField titleField;
    @FXML private TextField companyField;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionField;

    private final JobService jobService = new JobService();
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    private void initialize() {
        // Get user from session if not directly set
        if (currentUser == null) {
            currentUser = SessionManager.getInstance().getCurrentUser();
        }
        
        // Pre-fill company name if available
        if (currentUser != null && companyField != null) {
            companyField.setText(currentUser.getFullName());
        }
    }

    @FXML
    private void handlePostJob() {
        String title = titleField.getText();
        String company = companyField.getText();
        String location = locationField.getText();
        String description = descriptionField.getText();

        if (title.isEmpty() || company.isEmpty() || location.isEmpty() || description.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }
        
        if (currentUser == null) {
            showAlert("Error", "You must be logged in to post a job.");
            return;
        }

        // Create a more complete job model
        Job newJob = new Job(
            title, 
            company, 
            location, 
            description,
            currentUser.getEmail() // Add employer email
        );
        
        if (jobService.addJob(newJob)) {
            showAlert("Success", "Job posted successfully!");
            clearFields();
        } else {
            showAlert("Error", "Failed to post job. Please try again.");
        }
    }

    @FXML
    private void goToDashboard() {
        JobPortal.loadScene("employer_dashboard.fxml", "Employer Dashboard");
    }

    private void clearFields() {
        titleField.clear();
        // Don't clear company field as it's pre-filled
        locationField.clear();
        descriptionField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
