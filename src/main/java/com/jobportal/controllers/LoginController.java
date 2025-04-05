package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.services.UserService;
import com.jobportal.utils.SessionManager;
import com.jobportal.utils.PasswordUtil;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        JobPortal.loadScene("register.fxml", "Register");
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
                    // Set the user in session manager
                    SessionManager.getInstance().setCurrentUser(user);
                    
                    String role = user.getRole();
                    if (role != null) {
                        switch (role.toLowerCase()) {
                            case "job seeker":
                                JobPortal.loadScene("jobseeker_dashboard.fxml", "Job Seeker Dashboard");
                                break;
                            case "employer":
                                JobPortal.loadScene("employer_dashboard.fxml", "Employer Dashboard");
                                break;
                            case "recruiter":
                                JobPortal.loadScene("recruiter_dashboard.fxml", "Recruiter Dashboard");
                                break;
                            case "admin": // Added admin role
                                JobPortal.loadScene("admin_dashboard.fxml", "Admin Dashboard");
                                break;
                            default:
                                JobPortal.loadScene("dashboard.fxml", "Job Portal Dashboard");
                                break;
                        }
                    } else {
                        JobPortal.loadScene("dashboard.fxml", "Job Portal Dashboard");
                    }
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

