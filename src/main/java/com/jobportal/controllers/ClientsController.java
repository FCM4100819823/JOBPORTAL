package com.jobportal.controllers;

import com.jobportal.main.JobPortal;
import com.jobportal.models.Client;
import com.jobportal.services.ClientService;
import com.jobportal.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.List;

public class ClientsController {
    @FXML private ListView<Client> clientsListView;
    @FXML private TextField searchField;
    @FXML private TextField newClientField;
    @FXML private Button addClientButton;
    @FXML private Button editClientButton;
    @FXML private Button deleteClientButton;
    @FXML private Button backButton;

    private final ClientService clientService = new ClientService();
    private ObservableList<Client> clients;
    private FilteredList<Client> filteredData;

    @FXML
    private void initialize() {
        loadClients();
        setupSearchFilter();
        setupButtonHandlers();
    }

    private void loadClients() {
        String recruiterEmail = SessionManager.getInstance().getCurrentUser().getEmail();
        List<Client> clientList = clientService.getClientsByRecruiter(recruiterEmail);
        clients = FXCollections.observableArrayList(clientList);
        filteredData = new FilteredList<>(clients, p -> true);
        clientsListView.setItems(filteredData);
        
        // Set cell factory to display client names
        clientsListView.setCellFactory(lv -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        });
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue.toLowerCase();
            filteredData.setPredicate(client -> 
                searchText.isEmpty() || client.getName().toLowerCase().contains(searchText)
            );
        });
    }

    private void setupButtonHandlers() {
        addClientButton.setOnAction(e -> handleAddClient());
        editClientButton.setOnAction(e -> handleEditClient());
        deleteClientButton.setOnAction(e -> handleDeleteClient());
        backButton.setOnAction(e -> goToDashboard());
    }

    @FXML
    private void handleAddClient() {
        String newClientName = newClientField.getText().trim();
        if (newClientName.isEmpty()) {
            showAlert("Error", "Please enter a client name");
            return;
        }

        String recruiterEmail = SessionManager.getInstance().getCurrentUser().getEmail();
        
        Client newClient = new Client();
        newClient.setName(newClientName);
        newClient.setRecruiterEmail(recruiterEmail);
        
        try {
            clientService.addClient(newClient);
            showAlert("Success", "Client added successfully");
            newClientField.clear();
            loadClients(); // Refresh the list
        } catch (Exception e) {
            showAlert("Error", "Failed to add client: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditClient() {
        Client selected = clientsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a client to edit");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Edit Client");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter new client name:");

        dialog.showAndWait().ifPresent(newName -> {
            selected.setName(newName);
            clientService.updateClient(selected);
            loadClients(); // Refresh the list
        });
    }

    @FXML
    private void handleDeleteClient() {
        Client selected = clientsListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a client to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Client");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this client?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            clientService.deleteClient(selected.getId());
            loadClients(); // Refresh the list
        }
    }

    @FXML
    private void goToDashboard() {
        JobPortal.loadScene("recruiter_dashboard.fxml", "Recruiter Dashboard");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 