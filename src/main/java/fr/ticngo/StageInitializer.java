package fr.ticngo;

import fr.ticngo.util.FxmlLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxmlLoader fxmlLoader;

    public StageInitializer(FxmlLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            Parent root = fxmlLoader.load("/fxml/login.fxml");
            Scene scene = new Scene(root, 420, 520);
            scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm()
            );
            stage.setTitle("Tic'n Go — Connexion");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
