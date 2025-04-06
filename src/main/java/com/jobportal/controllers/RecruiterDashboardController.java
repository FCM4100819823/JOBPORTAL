package com.jobportal.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Candidate;
import com.jobportal.services.CandidateService;
import com.jobportal.services.ClientService;
import com.jobportal.services.JobRequisitionService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RecruiterDashboardController implements Initializable {

    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private TableView<Candidate> candidatesTableView;
    
    @FXML
    private TableColumn<Candidate, String> nameColumn;
    
    @FXML
    private TableColumn<Candidate, String> skillsColumn;
    
    @FXML
    private TableColumn<Candidate, Integer> experienceColumn;
    
    @FXML
    private TableColumn<Candidate, String> locationColumn;
    
    @FXML
    private TableColumn<Candidate, String> statusColumn;
    
    private final ClientService clientService = new ClientService();
    private final CandidateService candidateService = new CandidateService();
    private final JobRequisitionService jobRequisitionService = new JobRequisitionService();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupFilterComboBox();
        setupTableColumns();
    }
    
    private void setupFilterComboBox() {
        filterComboBox.getItems().addAll("All Candidates", "Active", "Interviewing", "Placed");
        filterComboBox.setValue("All Candidates");
    }
    
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText().trim();
        String filter = filterComboBox.getValue();
        
        System.out.println("Searching for: " + searchTerm + " with filter: " + filter);
        
        // TODO: Implement search functionality
    }
    
    @FXML
    public void handleJobListings() {
        JobPortal.loadScene("job_listings.fxml", "Job Portal - Job Listings");
    }
    
    @FXML
    public void handleCandidates() {
        System.out.println("Viewing candidates");
        // This would refresh the candidates view
    }
    
    @FXML
    public void handlePlacement() {
        JobPortal.loadScene("placements.fxml", "Job Portal - Placements");
    }
    
    @FXML
    public void handleInterviews() {
        JobPortal.loadScene("interviews.fxml", "Job Portal - Interviews");
    }
    
    @FXML
    public void handleAddCandidate() {
        JobPortal.loadScene("add_candidate.fxml", "Job Portal - Add Candidate");
    }
    
    @FXML
    public void handleLogout() {
        SessionManager.getInstance().clearSession();
        JobPortal.loadScene("login.fxml", "Job Portal - Login");
    }

    @FXML
    private void navigateToDashboard() {
        JobPortal.loadScene("recruiter_dashboard.fxml", "Recruiter Dashboard");
    }

    @FXML
    private void navigateToClients() {
        JobPortal.loadScene("clients.fxml", "Client Management");
    }

    @FXML
    private void navigateToJobRequisitions() {
        JobPortal.loadScene("job_requisitions.fxml", "Job Requisitions");
    }

    @FXML
    private void navigateToCandidateDatabase() {
        JobPortal.loadScene("candidate_database.fxml", "Candidate Database");
    }

    @FXML
    private void navigateToInterviews() {
        JobPortal.loadScene("interviews.fxml", "Interview Management");
    }

    @FXML
    private void navigateToMessages() {
        JobPortal.loadScene("messages.fxml", "Messages");
    }

    @FXML
    private void navigateToSettings() {
        JobPortal.loadScene("settings.fxml", "Settings");
    }

    @FXML
    private void addNewCandidate() {
        JobPortal.loadScene("add_candidate.fxml", "Add Candidate");
    }

    @FXML
    private void scheduleInterview() {
        JobPortal.loadScene("schedule_interview.fxml", "Schedule Interview");
    }

    @FXML
    private void generateReports() {
        JobPortal.loadScene("reports.fxml", "Reports");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
