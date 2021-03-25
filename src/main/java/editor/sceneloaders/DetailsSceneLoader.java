package editor.sceneloaders;

import editor.Controller;
import java.io.File;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.DetailsSceneModel;
import kiosk.models.ImageModel;

public class DetailsSceneLoader {
    // The default padding to space the editing Nodes
    static final Insets PADDING = new Insets(0, 0, 10, 10);
    static final Insets ANSWER_PADDING = new Insets(15, 0, 15, 0);
    static final int COLOR_RANGE = 255; // The range the colors can be set to

    static final FileChooser imageFileChooser = new FileChooser();

    /**
     * Populates the editor pane with fields for editing the provided DetailsScene.
     * @param model The current scene model we want to modify.
     * @param toolbarBox The main editor view.
     * @param graph The scene graph used to manage application state.
     */
    public static void loadScene(Controller controller,
                                 DetailsSceneModel model, VBox toolbarBox, SceneGraph graph) {
        toolbarBox.getChildren().clear();

        // Get the editing Nodes for the DetailsSceneModel properties
        VBox vbox = new VBox(
                SceneLoader.getNameBox(controller, model, graph),
                getTitleBox(model, graph),
                getDescriptionBox(model, graph),
                createButton(model, graph, controller)
        );
        toolbarBox.getChildren().add(vbox);

        // Add extension filters to the image file chooser
        imageFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("Any", "*.*")
        );
    }

    // Adds a Node containing a text field for editing the title.
    private static Node getTitleBox(DetailsSceneModel model, SceneGraph graph) {
        TextArea titleField = new TextArea(model.title);
        titleField.setMaxHeight(5);

        // Listener to update the title
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.title = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        VBox vbox = new VBox(new Label("Title:"), titleField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static Node getDescriptionBox(DetailsSceneModel model, SceneGraph graph) {
        TextArea bodyField = new TextArea(model.title);
        bodyField.setMaxHeight(5);

        // Listener to update the title
        bodyField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.body = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        VBox vbox = new VBox(new Label("Body:"), bodyField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static Node createButton(DetailsSceneModel model,
                                     SceneGraph graph, Controller controller) {
        ButtonModel answer = model.button;

        // Setup the text field for editing the answer
        TextArea answerField = new TextArea(answer.text);
        answerField.setMaxHeight(5);
        answerField.textProperty().addListener((observable, oldValue, newValue) -> {
            answer.text = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        // Setup the color picker for changing the answer color
        Color initialColor = Color.rgb(answer.rgb[0], answer.rgb[1], answer.rgb[2]);
        ColorPicker colorPicker = new ColorPicker(initialColor);
        colorPicker.setOnAction(event -> {
            // Set the answer color to the new color
            Color newColor = colorPicker.getValue();
            answer.rgb[0] = (int) (newColor.getRed() * COLOR_RANGE);
            answer.rgb[1] = (int) (newColor.getGreen() * COLOR_RANGE);
            answer.rgb[2] = (int) (newColor.getBlue() * COLOR_RANGE);

            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        // Setup button for changing answer shape
        // (may need to convert to a combo-box if more shapes are added)
        Button shapeButton = new Button(answer.isCircle ? "■" : "⬤");
        shapeButton.setOnAction(event -> {
            answer.isCircle = !answer.isCircle;
            graph.registerSceneModel(model); // Re-register the model to update the scene

            shapeButton.setText(answer.isCircle ? "■" : "⬤"); // Update the button symbol
        });

        // Setup the button for adding an image to the answer
        Button imageChooseButton = new Button("Image");
        imageChooseButton.setOnAction(event -> {
            // Open the image file chooser
            File file = imageFileChooser.showOpenDialog(null);

            // If null, no file was chosenA
            if (file != null) {
                // Set the chooser to open in the same directory next time
                String imagePath = new File("./").toURI().relativize(file.toURI()).getPath();
                String directoryPath = file.getParentFile().getPath();
                imageFileChooser.setInitialDirectory(new File(directoryPath));

                // Create an image if the answer does not already have one
                if (answer.image == null) {
                    answer.image = new ImageModel();
                }

                // Set the new image path
                answer.image.path = imagePath;
                graph.registerSceneModel(model); // Re-register the model to update the scene
            }
        });

        // Setup the combo-box for choosing the answers target scene
        ArrayList<String> sceneIds = new ArrayList<>(graph.getSceneIds());
        sceneIds.remove(model.id); // Prevent a scene from navigating to itself

        ArrayList<SceneTarget> sceneTargets = new ArrayList<>();
        for (String id : sceneIds) {
            sceneTargets.add(new SceneTarget(id, graph.getSceneById(id).getName()));
        }

        ComboBox<SceneTarget> targetComboBox =
                new ComboBox<>(FXCollections.observableList(sceneTargets));

        SceneTarget currentAnswer = new SceneTarget(answer.target,
                graph.getSceneById(answer.target).getName());

        targetComboBox.setValue(currentAnswer); // Set initial value to match the answer's target
        targetComboBox.setOnAction(event -> {
            String target = targetComboBox.getValue().getSceneId();
            if (!target.equals(model.getId())) {
                answer.target = target;
                graph.registerSceneModel(model); // Re-register the model to update the scene

                // Update the scene graph view
                controller.rebuildSceneGraphTreeView();
            }
        });

        // Create an HBox with a "Target: " label and the combo-box
        HBox targetsBox = new HBox(new Label("Target: "), targetComboBox);
        targetsBox.setPadding(new Insets(0, 0, 0, 5));

        VBox answerVbox = new VBox(); // Contains all the editing controls for this answer
        // Put all the answer controls together
        HBox editingControls = new HBox(colorPicker, imageChooseButton, shapeButton);
        answerVbox.getChildren().addAll(
                new Label("Button Text:"), answerField, editingControls, targetsBox);
        answerVbox.setPadding(PADDING);
        return answerVbox;
    }

}
