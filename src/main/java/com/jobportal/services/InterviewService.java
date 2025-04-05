package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.Interview;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InterviewService {
    private final MongoCollection<Document> collection;

    public InterviewService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database != null) {
            this.collection = database.getCollection("interviews");
        } else {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    public List<Interview> getInterviewsByRecruiter(String recruiterEmail) {
        if (recruiterEmail == null || recruiterEmail.isEmpty()) {
            throw new IllegalArgumentException("Recruiter email is required");
        }

        List<Interview> interviews = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(
            Filters.eq("recruiterEmail", recruiterEmail)
        );

        for (Document doc : findIterable) {
            interviews.add(documentToInterview(doc));
        }
        return interviews;
    }

    public boolean scheduleInterview(Interview interview) {
        if (interview == null) {
            throw new IllegalArgumentException("Interview cannot be null");
        }

        try {
            Document doc = new Document()
                .append("candidateName", interview.getCandidateName())
                .append("candidateEmail", interview.getCandidateEmail())
                .append("position", interview.getPosition())
                .append("dateTime", Date.from(interview.getDateTime().atZone(ZoneId.systemDefault()).toInstant()))
                .append("status", interview.getStatus())
                .append("type", interview.getType())
                .append("recruiterEmail", interview.getRecruiterEmail())
                .append("notes", interview.getNotes());

            collection.insertOne(doc);
            interview.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error scheduling interview: " + e.getMessage());
            return false;
        }
    }

    public boolean rescheduleInterview(String interviewId, LocalDateTime newDateTime) {
        if (interviewId == null || interviewId.isEmpty() || newDateTime == null) {
            throw new IllegalArgumentException("Interview ID and new date/time are required");
        }

        try {
            collection.updateOne(
                Filters.eq("_id", new ObjectId(interviewId)),
                Updates.set("dateTime", Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant()))
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error rescheduling interview: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelInterview(String interviewId) {
        if (interviewId == null || interviewId.isEmpty()) {
            throw new IllegalArgumentException("Interview ID is required");
        }

        try {
            collection.deleteOne(Filters.eq("_id", new ObjectId(interviewId)));
            return true;
        } catch (Exception e) {
            System.err.println("Error canceling interview: " + e.getMessage());
            return false;
        }
    }

    public boolean updateInterviewStatus(String interviewId, String newStatus) {
        if (interviewId == null || interviewId.isEmpty() || newStatus == null || newStatus.isEmpty()) {
            throw new IllegalArgumentException("Interview ID and new status are required");
        }

        try {
            collection.updateOne(
                Filters.eq("_id", new ObjectId(interviewId)),
                Updates.set("status", newStatus)
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating interview status: " + e.getMessage());
            return false;
        }
    }

    private Interview documentToInterview(Document doc) {
        Interview interview = new Interview(
            doc.getObjectId("_id").toString(),
            doc.getString("candidateName"),
            doc.getString("candidateEmail"),
            doc.getString("position"),
            doc.getDate("dateTime").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            doc.getString("status"),
            doc.getString("type"),
            doc.getString("recruiterEmail")
        );
        interview.setNotes(doc.getString("notes"));
        return interview;
    }
} 