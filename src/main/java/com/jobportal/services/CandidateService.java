package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.Candidate;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class CandidateService {
    private final MongoCollection<Document> collection;

    public CandidateService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database != null) {
            this.collection = database.getCollection("candidates");
        } else {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    public List<Candidate> getCandidatesByRecruiter(String recruiterEmail) {
        List<Candidate> candidates = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(
            Filters.eq("recruiterEmail", recruiterEmail)
        );

        for (Document doc : findIterable) {
            candidates.add(documentToCandidate(doc));
        }
        return candidates;
    }

    public Candidate getCandidateById(String id) {
        Document doc = collection.find(
            Filters.eq("_id", new ObjectId(id))
        ).first();
        return doc != null ? documentToCandidate(doc) : null;
    }

    public boolean addCandidate(Candidate candidate) {
        try {
            Document doc = new Document()
                .append("name", candidate.getName())
                .append("email", candidate.getEmail())
                .append("phone", candidate.getPhone())
                .append("skills", candidate.getSkills())
                .append("yearsOfExperience", candidate.getYearsOfExperience())
                .append("location", candidate.getLocation())
                .append("status", candidate.getStatus())
                .append("recruiterEmail", candidate.getRecruiterEmail())
                .append("resumeUrl", candidate.getResumeUrl())
                .append("notes", candidate.getNotes())
                .append("createdAt", candidate.getCreatedAt())
                .append("updatedAt", candidate.getUpdatedAt());

            collection.insertOne(doc);
            candidate.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error adding candidate: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCandidate(Candidate candidate) {
        try {
            collection.updateOne(
                Filters.eq("_id", new ObjectId(candidate.getId())),
                Updates.combine(
                    Updates.set("name", candidate.getName()),
                    Updates.set("email", candidate.getEmail()),
                    Updates.set("phone", candidate.getPhone()),
                    Updates.set("skills", candidate.getSkills()),
                    Updates.set("yearsOfExperience", candidate.getYearsOfExperience()),
                    Updates.set("location", candidate.getLocation()),
                    Updates.set("status", candidate.getStatus()),
                    Updates.set("resumeUrl", candidate.getResumeUrl()),
                    Updates.set("notes", candidate.getNotes()),
                    Updates.set("updatedAt", candidate.getUpdatedAt())
                )
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating candidate: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCandidate(String id) {
        try {
            collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting candidate: " + e.getMessage());
            return false;
        }
    }

    public List<Candidate> searchCandidates(String recruiterEmail, String searchTerm) {
        List<Candidate> candidates = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(
            Filters.and(
                Filters.eq("recruiterEmail", recruiterEmail),
                Filters.or(
                    Filters.regex("name", searchTerm, "i"),
                    Filters.regex("email", searchTerm, "i"),
                    Filters.regex("skills", searchTerm, "i"),
                    Filters.regex("location", searchTerm, "i")
                )
            )
        );

        for (Document doc : findIterable) {
            candidates.add(documentToCandidate(doc));
        }
        return candidates;
    }

    private Candidate documentToCandidate(Document doc) {
        Candidate candidate = new Candidate();
        candidate.setId(doc.getObjectId("_id").toString());
        candidate.setName(doc.getString("name"));
        candidate.setEmail(doc.getString("email"));
        candidate.setPhone(doc.getString("phone"));
        candidate.setSkills(doc.getList("skills", String.class));
        candidate.setYearsOfExperience(doc.getInteger("yearsOfExperience"));
        candidate.setLocation(doc.getString("location"));
        candidate.setStatus(doc.getString("status"));
        candidate.setRecruiterEmail(doc.getString("recruiterEmail"));
        candidate.setResumeUrl(doc.getString("resumeUrl"));
        candidate.setNotes(doc.getString("notes"));
        candidate.setCreatedAt(doc.getDate("createdAt"));
        candidate.setUpdatedAt(doc.getDate("updatedAt"));
        return candidate;
    }
}