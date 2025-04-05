package com.jobportal.models;

import java.util.Date;

public class Application {
    private String id;
    private String jobId;
    private String userId;
    private Date applicationDate;
    private String status; // Pending, Accepted, Rejected
    private String resume;
    private String resumeId;
    private String coverLetter;

    // Constructor with resumeId
    public Application(String id, String jobId, String userId, Date applicationDate, String status, String resume, String resumeId, String coverLetter) {
        this.id = id;
        this.jobId = jobId;
        this.userId = userId;
        this.applicationDate = applicationDate;
        this.status = status;
        this.resume = resume;
        this.resumeId = resumeId;
        this.coverLetter = coverLetter;
    }

    // Constructor without resumeId (required signature)
    public Application(String id, String jobId, String userId, Date applicationDate, String status, String resume, String coverLetter) {
        this.id = id;
        this.jobId = jobId;
        this.userId = userId;
        this.applicationDate = applicationDate;
        this.status = status;
        this.resume = resume;
        this.coverLetter = coverLetter;
    }

    // Getters
    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public String getUserId() { return userId; }
    public Date getApplicationDate() { return applicationDate; }
    public String getStatus() { return status; }
    public String getResume() { return resume; }
    public String getResumeId() { return resumeId; }
    public String getCoverLetter() { return coverLetter; }

    // Setters
    public void setStatus(String status) { this.status = status; }
}
