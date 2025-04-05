package com.jobportal.models;

public class User {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String location;
    private String role;
    private String profilePicturePath;

    public User(String username, String email, String password, String fullName, String phone,
                String location, String role, String profilePicturePath) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.location = location;
        this.role = role;
        this.profilePicturePath = profilePicturePath;
    }
    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getLocation() { return location; }
    public String getRole() { return role; }
    public String getProfilePicturePath() { return profilePicturePath; }
    // Setters
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }
}
