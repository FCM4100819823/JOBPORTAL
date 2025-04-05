package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Interview;
import com.jobportal.services.InterviewService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;

public class InterviewsController {
    @FXML private TableView<Interview> interviewsTable;
    @FXML private TableColumn<Interview, String> candidateColumn;
    @FXML private TableColumn<Interview, String> positionColumn;
    @FXML private TableColumn<Interview, LocalDateTime> dateTimeColumn;
    @FXML private TableColumn<Interview, String> statusColumn;
    @FXML private TableColumn<Interview, String> typeColumn;
    @FXML private Button scheduleButton;
    @FXML private Button rescheduleButton;
    @FXML private Button cancelButton;
    @FXML private Button backButton;

    private final InterviewService interviewService = new InterviewService();
    private ObservableList<Interview> interviews;

    @FXML
    private void initialize() {
        setupTableColumns();
        loadInterviews();
        setupButtonHandlers();
    }

    private void setupTableColumns() {
        candidateColumn.setCellValueFactory(new PropertyValueFactory<>("candidateName"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    private void loadInterviews() {
        String recruiterEmail = SessionManager.getInstance().getCurrentUser().getEmail();
        List<Interview> interviewList = interviewService.getInterviewsByRecruiter(recruiterEmail);
        interviews = FXCollections.observableArrayList(interviewList);
        interviewsTable.setItems(interviews);
    }

    private void setupButtonHandlers() {
        scheduleButton.setOnAction(e -> handleScheduleInterview());
        rescheduleButton.setOnAction(e -> handleRescheduleInterview());
        cancelButton.setOnAction(e -> handleCancelInterview());
        backButton.setOnAction(e -> goToDashboard());
    }

    @FXML
    private void handleScheduleInterview() {
        JobPortal.loadScene("schedule_interview.fxml", "Schedule Interview");
    }

    @FXML
    private void handleRescheduleInterview() {
        Interview selected = interviewsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an interview to reschedule");
            return;
        }
        // TODO: Implement reschedule dialog
    }

    @FXML
    private void handleCancelInterview() {
        Interview selected = interviewsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an interview to cancel");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Interview");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to cancel this interview?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (interviewService.cancelInterview(selected.getId())) {
                showAlert("Success", "Interview cancelled successfully");
                loadInterviews(); // Refresh the table
            } else {
                showAlert("Error", "Failed to cancel interview");
            }
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