package com.jobportal.models;

public class Job {
    private String title;
    private String company;
    private String location;
    private String description;
    private String employerEmail;
    private String companyId;

    public Job(String title, String company, String location, String description) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
    }
    
    public Job(String title, String company, String location, String description, String employerEmail) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.employerEmail = employerEmail;
    }

    public Job(String title, String company, String location, String description, String employerEmail, String companyId) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.description = description;
        this.employerEmail = employerEmail;
        this.companyId = companyId;
    }

    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getEmployerEmail() { return employerEmail; }
    public String getCompanyId() { return companyId; }
}
