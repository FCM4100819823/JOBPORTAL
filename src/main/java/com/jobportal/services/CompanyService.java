package com.jobportal.services;

import com.jobportal.database.DatabaseConnection;
import com.jobportal.models.Company;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CompanyService {
    private final MongoCollection<Document> collection;

    public CompanyService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database != null) {
            this.collection = database.getCollection("companies");
        } else {
            throw new RuntimeException("Failed to connect to database");
        }
    }

    public Company getCompanyByEmployerEmail(String employerEmail) {
        if (employerEmail == null || employerEmail.isEmpty()) {
            throw new IllegalArgumentException("Employer email is required");
        }

        Document doc = collection.find(
            Filters.eq("employerEmail", employerEmail)
        ).first();
        
        return doc != null ? documentToCompany(doc) : null;
    }

    public boolean addCompany(Company company) {
        try {
            Document doc = new Document()
                .append("name", company.getName())
                .append("industry", company.getIndustry())
                .append("description", company.getDescription())
                .append("website", company.getWebsite())
                .append("location", company.getLocation())
                .append("size", company.getSize())
                .append("foundedYear", company.getFoundedYear())
                .append("logoUrl", company.getLogoUrl())
                .append("employerEmail", company.getEmployerEmail())
                .append("contactPerson", company.getContactPerson())
                .append("contactEmail", company.getContactEmail())
                .append("phone", company.getPhone())
                .append("createdAt", company.getCreatedAt())
                .append("updatedAt", company.getUpdatedAt());

            collection.insertOne(doc);
            company.setId(doc.getObjectId("_id").toString());
            return true;
        } catch (Exception e) {
            System.err.println("Error adding company: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCompany(Company company) {
        try {
            company.setUpdatedAt(new Date());
            
            collection.updateOne(
                Filters.eq("_id", new ObjectId(company.getId())),
                Updates.combine(
                    Updates.set("name", company.getName()),
                    Updates.set("industry", company.getIndustry()),
                    Updates.set("description", company.getDescription()),
                    Updates.set("website", company.getWebsite()),
                    Updates.set("location", company.getLocation()),
                    Updates.set("size", company.getSize()),
                    Updates.set("foundedYear", company.getFoundedYear()),
                    Updates.set("logoUrl", company.getLogoUrl()),
                    Updates.set("contactPerson", company.getContactPerson()),
                    Updates.set("contactEmail", company.getContactEmail()),
                    Updates.set("phone", company.getPhone()),
                    Updates.set("updatedAt", company.getUpdatedAt())
                )
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating company: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCompany(String id) {
        try {
            collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting company: " + e.getMessage());
            return false;
        }
    }

    private Company documentToCompany(Document doc) {
        Company company = new Company();
        company.setId(doc.getObjectId("_id").toString());
        company.setName(doc.getString("name"));
        company.setIndustry(doc.getString("industry"));
        company.setDescription(doc.getString("description"));
        company.setWebsite(doc.getString("website"));
        company.setLocation(doc.getString("location"));
        company.setSize(doc.getString("size"));
        company.setFoundedYear(doc.getString("foundedYear"));
        company.setLogoUrl(doc.getString("logoUrl"));
        company.setEmployerEmail(doc.getString("employerEmail"));
        company.setContactPerson(doc.getString("contactPerson"));
        company.setContactEmail(doc.getString("contactEmail"));
        company.setPhone(doc.getString("phone"));
        company.setCreatedAt(doc.getDate("createdAt"));
        company.setUpdatedAt(doc.getDate("updatedAt"));
        return company;
    }
} 