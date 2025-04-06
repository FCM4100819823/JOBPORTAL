package com.jobportal.controllers;
import com.jobportal.main.JobPortal;

import com.jobportal.services.ApplicationService;
import com.jobportal.services.JobService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import com.jobportal.utils.SessionManager;
import com.jobportal.utils.NotificationManager;
import com.jobportal.models.User;

public class AnalyticsController {
    @FXML private PieChart jobApplicationsChart;
    @FXML private BarChart<String, Number> jobPostingsChart;

    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();

    @FXML
    private void initialize() {
        loadJobApplicationsChart();
        loadJobPostingsChart();
    }

    private void loadJobApplicationsChart() {
        jobApplicationsChart.getData().addAll(
                new PieChart.Data("Pending", applicationService.getApplicationsByStatus("Pending").size()),
                new PieChart.Data("Accepted", applicationService.getApplicationsByStatus("Accepted").size()),
                new PieChart.Data("Rejected", applicationService.getApplicationsByStatus("Rejected").size())
        );
    }

    private void loadJobPostingsChart() {
        jobPostingsChart.getData().clear(); // Clear existing data
        BarChart.Series<String, Number> series = new BarChart.Series<>();
        jobService.getJobPostingsByEmployer().forEach((employer, count) -> {
            series.getData().add(new BarChart.Data<>(employer, count));
        });
        jobPostingsChart.getData().add(series);
    }

    @FXML
    private void goToDashboard() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            NotificationManager.showError("Error", "No user session found");
            JobPortal.loadScene("login.fxml", "Login");
            return;
        }
        
        String role = currentUser.getRole().toLowerCase();
        switch (role) {
            case "jobseeker":
                JobPortal.loadScene("jobseeker_dashboard.fxml", "Job Seeker Dashboard");
                break;
            case "employer":
                JobPortal.loadScene("employer_dashboard.fxml", "Employer Dashboard");
                break;
            case "recruiter":
                JobPortal.loadScene("recruiter_dashboard.fxml", "Recruiter Dashboard");
                break;
            case "admin":
                JobPortal.loadScene("admin_dashboard.fxml", "Admin Dashboard");
                break;
            default:
                NotificationManager.showError("Error", "Invalid user role: " + role);
                JobPortal.loadScene("login.fxml", "Login");
                break;
        }
    }
}
