package editor.sceneloaders;

import editor.Controller;
import javafx.scene.layout.VBox;
import kiosk.SceneGraph;
import kiosk.models.CareerPathwaySceneModel;

/**
 * Used to load the editing controls for the CareerPathwayScene.
 */
public class CareerPathwaySceneLoader extends PathwaySceneLoader {
    /**
     * Populates the editor pane with fields for editing the provided SceneModel.
     * @param model The current scene model we want to modify.
     * @param graph The scene graph used to manage application state.
     */
    public static void loadScene(Controller controller, CareerPathwaySceneModel model,
                                 VBox toolbarBox, SceneGraph graph) {
        // Get the editing Nodes for the CareerPathwaySceneModel properties
        VBox vbox = new VBox(
            SceneLoader.getNameBox(controller, model, graph),
            getHeaderTitleBox(model, graph),
            getHeaderBodyBox(model, graph),
            getCenterTextBox(model, graph)
        );

        // Clear the editor pane and re-populate with the new Nodes
        toolbarBox.getChildren().clear();
        toolbarBox.getChildren().add(vbox);
    }
}
