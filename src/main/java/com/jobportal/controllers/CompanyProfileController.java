package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Company;
import com.jobportal.services.CompanyService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CompanyProfileController implements Initializable {

    @FXML private TextField nameField;
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
    @FXML private Label companyNameLabel;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize size options
        sizeComboBox.getItems().addAll(
            "1-10 employees",
            "11-50 employees",
            "51-200 employees",
            "201-500 employees",
            "501-1000 employees",
            "1000+ employees"
        );

        // Load company data
        loadCompanyData();
        
        // Set up button handlers
        saveButton.setOnAction(e -> handleSave());
        backButton.setOnAction(e -> goToDashboard());
        uploadLogoButton.setOnAction(e -> handleUploadLogo());
        editButton.setOnAction(e -> toggleEditMode());
    }

    private void loadCompanyData() {
        String employerEmail = SessionManager.getInstance().getCurrentUser().getEmail();
        company = companyService.getCompanyByEmployerEmail(employerEmail);
        
        if (company != null) {
            // Populate form fields
            populateFormFields();
            // Show view mode
            showViewMode();
        } else {
            // Show form mode for new company
            showFormMode();
        }
    }

    private void populateFormFields() {
        nameField.setText(company.getName());
        industryField.setText(company.getIndustry());
        descriptionArea.setText(company.getDescription());
        websiteField.setText(company.getWebsite());
        locationField.setText(company.getLocation());
        sizeComboBox.setValue(company.getSize());
        foundedYearField.setText(company.getFoundedYear());
        contactPersonField.setText(company.getContactPerson());
        contactEmailField.setText(company.getContactEmail());
        phoneField.setText(company.getPhone());
        
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
        
        // Populate view labels
        companyNameLabel.setText(company.getName());
        industryLabel.setText(company.getIndustry());
        descriptionLabel.setText(company.getDescription());
        websiteLabel.setText(company.getWebsite());
        locationLabel.setText(company.getLocation());
        sizeLabel.setText(company.getSize());
        foundedYearLabel.setText(company.getFoundedYear());
        contactPersonLabel.setText(company.getContactPerson());
        contactEmailLabel.setText(company.getContactEmail());
        phoneLabel.setText(company.getPhone());
    }

    private void handleSave() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Error", "Company name is required");
            return;
        }
        
        // Create or update company object
        if (company == null) {
            company = new Company();
            company.setEmployerEmail(SessionManager.getInstance().getCurrentUser().getEmail());
        }
        
        company.setName(nameField.getText().trim());
        company.setIndustry(industryField.getText().trim());
        company.setDescription(descriptionArea.getText().trim());
        company.setWebsite(websiteField.getText().trim());
        company.setLocation(locationField.getText().trim());
        company.setSize(sizeComboBox.getValue());
        company.setFoundedYear(foundedYearField.getText().trim());
        company.setContactPerson(contactPersonField.getText().trim());
        company.setContactEmail(contactEmailField.getText().trim());
        company.setPhone(phoneField.getText().trim());
        
        if (logoPath != null && !logoPath.isEmpty()) {
            company.setLogoUrl(logoPath);
        }
        
        // Save to database
        boolean success;
        if (company.getId() == null) {
            success = companyService.addCompany(company);
        } else {
            success = companyService.updateCompany(company);
        }
        
        if (success) {
            showAlert("Success", "Company profile saved successfully");
            showViewMode();
        } else {
            showAlert("Error", "Failed to save company profile");
        }
    }

    private void handleUploadLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Company Logo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File selectedFile = fileChooser.showOpenDialog(logoImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // In a real application, you would upload this file to a server or storage service
                // For now, we'll just use the local file path
                logoPath = selectedFile.toURI().toString();
                Image logoImage = new Image(logoPath);
                logoImageView.setImage(logoImage);
            } catch (Exception e) {
                showAlert("Error", "Failed to load logo: " + e.getMessage());
            }
        }
    }

    private void toggleEditMode() {
        if (isEditMode) {
            showViewMode();
        } else {
            showFormMode();
        }
    }

    private void showFormMode() {
        formContainer.setVisible(true);
        formContainer.setManaged(true);
        viewContainer.setVisible(false);
        viewContainer.setManaged(false);
        isEditMode = true;
    }

    private void showViewMode() {
        formContainer.setVisible(false);
        formContainer.setManaged(false);
        viewContainer.setVisible(true);
        viewContainer.setManaged(true);
        isEditMode = false;
    }

    private void goToDashboard() {
        JobPortal.loadScene("employer_dashboard.fxml", "Employer Dashboard");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 