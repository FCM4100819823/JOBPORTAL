package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.services.ApplicationService;
import com.jobportal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class UpdateApplicationStatusController {
    @FXML private TextField applicationIdField;
    @FXML private ComboBox<String> statusComboBox;

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
        
        // Populate status options
        statusComboBox.getItems().addAll("Pending", "Accepted", "Rejected");
        statusComboBox.setValue("Pending"); // Default value
    }

    @FXML
    private void handleUpdateStatus() {
        String applicationId = applicationIdField.getText();
        String status = statusComboBox.getValue();

        if (applicationId.isEmpty() || status == null) {
            showAlert("Error", "Application ID and status must be provided.");
            return;
        }

        if (currentUser == null || !currentUser.getRole().equalsIgnoreCase("employer")) {
            showAlert("Error", "You don't have permission to update application status.");
            return;
        }

        if (applicationService.updateApplicationStatus(applicationId, status)) {
            showAlert("Success", "Application status updated successfully!");
            applicationIdField.clear();
            statusComboBox.setValue("Pending");
        } else {
            showAlert("Error", "Failed to update application status. Please try again.");
        }
    }

    @FXML
    private void goToDashboard() {
        JobPortal.loadScene("employer_dashboard.fxml", "Employer Dashboard");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
