package com.jobportal.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.services.UserService;
import com.jobportal.utils.PasswordUtil;
import javafx.scene.text.Text;

public class RegisterController {
    @FXML
    private Button registerButton;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private CheckBox termsCheckbox;
    @FXML private Text errorText;
    
    private final UserService userService = new UserService();
    
    @FXML
    private void initialize() {
        // Populate the role dropdown
        roleComboBox.getItems().addAll("Job Seeker", "Employer", "Recruiter");
    }
    
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String fullName = fullNameField.getText();
        String phone = phoneField.getText();
        String location = locationField.getText();
        String role = roleComboBox.getValue();
        boolean termsAccepted = termsCheckbox.isSelected();
        
        // Validate input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || fullName.isEmpty() || role == null || !termsAccepted) {
            showError("All required fields must be filled and terms must be accepted.");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }
        
        if (!phone.matches("\\d{10}")) { // Fixed: Validate phone number format
            showError("Phone number must be 10 digits.");
            return;
        }
        
        // Hash the password before storing
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(
            username,
            email,
            hashedPassword,
            fullName,
            phone,
            location,
            role,
            null // No profile picture during registration
        );
        
        if (userService.registerUser(newUser)) {
            showAlert("Success", "Registration successful! You can now log in.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
        } else {
            showError("Email already exists! Try another.");
        }
    }
    
    @FXML
    private void goToLogin() {
        JobPortal.loadScene("login.fxml", "Job Portal - Login");
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }
}