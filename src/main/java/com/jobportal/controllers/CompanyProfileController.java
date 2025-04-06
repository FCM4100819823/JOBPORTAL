package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Company;
import com.jobportal.services.CompanyService;
import com.jobportal.utils.NotificationManager;
import com.jobportal.utils.SessionManager;
import com.jobportal.models.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CompanyProfileController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField industryField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField websiteField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> sizeComboBox;
    @FXML private TextField foundedYearField;
    @FXML private ImageView logoImageView;
    @FXML private TextField contactPersonField;
    @FXML private TextField contactEmailField;
    @FXML private TextField phoneField;
    @FXML private Button saveButton;
    @FXML private Button backButton;
    @FXML private Button uploadLogoButton;
    @FXML private VBox formContainer;
    @FXML private VBox viewContainer;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label industryLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label websiteLabel;
    @FXML private Label locationLabel;
    @FXML private Label sizeLabel;
    @FXML private Label foundedYearLabel;
    @FXML private Label contactPersonLabel;
    @FXML private Label contactEmailLabel;
    @FXML private Label phoneLabel;
    @FXML private Button editButton;

    private final CompanyService companyService = new CompanyService();
    private Company company;
    private String logoPath;
    private boolean isEditMode = false;
    private String currentUserEmail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!initializeComponents()) {
            NotificationManager.showError("Error", "Failed to initialize company profile");
            return;
        }

        setupSizeComboBox();
        loadCompanyProfile();
    }

    private boolean initializeComponents() {
        if (formContainer == null || viewContainer == null) {
            System.err.println("Error: Required FXML components not initialized");
            return false;
        }

        // Initialize size combo box options
        if (sizeComboBox != null) {
            sizeComboBox.getItems().addAll(
                "1-10 employees",
                "11-50 employees",
                "51-200 employees",
                "201-500 employees",
                "501-1000 employees",
                "1000+ employees"
            );
        }

        return true;
    }

    private void setupSizeComboBox() {
        if (sizeComboBox != null) {
            sizeComboBox.setPromptText("Select company size");
        }
    }

    private void loadCompanyProfile() {
        if (currentUserEmail == null) {
            NotificationManager.showError("Error", "No user session found");
            return;
        }

        company = companyService.getCompanyByEmployerEmail(currentUserEmail);
        if (company == null) {
            NotificationManager.showError("Error", "Company profile not found");
            return;
        }

        updateViewMode();
    }

    private void updateViewMode() {
        if (formContainer == null || viewContainer == null) return;

        formContainer.setVisible(isEditMode);
        formContainer.setManaged(isEditMode);
        viewContainer.setVisible(!isEditMode);
        viewContainer.setManaged(!isEditMode);

        if (isEditMode) {
            populateFormFields();
        } else {
            populateViewLabels();
        }
    }

    private void populateFormFields() {
        if (company == null) return;

        if (nameField != null) nameField.setText(company.getName());
        if (emailField != null) emailField.setText(company.getEmail());
        if (industryField != null) industryField.setText(company.getIndustry());
        if (descriptionArea != null) descriptionArea.setText(company.getDescription());
        if (websiteField != null) websiteField.setText(company.getWebsite());
        if (locationField != null) locationField.setText(company.getLocation());
        if (sizeComboBox != null) sizeComboBox.setValue(company.getSize());
        if (foundedYearField != null) foundedYearField.setText(company.getFoundedYear());
        if (contactPersonField != null) contactPersonField.setText(company.getContactPerson());
        if (contactEmailField != null) contactEmailField.setText(company.getContactEmail());
        if (phoneField != null) phoneField.setText(company.getPhone());
        
        // Set logo if available
        if (company.getLogoUrl() != null && !company.getLogoUrl().isEmpty()) {
            try {
                Image logoImage = new Image(company.getLogoUrl());
                logoImageView.setImage(logoImage);
                logoPath = company.getLogoUrl();
            } catch (Exception e) {
                System.err.println("Error loading logo: " + e.getMessage());
            }
        }
    }

    private void populateViewLabels() {
        if (company == null) return;

        if (nameLabel != null) nameLabel.setText(company.getName());
        if (emailLabel != null) emailLabel.setText(company.getEmail());
        if (industryLabel != null) industryLabel.setText(company.getIndustry());
        if (descriptionLabel != null) descriptionLabel.setText(company.getDescription());
        if (websiteLabel != null) websiteLabel.setText(company.getWebsite());
        if (locationLabel != null) locationLabel.setText(company.getLocation());
        if (sizeLabel != null) sizeLabel.setText(company.getSize());
        if (foundedYearLabel != null) foundedYearLabel.setText(company.getFoundedYear());
        if (contactPersonLabel != null) contactPersonLabel.setText(company.getContactPerson());
        if (contactEmailLabel != null) contactEmailLabel.setText(company.getContactEmail());
        if (phoneLabel != null) phoneLabel.setText(company.getPhone());
    }

    @FXML
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        Company updatedCompany = new Company();
        updatedCompany.setName(nameField.getText().trim());
        updatedCompany.setEmail(emailField.getText().trim());
        updatedCompany.setIndustry(industryField.getText().trim());
        updatedCompany.setDescription(descriptionArea.getText().trim());
        updatedCompany.setWebsite(websiteField.getText().trim());
        updatedCompany.setLocation(locationField.getText().trim());
        updatedCompany.setSize(sizeComboBox.getValue());
        updatedCompany.setFoundedYear(foundedYearField.getText().trim());
        updatedCompany.setContactPerson(contactPersonField.getText().trim());
        updatedCompany.setContactEmail(contactEmailField.getText().trim());
        updatedCompany.setPhone(phoneField.getText().trim());
        updatedCompany.setLogoUrl(logoPath);

        if (companyService.updateCompany(updatedCompany)) {
            NotificationManager.showNotification("Success", "Company profile updated successfully");
            company = updatedCompany;
            isEditMode = false;
            updateViewMode();
        } else {
            NotificationManager.showError("Error", "Failed to update company profile");
        }
    }

    private boolean validateForm() {
        if (nameField == null || emailField == null) {
            NotificationManager.showError("Error", "Required fields not initialized");
            return false;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            NotificationManager.showError("Error", "Name and email are required fields");
            return false;
        }

        return true;
    }

    @FXML
    private void handleEdit() {
        isEditMode = true;
        updateViewMode();
    }

    @FXML
    private void handleUploadLogo() {
        if (uploadLogoButton == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Company Logo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadLogoButton.getScene().getWindow());
        if (selectedFile != null) {
            logoPath = selectedFile.getAbsolutePath();
            if (logoImageView != null) {
                logoImageView.setImage(new Image("file:" + logoPath));
            }
        }
    }

    @FXML
    private void handleBack() {
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

    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email;
    }
} 