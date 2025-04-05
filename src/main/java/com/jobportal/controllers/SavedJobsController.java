package com.jobportal.controllers;
import java.util.List;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.services.SavedJobService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class SavedJobsController {
    @FXML private ListView<String> savedJobsListView;

    private final SavedJobService savedJobService = new SavedJobService();

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            System.err.println("SavedJobsController initialized without user session. Redirecting to login.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
            return;
        }
        loadSavedJobs(currentUser);
    }

    private void loadSavedJobs(User currentUser) {
        savedJobsListView.getItems().clear();
        try {
            List<String> savedJobs = savedJobService.getSavedJobs(currentUser.getEmail());
            if (savedJobs.isEmpty()) {
                savedJobsListView.getItems().add("No saved jobs found.");
            } else {
                savedJobsListView.getItems().addAll(savedJobs);
            }
        } catch (Exception e) {
             System.err.println("Error loading saved jobs: " + e.getMessage());
             savedJobsListView.getItems().add("Error loading saved jobs.");
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
                case "job seeker": // Assuming only job seekers save jobs
                    targetFxml = "jobseeker_dashboard.fxml";
                    targetTitle = "Job Seeker Dashboard";
                    break;
                // Add other roles if applicable
                default:
                    System.err.println("Unknown role for dashboard navigation from SavedJobs: " + currentUser.getRole());
                    targetFxml = "login.fxml";
                    targetTitle = "Job Portal - Login";
            }
            JobPortal.loadScene(targetFxml, targetTitle);
        } else {
            System.err.println("SavedJobsController: No user session found. Redirecting to login.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
        }
    }
}



