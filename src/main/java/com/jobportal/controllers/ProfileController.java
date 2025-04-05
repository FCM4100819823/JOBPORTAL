package com.jobportal.controllers;

import java.io.File;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.services.UserService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ProfileController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextField roleField;
    @FXML private ImageView profileImageView;
    @FXML private Button backToDashboardButton;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            populateFields(currentUser);
        } else {
            System.err.println("ProfileController initialized without user session. Redirecting to login.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
        }
    }

    private void populateFields(User user) {
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        locationField.setText(user.getLocation());
        roleField.setText(user.getRole());

        // Load profile picture if available
        if (user.getProfilePicturePath() != null) {
            File profilePictureFile = new File(user.getProfilePicturePath());
            if (profilePictureFile.exists()) {
                try {
                    profileImageView.setImage(new Image(profilePictureFile.toURI().toString()));
                } catch (Exception e) {
                    System.err.println("Error loading profile image: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleUploadProfilePicture() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) { 
            showAlert("Error", "Session expired. Please log in again."); 
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
            return; 
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Validate image file
                Image image = new Image(selectedFile.toURI().toString());
                if (image.isError()) {
                    showAlert("Error", "Selected file is not a valid image.");
                    return;
                }
                
                // Copy file to application directory to ensure persistence
                File appDir = new File(System.getProperty("user.home") + "/.jobportal/profiles/");
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                
                String fileName = currentUser.getEmail().replace("@", "_at_").replace(".", "_") + "_profile" + 
                        selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                File destFile = new File(appDir, fileName);
                
                // Copy file
                java.nio.file.Files.copy(
                    selectedFile.toPath(), 
                    destFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                
                // Update user with the new path
                currentUser.setProfilePicturePath(destFile.getAbsolutePath());
                profileImageView.setImage(new Image(destFile.toURI().toString()));

                if (userService.updateUserProfilePicture(currentUser)) {
                    showAlert("Success", "Profile picture updated successfully!");
                } else {
                    showAlert("Error", "Failed to update profile picture in database. Please try again.");
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to process image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSaveChanges() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) { 
            showAlert("Error", "Session expired. Please log in again."); 
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
            return; 
        }
        
        String fullName = fullNameField.getText();
        String phone = phoneField.getText();
        String location = locationField.getText();
        
        // Validate input
        if (fullName.trim().isEmpty()) {
            showAlert("Error", "Full name cannot be empty");
            return;
        }
        
        if (!phone.isEmpty() && !phone.matches("\\d{10}")) {
            showAlert("Error", "Phone number must be 10 digits");
            return;
        }

        // Create updated user object
        User updatedUser = new User(
            currentUser.getUsername(),
            currentUser.getEmail(),
            currentUser.getPassword(),
            fullName,
            phone,
            location,
            currentUser.getRole(),
            currentUser.getProfilePicturePath()
        );

        if (userService.updateUser(updatedUser)) {
            SessionManager.getInstance().setCurrentUser(updatedUser);
            showAlert("Success", "Profile updated successfully!");
        } else {
            showAlert("Error", "Failed to update profile. Please try again.");
        }
    }

    @FXML
    private void goToDashboard() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            String role = currentUser.getRole().toLowerCase();
            String targetFxml = null;
            String targetTitle = null;
            switch (role) {
                case "job seeker":
                    targetFxml = "jobseeker_dashboard.fxml";
                    targetTitle = "Job Seeker Dashboard";
                    break;
                case "employer": 
                    targetFxml = "employer_dashboard.fxml";
                    targetTitle = "Employer Dashboard";
                    break;
                case "recruiter":
                    targetFxml = "recruiter_dashboard.fxml";
                    targetTitle = "Recruiter Dashboard";
                    break;
                case "admin":
                    targetFxml = "admin_dashboard.fxml";
                    targetTitle = "Admin Dashboard";
                    break;
                default:
                    System.err.println("Unknown role for dashboard navigation: " + currentUser.getRole());
                    targetFxml = "login.fxml";
                    targetTitle = "Job Portal - Login";
            }
             JobPortal.loadScene(targetFxml, targetTitle);
        } else {
            System.err.println("ProfileController: No user session found. Redirecting to login.");
             JobPortal.loadScene("login.fxml", "Job Portal - Login");
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
