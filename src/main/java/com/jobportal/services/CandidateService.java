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
import java.util.Date;

public class CandidateService {
    private final MongoCollection<Document> collection;

    public CandidateService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database == null) {
            throw new RuntimeException("Failed to connect to database");
        }
        this.collection = database.getCollection("candidates");
    }

    public List<Candidate> getCandidatesByRecruiter(String recruiterEmail) {
        if (recruiterEmail == null || recruiterEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Recruiter email cannot be null or empty");
        }

        List<Candidate> candidates = new ArrayList<>();
        try {
            FindIterable<Document> findIterable = collection.find(
                Filters.eq("recruiterEmail", recruiterEmail.trim().toLowerCase())
            );

            for (Document doc : findIterable) {
                candidates.add(documentToCandidate(doc));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving candidates for recruiter " + recruiterEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
        return candidates;
    }

    public Candidate getCandidateById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Candidate ID cannot be null or empty");
        }

        try {
            Document doc = collection.find(
                Filters.eq("_id", new ObjectId(id.trim()))
            ).first();
            return doc != null ? documentToCandidate(doc) : null;
        } catch (Exception e) {
            System.err.println("Error retrieving candidate with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean addCandidate(Candidate candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }

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
                .append("createdAt", new Date())
                .append("updatedAt", new Date());

            collection.insertOne(doc);
            candidate.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error adding candidate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCandidate(Candidate candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }
        if (candidate.getId() == null || candidate.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Candidate ID cannot be null or empty");
        }

        try {
            collection.updateOne(
                Filters.eq("_id", new ObjectId(candidate.getId().trim())),
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
                    Updates.set("updatedAt", new Date())
                )
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating candidate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCandidate(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Candidate ID cannot be null or empty");
        }

        try {
            collection.deleteOne(Filters.eq("_id", new ObjectId(id.trim())));
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting candidate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Candidate> searchCandidates(String recruiterEmail, String searchTerm) {
        if (recruiterEmail == null || recruiterEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Recruiter email cannot be null or empty");
        }
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be null or empty");
        }

        List<Candidate> candidates = new ArrayList<>();
        try {
            FindIterable<Document> findIterable = collection.find(
                Filters.and(
                    Filters.eq("recruiterEmail", recruiterEmail.trim().toLowerCase()),
                    Filters.or(
                        Filters.regex("name", searchTerm.trim(), "i"),
                        Filters.regex("email", searchTerm.trim(), "i"),
                        Filters.regex("skills", searchTerm.trim(), "i"),
                        Filters.regex("location", searchTerm.trim(), "i")
                    )
                )
            );

            for (Document doc : findIterable) {
                candidates.add(documentToCandidate(doc));
            }
        } catch (Exception e) {
            System.err.println("Error searching candidates: " + e.getMessage());
            e.printStackTrace();
        }
        return candidates;
    }

    private Candidate documentToCandidate(Document doc) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        try {
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
            candidate.setUpdatedAt(doc.getDate("updatedAt"));
            return candidate;
        } catch (Exception e) {
            System.err.println("Error converting document to candidate: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to convert document to candidate", e);
        }
    }
}