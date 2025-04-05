package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class SettingsController {

    @FXML
    private void initialize() {
        // Check session on initialization
        if (!checkSession()) return;
        // Load current settings if needed
    }

    @FXML
    private void goToDashboard() {
        // Removed checkSession call, rely on initialize
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
                case "recruiter":
                    targetFxml = "recruiter_dashboard.fxml";
                    targetTitle = "Recruiter Dashboard";
                    break;
                case "admin":
                    targetFxml = "admin_dashboard.fxml";
                    targetTitle = "Admin Dashboard";
                    break;
                default:
                    System.err.println("Unknown role for dashboard navigation from Settings: " + currentUser.getRole());
                    targetFxml = "login.fxml";
                    targetTitle = "Job Portal - Login";
            }
            JobPortal.loadScene(targetFxml, targetTitle); // Reverted
        } else {
            System.err.println("SettingsController: No user session found. Redirecting to login.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login"); // Reverted
        }
    }
    
    private boolean checkSession() {
        if (SessionManager.getInstance().getCurrentUser() == null) {
            System.err.println("Session invalid or expired. Redirecting to login.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Add methods here to handle saving settings, changing password, deleting account etc.
} 