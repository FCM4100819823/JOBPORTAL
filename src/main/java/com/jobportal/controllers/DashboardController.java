package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Job;
import com.jobportal.models.User;
import com.jobportal.services.JobService;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

public class DashboardController {
    @FXML private ListView<String> jobListView; // For job seekers
    @FXML private Text welcomeText;
    @FXML private VBox dashboardContent;
    @FXML private ListView<String> manageJobsListView; // For employers
    @FXML private ListView<String> applicationsListView; // For applications
    @FXML private TextField searchField; // For search functionality
    @FXML private ListView<String> candidateListView;
    @FXML private ListView<String> placementsListView;

    private static User currentUser;
    private final JobService jobService = new JobService();

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML
    private void initialize() {
        try {
            System.out.println("DashboardController initializing...");

            // Show welcome message based on role if user is set
            if (currentUser != null && welcomeText != null) {
                String role = currentUser.getRole();
                welcomeText.setText("Welcome, " + currentUser.getUsername() + (role != null ? " (" + role + ")" : ""));
                System.out.println("Set welcome text for: " + currentUser.getUsername());
            }

            // Initialize the job list if it exists (for job seekers)
            if (jobListView != null) {
                System.out.println("Initializing job list view");
                addJobWithAnimation("Software Engineer - Google");
                addJobWithAnimation("Data Scientist - Amazon");
                addJobWithAnimation("Frontend Developer - Facebook");
            } else {
                System.out.println("jobListView is null - not all dashboard types have this component");
            }

            // Apply slide-in animation for dashboard content
            applySlideIn(dashboardContent);

            if (manageJobsListView != null) {
                loadEmployerJobs();
            }
        } catch (Exception e) {
            System.err.println("Error initializing dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addJobWithAnimation(String job) {
        jobListView.getItems().add(job);
        Node jobNode = jobListView.lookup(".list-cell:last-child"); // Get the last added job node
        if (jobNode != null) {
            animateJobCard(jobNode);
        }
    }

    private void animateJobCard(Node node) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), node);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1);
        scale.setToY(1);
        scale.play();
    }

    private void applySlideIn(VBox node) {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(800), node);
        slideIn.setFromX(-300); // Start off-screen
        slideIn.setToX(0);
        slideIn.play();
    }

    @FXML
    private void handleLogout() {
        // Clear current user
        currentUser = null;
        JobPortal.loadScene("login.fxml", "Job Portal - Login");
    }

    @FXML
    private void viewProfile() {
        ProfileController.setCurrentUser(currentUser);
        JobPortal.loadScene("profile.fxml", "My Profile");
    }

    @FXML
    private void viewJobs() {
        showAlert("Jobs", "Job listings view will be implemented soon.");
    }

    @FXML
    private void viewApplications() {
        JobPortal.loadScene("my_applications.fxml", "My Applications");
    }

    @FXML
    private void postJob() {
        JobPortal.loadScene("post_job.fxml", "Post a Job");
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            // Reload all jobs or applications if the search field is empty
            if (jobListView != null) {
                loadEmployerJobs();
            } else if (applicationsListView != null) {
                loadApplications();
            }
            return;
        }

        if (jobListView != null) {
            // Filter job postings
            List<String> filteredJobs = manageJobsListView.getItems().stream()
                .filter(job -> job.toLowerCase().contains(query))
                .toList();
            manageJobsListView.getItems().setAll(filteredJobs);
        } else if (applicationsListView != null) {
            // Filter applications
            List<String> filteredApplications = applicationsListView.getItems().stream()
                .filter(application -> application.toLowerCase().contains(query))
                .toList();
            applicationsListView.getItems().setAll(filteredApplications);
        }
    }

    @FXML
    private void goToUpdateApplicationStatus() {
        if (currentUser != null && currentUser.getRole().equalsIgnoreCase("employer")) {
            JobPortal.loadScene("update_application_status.fxml", "Update Application Status");
        } else {
            showAlert("Access Denied", "You do not have permission to access this feature.");
        }
    }

    private void loadEmployerJobs() {
        if (currentUser != null && currentUser.getRole().equalsIgnoreCase("employer")) {
            List<Job> jobs = jobService.getJobsByPoster(currentUser.getEmail());
            manageJobsListView.getItems().clear();
            for (Job job : jobs) {
                manageJobsListView.getItems().add(job.getTitle() + " - " + job.getLocation());
            }
        }
    }

    private void loadApplications() {
        if (currentUser != null && currentUser.getRole().equalsIgnoreCase("employer")) {
            List<Job> jobs = jobService.getJobsByEmployer(currentUser.getEmail());
            applicationsListView.getItems().clear();
            for (Job job : jobs) {
                applicationsListView.getItems().add(job.getTitle() + " - " + job.getLocation());
            }
        }
    }

    @FXML
    private void handleDeleteJob() {
        String selectedJob = manageJobsListView.getSelectionModel().getSelectedItem();
        if (selectedJob != null) {
            String jobTitle = selectedJob.split(" - ")[0];
            if (jobService.deleteJobByTitle(jobTitle, currentUser.getEmail())) {
                showAlert("Success", "Job deleted successfully!");
                loadEmployerJobs();
            } else {
                showAlert("Error", "Failed to delete the job. Please try again.");
            }
        } else {
            showAlert("Error", "Please select a job to delete.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
