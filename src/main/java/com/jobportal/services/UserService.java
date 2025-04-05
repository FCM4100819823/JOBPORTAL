package com.jobportal.services;

import com.jobportal.models.User;
import com.jobportal.utils.PasswordUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.jobportal.database.*;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final MongoCollection<Document> userCollection;

    public UserService() {
        MongoDatabase database = DatabaseConnection.getDatabase();
        if (database != null) {
            this.userCollection = database.getCollection("users");
        } else {
            this.userCollection = null;
            System.err.println("WARNING: Database connection failed. UserService will not function properly.");
        }
    }
    
    public boolean registerUser(User user) {
        try {
            if (userCollection == null) {
                System.err.println("Cannot register user: Database not connected");
                return false;
            }
            
            if (getUserByEmail(user.getEmail()) != null) {
                return false; // Email already exists
            }
            
            Document newUser = new Document("username", user.getUsername())
                    .append("email", user.getEmail())
                    .append("password", user.getPassword())
                    .append("fullName", user.getFullName())
                    .append("phone", user.getPhone())
                    .append("location", user.getLocation())
                    .append("role", user.getRole());
            userCollection.insertOne(newUser);
            return true;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByEmail(String email) {
        try {
            if (userCollection == null) {
                System.err.println("Cannot get user: Database not connected");
                return null;
            }

            Document doc = userCollection.find(new Document("email", email)).first();
            if (doc == null) {
                return null;
            }

            return new User(
                doc.getString("username"),
                doc.getString("email"),
                doc.getString("password"),
                doc.getString("fullName"),
                doc.getString("phone"),
                doc.getString("location"),
                doc.getString("role"),
                doc.getString("profilePicturePath") // Fixed: Added profilePicturePath
            );
        } catch (Exception e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public User loginUser(String email, String password) {
        try {
            if (userCollection == null) {
                System.err.println("Cannot login: Database not connected");
                return null;
            }
            
            Document doc = userCollection.find(eq("email", email)).first();
            
            if (doc != null) {
                String storedPassword = doc.getString("password");
                System.out.println("Checking password for: " + email);
                
                if (PasswordUtil.verifyPassword(password, storedPassword)) {
                    return new User(
                        doc.getString("username"),
                        doc.getString("email"), 
                        doc.getString("password"), 
                        doc.getString("fullName"),
                        doc.getString("phone"),
                        doc.getString("location"),
                        doc.getString("role"),
                        doc.getString("profilePicturePath")
                    );
                } else {
                    System.out.println("Password verification failed");
                }
            } else {
                System.out.println("User not found: " + email);
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateUser(User user) {
        try {
            if (userCollection == null) {
                System.err.println("Cannot update user: Database not connected");
                return false;
            }

            userCollection.updateOne(
                eq("email", user.getEmail()),
                new Document("$set", new Document("fullName", user.getFullName())
                        .append("phone", user.getPhone())
                        .append("location", user.getLocation()))
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserProfilePicture(User user) {
        try {
            if (userCollection == null) {
                System.err.println("Cannot update profile picture: Database not connected");
                return false;
            }

            userCollection.updateOne(
                eq("email", user.getEmail()),
                new Document("$set", new Document("profilePicturePath", user.getProfilePicturePath()))
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating profile picture: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(String email) {
        try {
            if (userCollection == null) {
                System.err.println("Cannot reset password: Database not connected");
                return false;
            }
            
            // Generate a temporary password
            String tempPassword = generateTempPassword();
            String hashedPassword = PasswordUtil.hashPassword(tempPassword);
            
            // Update the user's password in the database
            userCollection.updateOne(
                eq("email", email),
                new Document("$set", new Document("password", hashedPassword))
            );
            
            // In a real app, send an email with the temporary password
            System.out.println("Temporary password for " + email + ": " + tempPassword);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error resetting password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String generateTempPassword() {
        // Generate a random 8-character password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        if (userCollection == null) {
            System.err.println("Cannot retrieve users: Database not connected");
            return users;
        }

        try {
            for (Document doc : userCollection.find()) {
                users.add(doc.getString("email")); // Return user emails
            }
        } catch (Exception e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    public boolean deleteUser(String email) {
        if (userCollection == null) {
            System.err.println("Cannot delete user: Database not connected");
            return false;
        }

        try {
            userCollection.deleteOne(eq("email", email));
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}
