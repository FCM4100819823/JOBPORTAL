package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.Job;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

public class JobService {
    private final MongoCollection<Document> jobCollection;
    private final MongoCollection<Document> companyCollection;

    public JobService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database != null) {
            this.jobCollection = database.getCollection("jobs");
            this.companyCollection = database.getCollection("companies"); // Added companies collection
        } else {
            this.jobCollection = null;
            this.companyCollection = null;
            System.err.println("WARNING: Database connection failed. JobService will not function properly.");
        }
    }

    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        
        // Return some sample jobs if database is not connected
        if (jobCollection == null) {
            jobs.add(new Job("Software Engineer", "TechCorp", "San Francisco", "Develop and maintain software applications.", "employer@example.com"));
            jobs.add(new Job("Data Scientist", "DataInsights", "New York", "Analyze large datasets and build predictive models.", "employer@example.com"));
            jobs.add(new Job("Product Manager", "InnovateTech", "Seattle", "Lead product development and strategy.", "employer@example.com"));
            return jobs;
        }
        
        try {
            for (Document doc : jobCollection.find()) {
                jobs.add(documentToJob(doc));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving jobs: " + e.getMessage());
            // Add default jobs if there's an error
            jobs.add(new Job("Software Engineer", "TechCorp", "San Francisco", "Develop and maintain software applications.", "employer@example.com"));
            jobs.add(new Job("Data Scientist", "DataInsights", "New York", "Analyze large datasets and build predictive models.", "employer@example.com"));
        }
        return jobs;
    }

    public boolean addJob(Job job) {
        if (jobCollection == null) {
            System.err.println("Cannot add job: Database not connected");
            return false;
        }
        
        try {
            Document newJob = new Document("title", job.getTitle())
                    .append("company", job.getCompany())
                    .append("location", job.getLocation())
                    .append("description", job.getDescription())
                    .append("employerEmail", job.getEmployerEmail());
            jobCollection.insertOne(newJob);
            return true;
        } catch (Exception e) {
            System.err.println("Error adding job: " + e.getMessage());
            return false;
        }
    }

    public List<Job> searchJobsByTitle(String title) {
        return searchJobs("title", title);
    }

    public List<Job> searchJobsByLocation(String location) {
        return searchJobs("location", location);
    }

    public List<Job> searchJobsByCompany(String company) {
        return searchJobs("company", company);
    }

    private List<Job> searchJobs(String field, String query) {
        List<Job> jobs = new ArrayList<>();
        if (jobCollection == null) {
            System.err.println("Cannot search jobs: Database not connected");
            return jobs;
        }

        try {
            for (Document doc : jobCollection.find(regex(field, query, "i"))) { // Case-insensitive search
                jobs.add(documentToJob(doc));
            }
        } catch (Exception e) {
            System.err.println("Error searching jobs: " + e.getMessage());
        }
        return jobs;
    }

    public List<Job> getJobsByEmployer(String employerEmail) {
        List<Job> jobs = new ArrayList<>();
        if (jobCollection == null) {
            System.err.println("Cannot retrieve jobs: Database not connected");
            return jobs;
        }

        try {
            for (Document doc : jobCollection.find(eq("employerEmail", employerEmail))) {
                jobs.add(documentToJob(doc));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving jobs: " + e.getMessage());
        }
        return jobs;
    }

    public List<Job> getJobsByPoster(String posterEmail) {
        List<Job> jobs = new ArrayList<>();
        if (jobCollection == null) {
            System.err.println("Cannot retrieve jobs: Database not connected");
            return jobs;
        }

        try {
            for (Document doc : jobCollection.find(eq("employerEmail", posterEmail))) {
                jobs.add(documentToJob(doc));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving jobs for poster: " + e.getMessage());
        }
        return jobs;
    }

    public boolean deleteJobByTitle(String title, String employerEmail) {
        if (jobCollection == null) {
            System.err.println("Cannot delete job: Database not connected");
            return false;
        }

        try {
            jobCollection.deleteOne(and(eq("title", title), eq("employerEmail", employerEmail)));
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting job: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Long> getJobPostingsByEmployer() {
        Map<String, Long> jobCounts = new HashMap<>();
        if (jobCollection == null) {
            System.err.println("Cannot retrieve job postings: Database not connected");
            return jobCounts;
        }

        try {
            for (Document doc : jobCollection.aggregate(Arrays.asList(
                    new Document("$group", new Document("_id", "$employerEmail").append("count", new Document("$sum", 1)))
            ))) {
                jobCounts.put(doc.getString("_id"), doc.getLong("count"));
            }
        } catch (Exception e) {
            System.err.println("Error retrieving job postings by employer: " + e.getMessage());
        }
        return jobCounts;
    }

    public Document getCompanyByEmail(String email) {
        if (companyCollection == null) {
            System.err.println("Cannot retrieve company: Database not connected");
            return null;
        }

        try {
            return companyCollection.find(eq("email", email)).first();
        } catch (Exception e) {
            System.err.println("Error retrieving company: " + e.getMessage());
            return null;
        }
    }

    public String getEmployerEmailByJobTitle(String jobTitle) {
        if (jobCollection == null) {
            System.err.println("Cannot retrieve employer email: Database not connected");
            return null;
        }

        try {
            Document doc = jobCollection.find(eq("title", jobTitle)).first();
            return doc != null ? doc.getString("employerEmail") : null;
        } catch (Exception e) {
            System.err.println("Error retrieving employer email: " + e.getMessage());
            return null;
        }
    }

    private Job documentToJob(Document doc) {
        return new Job(
            doc.getString("title"),
            doc.getString("company"),
            doc.getString("location"),
            doc.getString("description"),
            doc.getString("employerEmail")
        );
    }
}
