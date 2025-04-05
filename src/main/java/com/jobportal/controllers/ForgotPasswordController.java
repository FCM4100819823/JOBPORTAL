package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ForgotPasswordController {
    @FXML private TextField emailField;
    @FXML private Label statusMessage;

    private final UserService userService = new UserService();

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        
        if (email == null || email.isEmpty()) {
            showMessage("Please enter your email address", false);
            return;
        }

        // Check if user exists
        if (userService.getUserByEmail(email) == null) {
            showMessage("No account found with this email", false);
            return;
        }

        // In a real app, this would send a reset link via email
        // For this demo, we'll generate a temporary password
        if (userService.resetPassword(email)) {
            showMessage("A temporary password has been sent to your email", true);
        } else {
            showMessage("Failed to reset password. Please try again.", false);
        }
    }

    @FXML
    private void goToLogin() {
        JobPortal.loadScene("login.fxml", "Job Portal - Login");
    }

    private void showMessage(String message, boolean success) {
        statusMessage.setText(message);
        statusMessage.setVisible(true);
        statusMessage.setManaged(true);
        
        if (success) {
            statusMessage.setStyle("-fx-text-fill: green;");
        } else {
            statusMessage.setStyle("-fx-text-fill: red;");
        }
    }
}
