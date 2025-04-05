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
    @FXML private BorderPane loginPane; // Changed from VBox to BorderPane
    @FXML private Button signInButton;

    private final UserService userService = new UserService();
    
    @FXML
    private void initialize() {
        // Clear any existing session
        SessionManager.getInstance().clearSession();
        
        System.out.println("LoginController initialized with emailField=" + emailField);
        applyFadeIn(loginForm);
        
        // If we have the forgot password link in FXML
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
        try {
            if (emailField == null) {
                System.err.println("emailField is null in LoginController.handleLogin()");
                showError("Internal error: Email field not initialized");
                return;
            }
            
            String email = emailField.getText();
            String password = passwordField.getText();
            
            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                showError("Please enter both email and password");
                return;
            }
            
            User user = userService.getUserByEmail(email);
            if (user != null) {
                boolean isPasswordValid = PasswordUtil.verifyPassword(password, user.getPassword());
                if (isPasswordValid) {
                    SessionManager.getInstance().setCurrentUser(user);
                    String dashboardFxml = "";
                    String dashboardTitle = "";
                    switch (user.getRole().toLowerCase()) {
                        case "admin":
                            dashboardFxml = "admin_dashboard.fxml";
                            dashboardTitle = "Admin Dashboard";
                            break;
                        case "job seeker":
                            dashboardFxml = "jobseeker_dashboard.fxml";
                            dashboardTitle = "Job Seeker Dashboard";
                            break;
                        case "employer":
                            dashboardFxml = "employer_dashboard.fxml";
                            dashboardTitle = "Employer Dashboard";
                            break;
                        case "recruiter":
                            dashboardFxml = "recruiter_dashboard.fxml";
                            dashboardTitle = "Recruiter Dashboard";
                            break;
                        default:
                            showAlert("Login Error", "Unknown user role.");
                            return;
                    }
                    JobPortal.loadScene(dashboardFxml, dashboardTitle);
                } else {
                    showError("Invalid password");
                }
            } else {
                showError("User not found: " + email);
            }
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Clear the session
        SessionManager.getInstance().clearSession();
        // Navigate to the login page using standard loadScene
        JobPortal.loadScene("login.fxml", "Job Portal - Login");
    }
    
    @FXML
    private void handleForgotPassword() {
        JobPortal.loadScene("forgot_password.fxml", "Reset Password");
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
    
    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
    }

    private void applyFadeIn(VBox node) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}

