package com.jobportal.utils;

import com.jobportal.models.User;

/**
 * Singleton class to manage user session across the application.
 * Thread-safe implementation using double-checked locking.
 */
public class SessionManager {
    private static volatile SessionManager instance;
    private volatile User currentUser;

    private SessionManager() {
        // Private constructor to enforce singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                    System.out.println("SessionManager instance created");
                }
            }
        }
        return instance;
    }

    public void setCurrentUser(User user) {
        if (user == null) {
            System.err.println("Warning: Attempt to set null user in session");
            return;
        }
        synchronized (this) {
            this.currentUser = user;
            System.out.println("Current user set to: " + user.getEmail());
        }
    }

    public User getCurrentUser() {
        synchronized (this) {
            if (currentUser == null) {
                System.err.println("Warning: Attempt to get current user when no user is logged in");
            }
            return currentUser;
        }
    }

    public void clearSession() {
        synchronized (this) {
            if (currentUser != null) {
                System.out.println("Clearing session for user: " + currentUser.getEmail());
            }
            this.currentUser = null;
        }
    }

    public boolean isLoggedIn() {
        synchronized (this) {
            return currentUser != null;
        }
    }

    public String getUserRole() {
        synchronized (this) {
            if (currentUser == null) {
                System.err.println("Warning: Attempt to get user role when no user is logged in");
                return null;
            }
            return currentUser.getRole();
        }
    }
}
