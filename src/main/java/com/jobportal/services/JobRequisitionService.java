package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.JobRequisition;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class JobRequisitionService {
    private final MongoCollection<Document> collection;

    public JobRequisitionService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database != null) {
            this.collection = database.getCollection("jobRequisitions");
        } else {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    public List<JobRequisition> getJobRequisitionsByRecruiter(String recruiterEmail) {
        List<JobRequisition> requisitions = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(
            Filters.eq("recruiterEmail", recruiterEmail)
        );

        for (Document doc : findIterable) {
            requisitions.add(documentToJobRequisition(doc));
        }
        return requisitions;
    }

    public JobRequisition getJobRequisitionById(String id) {
        Document doc = collection.find(
            Filters.eq("_id", new ObjectId(id))
        ).first();
        return doc != null ? documentToJobRequisition(doc) : null;
    }

    public boolean addJobRequisition(JobRequisition requisition) {
        try {
            Document doc = new Document()
                .append("title", requisition.getTitle())
                .append("company", requisition.getCompany())
                .append("location", requisition.getLocation())
                .append("status", requisition.getStatus())
                .append("description", requisition.getDescription())
                .append("requirements", requisition.getRequirements())
                .append("recruiterEmail", requisition.getRecruiterEmail())
                .append("createdAt", requisition.getCreatedAt())
                .append("updatedAt", requisition.getUpdatedAt());

            collection.insertOne(doc);
            requisition.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error adding job requisition: " + e.getMessage());
            return false;
        }
    }

    public boolean updateJobRequisition(JobRequisition requisition) {
        try {
            collection.updateOne(
                Filters.eq("_id", new ObjectId(requisition.getId())),
                Updates.combine(
                    Updates.set("title", requisition.getTitle()),
                    Updates.set("company", requisition.getCompany()),
                    Updates.set("location", requisition.getLocation()),
                    Updates.set("status", requisition.getStatus()),
                    Updates.set("description", requisition.getDescription()),
                    Updates.set("requirements", requisition.getRequirements()),
                    Updates.set("updatedAt", requisition.getUpdatedAt())
                )
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating job requisition: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteJobRequisition(String id) {
        try {
            collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting job requisition: " + e.getMessage());
            return false;
        }
    }

    public List<JobRequisition> searchJobRequisitions(String recruiterEmail, String searchTerm) {
        List<JobRequisition> requisitions = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(
            Filters.and(
                Filters.eq("recruiterEmail", recruiterEmail),
                Filters.or(
                    Filters.regex("title", searchTerm, "i"),
                    Filters.regex("company", searchTerm, "i"),
                    Filters.regex("location", searchTerm, "i"),
                    Filters.regex("description", searchTerm, "i")
                )
            )
        );

        for (Document doc : findIterable) {
            requisitions.add(documentToJobRequisition(doc));
        }
        return requisitions;
    }

    private JobRequisition documentToJobRequisition(Document doc) {
        JobRequisition requisition = new JobRequisition();
        requisition.setId(doc.getObjectId("_id").toString());
        requisition.setTitle(doc.getString("title"));
        requisition.setCompany(doc.getString("company"));
        requisition.setLocation(doc.getString("location"));
        requisition.setStatus(doc.getString("status"));
        requisition.setDescription(doc.getString("description"));
        requisition.setRequirements(doc.getList("requirements", String.class));
        requisition.setRecruiterEmail(doc.getString("recruiterEmail"));
        requisition.setCreatedAt(doc.getDate("createdAt"));
        requisition.setUpdatedAt(doc.getDate("updatedAt"));
        return requisition;
    }
}
