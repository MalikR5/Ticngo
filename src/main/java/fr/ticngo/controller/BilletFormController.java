package fr.ticngo.controller;

import fr.ticngo.model.Client;
import fr.ticngo.model.Seance;
import fr.ticngo.service.BilletService;
import fr.ticngo.service.ClientService;
import fr.ticngo.service.SeanceService;
import fr.ticngo.util.NavigationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class BilletFormController implements Initializable {

    @FXML private ComboBox<Client> clientCombo;
    @FXML private ComboBox<Seance> seanceCombo;
    @FXML private TextField prixField;
    @FXML private Label errorLabel;

    @Autowired private BilletService billetService;
    @Autowired private ClientService clientService;
    @Autowired private SeanceService seanceService;
    @Autowired private NavigationService navigationService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clientCombo.setItems(FXCollections.observableArrayList(clientService.findAll()));
        seanceCombo.setItems(FXCollections.observableArrayList(seanceService.findAll()));

        // auto-fill prix when seance selected
        seanceCombo.valueProperty().addListener((obs, o, seance) -> {
            if (seance != null && seance.getSpectacle().getPrixBase() != null) {
                prixField.setText(seance.getSpectacle().getPrixBase().toString());
            }
        });
    }

    @FXML public void onSave() {
        errorLabel.setVisible(false);
        Client client = clientCombo.getValue();
        Seance seance = seanceCombo.getValue();
        String prixStr = prixField.getText().trim();

        if (client == null || seance == null || prixStr.isEmpty()) {
            showError("Tous les champs sont obligatoires.");
            return;
        }

        BigDecimal prix;
        try {
            prix = new BigDecimal(prixStr.replace(",", "."));
        } catch (NumberFormatException e) {
            showError("Prix invalide.");
            return;
        }

        try {
            billetService.creer(client, seance, prix);
            navigationService.navigateTo("/fxml/billet-list.fxml", "Billets");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML public void onCancel() {
        navigationService.navigateTo("/fxml/billet-list.fxml", "Billets");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
