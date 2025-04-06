package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Candidate;
import com.jobportal.services.CandidateService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.List;

public class CandidateDatabaseController {
    @FXML private TableView<Candidate> candidatesTable;
    @FXML private TableColumn<Candidate, String> nameColumn;
    @FXML private TableColumn<Candidate, String> emailColumn;
    @FXML private TableColumn<Candidate, String> skillsColumn;
    @FXML private TableColumn<Candidate, Integer> experienceColumn;
    @FXML private TableColumn<Candidate, String> locationColumn;
    @FXML private TableColumn<Candidate, String> statusColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Button addCandidateButton;
    @FXML private Button viewDetailsButton;
    @FXML private Button backButton;

    private final CandidateService candidateService = new CandidateService();
    private ObservableList<Candidate> candidates;
    private FilteredList<Candidate> filteredData;

    @FXML
    private void initialize() {
        setupTableColumns();
        setupFilterComboBox();
        loadCandidates();
        setupSearchFilter();
        setupButtonHandlers();
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));
        experienceColumn.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupFilterComboBox() {
        filterComboBox.getItems().addAll(
            "All Candidates",
            "Active",
            "Interviewing",
            "Placed",
            "New"
        );
        filterComboBox.setValue("All Candidates");
        
        filterComboBox.setOnAction(e -> updateFilter());
    }

    private void loadCandidates() {
        String recruiterEmail = SessionManager.getInstance().getCurrentUser().getEmail();
        List<Candidate> candidateList = candidateService.getCandidatesByRecruiter(recruiterEmail);
        candidates = FXCollections.observableArrayList(candidateList);
        filteredData = new FilteredList<>(candidates, p -> true);
        
        SortedList<Candidate> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(candidatesTable.comparatorProperty());
        
        candidatesTable.setItems(sortedData);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });
    }

    private void updateFilter() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();
        
        filteredData.setPredicate(candidate -> {
            boolean matchesSearch = searchText.isEmpty() ||
                candidate.getName().toLowerCase().contains(searchText) ||
                candidate.getEmail().toLowerCase().contains(searchText) ||
                String.join(", ", candidate.getSkills()).toLowerCase().contains(searchText) ||
                candidate.getLocation().toLowerCase().contains(searchText);
                
            boolean matchesFilter = filterValue.equals("All Candidates") ||
                candidate.getStatus().equals(filterValue);
                
            return matchesSearch && matchesFilter;
        });
    }

    private void setupButtonHandlers() {
        addCandidateButton.setOnAction(e -> handleAddCandidate());
        viewDetailsButton.setOnAction(e -> handleViewDetails());
        backButton.setOnAction(e -> goToDashboard());
    }

    @FXML
    private void handleAddCandidate() {
        JobPortal.loadScene("add_candidate.fxml", "Add Candidate");
    }

    @FXML
    private void handleViewDetails() {
        Candidate selected = candidatesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a candidate to view details");
            return;
        }
        
        showAlert("Candidate Details", 
            "Name: " + selected.getName() + "\n" +
            "Email: " + selected.getEmail() + "\n" +
            "Skills: " + selected.getSkills() + "\n" +
            "Experience: " + selected.getYearsOfExperience() + " years\n" +
            "Location: " + selected.getLocation() + "\n" +
            "Status: " + selected.getStatus()
        );
    }

    @FXML
    private void goToDashboard() {
        JobPortal.loadScene("recruiter_dashboard.fxml", "Recruiter Dashboard");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 