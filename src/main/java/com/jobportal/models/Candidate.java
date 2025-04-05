package com.jobportal.models;

import java.util.List;
import java.util.Date;

public class Candidate {
    private String id;
    private String name;
    private String email;
    private String phone;
    private List<String> skills;
    private int yearsOfExperience;
    private String location;
    private String status;
    private String recruiterEmail;
    private String resumeUrl;
    private String notes;
    private Date createdAt;
    private Date updatedAt;

    // Default constructor
    public Candidate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Constructor for creating a new candidate
    public Candidate(String name, String email, int yearsOfExperience, String location, String recruiterEmail) {
        this.name = name;
        this.email = email;
        this.yearsOfExperience = yearsOfExperience;
        this.location = location;
        this.recruiterEmail = recruiterEmail;
        this.status = "Active";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRecruiterEmail() { return recruiterEmail; }
    public void setRecruiterEmail(String recruiterEmail) { this.recruiterEmail = recruiterEmail; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}