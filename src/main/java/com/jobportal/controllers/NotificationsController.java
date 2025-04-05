package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Notification;
import com.jobportal.services.NotificationService;
import com.jobportal.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class NotificationsController {
    @FXML private ListView<String> notificationsListView;

    private final NotificationService notificationService = new NotificationService();

    @FXML
    private void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        notificationsListView.getItems().clear();
        List<Notification> notifications = notificationService.getNotificationsByUser(SessionManager.getInstance().getCurrentUser().getEmail());
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
        JobPortal.loadScene("dashboard.fxml", "Dashboard");
    }
}
