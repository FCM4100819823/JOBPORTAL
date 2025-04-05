package com.jobportal.auth;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class LoginController {
    @FXML
    private Pane loginPane;

    @FXML
    private void initialize() {
        // Ensure the login pane scales properly
        loginPane.setPrefSize(400, 300); // Adjust dimensions as needed
    }
}
