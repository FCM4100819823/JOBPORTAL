package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Message;
import com.jobportal.models.User;
import com.jobportal.services.MessageService;
import com.jobportal.utils.SessionManager;  // Added missing import
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

public class MessagesController {
    @FXML private TextField recipientField;
    @FXML private TextArea messageField;
    @FXML private ListView<String> messageListView;

    private final MessageService messageService = new MessageService();
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML
    private void initialize() {
        if (currentUser != null) {
            loadMessages();
        }
    }

    @FXML
    private void handleSendMessage() {
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
            loadMessages();
        } else {
            showAlert("Error", "Failed to send message. Please try again.");
        }
    }

    private void loadMessages() {
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
            }
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
