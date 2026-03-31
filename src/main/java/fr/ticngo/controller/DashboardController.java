package fr.ticngo.controller;

import fr.ticngo.config.AppContext;
import fr.ticngo.model.Billet;
import fr.ticngo.service.BilletService;
import fr.ticngo.service.ClientService;
import fr.ticngo.service.SpectacleService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label statBillets;
    @FXML private Label statSpectacles;
    @FXML private Label statClients;
    @FXML private Label statRevenu;
    @FXML private Label statValides;
    @FXML private Label statAnnules;

    @FXML private TableView<Billet> recentTable;
    @FXML private TableColumn<Billet, String>           colNumero;
    @FXML private TableColumn<Billet, String>           colClient;
    @FXML private TableColumn<Billet, String>           colSpectacle;
    @FXML private TableColumn<Billet, String>           colDate;
    @FXML private TableColumn<Billet, BigDecimal>       colPrix;
    @FXML private TableColumn<Billet, Billet.StatutBillet> colStatut;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BilletService    billetService    = AppContext.getInstance().getBilletService();
        SpectacleService spectacleService = AppContext.getInstance().getSpectacleService();
        ClientService    clientService    = AppContext.getInstance().getClientService();

        statBillets.setText(String.valueOf(billetService.countAll()));
        statSpectacles.setText(String.valueOf(spectacleService.count()));
        statClients.setText(String.valueOf(clientService.count()));
        statRevenu.setText(billetService.getTotalRevenu().setScale(2) + " €");
        statValides.setText(String.valueOf(billetService.countByStatut(Billet.StatutBillet.VALIDE)));
        statAnnules.setText(String.valueOf(billetService.countByStatut(Billet.StatutBillet.ANNULE)));

        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroBillet"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colClient.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getClient().getPrenom() + " " + data.getValue().getClient().getNom()));

        colSpectacle.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getSeance().getSpectacle().getTitre()));

        colDate.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getSeance().getDateHeure().format(DTF)));

        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Billet.StatutBillet item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item.name());
                String color = switch (item) {
                    case VALIDE    -> "#48bb78";
                    case ANNULE    -> "#e53e3e";
                    case REMBOURSE -> "#ed8936";
                    case UTILISE   -> "#667eea";
                };
                setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
            }
        });

        List<Billet> all = billetService.findAll();
        List<Billet> recent = all.subList(Math.max(0, all.size() - 10), all.size());
        recentTable.setItems(FXCollections.observableArrayList(recent));
    }
}
