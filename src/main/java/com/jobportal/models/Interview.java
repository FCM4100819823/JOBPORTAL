package com.jobportal.models;

import java.time.LocalDateTime;

public class Interview {
    private String id;
    private String candidateName;
    private String candidateEmail;
    private String position;
    private LocalDateTime dateTime;
    private String status;
    private String type;
    private String recruiterEmail;
    private String notes;

    public Interview(String id, String candidateName, String candidateEmail, String position, 
                    LocalDateTime dateTime, String status, String type, String recruiterEmail) {
        this.id = id;
        this.candidateName = candidateName;
        this.candidateEmail = candidateEmail;
        this.position = position;
        this.dateTime = dateTime;
        this.status = status;
        this.type = type;
        this.recruiterEmail = recruiterEmail;
    }

    // Getters
    public String getId() { return id; }
    public String getCandidateName() { return candidateName; }
    public String getCandidateEmail() { return candidateEmail; }
    public String getPosition() { return position; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public String getRecruiterEmail() { return recruiterEmail; }
    public String getNotes() { return notes; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public void setCandidateEmail(String candidateEmail) { this.candidateEmail = candidateEmail; }
    public void setPosition(String position) { this.position = position; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public void setStatus(String status) { this.status = status; }
    public void setType(String type) { this.type = type; }
    public void setRecruiterEmail(String recruiterEmail) { this.recruiterEmail = recruiterEmail; }
    public void setNotes(String notes) { this.notes = notes; }
} 