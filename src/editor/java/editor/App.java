package editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import processing.javafx.PSurfaceFX;

public class App extends Application {

    public static PSurfaceFX surface;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Editor.fxml"));
        Parent root = loader.load();
        Controller.stage = primaryStage;
        Scene scene = new Scene(root,
                Editor.PREVIEW_WIDTH + Editor.SIDEBAR_WIDTH,
                (int) (Editor.PREVIEW_WIDTH / Editor.PREVIEW_ASPECT_RATIO));

        primaryStage.setTitle("Kiosk Editor 3000");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        surface.stage = primaryStage;
        Controller.stage = primaryStage;
    }
}
