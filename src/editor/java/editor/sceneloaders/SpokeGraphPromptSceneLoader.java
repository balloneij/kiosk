package editor.sceneloaders;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.ImageModel;
import kiosk.models.SpokeGraphPromptSceneModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SpokeGraphPromptSceneLoader {
    // The default padding to space the editing Nodes
    static final Insets PADDING = new Insets(0, 0, 10, 10);
    static final Insets ANSWER_PADDING = new Insets(15, 0, 15, 0);
    static final int COLOR_RANGE = 255; // The range the colors can be set to
    static final FileChooser imageFileChooser = new FileChooser();

    /**
     * Populates the editor pane with fields for editing the provided SceneModel.
     * @param model The current scene model we want to modify.
     * @param editorPane The main editor view.
     * @param graph The scene graph used to manage application state.
     */
//    public static void loadScene(SpokeGraphPromptSceneModel model, AnchorPane editorPane, SceneGraph graph) {
//        // Get the editing Nodes for the SpokeGraphSceneModel properties
//        VBox vbox = new VBox(
//                getHeaderTitleBox(model, graph),
//                getHeaderBodyBox(model, graph),
//                getCareerCTBox(model, graph), // enter Text
//                getCareerOptionsBox(model, graph), //TODO in SGPSM; make career options and weights a special button (not clickable, weight field)
//                getPromptCTBox(model, graph), //TODO in SGPSM
//                getAnswersBox(model, graph) //TODO in SGPSM
//        );
//
//        // Clear the editor pane and re-populate with the new Nodes
//        editorPane.getChildren().clear();
//        editorPane.getChildren().add(vbox);
//
//        // Add extension filters to the image file chooser
//        imageFileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("PNG", "*.png"),
//                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
//                new FileChooser.ExtensionFilter("GIF", "*.gif"),
//                new FileChooser.ExtensionFilter("Any", "*.*")
//        );
//    }
//
//
//    private static Node getPositionBoxes(SpokeGraphSceneModel model, SceneGraph graph) {
//        var xposField = new TextField(String.valueOf(model.xpos));
//        var yposField = new TextField(String.valueOf(model.ypos));
//
//        // Listeners to update the position
//        xposField.textProperty().addListener((observable, oldValue, newValue) -> {
//            try {
//                model.xpos = Float.parseFloat(newValue);
//                graph.registerSceneModel(model); // Re-register the model to update the scene
//            } catch (NumberFormatException ex) {
//                xposField.setText(oldValue);
//            }
//        });
//
//        yposField.textProperty().addListener((observable, oldValue, newValue) -> {
//            //TODO is this better than the xposField logic?
//            if (!newValue.matches("\\d*")) {
//                yposField.setText(newValue.replaceAll("[^\\d]", ""));
//            }
//            model.ypos = Float.parseFloat(newValue);
//            graph.registerSceneModel(model); // Re-register the model to update the scene
//
//            });
//
//        var hboxx = new HBox(new Label("X Pos:"), xposField);
//        var hboxy = new HBox(new Label("Y Pos:"), yposField);
//
//        var vbox = new VBox();
//        vbox.getChildren().addAll(hboxx, hboxy);
//        vbox.setPadding(PADDING);
//        return vbox;
//    }
//
//    private static Node getSizeBox(SpokeGraphSceneModel model, SceneGraph graph) {
//        var sizeField = new TextField(String.valueOf(model.size));
//
//        // Listeners to update the position
//        sizeField.textProperty().addListener((observable, oldValue, newValue) -> {
//            try { //todo use the better method
//                model.size = Float.parseFloat(newValue);
//                graph.registerSceneModel(model); // Re-register the model to update the scene
//            } catch (NumberFormatException ex) {
//                sizeField.setText(oldValue);
//            }
//        });
//
//        var vbox = new VBox(new Label("Size:"), sizeField);
//        vbox.setPadding(PADDING);
//        return vbox;
//    }
//
//    private static Node getPaddingBox(SpokeGraphSceneModel model, SceneGraph graph) {
//        var paddingField = new TextField(String.valueOf(model.padding));
//
//        // Listeners to update the position
//        paddingField.textProperty().addListener((observable, oldValue, newValue) -> {
//            try { //todo use the better method
//                model.padding = Float.parseFloat(newValue);
//                graph.registerSceneModel(model); // Re-register the model to update the scene
//            } catch (NumberFormatException ex) {
//                paddingField.setText(oldValue);
//            }
//        });
//
//        var vbox = new VBox(new Label("Padding:"), paddingField);
//        vbox.setPadding(PADDING);
//        return vbox;
//    }
//
//    private static Node getCenterTextBox(SpokeGraphSceneModel model, SceneGraph graph) {
//        var centerTextField = new TextField(model.centerText);
//
//        // Listeners to update the position
//        centerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//            model.centerText = newValue;
//            graph.registerSceneModel(model); // Re-register the model to update the scene
//        });
//
//        var vbox = new VBox(new Label("Center Text:"), centerTextField);
//        vbox.setPadding(PADDING);
//        return vbox;
//
//    }
//
//    /**
//     * Creates a Node with editing controls for all the options, as well as a button to add
//     * additional options. See createOptionNode for more information on option editing controls.
//     * @param model The SpokeGraphSceneModel being edited.
//     * @param graph The SceneGraph of the current survey being edited.
//     * @return A Node with editing controls for all the options and a button to add additional
//     *         options.
//     */
//    private static Node getOptionsBox(SpokeGraphSceneModel model, SceneGraph graph) {
//        // Add a separator to separate the "Answers:" label from the answer sections
//        Separator separator = new Separator();
//        separator.setPadding(new Insets(0, 0, 10, 0));
//        var vbox = new VBox(new Label("Options:"), separator);
//
//        // Create controls for each answer (and add them to the Node)
//        for (String option : model.options) {
//            vbox.getChildren().add(createOptionNode(answer, vbox, model, graph));
//        }
//
//        // Setup the button for adding answers
//        Button addButton = new Button("+");
//        addButton.setOnAction(event -> {
//            ButtonModel newAnswer = new ButtonModel();
//
//            // Add the new answer to the PromptSceneModel's answers
//            ArrayList<ButtonModel> answersList = new ArrayList<>(Arrays.asList(model.answers));
//            answersList.add(newAnswer);
//            model.answers = answersList.toArray(ButtonModel[]::new);
//            graph.registerSceneModel(model); // Re-register the model to update the scene
//
//            // Add editing controls for the new answer
//            int index = vbox.getChildren().size() - 1; // Add controls just before the add button
//            vbox.getChildren().add(index, createAnswerNode(newAnswer, vbox, model, graph));
//        });
//
//        vbox.getChildren().add(addButton);
//        vbox.setPadding(PADDING);
//        return vbox;
//    }
//
//    /**
//     * Creates a Node containing all the controls for editing an option button, including a field
//     * to adjust the text, a color picker, buttons to change the shape and image, a drop down for
//     * selecting the target, and a button to remove the answer.
//     * @param answersContainer The VBox that will contain the controls for all the answers.
//     * @param answer The ButtonModel for the answer controls being created.
//     * @param model The PromptSceneModel being edited.
//     * @param graph The SceneGraph of the current survey being edited.
//     * @return A Node containing all the controls for editing an answer button
//     */
//    private static Node createOptionNode(String option, VBox optionContainer,
//                                         SpokeGraphSceneModel model, SceneGraph graph) {
//        // Setup the text field for editing the answer
//        var answerField = new TextField(answer.text);
//        answerField.textProperty().addListener((observable, oldValue, newValue) -> {
//            answer.text = newValue;
//            graph.registerSceneModel(model); // Re-register the model to update the scene
//        });
//
//        // Setup the color picker for changing the answer color
//        Color initialColor = Color.rgb(answer.rgb[0], answer.rgb[1], answer.rgb[2]);
//        ColorPicker colorPicker = new ColorPicker(initialColor);
//        colorPicker.setOnAction(event -> {
//            // Set the answer color to the new color
//            var newColor = colorPicker.getValue();
//            answer.rgb[0] = (int) (newColor.getRed() * COLOR_RANGE);
//            answer.rgb[1] = (int) (newColor.getGreen() * COLOR_RANGE);
//            answer.rgb[2] = (int) (newColor.getBlue() * COLOR_RANGE);
//
//            graph.registerSceneModel(model); // Re-register the model to update the scene
//        });
//
//        // Setup button for changing answer shape
//        // (may need to convert to a combo-box if more shapes are added)
//        Button shapeButton = new Button(answer.isCircle ? "■" : "⬤");
//        shapeButton.setOnAction(event -> {
//            answer.isCircle = !answer.isCircle;
//            graph.registerSceneModel(model); // Re-register the model to update the scene
//
//            shapeButton.setText(answer.isCircle ? "■" : "⬤"); // Update the button symbol
//        });
//
//        // Setup the button for adding an image to the answer
//        Button imageChooseButton = new Button("Image");
//        imageChooseButton.setOnAction(event -> {
//            // Open the image file chooser
//            var file = imageFileChooser.showOpenDialog(null);
//
//            // If null, no file was chosen
//            if (file != null) {
//                // Set the chooser to open in the same directory next time
//                String imagePath = file.getPath();
//                String directoryPath =
//                        imagePath.substring(0, imagePath.lastIndexOf(File.separator));
//                imageFileChooser.setInitialDirectory(new File(directoryPath));
//
//                // Create an image if the answer does not already have one
//                if (answer.image == null) {
//                    answer.image = new ImageModel();
//                }
//
//                // Set the new image path
//                answer.image.path = imagePath;
//                graph.registerSceneModel(model); // Re-register the model to update the scene
//            }
//        });
//
//        var answerVbox = new VBox(); // Contains all the editing controls for this answer
//
//        // Setup the button for removing an answer
//        Button removeButton = new Button("x");
//        removeButton.setOnAction(event -> {
//            // Remove the answer from the PromptSceneModel's answers
//            ArrayList<ButtonModel> answersList = new ArrayList<>(Arrays.asList(model.answers));
//            answersList.remove(answer);
//            model.answers = answersList.toArray(ButtonModel[]::new);
//            graph.registerSceneModel(model); // Re-register the model to update the scene
//
//            // Remove the editing controls for this answer from the parent container
//            answersContainer.getChildren().remove(answerVbox);
//        });
//
//        // Setup the combo-box for choosing the answers target scene
//        ArrayList<String> sceneIds = new ArrayList<>(graph.getSceneIds());
//        ComboBox<String> targetComboBox = new ComboBox<>(FXCollections.observableList(sceneIds));
//        targetComboBox.setValue(answer.target); // Set initial value to match the answer's target
//        targetComboBox.setOnAction(event -> {
//            answer.target = targetComboBox.getValue();
//            graph.registerSceneModel(model); // Re-register the model to update the scene
//        });
//
//        // Create an HBox with a "Target: " label and the combo-box
//        HBox targetsBox = new HBox(new Label("Target: "), targetComboBox);
//        targetsBox.setPadding(new Insets(0, 0, 0, 5));
//
//        // Add a separator so answers are visually separated
//        Separator separator = new Separator();
//        separator.setPadding(ANSWER_PADDING);
//
//        // Put all the answer controls together
//        HBox editingControls = new HBox(colorPicker, imageChooseButton, shapeButton, removeButton);
//        answerVbox.getChildren().addAll(answerField, editingControls, targetsBox, separator);
//        return answerVbox;
//    }
//
//    private static Node getColorsBox(SpokeGraphSceneModel model, SceneGraph graph) {
//        return null;
//
//    }
}
