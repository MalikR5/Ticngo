package fr.ticngo.controller;

import fr.ticngo.config.AppContext;
import fr.ticngo.model.Spectacle;
import fr.ticngo.service.SpectacleService;
import fr.ticngo.util.FxmlLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class SpectacleListController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<Spectacle> spectacleTable;
    @FXML private TableColumn<Spectacle, String>     colTitre;
    @FXML private TableColumn<Spectacle, String>     colCategorie;
    @FXML private TableColumn<Spectacle, String>     colLieu;
    @FXML private TableColumn<Spectacle, BigDecimal> colPrix;
    @FXML private TableColumn<Spectacle, Void>       colActions;
    @FXML private Label countLabel;

    private SpectacleService spectacleService;
    private FxmlLoader       fxmlLoader;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spectacleService = AppContext.getInstance().getSpectacleService();
        fxmlLoader       = AppContext.getInstance().getFxmlLoader();

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixBase"));
        colLieu.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getLieu() != null ? data.getValue().getLieu().getNom() : "—"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDel  = new Button("Supprimer");
            {
                btnEdit.getStyleClass().add("btn-secondary");
                btnDel.getStyleClass().add("btn-danger");
                btnEdit.setOnAction(e -> openForm(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> deleteSpectacle(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                setGraphic(new javafx.scene.layout.HBox(4, btnEdit, btnDel));
            }
        });

        searchField.textProperty().addListener((obs, o, n) -> loadData());
        loadData();
    }

    private void loadData() {
        var list = spectacleService.search(searchField.getText());
        spectacleTable.setItems(FXCollections.observableArrayList(list));
        countLabel.setText(list.size() + " spectacle(s)");
    }

    @FXML public void onNouveauSpectacle() { openForm(null); }

    private void openForm(Spectacle spectacle) {
        try {
            FXMLLoader loader = fxmlLoader.createLoader("/fxml/spectacle-form.fxml");
            Parent root = loader.load();
            SpectacleFormController ctrl = loader.getController();
            ctrl.setSpectacle(spectacle);
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle(spectacle == null ? "Nouveau spectacle" : "Modifier spectacle");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            dlg.setScene(scene);
            dlg.setOnHidden(e -> loadData());
            dlg.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteSpectacle(Spectacle s) {
        new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + s.getTitre() + " ?",
            ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) { spectacleService.delete(s.getId()); loadData(); }
        });
    }
}
