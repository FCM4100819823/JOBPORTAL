package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.Application;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ApplicationService {
    private final MongoCollection<Document> applicationCollection;
    private final MongoCollection<Document> resumeCollection;

    public ApplicationService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database != null) {
            this.applicationCollection = database.getCollection("applications");
            this.resumeCollection = database.getCollection("resumes"); // Added resumes collection
        } else {
            this.applicationCollection = null;
            this.resumeCollection = null;
            System.err.println("WARNING: Database connection failed. ApplicationService will not function properly.");
        }
    }

    public boolean submitApplication(Application application) {
        if (applicationCollection == null) {
            System.err.println("Cannot submit application: Database not connected");
            return false;
        }

        // Check if application already exists
        try {
            Document existingApp = applicationCollection.find(
                new Document("jobId", application.getJobId())
                    .append("userId", application.getUserId())
            ).first();
            
            if (existingApp != null) {
                System.err.println("User has already applied for this job");
                return false;
            }
            
            Document newApplication = new Document("jobId", application.getJobId())
                    .append("userId", application.getUserId())
                    .append("applicationDate", application.getApplicationDate())
                    .append("status", application.getStatus())
                    .append("resume", application.getResume())
                    .append("coverLetter", application.getCoverLetter());
            applicationCollection.insertOne(newApplication);
            return true;
        } catch (Exception e) {
            System.err.println("Error submitting application: " + e.getMessage());
            return false;
        }
    }

    public List<Application> getApplicationsByUserId(String userId) {
        List<Application> applications = new ArrayList<>();
        if (applicationCollection == null) {
            System.err.println("Cannot retrieve applications: Database not connected");
            return applications;
        }

        try {
            for (Document doc : applicationCollection.find(eq("userId", userId))) {
                applications.add(new Application(
                        doc.getObjectId("_id").toString(),
                        doc.getString("jobId"),
                        doc.getString("userId"),
                        doc.getDate("applicationDate"),
                        doc.getString("status"),
                        doc.getString("resume"),
                        doc.getString("coverLetter") // Ensure this matches the constructor signature
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving applications: " + e.getMessage());
        }
        return applications;
    }

    public List<Application> getApplicationsByJobId(String jobId) {
        List<Application> applications = new ArrayList<>();
        if (applicationCollection == null) {
            System.err.println("Cannot retrieve applications: Database not connected");
            return applications;
        }

        try {
            for (Document doc : applicationCollection.find(eq("jobId", jobId))) {
                applications.add(new Application(
                        doc.getObjectId("_id").toString(),
                        doc.getString("jobId"),
                        doc.getString("userId"),
                        doc.getDate("applicationDate"),
                        doc.getString("status"),
                        doc.getString("resume"),
                        doc.getString("coverLetter") // Ensure this matches the constructor signature
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving applications: " + e.getMessage());
        }
        return applications;
    }

    public Application getApplicationById(String applicationId) {
        if (applicationCollection == null) {
            System.err.println("Cannot retrieve application: Database not connected");
            return null;
        }

        try {
            Document doc = applicationCollection.find(eq("_id", new ObjectId(applicationId))).first();
            if (doc != null) {
                return new Application(
                    doc.getObjectId("_id").toString(),
                    doc.getString("jobId"),
                    doc.getString("userId"),
                    doc.getDate("applicationDate"),
                    doc.getString("status"),
                    doc.getString("resume"),
                    doc.getString("coverLetter") // Ensure this matches the constructor signature
                );
            }
        } catch (Exception e) {
            System.err.println("Error retrieving application: " + e.getMessage());
        }
        return null;
    }

    public boolean updateApplicationStatus(String applicationId, String status) {
        if (applicationCollection == null) {
            System.err.println("Cannot update application status: Database not connected");
            return false;
        }

        try {
            ObjectId objectId = new ObjectId(applicationId); // Fixed: Validate ObjectId format
            Document application = applicationCollection.find(eq("_id", objectId)).first();
            if (application == null) {
                System.err.println("Application not found: " + applicationId);
                return false;
            }

            applicationCollection.updateOne(eq("_id", objectId), new Document("$set", new Document("status", status)));
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid application ID format: " + applicationId);
            return false;
        } catch (Exception e) {
            System.err.println("Error updating application status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteApplication(String applicationId) {
        if (applicationCollection == null) {
            System.err.println("Cannot delete application: Database not connected");
            return false;
        }

        try {
            applicationCollection.deleteOne(eq("_id", new ObjectId(applicationId)));
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting application: " + e.getMessage());
            return false;
        }
    }

    public List<Application> getAllApplications() {
        List<Application> applications = new ArrayList<>();
        if (applicationCollection == null) {
            System.err.println("Cannot retrieve applications: Database not connected");
            return applications;
        }

        try {
            for (Document doc : applicationCollection.find()) {
                applications.add(new Application(
                        doc.getObjectId("_id").toString(),
                        doc.getString("jobId"),
                        doc.getString("userId"),
                        doc.getDate("applicationDate"),
                        doc.getString("status"),
                        doc.getString("resume"),
                        doc.getString("coverLetter") // Ensure this matches the constructor signature
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving applications: " + e.getMessage());
        }
        return applications;
    }

    public List<Application> getApplicationsByStatus(String status) {
        List<Application> applications = new ArrayList<>();
        if (applicationCollection == null) {
            System.err.println("Cannot retrieve applications: Database not connected");
            return applications;
        }

        try {
            for (Document doc : applicationCollection.find(eq("status", status))) {
                applications.add(new Application(
                        doc.getObjectId("_id").toString(),
                        doc.getString("jobId"),
                        doc.getString("userId"),
                        doc.getDate("applicationDate"),
                        doc.getString("status"),
                        doc.getString("resume"),
                        doc.getString("coverLetter") // Ensure this matches the constructor signature
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving applications by status: " + e.getMessage());
        }
        return applications;
    }

    public List<Application> getApplicationsByJobSeeker(String jobSeekerEmail) {
        List<Application> applications = new ArrayList<>();
        if (applicationCollection == null) {
            System.err.println("Cannot retrieve applications: Database not connected");
            return applications;
        }

        try {
            for (Document doc : applicationCollection.find(eq("userId", jobSeekerEmail))) {
                applications.add(new Application(
                        doc.getObjectId("_id").toString(),
                        doc.getString("jobId"),
                        doc.getString("userId"),
                        doc.getDate("applicationDate"),
                        doc.getString("status"),
                        doc.getString("resume"),
                        doc.getString("coverLetter") // Ensure this matches the constructor signature
                ));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving applications for job seeker: " + e.getMessage());
        }
        return applications;
    }

    public Document getResumeById(String resumeId) {
        if (resumeCollection == null) {
            System.err.println("Cannot retrieve resume: Database not connected");
            return null;
        }

        try {
            return resumeCollection.find(eq("_id", new ObjectId(resumeId))).first();
        } catch (Exception e) {
            System.err.println("Error retrieving resume: " + e.getMessage());
            return null;
        }
    }
}
