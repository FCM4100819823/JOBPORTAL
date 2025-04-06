package com.jobportal.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.services.MessageService;
import com.jobportal.services.MessageService.Message;
import com.jobportal.utils.NotificationManager;
import com.jobportal.utils.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MessagesController implements Initializable {
    @FXML private ListView<Message> messageListView;
    @FXML private TextField searchField;
    @FXML private Button backToDashboardButton;
    @FXML private Button newMessageButton;

    private final MessageService messageService = new MessageService();
    private final ObservableList<Message> messages = FXCollections.observableArrayList();
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (messageListView == null) {
            System.err.println("Error: messageListView not initialized");
            return;
        }

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            NotificationManager.showError("Error", "No user session found");
            goToDashboard();
            return;
        }

        initializeMessageList();
        setupSearchFunctionality();
    }

    private void initializeMessageList() {
        if (messageListView == null) return;

        messageListView.setItems(messages);
        messageListView.setCellFactory(lv -> new MessageListCell());
        
        // Load messages for the current user
        List<Message> userMessages = messageService.getMessagesByUser(currentUser.getEmail());
        messages.addAll(userMessages);
    }

    private void setupSearchFunctionality() {
        if (searchField == null || messageListView == null) return;

        FilteredList<Message> filteredData = new FilteredList<>(messages, s -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(message -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                return message.getSubject().toLowerCase().contains(lowerCaseFilter) ||
                       message.getContent().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Message> sortedData = new SortedList<>(filteredData);
        sortedData.setComparator((m1, m2) -> m2.getDate().compareTo(m1.getDate()));
        messageListView.setItems(sortedData);
    }

    @FXML
    private void handleNewMessage() {
        if (currentUser == null) {
            NotificationManager.showError("Error", "No user session found");
            return;
        }
        JobPortal.loadScene("new_message.fxml", "New Message");
    }

    @FXML
    private void goToDashboard() {
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

    private static class MessageListCell extends javafx.scene.control.ListCell<Message> {
        @Override
        protected void updateItem(Message message, boolean empty) {
            super.updateItem(message, empty);
            if (empty || message == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            setText(String.format("%s - %s", message.getSubject(), message.getSender()));
            setStyle("-fx-text-fill: " + (message.isRead() ? "black" : "blue") + ";");
        }
    }
}
