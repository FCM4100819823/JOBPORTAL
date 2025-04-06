package com.jobportal.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Job;
import com.jobportal.utils.SessionManager;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EmployerDashboardController implements Initializable {

    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private TableView<Job> jobsTable;
    
    @FXML
    private TableColumn<Job, String> jobTitleColumn;
    
    @FXML
    private TableColumn<Job, String> departmentColumn;
    
    @FXML
    private TableColumn<Job, String> locationColumn;
    
    @FXML
    private TableColumn<Job, LocalDate> postingDateColumn;
    
    @FXML
    private TableColumn<Job, String> statusColumn;
    
    @FXML
    private TableColumn<Job, Integer> applicationsColumn;
    
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
        // Set up job title column
        jobTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        // Set up department column
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        
        // Set up location column
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        // Set up posting date column
        postingDateColumn.setCellValueFactory(new PropertyValueFactory<>("postingDate"));
        postingDateColumn.setCellFactory(column -> {
            return new TableCell<Job, LocalDate>() {
                @Override
                protected void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || date == null) {
                        setText(null);
                    } else {
                        setText(date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                    }
                }
            };
        });
        
        // Set up status column
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> {
            return new TableCell<Job, String>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(status);
                        setStyle("-fx-text-fill: " + getStatusColor(status));
                    }
                }
            };
        });
        
        // Set up applications column to show number of applications
        applicationsColumn.setCellValueFactory(cellData -> {
            // Since Job doesn't have getApplicationCount, use a default value
            return new SimpleIntegerProperty(0).asObject();
        });
        
        // Enable sorting
        jobsTable.getSortOrder().add(postingDateColumn);
        postingDateColumn.setSortType(TableColumn.SortType.DESCENDING);
    }

    /**
     * Returns a color code based on the job status
     * @param status The job status
     * @return A color code in CSS format
     */
    private String getStatusColor(String status) {
        if (status == null) return "black";
        
        switch (status.toLowerCase()) {
            case "active":
                return "green";
            case "expired":
                return "red";
            case "draft":
                return "orange";
            default:
                return "black";
        }
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
