package fr.ticngo.controller;

import fr.ticngo.model.Billet;
import fr.ticngo.service.BilletService;
import fr.ticngo.service.PdfService;
import fr.ticngo.util.NavigationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class BilletListController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statutFilter;
    @FXML private TableView<Billet> billetTable;
    @FXML private TableColumn<Billet, String> colNumero;
    @FXML private TableColumn<Billet, String> colClient;
    @FXML private TableColumn<Billet, String> colSpectacle;
    @FXML private TableColumn<Billet, String> colDate;
    @FXML private TableColumn<Billet, BigDecimal> colPrix;
    @FXML private TableColumn<Billet, Billet.StatutBillet> colStatut;
    @FXML private TableColumn<Billet, Void> colActions;
    @FXML private Label countLabel;

    @Autowired private BilletService billetService;
    @Autowired private PdfService pdfService;
    @Autowired private NavigationService navigationService;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statutFilter.setItems(FXCollections.observableArrayList("Tous", "VALIDE", "ANNULE", "UTILISE", "REMBOURSE"));
        statutFilter.setValue("Tous");

        setupColumns();
        loadData();

        searchField.textProperty().addListener((obs, o, n) -> loadData());
        statutFilter.valueProperty().addListener((obs, o, n) -> loadData());
    }

    private void setupColumns() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroBillet"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colClient.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getClient().getPrenom() + " " + data.getValue().getClient().getNom()));
        colSpectacle.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getSeance().getSpectacle().getTitre()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getSeance().getDateHeure().format(DTF)));

        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Billet.StatutBillet item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item.name());
                String c = switch (item) {
                    case VALIDE -> "#48bb78"; case ANNULE -> "#e53e3e";
                    case REMBOURSE -> "#ed8936"; case UTILISE -> "#667eea";
                };
                setStyle("-fx-text-fill:" + c + ";-fx-font-weight:bold;");
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnPdf    = new Button("PDF");
            private final Button btnAnnul  = new Button("Annuler");
            private final Button btnValide = new Button("Valider");
            {
                btnPdf.getStyleClass().add("btn-secondary");
                btnAnnul.getStyleClass().add("btn-danger");
                btnValide.getStyleClass().add("btn-success");
                btnPdf.setOnAction(e -> exportPdf(getTableView().getItems().get(getIndex())));
                btnAnnul.setOnAction(e -> annuler(getTableView().getItems().get(getIndex())));
                btnValide.setOnAction(e -> valider(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Billet b = getTableView().getItems().get(getIndex());
                javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(4);
                box.getChildren().add(btnPdf);
                if (b.getStatut() == Billet.StatutBillet.VALIDE) {
                    box.getChildren().addAll(btnValide, btnAnnul);
                }
                setGraphic(box);
            }
        });
    }

    private void loadData() {
        String search = searchField.getText();
        String sv = statutFilter.getValue();
        Billet.StatutBillet statut = (sv == null || sv.equals("Tous")) ? null
                : Billet.StatutBillet.valueOf(sv);
        List<Billet> billets = billetService.search(search, statut);
        billetTable.setItems(FXCollections.observableArrayList(billets));
        countLabel.setText(billets.size() + " billet(s)");
    }

    @FXML public void onNouveauBillet() {
        navigationService.navigateTo("/fxml/billet-form.fxml", "Nouveau billet");
    }

    @FXML public void onExportPdfListe() {
        try {
            byte[] pdf = pdfService.generateListeBilletsPdf(billetTable.getItems());
            savePdf(pdf, "billets-liste.pdf");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur PDF", e.getMessage());
        }
    }

    private void exportPdf(Billet b) {
        try {
            byte[] pdf = pdfService.generateBilletPdf(b);
            savePdf(pdf, "billet-" + b.getNumeroBillet() + ".pdf");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur PDF", e.getMessage());
        }
    }

    private void annuler(Billet b) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Annuler le billet " + b.getNumeroBillet() + " ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                try { billetService.annuler(b.getId()); loadData(); }
                catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage()); }
            }
        });
    }

    private void valider(Billet b) {
        try { billetService.valider(b.getId()); loadData(); }
        catch (Exception e) { showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage()); }
    }

    private void savePdf(byte[] data, String defaultName) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Enregistrer le PDF");
        fc.setInitialFileName(defaultName);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = fc.showSaveDialog(billetTable.getScene().getWindow());
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "PDF sauvegardé !");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        new Alert(type, msg).showAndWait();
    }
}
