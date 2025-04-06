package com.jobportal.models;

import java.util.Objects;

public class User {
    private final String username;
    private final String email;
    private String password; // Mutable for password changes
    private String fullName;
    private String phone;
    private String location;
    private final String role;
    private String profilePicturePath;

    public User(String username, String email, String password, String fullName, String phone,
                String location, String role, String profilePicturePath) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }

        this.username = username.trim();
        this.email = email.trim().toLowerCase();
        this.password = password;
        this.fullName = fullName != null ? fullName.trim() : "";
        this.phone = phone != null ? phone.trim() : "";
        this.location = location != null ? location.trim() : "";
        this.role = role.trim().toLowerCase();
        this.profilePicturePath = profilePicturePath;
    }

    // Getters
    public String getUsername() { 
        return username; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public String getFullName() { 
        return fullName; 
    }
    
    public String getPhone() { 
        return phone; 
    }
    
    public String getLocation() { 
        return location; 
    }
    
    public String getRole() { 
        return role; 
    }
    
    public String getProfilePicturePath() { 
        return profilePicturePath; 
    }

    // Setters with validation
    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = newPassword;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : "";
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : "";
    }

    public void setLocation(String location) {
        this.location = location != null ? location.trim() : "";
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
