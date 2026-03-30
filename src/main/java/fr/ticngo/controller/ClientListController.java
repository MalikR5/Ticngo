package fr.ticngo.controller;

import fr.ticngo.model.Client;
import fr.ticngo.service.ClientService;
import fr.ticngo.util.FxmlLoader;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class ClientListController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> colNom;
    @FXML private TableColumn<Client, String> colPrenom;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colTel;
    @FXML private TableColumn<Client, String> colDate;
    @FXML private TableColumn<Client, Void>   colActions;
    @FXML private Label countLabel;

    @Autowired private ClientService clientService;
    @Autowired private FxmlLoader fxmlLoader;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getDateInscription() != null
                ? data.getValue().getDateInscription().format(DF) : "—"));

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Modifier");
            private final Button btnDel  = new Button("Supprimer");
            { btnEdit.getStyleClass().add("btn-secondary"); btnDel.getStyleClass().add("btn-danger");
              btnEdit.setOnAction(e -> openForm(getTableView().getItems().get(getIndex())));
              btnDel.setOnAction(e -> deleteClient(getTableView().getItems().get(getIndex())));
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
        var list = clientService.search(searchField.getText());
        clientTable.setItems(FXCollections.observableArrayList(list));
        countLabel.setText(list.size() + " client(s)");
    }

    @FXML public void onNouveauClient() { openForm(null); }

    private void openForm(Client client) {
        try {
            FXMLLoader loader = fxmlLoader.createLoader("/fxml/client-form.fxml");
            Parent root = loader.load();
            ClientFormController ctrl = loader.getController();
            ctrl.setClient(client);
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle(client == null ? "Nouveau client" : "Modifier client");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            dlg.setScene(scene);
            dlg.setOnHidden(e -> loadData());
            dlg.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void deleteClient(Client c) {
        new Alert(Alert.AlertType.CONFIRMATION,
            "Supprimer " + c.getPrenom() + " " + c.getNom() + " ?",
            ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) { clientService.delete(c.getId()); loadData(); }
        });
    }
}
