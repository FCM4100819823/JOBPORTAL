package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.services.UserService;
import com.jobportal.utils.SessionManager;  // Added missing import
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;

import java.io.File;

public class ProfileController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private TextField roleField;
    @FXML private ImageView profileImageView;
    @FXML private Button backToDashboardButton;

    private final UserService userService = new UserService();
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML
    private void initialize() {
        if (currentUser != null) {
            fullNameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            locationField.setText(currentUser.getLocation());
            roleField.setText(currentUser.getRole());

            // Load profile picture if available
            if (currentUser.getProfilePicturePath() != null) {
                File profilePictureFile = new File(currentUser.getProfilePicturePath());
                if (profilePictureFile.exists()) {
                    profileImageView.setImage(new Image(profilePictureFile.toURI().toString()));
                }
            }
        }
    }

    @FXML
    private void handleUploadProfilePicture() {
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
        if (currentUser != null) {
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
                // Update session user
                SessionManager.getInstance().setCurrentUser(updatedUser);
                currentUser = updatedUser;
                showAlert("Success", "Profile updated successfully!");
            } else {
                showAlert("Error", "Failed to update profile. Please try again.");
            }
        }
    }

    @FXML
    private void goToDashboard() {
        if (currentUser != null) {
            switch (currentUser.getRole().toLowerCase()) {
                case "job seeker":
                    JobPortal.loadScene("jobseeker_dashboard.fxml", "Job Seeker Dashboard");
                    break;
                case "employer":
                    JobPortal.loadScene("employer_dashboard.fxml", "Employer Dashboard");
                    break;
                case "recruiter":
                    JobPortal.loadScene("recruiter_dashboard.fxml", "Recruiter Dashboard");
                    break;
                default:
                    System.err.println("Unknown role: " + currentUser.getRole());
                    JobPortal.loadScene("dashboard.fxml", "Dashboard");
            }
        } else {
            // Fallback to login if user is null
            JobPortal.loadScene("login.fxml", "Login");
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
