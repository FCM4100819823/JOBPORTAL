package com.jobportal.services;

import com.jobportal.models.Client;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private final MongoCollection<Document> collection;

    public ClientService() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("jobportal");
        this.collection = database.getCollection("clients");
    }

    public List<Client> getClientsByRecruiter(String recruiterEmail) {
        List<Client> clients = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(
            Filters.eq("recruiterEmail", recruiterEmail)
        );

        for (Document doc : findIterable) {
            clients.add(documentToClient(doc));
        }
        return clients;
    }

    public Client getClientById(String id) {
        Document doc = collection.find(
            Filters.eq("_id", new ObjectId(id))
        ).first();
        return doc != null ? documentToClient(doc) : null;
    }

    public void addClient(Client client) {
        Document doc = new Document()
            .append("name", client.getName())
            .append("industry", client.getIndustry())
            .append("location", client.getLocation())
            .append("recruiterEmail", client.getRecruiterEmail())
            .append("contactPerson", client.getContactPerson())
            .append("contactEmail", client.getContactEmail())
            .append("phone", client.getPhone())
            .append("createdAt", client.getCreatedAt())
            .append("updatedAt", client.getUpdatedAt());

        collection.insertOne(doc);
        client.setId(doc.getObjectId("_id").toString());
    }

    public void updateClient(Client client) {
        collection.updateOne(
            Filters.eq("_id", new ObjectId(client.getId())),
            Updates.combine(
                Updates.set("name", client.getName()),
                Updates.set("industry", client.getIndustry()),
                Updates.set("location", client.getLocation()),
                Updates.set("contactPerson", client.getContactPerson()),
                Updates.set("contactEmail", client.getContactEmail()),
                Updates.set("phone", client.getPhone()),
                Updates.set("updatedAt", client.getUpdatedAt())
            )
        );
    }

    public void deleteClient(String id) {
        collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
    }

    public List<Client> searchClients(String recruiterEmail, String searchTerm) {
        List<Client> clients = new ArrayList<>();
        FindIterable<Document> findIterable = collection.find(
            Filters.and(
                Filters.eq("recruiterEmail", recruiterEmail),
                Filters.or(
                    Filters.regex("name", searchTerm, "i"),
                    Filters.regex("industry", searchTerm, "i"),
                    Filters.regex("location", searchTerm, "i"),
                    Filters.regex("contactPerson", searchTerm, "i")
                )
            )
        );

        for (Document doc : findIterable) {
            clients.add(documentToClient(doc));
        }
        return clients;
    }

    private Client documentToClient(Document doc) {
        Client client = new Client();
        client.setId(doc.getObjectId("_id").toString());
        client.setName(doc.getString("name"));
        client.setIndustry(doc.getString("industry"));
        client.setLocation(doc.getString("location"));
        client.setRecruiterEmail(doc.getString("recruiterEmail"));
        client.setContactPerson(doc.getString("contactPerson"));
        client.setContactEmail(doc.getString("contactEmail"));
        client.setPhone(doc.getString("phone"));
        client.setCreatedAt(doc.getDate("createdAt"));
        client.setUpdatedAt(doc.getDate("updatedAt"));
        return client;
    }
}
