package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Candidate;
import com.jobportal.models.User;
import com.jobportal.services.CandidateService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

public class AddCandidateController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextField currentJobTitleField;
    @FXML private TextField experienceField; // e.g., Years
    @FXML private TextArea skillsArea; // Comma-separated or similar
    @FXML private TextArea notesArea;
    @FXML private Button saveButton;
    @FXML private Button backButton;

    private CandidateService candidateService = new CandidateService();

    @FXML
    private void initialize() {
        // Check session on initialization
        if (!checkSession()) return;
        // Any initial setup for the form can go here
    }

    @FXML
    private void handleSaveCandidate() {
        if (!checkSession()) return;
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) return;

        // --- Basic Validation --- 
        String name = nameField.getText();
        String email = emailField.getText();
        String skillsString = skillsArea.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert("Validation Error", "Candidate name cannot be empty.");
            return;
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) { // Simple email check
            showAlert("Validation Error", "Please enter a valid email address.");
            return;
        }
        if (skillsString == null || skillsString.trim().isEmpty()) {
            showAlert("Validation Error", "Skills cannot be empty.");
            return;
        }
        int experience = 0;
        try {
            if (experienceField.getText() != null && !experienceField.getText().trim().isEmpty()) {
                experience = Integer.parseInt(experienceField.getText().trim());
            }
        } catch (NumberFormatException e) {
             showAlert("Validation Error", "Years of experience must be a valid number.");
            return;
        }
        
        // --- Convert skills string to list ---
        List<String> skills = Arrays.asList(skillsString.split(","));
        skills.replaceAll(String::trim);

        // --- Create Candidate Object ---
        Candidate newCandidate = new Candidate(
            name,
            email,
            experience,
            locationField.getText() != null ? locationField.getText().trim() : "",
            currentUser.getEmail()
        );
        newCandidate.setSkills(skills);
        newCandidate.setPhone(phoneField.getText() != null ? phoneField.getText().trim() : "");
        newCandidate.setNotes(notesArea.getText() != null ? notesArea.getText().trim() : "");
        newCandidate.setStatus("New");

        // --- Call Service ---
        try {
            boolean success = candidateService.addCandidate(newCandidate);

            if (success) {
                showAlert("Success", "Candidate '" + name + "' added successfully!");
                clearFields();
            } else {
                showAlert("Error", "Failed to add candidate. Please check the details or logs and try again.");
            }
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToDashboard() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && "recruiter".equalsIgnoreCase(currentUser.getRole())) {
             JobPortal.loadScene("recruiter_dashboard.fxml", "Recruiter Dashboard");
        } else {
             // Fallback if role is wrong or session lost
             System.err.println("Error navigating back or invalid session. Redirecting to login.");
             JobPortal.loadScene("login.fxml", "Job Portal - Login");
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
    
    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        locationField.clear();
        currentJobTitleField.clear();
        experienceField.clear();
        skillsArea.clear();
        notesArea.clear();
    }
}