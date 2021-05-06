package editor.sceneloaders;

import editor.Controller;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import kiosk.SceneGraph;
import kiosk.models.CareerPathwaySceneModel;
import kiosk.models.SpokeGraphPromptSceneModel;

/**
 * Used to load the editing controls for the CareerPathwayScene.
 */
public class CareerPathwaySceneLoader {
    static final Insets PADDING = new Insets(0, 0, 10, 10);
    static final int COLOR_RANGE = 255; // The range the colors can be set to

    /**
     * Populates the editor pane with fields for editing the provided SceneModel.
     *
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
                getCenterColor(model, graph),
        new Text(" This is the final scene in the survey;\n users"
                                + " now learn about their recommended careers")
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
        TextArea bodyField = new TextArea(model.headerBody);
        bodyField.setPrefRowCount(2);

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

    private static Node getCenterColor(CareerPathwaySceneModel model, SceneGraph graph) {
        VBox vBox = new VBox(new Label("Answers Center Color"));
        // Setup the color picker for changing the answer color
        Color initialColor = Color.rgb(model.centerColor[0], model.centerColor[1], model.centerColor[2]);
        ColorPicker colorPicker = new ColorPicker(initialColor);
        colorPicker.setOnAction(event -> {
            // Set the answer color to the new color
            Color newColor = colorPicker.getValue();
            model.centerColor[0] = (int) (newColor.getRed() * COLOR_RANGE);
            model.centerColor[1] = (int) (newColor.getGreen() * COLOR_RANGE);
            model.centerColor[2] = (int) (newColor.getBlue() * COLOR_RANGE);

            graph.registerSceneModel(model); // Re-register the model to update the scene
        });
        vBox.getChildren().add(colorPicker);
        vBox.setPadding(PADDING);
        return vBox;
    }
}