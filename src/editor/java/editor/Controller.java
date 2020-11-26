package editor;

import editor.sceneloaders.PromptSceneLoader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kiosk.EventListener;
import kiosk.SceneGraph;
import kiosk.models.PromptSceneModel;
import kiosk.models.SceneModel;
import processing.javafx.PSurfaceFX;


public class Controller implements Initializable {

    public static PSurfaceFX surface;
    public static SceneGraph sceneGraph;
    protected static Stage stage;

    private String previousId;

    @FXML
    AnchorPane rootPane;
    @FXML
    AnchorPane editorPane;
    @FXML
    StackPane surveyPreviewPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Canvas canvas = (Canvas) surface.getNative();
        surface.fx.context = canvas.getGraphicsContext2D();
        surveyPreviewPane.getChildren().add(canvas);
        canvas.widthProperty().bind(surveyPreviewPane.widthProperty());
        canvas.heightProperty().bind(surveyPreviewPane.heightProperty());

        sceneGraph.addSceneChangeCallback(new EditorSceneChangeCallback(this));
        previousId = "";
    }

    private void rebuildEditor(SceneModel model) {
        previousId = model.getId();
        if (model instanceof PromptSceneModel) {
            PromptSceneLoader.loadScene((PromptSceneModel) model, editorPane, sceneGraph);
        }
    }

    private class EditorSceneChangeCallback implements EventListener<SceneModel> {
        private final Controller controller;

        public EditorSceneChangeCallback(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void invoke(SceneModel arg) {
            if (!arg.getId().equals(previousId)) {
                controller.rebuildEditor(arg);
            }
        }
    }
}
