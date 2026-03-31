package fr.ticngo.controller;

import fr.ticngo.config.AppContext;
import fr.ticngo.model.Administrateur;
import fr.ticngo.service.AuthService;
import fr.ticngo.util.FxmlLoader;
import fr.ticngo.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginBtn;

    private AuthService authService;
    private FxmlLoader fxmlLoader;

    @FXML
    public void initialize() {
        authService = AppContext.getInstance().getAuthService();
        fxmlLoader  = AppContext.getInstance().getFxmlLoader();
    }

    @FXML
    public void onLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        loginBtn.setDisable(true);
        loginBtn.setText("Connexion...");

        Optional<Administrateur> admin = authService.authenticate(email, password);
        if (admin.isPresent()) {
            SessionManager.setCurrentAdmin(admin.get());
            try {
                Parent root = fxmlLoader.load("/fxml/main.fxml");
                Scene scene = new Scene(root, 1200, 750);
                scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm()
                );
                Stage stage = (Stage) loginBtn.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Tic'n Go — Back-office");
                stage.setResizable(true);
                stage.setMinWidth(900);
                stage.setMinHeight(600);
                stage.centerOnScreen();
            } catch (Exception e) {
                showError("Erreur lors du chargement de l'interface.");
                e.printStackTrace();
            }
        } else {
            showError("Email ou mot de passe incorrect.");
            loginBtn.setDisable(false);
            loginBtn.setText("Se connecter");
        }
    }

    @FXML
    public void onPasswordKeyPressed(javafx.scene.input.KeyEvent e) {
        if (e.getCode() == javafx.scene.input.KeyCode.ENTER) onLogin();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        loginBtn.setDisable(false);
        loginBtn.setText("Se connecter");
    }
}
