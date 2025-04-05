package com.jobportal.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jobportal.main.JobPortal;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EmployerDashboardController implements Initializable {

    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private TableView<?> jobsTableView;
    
    @FXML
    private TableColumn<?, ?> titleColumn;
    
    @FXML
    private TableColumn<?, ?> locationColumn;
    
    @FXML
    private TableColumn<?, ?> dateColumn;
    
    @FXML
    private TableColumn<?, ?> applicantsColumn;
    
    @FXML
    private TableColumn<?, ?> statusColumn;
    
    @FXML
    private TableColumn<?, ?> actionsColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the filterComboBox with options
        filterComboBox.getItems().addAll("All Jobs", "Active", "Expired", "Draft");
        filterComboBox.setValue("All Jobs");
        
        // Initialize table columns and data - would be implemented with actual data in real application
        setupTableColumns();
    }
    
    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText().trim();
        String filter = filterComboBox.getValue();
        
        System.out.println("Searching for: " + searchTerm + " with filter: " + filter);
        
        // In a real application, you would query the database and update the table
        // For now, we'll just print the search term
        if (searchTerm.isEmpty()) {
            System.out.println("Please enter a search term");
        } else {
            // Perform search logic here
            System.out.println("Performing search for: " + searchTerm);
        }
    }
    
    @FXML
    public void handlePostJob() {
        JobPortal.loadScene("post_job.fxml", "Job Portal - Post a Job");
    }
    
    @FXML
    public void handleMyJobs() {
        System.out.println("Viewing my job listings");
        // This would normally filter the table to show only user's jobs
        // For now we'll just print a message
    }
    
    @FXML
    public void handleApplications() {
        JobPortal.loadScene("my_applications.fxml", "Applications");
    }
    
    @FXML
    public void handleLogout() {
        SessionManager.getInstance().clearSession();
        JobPortal.loadScene("login.fxml", "Job Portal - Login");
    }

    // Add these navigation methods for sidebar buttons
    @FXML
    private void navigateToDashboard() {
        JobPortal.loadScene("employer_dashboard.fxml", "Job Portal - Employer Dashboard");
    }

    @FXML
    private void navigateToPostJob() {
        JobPortal.loadScene("post_job.fxml", "Post Job");
    }

    @FXML
    private void navigateToMyJobs() {
        JobPortal.loadScene("my_jobs.fxml", "My Jobs");
    }

    @FXML
    private void navigateToApplications() {
        JobPortal.loadScene("my_applications.fxml", "Applications");
    }

    @FXML
    private void navigateToMessages() {
        JobPortal.loadScene("messages.fxml", "Messages");
    }

    @FXML
    private void navigateToCompanyProfile() {
        JobPortal.loadScene("company_profile.fxml", "Company Profile");
    }

    @FXML
    private void navigateToSettings() {
        JobPortal.loadScene("settings.fxml", "Settings");
    }
    
    @FXML
    private void navigateToManageJobs() {
        System.out.println("Navigating to Manage Jobs...");
        JobPortal.loadScene("manage_jobs.fxml", "Manage Jobs");
    }

    private void setupTableColumns() {
        // This would be implemented to display actual job data
        // For now, it's just a placeholder
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
