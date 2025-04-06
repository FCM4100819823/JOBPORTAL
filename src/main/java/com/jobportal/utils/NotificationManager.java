package com.jobportal.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class NotificationManager {
    private NotificationManager() {
        // Private constructor to prevent instantiation
    }

    public static void showNotification(String title, String message) {
        if (title == null || message == null) {
            System.err.println("Warning: Null parameters passed to showNotification");
            return;
        }

        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing notification: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void showError(String title, String message) {
        if (title == null || message == null) {
            System.err.println("Warning: Null parameters passed to showError");
            return;
        }

        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing error notification: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void showWarning(String title, String message) {
        if (title == null || message == null) {
            System.err.println("Warning: Null parameters passed to showWarning");
            return;
        }

        Platform.runLater(() -> {
            try {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            } catch (Exception e) {
                System.err.println("Error showing warning notification: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
