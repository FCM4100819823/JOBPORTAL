package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Application;
import com.jobportal.models.Job;
import com.jobportal.services.ApplicationService;
import com.jobportal.services.JobService;
import com.jobportal.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import java.util.List;

public class AdminDashboardController {
    @FXML private ListView<String> userListView;
    @FXML private ListView<String> jobListView;
    @FXML private ListView<String> applicationListView;

    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();

    @FXML
    private void initialize() {
        loadUsers();
        loadJobs();
        loadApplications();
    }

    private void loadUsers() {
        userListView.getItems().clear();
        List<String> users = userService.getAllUsers();
        if (users.isEmpty()) {
            showAlert("Info", "No users found.");
        } else {
            userListView.getItems().addAll(users);
        }
    }

    private void loadJobs() {
        jobListView.getItems().clear();
        List<Job> jobs = jobService.getAllJobs();
        if (jobs.isEmpty()) {
            showAlert("Info", "No jobs found.");
        } else {
            jobs.forEach(job -> jobListView.getItems().add(job.getTitle() + " - " + job.getEmployerEmail()));
        }
    }

    private void loadApplications() {
        applicationListView.getItems().clear();
        List<Application> applications = applicationService.getAllApplications();
        if (applications.isEmpty()) {
            showAlert("Info", "No applications found.");
        } else {
            applications.forEach(application -> applicationListView.getItems().add(
                    "Job ID: " + application.getJobId() + ", User: " + application.getUserId() + ", Status: " + application.getStatus()
            ));
        }
    }

    @FXML
    private void handleDeleteUser() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userService.deleteUser(selectedUser)) {
                showAlert("Success", "User deleted successfully!");
                loadUsers();
            } else {
                showAlert("Error", "Failed to delete user.");
            }
        } else {
            showAlert("Error", "Please select a user to delete.");
        }
    }

    @FXML
    private void handleDeleteJob() {
        String selectedJob = jobListView.getSelectionModel().getSelectedItem();
        if (selectedJob != null) {
            String jobTitle = selectedJob.split(" - ")[0];
            String employerEmail = selectedJob.split(" - ")[1];
            if (jobService.deleteJobByTitle(jobTitle, employerEmail)) {
                showAlert("Success", "Job deleted successfully!");
                loadJobs();
            } else {
                showAlert("Error", "Failed to delete job.");
            }
        } else {
            showAlert("Error", "Please select a job to delete.");
        }
    }

    @FXML
    private void handleDeleteApplication() {
        String selectedApplication = applicationListView.getSelectionModel().getSelectedItem();
        if (selectedApplication != null) {
            String applicationId = selectedApplication.split(",")[0].split(":")[1].trim();
            if (applicationService.deleteApplication(applicationId)) {
                showAlert("Success", "Application deleted successfully!");
                loadApplications();
            } else {
                showAlert("Error", "Failed to delete application.");
            }
        } else {
            showAlert("Error", "Please select an application to delete.");
        }
    }

    @FXML
    private void handleLogout() {
        JobPortal.loadScene("login.fxml", "Job Portal - Login");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
