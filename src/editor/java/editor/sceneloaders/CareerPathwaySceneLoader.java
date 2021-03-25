package editor.sceneloaders;

import editor.Controller;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import kiosk.SceneGraph;
import kiosk.models.CareerPathwaySceneModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.LoadedSurveyModel;

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
                                 VBox toolbarBox, SceneGraph graph, FilterGroupModel[] filters) {
        // Get the editing Nodes for the CareerPathwaySceneModel properties
        VBox vbox = new VBox(
            SceneLoader.getNameBox(controller, model, graph),
            getHeaderTitleBox(model, graph),
            getHeaderBodyBox(model, graph),
            getCenterTextBox(model, graph),
            getFilterBox(model, graph, filters)
        );
        vbox.setPadding(PADDING);

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

    /**
     * Returns a ComboBox that can be used to change the filter the CareerPathwayScene is using.
     * @param model The model of the CareerPathwayScene being edited.
     * @param graph The SceneGraph to re-register the scene to.
     * @return ComboBox that can be used to change the filter the CareerPathwayScene is using.
     */
    private static Node getFilterBox(CareerPathwaySceneModel model, SceneGraph graph,
                                     FilterGroupModel[] filters) {
        // Create a ComboBox with all the available filters
        ComboBox<FilterGroupModel> filterBox =
            new ComboBox<>(FXCollections.observableList(Arrays.asList(filters)));
        filterBox.setValue(model.filter); // Set initial value to match the current filter

        // On change, update the scenes filter (if it is different from the current filter)
        filterBox.setOnAction(event -> {
            FilterGroupModel target = filterBox.getValue();
            if (!target.equals(model.filter)) {
                model.filter = target;
                graph.registerSceneModel(model); // Re-register the model to update the scene
            }
        });

        return filterBox;
    }
}
