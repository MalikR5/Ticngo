package fr.ticngo.controller;

import fr.ticngo.model.Client;
import fr.ticngo.service.ClientService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ClientFormController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telField;
    @FXML private Label errorLabel;

    @Autowired private ClientService clientService;

    private Client client;

    public void setClient(Client c) {
        this.client = c;
        if (c != null) {
            nomField.setText(c.getNom());
            prenomField.setText(c.getPrenom());
            emailField.setText(c.getEmail());
            telField.setText(c.getTelephone() != null ? c.getTelephone() : "");
        }
    }

    @FXML public void onSave() {
        errorLabel.setVisible(false);
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            showError("Nom, prénom et email sont obligatoires."); return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showError("Email invalide."); return;
        }

        if (client == null) client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(email);
        client.setTelephone(telField.getText().trim());

        try {
            clientService.save(client);
            ((Stage) nomField.getScene().getWindow()).close();
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    @FXML public void onCancel() {
        ((Stage) nomField.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        errorLabel.setText(msg); errorLabel.setVisible(true);
    }
}
