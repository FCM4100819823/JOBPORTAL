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
    public static MongoDatabase database;
    private static boolean useFallback = false;

    private DatabaseConnection() {}

    public static MongoDatabase getDatabase() {
        if (database == null) {
            try {
                // Configure MongoDB client with timeout settings
                MongoClientSettings settings = MongoClientSettings.builder()
                    .applyToClusterSettings(builder -> 
                        builder.hosts(Arrays.asList(new ServerAddress("localhost", 27017)))
                        .serverSelectionTimeout(2, TimeUnit.SECONDS))
                    .applyToSocketSettings(builder -> 
                        builder.connectTimeout(2, TimeUnit.SECONDS))
                    .build();
                
                mongoClient = MongoClients.create(settings);
                database = mongoClient.getDatabase("jobportal");
                
                // Test connection
                database.runCommand(new Document("ping", 1));
                System.out.println("Successfully connected to MongoDB");
            } catch (MongoException e) {
                System.err.println("MongoDB connection failed: " + e.getMessage());
                
                // Return null so the service layer can handle the missing database
                return null;
            }
        }
        return database;
    }
    
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
