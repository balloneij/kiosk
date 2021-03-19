package editor.sceneloaders;

import editor.Controller;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import kiosk.SceneGraph;
import kiosk.models.CareerPathwaySceneModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.LoadedSurveyModel;

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
            getCenterTextBox(model, graph),
            getFilterBox(model, graph)
        );

        // Clear the editor pane and re-populate with the new Nodes
        toolbarBox.getChildren().clear();
        toolbarBox.getChildren().add(vbox);
    }

    /**
     * Returns a ComboBox that can be used to change the filter the CareerPathwayScene is using.
     * @param model The model of the CareerPathwayScene being edited.
     * @param graph The SceneGraph to re-register the scene to.
     * @return ComboBox that can be used to change the filter the CareerPathwayScene is using.
     */
    private static Node getFilterBox(CareerPathwaySceneModel model, SceneGraph graph) {
        // Create a ComboBox with all the available filters
        ComboBox<FilterGroupModel> filterBox =
            new ComboBox<>(FXCollections.observableList(Arrays.asList(LoadedSurveyModel.filters)));
        filterBox.setValue(model.getFilter()); // Set initial value to match the current filter

        // On change, update the scenes filter (if it is different from the current filter)
        filterBox.setOnAction(event -> {
            FilterGroupModel target = filterBox.getValue();
            if (!target.equals(model.getFilter())) {
                model.setFilter(target);
                graph.registerSceneModel(model); // Re-register the model to update the scene
            }
        });

        return filterBox;
    }
}
