package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.services.UserService;
import com.jobportal.utils.PasswordUtil;
import com.jobportal.utils.SessionManager;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMessage;
    @FXML private VBox loginForm;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private BorderPane loginPane;
    @FXML private Button signInButton;

    private final UserService userService = new UserService();
    
    @FXML
    private void initialize() {
        if (loginForm == null) {
            System.err.println("Warning: loginForm is null in LoginController.initialize()");
            return;
        }
        
        // Clear any existing session
        SessionManager.getInstance().clearSession();
        
        applyFadeIn(loginForm);
        
        // Initialize forgot password link if present
        if (forgotPasswordLink != null) {
            forgotPasswordLink.setOnAction(event -> handleForgotPassword());
        }
    }
    
    @FXML
    private void goToRegister() {
        JobPortal.loadScene("register.fxml", "Job Portal - Register");
    }
    
    @FXML
    private void handleLogin() {
        if (emailField == null || passwordField == null) {
            showError("Internal error: Form fields not initialized");
            return;
        }
        
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }
        
        User user = userService.getUserByEmail(email);
        if (user == null) {
            showError("Invalid email or password");
            return;
        }
        
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            showError("Invalid email or password");
            return;
        }
        
        // Store user in session
        SessionManager.getInstance().setCurrentUser(user);
        
        // Redirect based on user role
        String role = user.getRole().toLowerCase();
        switch (role) {
            case "jobseeker":
                JobPortal.loadScene("jobseeker_dashboard.fxml", "Job Seeker Dashboard");
                break;
            case "employer":
                JobPortal.loadScene("employer_dashboard.fxml", "Employer Dashboard");
                break;
            case "recruiter":
                JobPortal.loadScene("recruiter_dashboard.fxml", "Recruiter Dashboard");
                break;
            case "admin":
                JobPortal.loadScene("admin_dashboard.fxml", "Admin Dashboard");
                break;
            default:
                showError("Invalid user role");
                break;
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        JobPortal.loadScene("forgot_password.fxml", "Reset Password");
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        if (errorMessage != null) {
            errorMessage.setText(message);
            errorMessage.setVisible(true);
            errorMessage.setManaged(true);
        }
    }

    private void applyFadeIn(VBox node) {
        if (node == null) return;
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}

