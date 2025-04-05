package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.JobRequisition;
import com.jobportal.services.JobRequisitionService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.List;

public class JobRequisitionsController {
    @FXML private TableView<JobRequisition> requisitionsTable;
    @FXML private TableColumn<JobRequisition, String> titleColumn;
    @FXML private TableColumn<JobRequisition, String> companyColumn;
    @FXML private TableColumn<JobRequisition, String> locationColumn;
    @FXML private TableColumn<JobRequisition, String> statusColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private Button addRequisitionButton;
    @FXML private Button editRequisitionButton;
    @FXML private Button deleteRequisitionButton;
    @FXML private Button backButton;

    private final JobRequisitionService requisitionService = new JobRequisitionService();
    private ObservableList<JobRequisition> requisitions;
    private FilteredList<JobRequisition> filteredData;

    @FXML
    private void initialize() {
        setupTableColumns();
        setupFilterComboBox();
        loadRequisitions();
        setupSearchFilter();
        setupButtonHandlers();
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupFilterComboBox() {
        filterComboBox.getItems().addAll(
            "All Requisitions",
            "Open",
            "Closed",
            "On Hold"
        );
        filterComboBox.setValue("All Requisitions");
        
        filterComboBox.setOnAction(e -> updateFilter());
    }

    private void loadRequisitions() {
        String recruiterEmail = SessionManager.getInstance().getCurrentUser().getEmail();
        List<JobRequisition> requisitionList = requisitionService.getJobRequisitionsByRecruiter(recruiterEmail);
        requisitions = FXCollections.observableArrayList(requisitionList);
        filteredData = new FilteredList<>(requisitions, p -> true);
        
        SortedList<JobRequisition> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(requisitionsTable.comparatorProperty());
        
        requisitionsTable.setItems(sortedData);
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });
    }

    private void updateFilter() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();
        
        filteredData.setPredicate(requisition -> {
            boolean matchesSearch = searchText.isEmpty() ||
                requisition.getTitle().toLowerCase().contains(searchText) ||
                requisition.getCompany().toLowerCase().contains(searchText) ||
                requisition.getLocation().toLowerCase().contains(searchText);
                
            boolean matchesFilter = filterValue.equals("All Requisitions") ||
                requisition.getStatus().equals(filterValue);
                
            return matchesSearch && matchesFilter;
        });
    }

    private void setupButtonHandlers() {
        addRequisitionButton.setOnAction(e -> handleAddRequisition());
        editRequisitionButton.setOnAction(e -> handleEditRequisition());
        deleteRequisitionButton.setOnAction(e -> handleDeleteRequisition());
        backButton.setOnAction(e -> goToDashboard());
    }

    @FXML
    private void handleAddRequisition() {
        // TODO: Implement add requisition dialog
        showAlert("Info", "Add Requisition feature coming soon");
    }

    @FXML
    private void handleEditRequisition() {
        JobRequisition selected = requisitionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a requisition to edit");
            return;
        }
        
        // TODO: Implement edit requisition dialog
        showAlert("Info", "Edit Requisition feature coming soon");
    }

    @FXML
    private void handleDeleteRequisition() {
        JobRequisition selected = requisitionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a requisition to delete");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Requisition");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this requisition?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            // TODO: Implement delete functionality
            showAlert("Info", "Delete Requisition feature coming soon");
        }
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