package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.Message;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.eq;

public class MessageService {
    private final MongoCollection<Document> messagesCollection;
    private final MongoCollection<Document> notificationCollection;

    public MessageService() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("jobportal");
        this.messagesCollection = database.getCollection("messages");
        this.notificationCollection = database.getCollection("notifications"); // Added notifications collection
    }

    public boolean sendMessage(Message message) {
        if (messagesCollection == null) {
            System.err.println("Cannot send message: Database not connected");
            return false;
        }

        try {
            Document doc = new Document()
                .append("_id", new ObjectId())
                .append("sender", message.getSender())
                .append("recipient", message.getRecipient())
                .append("subject", message.getSubject())
                .append("content", message.getContent())
                .append("date", message.getDate())
                .append("read", false);
            
            messagesCollection.insertOne(doc);
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

    public List<Message> getMessagesByUser(String userEmail) {
        List<Message> messages = new ArrayList<>();
        if (messagesCollection == null) {
            System.err.println("Cannot retrieve messages: Database not connected");
            return messages;
        }

        try {
            FindIterable<Document> docs = messagesCollection
                .find(Filters.or(
                    Filters.eq("sender", userEmail),
                    Filters.eq("recipient", userEmail)
                ))
                .sort(Sorts.descending("date"));
            
            for (Document doc : docs) {
                messages.add(documentToMessage(doc));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving messages: " + e.getMessage());
        }
        return messages;
    }

    public boolean markAsRead(String messageId) {
        if (messagesCollection == null) {
            System.err.println("Cannot mark message as read: Database not connected");
            return false;
        }

        try {
            messagesCollection.updateOne(
                Filters.eq("_id", new ObjectId(messageId)),
                new Document("$set", new Document("read", true))
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error marking message as read: " + e.getMessage());
            return false;
        }
    }

    private Message documentToMessage(Document doc) {
        return new Message(
            doc.getString("sender"),
            doc.getString("recipient"),
            doc.getString("subject"),
            doc.getString("content"),
            doc.getDate("date"),
            doc.getBoolean("read", false)
        );
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

    public static class Message {
        private String sender;
        private String recipient;
        private String subject;
        private String content;
        private Date date;
        private boolean read;
        
        public Message(String sender, String recipient, String subject, String content, Date date, boolean read) {
            this.sender = sender;
            this.recipient = recipient;
            this.subject = subject;
            this.content = content;
            this.date = date;
            this.read = read;
        }
        
        public Message(String sender, String recipient, String subject, String content, Date date) {
            this(sender, recipient, subject, content, date, false);
        }
        
        public String getSender() { return sender; }
        public String getRecipient() { return recipient; }
        public String getSubject() { return subject; }
        public String getContent() { return content; }
        public Date getDate() { return date; }
        public boolean isRead() { return read; }
        
        @Override
        public String toString() {
            return subject + " - From: " + sender;
        }
    }
}
