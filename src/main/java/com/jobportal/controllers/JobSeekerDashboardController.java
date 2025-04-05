package com.jobportal.controllers;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Application;
import com.jobportal.models.Job;
import com.jobportal.models.User;
import com.jobportal.services.ApplicationService;
import com.jobportal.services.JobService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class JobSeekerDashboardController {
    @FXML private Text welcomeText;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private ListView<String> jobListView;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label jobsAppliedCount;
    @FXML private Label inReviewCount;
    @FXML private Label interviewsCount;
    @FXML private Label offersCount;
    @FXML private ChoiceBox<String> viewModeChoiceBox;
    @FXML private Button settingsButton, savedJobsButton, messagesButton;

    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private User currentUser;
    private List<Job> currentJobs;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateWelcomeText();
    }

    @FXML
    private void initialize() {
        // Get user from session if not directly set
        if (currentUser == null) {
            currentUser = SessionManager.getInstance().getCurrentUser();
        }
        
        updateWelcomeText();

        // Populate filter options
        filterComboBox.getItems().addAll("Title", "Location", "Company");
        filterComboBox.setValue("Title"); // Default filter

        // Populate sort options
        sortComboBox.getItems().addAll("Relevance", "Date Posted", "Salary", "Company");
        sortComboBox.setValue("Relevance"); // Default value

        // Load all jobs initially
        loadJobs(jobService.getAllJobs());
        
        // Add double-click handler for job details
        jobListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double click
                handleViewJobDetails();
            }
        });
    }
    
    private void updateWelcomeText() {
        if (currentUser != null && welcomeText != null) {
            welcomeText.setText("Welcome, " + currentUser.getFullName());
        } else if (welcomeText != null) {
            welcomeText.setText("Welcome to Job Seeker Dashboard");
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        String filter = filterComboBox.getValue();

        if (query == null || query.isEmpty()) {
            loadJobs(jobService.getAllJobs());
            return;
        }

        List<Job> filteredJobs;
        switch (filter.toLowerCase()) {
            case "location":
                filteredJobs = jobService.searchJobsByLocation(query);
                break;
            case "company":
                filteredJobs = jobService.searchJobsByCompany(query);
                break;
            case "title":
            default:
                filteredJobs = jobService.searchJobsByTitle(query);
                break;
        }

        loadJobs(filteredJobs);
    }

    private void loadJobs(List<Job> jobs) {
        this.currentJobs = jobs;
        jobListView.getItems().clear();
        if (jobs.isEmpty()) {
            jobListView.getItems().add("No jobs found.");
        } else {
            for (Job job : jobs) {
                jobListView.getItems().add(job.getTitle() + " - " + job.getCompany() + " (" + job.getLocation() + ")");
            }
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        JobPortal.loadScene("login.fxml", "Job Portal - Login");
    }

    @FXML
    private void viewProfile() {
        JobPortal.loadScene("profile.fxml", "Job Portal - Profile");
    }

    @FXML
    private void viewApplications() {
        JobPortal.loadScene("my_applications.fxml", "My Applications");
    }
    
    @FXML
    private void handleViewJobDetails() {
        int selectedIndex = jobListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < currentJobs.size()) {
            Job selectedJob = currentJobs.get(selectedIndex);
            showJobDetails(selectedJob);
        }
    }

    @FXML
    private void handleApply() {
        int selectedIndex = jobListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= currentJobs.size()) {
            showAlert("Error", "Please select a job to apply for.");
            return;
        }

        if (currentUser == null) {
            showAlert("Error", "User is not logged in. Please log in again.");
            return;
        }

        Job selectedJob = currentJobs.get(selectedIndex);

        // Upload resume
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Resume");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File resumeFile = fileChooser.showOpenDialog(jobListView.getScene().getWindow());
        if (resumeFile == null) {
            showAlert("Error", "You must upload a resume to apply.");
            return;
        }

        // Upload cover letter
        fileChooser.setTitle("Select Cover Letter");
        File coverLetterFile = fileChooser.showOpenDialog(jobListView.getScene().getWindow());
        if (coverLetterFile == null) {
            showAlert("Error", "You must upload a cover letter to apply.");
            return;
        }

        // Show confirmation dialog
        ButtonType result = showConfirmationDialog("Apply for Job",
                "Are you sure you want to apply for the position of " + selectedJob.getTitle() +
                        " at " + selectedJob.getCompany() + "?");

        if (result == ButtonType.OK) {
            Application application = new Application(
                    null, // ID will be generated by the database
                    selectedJob.getTitle(), // Using proper job ID
                    currentUser.getEmail(),
                    new Date(),
                    "Pending",
                    resumeFile.getAbsolutePath(),
                    coverLetterFile.getAbsolutePath()
            );
            
            if (applicationService.submitApplication(application)) {
                showAlert("Success", "Application submitted successfully!");
            } else {
                showAlert("Error", "Failed to submit application. Please try again.");
            }
        }
    }
    
    private void showJobDetails(Job job) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Job Details");
        alert.setHeaderText(job.getTitle() + " at " + job.getCompany());
        
        // Create a more detailed message
        String details = "Location: " + job.getLocation() + "\n\n" +
                "Description:\n" + job.getDescription();
        
        alert.setContentText(details);
        alert.showAndWait();
    }
    
    private ButtonType showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CANCEL);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void viewSavedJobs() {
        showAlert("Saved Jobs", "Saved jobs feature will be available soon!");
    }

    @FXML
    private void viewMessages() {
        JobPortal.loadScene("messages.fxml", "Messages");
    }

    @FXML
    private void viewCareerStats() {
        showAlert("Career Stats", "Detailed statistics about your job search will be available soon!");
    }

    @FXML
    private void viewResources() {
        showAlert("Learning Resources", "Access to learning resources and career development content will be available soon!");
    }

    @FXML
    private void handleSaveJob() {
        int selectedIndex = jobListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= currentJobs.size()) {
            showAlert("Error", "Please select a job to save.");
            return;
        }
        
        Job selectedJob = currentJobs.get(selectedIndex);
        showAlert("Job Saved", "You've saved \"" + selectedJob.getTitle() + "\" for later viewing.");
    }

    // --- Sidebar Navigation Methods --- 

    @FXML
    private void navigateToDashboard() {
        JobPortal.loadScene("jobseeker_dashboard.fxml", "Job Portal - Dashboard");
    }

    @FXML
    private void navigateToProfile() {
        viewProfile();
    }

    @FXML
    private void navigateToApplications() {
        viewApplications();
    }

    @FXML
    private void navigateToMessages() {
        viewMessages();
    }

    @FXML
    private void navigateToSavedJobs() {
        JobPortal.loadScene("saved_jobs.fxml", "Saved Jobs");
    }

    @FXML
    private void navigateToSettings() {
        JobPortal.loadScene("settings.fxml", "Settings");
    }

}

