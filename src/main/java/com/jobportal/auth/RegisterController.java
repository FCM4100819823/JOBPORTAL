package com.jobportal.auth;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class RegisterController {
    @FXML
    private Pane registerPane;

    @FXML
    private void initialize() {
        // Ensure the register pane scales properly
        registerPane.setPrefSize(400, 300); // Adjust dimensions as needed
    }
}
