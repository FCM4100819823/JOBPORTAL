package com.jobportal.employer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class EmployerDashboardController {
    @FXML
    private Button myJobsButton, applicationsButton, messagesButton, companyProfileButton, logoutButton;

    @FXML
    private void openMyJobs() {
        // Logic to open "My Jobs"
        System.out.println("Opening My Jobs...");
    }

    @FXML
    private void openApplications() {
        // Logic to open applications
        System.out.println("Opening Applications...");
    }

    @FXML
    private void openMessages() {
        // Logic to open messages
        System.out.println("Opening Messages...");
    }

    @FXML
    private void openCompanyProfile() {
        // Logic to open company profile
        System.out.println("Opening Company Profile...");
    }

    @FXML
    private void logout() {
        // Logic to log out
        System.out.println("Logging out...");
    }
}
