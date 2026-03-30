package fr.ticngo.controller;

import fr.ticngo.util.FxmlLoader;
import fr.ticngo.util.NavigationService;
import fr.ticngo.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@Scope("prototype")
public class MainController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Label pageTitleLabel;
    @FXML private Label adminNameLabel;
    @FXML private Button btnDashboard;
    @FXML private Button btnBillets;
    @FXML private Button btnSpectacles;
    @FXML private Button btnClients;

    @Autowired private FxmlLoader fxmlLoader;
    @Autowired private NavigationService navigationService;

    private Button activeBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navigationService.setMainController(this);
        if (SessionManager.getCurrentAdmin() != null) {
            adminNameLabel.setText(SessionManager.getCurrentAdmin().getNomComplet());
        }
        navigateToDashboard();
    }

    public void setContent(Parent view, String title) {
        contentPane.getChildren().setAll(view);
        pageTitleLabel.setText(title);
    }

    @FXML public void navigateToDashboard() {
        setActive(btnDashboard);
        navigationService.navigateTo("/fxml/dashboard.fxml", "Tableau de bord");
    }

    @FXML public void navigateToBillets() {
        setActive(btnBillets);
        navigationService.navigateTo("/fxml/billet-list.fxml", "Billets");
    }

    @FXML public void navigateToSpectacles() {
        setActive(btnSpectacles);
        navigationService.navigateTo("/fxml/spectacle-list.fxml", "Spectacles");
    }

    @FXML public void navigateToClients() {
        setActive(btnClients);
        navigationService.navigateTo("/fxml/client-list.fxml", "Clients");
    }

    @FXML public void onLogout() {
        try {
            SessionManager.clear();
            Parent root = fxmlLoader.load("/fxml/login.fxml");
            Scene scene = new Scene(root, 420, 520);
            scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
            );
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tic'n Go — Connexion");
            stage.setResizable(false);
            stage.setWidth(420);
            stage.setHeight(520);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActive(Button btn) {
        if (activeBtn != null) activeBtn.getStyleClass().remove("sidebar-btn-active");
        btn.getStyleClass().add("sidebar-btn-active");
        activeBtn = btn;
    }
}
