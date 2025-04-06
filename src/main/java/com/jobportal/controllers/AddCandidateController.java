package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Candidate;
import com.jobportal.models.User;
import com.jobportal.services.CandidateService;
import com.jobportal.utils.NotificationManager;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AddCandidateController implements Initializable {

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

    private final CandidateService candidateService = new CandidateService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check session on initialization
        if (!checkSession()) return;
        
        // Set up button handlers
        if (saveButton != null) {
            saveButton.setOnAction(e -> handleSaveCandidate());
        }
        
        if (backButton != null) {
            backButton.setOnAction(e -> goToDashboard());
        }
    }

    @FXML
    private void handleSaveCandidate() {
        if (!checkSession()) return;
        
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            NotificationManager.showError("Error", "User session not found");
            return;
        }

        // Validate required fields
        if (!validateFields()) {
            return;
        }
        
        // Get form data
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String skillsString = skillsArea.getText().trim();
        int experience = 0;
        
        try {
            if (experienceField.getText() != null && !experienceField.getText().trim().isEmpty()) {
                experience = Integer.parseInt(experienceField.getText().trim());
            }
        } catch (NumberFormatException e) {
            NotificationManager.showError("Validation Error", "Years of experience must be a valid number.");
            return;
        }
        
        // Convert skills string to list
        List<String> skills = Arrays.asList(skillsString.split(","));
        skills.replaceAll(String::trim);

        // Create Candidate Object
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

        // Call Service
        try {
            boolean success = candidateService.addCandidate(newCandidate);

            if (success) {
                NotificationManager.showNotification("Success", "Candidate '" + name + "' added successfully!");
                clearFields();
            } else {
                NotificationManager.showError("Error", "Failed to add candidate. Please check the details and try again.");
            }
        } catch (Exception e) {
            NotificationManager.showError("Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (nameField == null || emailField == null || skillsArea == null) {
            NotificationManager.showError("Error", "Required form fields not initialized");
            return false;
        }
        
        String name = nameField.getText();
        String email = emailField.getText();
        String skillsString = skillsArea.getText();
        
        if (name == null || name.trim().isEmpty()) {
            NotificationManager.showError("Validation Error", "Candidate name cannot be empty.");
            return false;
        }
        
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            NotificationManager.showError("Validation Error", "Please enter a valid email address.");
            return false;
        }
        
        if (skillsString == null || skillsString.trim().isEmpty()) {
            NotificationManager.showError("Validation Error", "Skills cannot be empty.");
            return false;
        }
        
        return true;
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
    
    private void clearFields() {
        if (nameField != null) nameField.clear();
        if (emailField != null) emailField.clear();
        if (phoneField != null) phoneField.clear();
        if (locationField != null) locationField.clear();
        if (currentJobTitleField != null) currentJobTitleField.clear();
        if (experienceField != null) experienceField.clear();
        if (skillsArea != null) skillsArea.clear();
        if (notesArea != null) notesArea.clear();
    }
}