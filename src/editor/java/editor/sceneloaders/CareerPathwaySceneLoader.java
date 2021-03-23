package editor.sceneloaders;

import editor.Controller;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import kiosk.SceneGraph;
import kiosk.models.CareerPathwaySceneModel;
import kiosk.models.PathwaySceneModel;

/**
 * Used to load the editing controls for the CareerPathwayScene.
 */
public class CareerPathwaySceneLoader {

    static final Insets PADDING = new Insets(0, 0, 10, 10);

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

    // Adds a Node containing a text field for editing the header title.
    protected static Node getHeaderTitleBox(CareerPathwaySceneModel model, SceneGraph graph) {
        TextField titleField = new TextField(model.headerTitle);

        // Listener to update the title
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.headerTitle = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        VBox vbox = new VBox(new Label("Header Title:"), titleField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    // Adds a Node containing a text field for editing the header body.
    protected static Node getHeaderBodyBox(CareerPathwaySceneModel model, SceneGraph graph) {
        TextField bodyField = new TextField(model.headerBody);

        // Listener to update the body
        bodyField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.headerBody = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        VBox vbox = new VBox(new Label("Header Body:"), bodyField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    protected static Node getCenterTextBox(CareerPathwaySceneModel model, SceneGraph graph) {
        TextField centerTextField = new TextField(model.centerText);

        // Listeners to update the position
        centerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.centerText = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        VBox vbox = new VBox(new Label("Center Text:"), centerTextField);
        vbox.setPadding(PADDING);
        return vbox;

    }
}
