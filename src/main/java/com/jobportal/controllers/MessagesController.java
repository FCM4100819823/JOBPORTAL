package com.jobportal.controllers;

import java.util.List;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Message;
import com.jobportal.models.User;
import com.jobportal.services.MessageService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MessagesController {
    @FXML private TextField recipientField;
    @FXML private TextArea messageField;
    @FXML private ListView<String> messageListView;

    private final MessageService messageService = new MessageService();

    @FXML
    private void initialize() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadMessages(currentUser);
        } else {
            System.err.println("MessagesController initialized without user session.");
        }
    }

    @FXML
    private void handleSendMessage() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "Session expired. Please log in again.");
            JobPortal.loadScene("login.fxml", "Job Portal - Login");
            return;
        }
        String recipient = recipientField.getText();
        String messageContent = messageField.getText();

        if (recipient.isEmpty() || messageContent.isEmpty()) {
            showAlert("Error", "Recipient and message content cannot be empty.");
            return;
        }

        if (messageContent.length() > 500) {
            showAlert("Error", "Message content cannot exceed 500 characters.");
            return;
        }

        if (!messageService.isRecipientValid(recipient)) {
            showAlert("Error", "Recipient does not exist.");
            return;
        }

        Message message = new Message(currentUser.getEmail(), recipient, messageContent);
        if (messageService.sendMessage(message)) {
            showAlert("Success", "Message sent successfully!");
            messageField.clear();
            loadMessages(currentUser);
        } else {
            showAlert("Error", "Failed to send message. Please try again.");
        }
    }

    private void loadMessages(User currentUser) {
        messageListView.getItems().clear();
        List<Message> messages = messageService.getMessagesByUser(currentUser.getEmail());
        if (messages.isEmpty()) {
            messageListView.getItems().add("No messages found.");
        } else {
            for (Message message : messages) {
                messageListView.getItems().add(
                    (message.getSender().equals(currentUser.getEmail()) ? "You" : message.getSender()) +
                    ": " + message.getContent()
                );
            }
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
            System.err.println("No user session found when trying to go back to dashboard. Redirecting to login.");
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
