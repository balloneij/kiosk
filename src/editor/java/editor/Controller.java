package editor;

import editor.sceneloaders.PromptSceneLoader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
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
    @FXML
    SplitPane splitPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Canvas canvas = (Canvas) surface.getNative();
        surface.fx.context = canvas.getGraphicsContext2D();
        surveyPreviewPane.getChildren().add(canvas);
        canvas.widthProperty().bind(surveyPreviewPane.widthProperty());
        canvas.heightProperty().bind(surveyPreviewPane.heightProperty());

        sceneGraph.addSceneChangeCallback(new EditorSceneChangeCallback(this));
        previousId = "";

        for (Node node : splitPane.lookupAll(".split-pane-divider")) {
            node.setVisible(true);
        }

        // Calculate the divider location for the split pane based off of the width
        // of the preview window and the width of the editor toolbar
        splitPane.setDividerPosition(0,
                (double) Editor.SIDEBAR_WIDTH / (Editor.SIDEBAR_WIDTH + Editor.PREVIEW_WIDTH));

        // The split pane will respect max widths, so by assigning these, the divider
        // cannot be moved
        // TODO: There is a better way of doing this. Using CSS-like JavaFX styling
        // you can hide the cursor so the divider cannot be moved. I could not get that to work.
        // - Isaac
        editorPane.maxWidthProperty().setValue(Editor.SIDEBAR_WIDTH);
        editorPane.minWidthProperty().setValue(Editor.SIDEBAR_WIDTH);
        surveyPreviewPane.maxWidthProperty().setValue(Editor.PREVIEW_WIDTH);
        surveyPreviewPane.minWidthProperty().setValue(Editor.PREVIEW_WIDTH);
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
