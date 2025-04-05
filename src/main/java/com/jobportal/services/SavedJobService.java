package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class SavedJobService {
    private final MongoCollection<Document> savedJobsCollection;

    public SavedJobService() {
        this.savedJobsCollection = DatabaseConnection.getDatabase().getCollection("saved_jobs");
    }

    public boolean saveJob(String userEmail, String jobId) {
        try {
            Document savedJob = new Document("userEmail", userEmail).append("jobId", jobId);
            savedJobsCollection.insertOne(savedJob);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving job: " + e.getMessage());
            return false;
        }
    }

    public List<String> getSavedJobs(String userEmail) {
        List<String> savedJobs = new ArrayList<>();
        for (Document doc : savedJobsCollection.find(eq("userEmail", userEmail))) {
            savedJobs.add(doc.getString("jobId"));
        }
        return savedJobs;
    }
}
