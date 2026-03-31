package fr.ticngo.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class FxmlLoader {

    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = createLoader(fxmlPath);
        return loader.load();
    }

    public FXMLLoader createLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlPath));
        return loader;
    }
}
