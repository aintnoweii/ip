package nova;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A JavaFX GUI for Nova, built from the FXML views under
 * {@code src/main/resources/view}.
 */
public class Main extends Application {
    /** Smallest window that still shows the input field and the Send button. */
    private static final double MIN_WIDTH = 360.0;
    private static final double MIN_HEIGHT = 320.0;

    private final Nova nova = new Nova();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Nova");

            // The layout is fluid now, so resizing is worth allowing. The
            // minimum stops the input bar being squeezed to nothing.
            stage.setMinWidth(MIN_WIDTH);
            stage.setMinHeight(MIN_HEIGHT);

            fxmlLoader.<MainWindow>getController().setNova(nova);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load MainWindow.fxml", e);
        }
    }
}
