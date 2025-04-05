package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.Message;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.eq;

public class MessageService {
    private final MongoCollection<Document> messageCollection;
    private final MongoCollection<Document> notificationCollection;

    public MessageService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database != null) {
            this.messageCollection = database.getCollection("messages");
            this.notificationCollection = database.getCollection("notifications"); // Added notifications collection
        } else {
            this.messageCollection = null;
            this.notificationCollection = null;
            System.err.println("WARNING: Database connection failed. MessageService will not function properly.");
        }
    }

    public boolean sendMessage(Message message) {
        if (messageCollection == null) {
            System.err.println("Cannot send message: Database not connected");
            return false;
        }

        try {
            Document newMessage = new Document("sender", message.getSender())
                    .append("recipient", message.getRecipient())
                    .append("content", message.getContent())
                    .append("timestamp", message.getTimestamp());
            messageCollection.insertOne(newMessage);
            return createNotification(message.getRecipient(), "You have a new message from " + message.getSender());
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    public boolean createNotification(String recipient, String messageContent) {
        if (notificationCollection == null) {
            System.err.println("Cannot create notification: Database not connected");
            return false;
        }

        try {
            Document notification = new Document("recipient", recipient)
                    .append("content", messageContent)
                    .append("timestamp", new java.util.Date());
            notificationCollection.insertOne(notification);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating notification: " + e.getMessage());
            return false;
        }
    }

    public List<Message> getMessagesByUser(String email) {
        List<Message> messages = new ArrayList<>();
        if (messageCollection == null) {
            System.err.println("Cannot retrieve messages: Database not connected");
            return messages;
        }

        try {
            for (Document doc : messageCollection.find(or(eq("sender", email), eq("recipient", email)))) {
                messages.add(new Message(
                        doc.getString("sender"),
                        doc.getString("recipient"),
                        doc.getString("content")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving messages: " + e.getMessage());
        }
        return messages;
    }

    public boolean isRecipientValid(String email) {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database == null) {  // Added null check for database
            System.err.println("Cannot validate recipient: Database not connected");
            return false;
        }
        
        MongoCollection<Document> userCollection = database.getCollection("users");
        if (userCollection == null) {
            System.err.println("Cannot validate recipient: Database not connected");
            return false;
        }

        try {
            return userCollection.find(eq("email", email)).first() != null;
        } catch (Exception e) {
            System.err.println("Error validating recipient: " + e.getMessage());
            return false;
        }
    }
}
