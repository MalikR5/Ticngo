package fr.ticngo;

import fr.ticngo.config.AppContext;
import fr.ticngo.config.DataInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TicnGoApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialise le contexte DI et les données de démo
        AppContext ctx = AppContext.getInstance();
        new DataInitializer(
            ctx.getAdministrateurRepository(),
            ctx.getLieuRepository(),
            ctx.getSpectacleRepository(),
            ctx.getSeanceRepository(),
            ctx.getClientRepository(),
            ctx.getBilletRepository(),
            ctx.getAuthService()
        ).run();

        FXMLLoader loader = new FXMLLoader(
                TicnGoApplication.class.getResource("/fxml/login.fxml")
        );

        Scene scene = new Scene(loader.load(), 420, 520);
        scene.getStylesheets().add(
            getClass().getResource("/css/style.css").toExternalForm()
        );
        stage.setTitle("Tic'n Go — Connexion");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
