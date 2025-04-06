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
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int SERVER_SELECTION_TIMEOUT_MS = 5000;
    private static final ReentrantLock lock = new ReentrantLock();
    private static volatile boolean isInitialized = false;

    private DatabaseConnection() {
        throw new IllegalStateException("Utility class");
    }

    public static MongoDatabase getDatabase() {
        if (!isInitialized) {
            lock.lock();
            try {
                if (!isInitialized) {
                    initializeDatabase();
                }
            } finally {
                lock.unlock();
            }
        }
        return database;
    }

    private static void initializeDatabase() {
        int retryCount = 0;
        Exception lastException = null;

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
                
                if (mongoClient != null) {
                    try {
                        mongoClient.close();
                    } catch (Exception e) {
                        System.err.println("Error closing existing MongoDB client: " + e.getMessage());
                    }
                }

                mongoClient = MongoClients.create(settings);
                database = mongoClient.getDatabase("jobportal");
                
                // Test connection
                database.runCommand(new Document("ping", 1));
                System.out.println("Successfully connected to MongoDB");
                isInitialized = true;
                return;
            } catch (MongoException e) {
                lastException = e;
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
            } catch (Exception e) {
                lastException = e;
                System.err.println("Unexpected error during MongoDB connection: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }

        String errorMessage = "Failed to connect to MongoDB after " + MAX_RETRIES + " attempts";
        if (lastException != null) {
            errorMessage += ": " + lastException.getMessage();
        }
        System.err.println(errorMessage);
        throw new RuntimeException(errorMessage, lastException);
    }
    
    public static void close() {
        lock.lock();
        try {
            if (mongoClient != null) {
                try {
                    mongoClient.close();
                    System.out.println("MongoDB connection closed successfully");
                } catch (Exception e) {
                    System.err.println("Error closing MongoDB connection: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    database = null;
                    mongoClient = null;
                    isInitialized = false;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static boolean isConnected() {
        if (!isInitialized || database == null) {
            return false;
        }
        try {
            database.runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            System.err.println("Error checking MongoDB connection: " + e.getMessage());
            return false;
        }
    }
}
