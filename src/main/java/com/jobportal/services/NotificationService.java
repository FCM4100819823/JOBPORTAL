package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.Notification;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class NotificationService {
    private final MongoCollection<Document> notificationCollection;

    public NotificationService() {
        this.notificationCollection = DatabaseConnection.getDatabase().getCollection("notifications");
    }

    public List<Notification> getNotificationsByUser(String email) {
        List<Notification> notifications = new ArrayList<>();
        for (Document doc : notificationCollection.find(eq("recipient", email))) {
            notifications.add(new Notification(
                    doc.getString("recipient"),
                    doc.getString("content"),
                    doc.getDate("timestamp")
            ));
        }
        return notifications;
    }
}
