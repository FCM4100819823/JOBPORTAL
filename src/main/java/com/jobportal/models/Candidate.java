package com.jobportal.models;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Objects;

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
    private final Date createdAt;
    private Date updatedAt;

    // Default constructor
    public Candidate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.skills = new ArrayList<>();
        this.status = "Active";
    }

    // Constructor for creating a new candidate
    public Candidate(String name, String email, int yearsOfExperience, String location, String recruiterEmail) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }
        if (recruiterEmail == null || recruiterEmail.trim().isEmpty() || !recruiterEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid recruiter email format");
        }

        this.name = name.trim();
        this.email = email.trim().toLowerCase();
        this.yearsOfExperience = yearsOfExperience;
        this.location = location != null ? location.trim() : "";
        this.recruiterEmail = recruiterEmail.trim().toLowerCase();
        this.status = "Active";
        this.skills = new ArrayList<>();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters with validation
    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        if (id != null && id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty if provided");
        }
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name.trim(); 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.trim().toLowerCase(); 
    }

    public String getPhone() { 
        return phone; 
    }
    
    public void setPhone(String phone) { 
        this.phone = phone != null ? phone.trim() : ""; 
    }

    public List<String> getSkills() { 
        return new ArrayList<>(skills); // Return a copy to prevent external modification
    }
    
    public void setSkills(List<String> skills) { 
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>(); 
    }

    public int getYearsOfExperience() { 
        return yearsOfExperience; 
    }
    
    public void setYearsOfExperience(int yearsOfExperience) { 
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of experience cannot be negative");
        }
        this.yearsOfExperience = yearsOfExperience; 
    }

    public String getLocation() { 
        return location; 
    }
    
    public void setLocation(String location) { 
        this.location = location != null ? location.trim() : ""; 
    }

    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        this.status = status.trim(); 
    }

    public String getRecruiterEmail() { 
        return recruiterEmail; 
    }
    
    public void setRecruiterEmail(String recruiterEmail) { 
        if (recruiterEmail == null || recruiterEmail.trim().isEmpty() || !recruiterEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid recruiter email format");
        }
        this.recruiterEmail = recruiterEmail.trim().toLowerCase(); 
    }

    public String getResumeUrl() { 
        return resumeUrl; 
    }
    
    public void setResumeUrl(String resumeUrl) { 
        this.resumeUrl = resumeUrl; 
    }

    public String getNotes() { 
        return notes; 
    }
    
    public void setNotes(String notes) { 
        this.notes = notes != null ? notes.trim() : ""; 
    }

    public Date getCreatedAt() { 
        return (Date) createdAt.clone(); 
    }

    public Date getUpdatedAt() { 
        return updatedAt != null ? (Date) updatedAt.clone() : null; 
    }
    
    public void setUpdatedAt(Date updatedAt) { 
        this.updatedAt = updatedAt != null ? (Date) updatedAt.clone() : new Date(); 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Candidate candidate = (Candidate) o;
        return Objects.equals(email, candidate.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", recruiterEmail='" + recruiterEmail + '\'' +
                '}';
    }
}