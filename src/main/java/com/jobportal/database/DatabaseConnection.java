package com.jobportal.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DatabaseConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int SERVER_SELECTION_TIMEOUT_MS = 5000;

    private DatabaseConnection() {}

    public static MongoDatabase getDatabase() {
        if (database == null) {
            int retryCount = 0;
            while (retryCount < MAX_RETRIES) {
                try {
                    // Configure MongoDB client with more generous timeout settings
                    MongoClientSettings settings = MongoClientSettings.builder()
                        .applyToClusterSettings(builder -> 
                            builder.hosts(Arrays.asList(new ServerAddress("localhost", 27017)))
                            .serverSelectionTimeout(SERVER_SELECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS))
                        .applyToSocketSettings(builder -> 
                            builder.connectTimeout(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS))
                        .build();
                    
                    mongoClient = MongoClients.create(settings);
                    database = mongoClient.getDatabase("jobportal");
                    
                    // Test connection
                    database.runCommand(new Document("ping", 1));
                    System.out.println("Successfully connected to MongoDB");
                    return database;
                } catch (MongoException e) {
                    retryCount++;
                    System.err.println("MongoDB connection attempt " + retryCount + " failed: " + e.getMessage());
                    if (retryCount < MAX_RETRIES) {
                        try {
                            Thread.sleep(RETRY_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
            System.err.println("Failed to connect to MongoDB after " + MAX_RETRIES + " attempts");
            return null;
        }
        return database;
    }
    
    public static void close() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                database = null;
                mongoClient = null;
            } catch (Exception e) {
                System.err.println("Error closing MongoDB connection: " + e.getMessage());
            }
        }
    }
}
