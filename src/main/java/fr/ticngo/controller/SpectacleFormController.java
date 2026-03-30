package fr.ticngo.controller;

import fr.ticngo.model.Lieu;
import fr.ticngo.model.Spectacle;
import fr.ticngo.repository.LieuRepository;
import fr.ticngo.service.SpectacleService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class SpectacleFormController implements Initializable {

    @FXML private TextField titreField;
    @FXML private ComboBox<String> categorieCombo;
    @FXML private TextField prixField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Lieu> lieuCombo;
    @FXML private Label errorLabel;

    @Autowired private SpectacleService spectacleService;
    @Autowired private LieuRepository lieuRepo;

    private Spectacle spectacle;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categorieCombo.setItems(FXCollections.observableArrayList(
            "Théâtre", "Concert", "Comédie", "Danse", "Opéra", "Cirque", "Autre"));
        lieuCombo.setItems(FXCollections.observableArrayList(lieuRepo.findAll()));
    }

    public void setSpectacle(Spectacle s) {
        this.spectacle = s;
        if (s != null) {
            titreField.setText(s.getTitre());
            categorieCombo.setValue(s.getCategorie());
            prixField.setText(s.getPrixBase() != null ? s.getPrixBase().toString() : "");
            descriptionArea.setText(s.getDescription());
            lieuCombo.setValue(s.getLieu());
        }
    }

    @FXML public void onSave() {
        errorLabel.setVisible(false);
        String titre = titreField.getText().trim();
        if (titre.isEmpty()) { showError("Le titre est obligatoire."); return; }

        BigDecimal prix = null;
        if (!prixField.getText().trim().isEmpty()) {
            try { prix = new BigDecimal(prixField.getText().trim().replace(",", ".")); }
            catch (NumberFormatException e) { showError("Prix invalide."); return; }
        }

        if (spectacle == null) spectacle = new Spectacle();
        spectacle.setTitre(titre);
        spectacle.setCategorie(categorieCombo.getValue());
        spectacle.setPrixBase(prix);
        spectacle.setDescription(descriptionArea.getText());
        spectacle.setLieu(lieuCombo.getValue());

        spectacleService.save(spectacle);
        ((Stage) titreField.getScene().getWindow()).close();
    }

    @FXML public void onCancel() {
        ((Stage) titreField.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        errorLabel.setText(msg); errorLabel.setVisible(true);
    }
}
