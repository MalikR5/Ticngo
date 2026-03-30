package fr.ticngo.util;

import fr.ticngo.controller.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;

@Service
public class NavigationService {

    private MainController mainController;
    private final FxmlLoader fxmlLoader;

    public NavigationService(FxmlLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void navigateTo(String fxmlPath, String title) {
        try {
            Parent view = fxmlLoader.load(fxmlPath);
            mainController.setContent(view, title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDialog(String fxmlPath, String title, Runnable onClose) {
        try {
            FXMLLoader loader = fxmlLoader.createLoader(fxmlPath);
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
            );
            dialog.setScene(scene);
            dialog.setOnHidden(e -> { if (onClose != null) onClose.run(); });
            dialog.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
