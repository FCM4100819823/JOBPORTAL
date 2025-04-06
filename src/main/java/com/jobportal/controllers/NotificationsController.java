package com.jobportal.controllers;

import java.util.List;

import com.jobportal.main.JobPortal;
import com.jobportal.models.User;
import com.jobportal.models.Notification;
import com.jobportal.services.NotificationService;
import com.jobportal.utils.NotificationManager;
import com.jobportal.utils.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NotificationsController {
    @FXML private ListView<String> notificationsListView;

    private final NotificationService notificationService = new NotificationService();

    @FXML
    private void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        notificationsListView.getItems().clear();
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            notificationsListView.getItems().add("No user session found.");
            return;
        }
        
        List<Notification> notifications = notificationService.getNotificationsByUser(currentUser.getEmail());
        if (notifications.isEmpty()) {
            notificationsListView.getItems().add("No notifications found.");
        } else {
            for (Notification notification : notifications) {
                notificationsListView.getItems().add(notification.getContent());
            }
        }
    }

    @FXML
    private void goToDashboard() {
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
}
