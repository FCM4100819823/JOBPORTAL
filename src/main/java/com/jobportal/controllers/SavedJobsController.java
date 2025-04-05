package com.jobportal.controllers;
import java.util.List;

import com.jobportal.services.SavedJobService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class SavedJobsController {
    @FXML private ListView<String> savedJobsListView;

    private final SavedJobService savedJobService = new SavedJobService();

    @FXML
    private void initialize() {
        loadSavedJobs();
    }

    private void loadSavedJobs() {
        savedJobsListView.getItems().clear();
        List<String> savedJobs = savedJobService.getSavedJobs(SessionManager.getInstance().getCurrentUser().getEmail());
        if (savedJobs.isEmpty()) {
            savedJobsListView.getItems().add("No saved jobs found.");
        } else {
            savedJobsListView.getItems().addAll(savedJobs);
        }
    }

    @FXML
    private void goToDashboard() {
        // Logic to navigate back to the dashboard
        System.out.println("Navigating back to the dashboard...");
        Stage stage = (Stage) savedJobsListView.getScene().getWindow();
        stage.close();
    }
}



