package editor.sceneloaders;

import editor.Controller;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.ImageModel;
import kiosk.models.SpokeGraphPromptSceneModel;

public class SpokeGraphPromptSceneLoader {
    // The default padding to space the editing Nodes
    static final Insets PADDING = new Insets(0, 0, 10, 10);
    static final Insets ANSWER_PADDING = new Insets(15, 0, 15, 0);
    static final int COLOR_RANGE = 255; // The range the colors can be set to
    static final FileChooser imageFileChooser = new FileChooser();

    // Never changing, so load it once to save on electricity
    private static final ObservableList<Riasec> RIASEC_VALUES =
        FXCollections.observableList(Arrays.asList(Riasec.values()));
    
    /**
     * Populates the editor pane with fields for editing the provided SceneModel.
     * @param model The current scene model we want to modify.
     * @param toolbarBox The main editor view.
     * @param graph The scene graph used to manage application state.
     */
    public static void loadScene(Controller controller, SpokeGraphPromptSceneModel model,
                                 VBox toolbarBox, SceneGraph graph) {
        toolbarBox.getChildren().clear();

        // Get the editing Nodes for the SpokeGraphPromptsSceneModel properties
        VBox vbox = new VBox(
                SceneLoader.getNameBox(controller, model, graph),
                getHeaderTitleBox(model, graph),
                getHeaderBodyBox(model, graph),
                getCareerBox(model, graph),
                getPromptBox(model, graph),
                getCareerCenterColor(model, graph),
                getAnswerCenterColor(model, graph),
                getAnswersBox(controller, model, graph)
        );

        // Clear the editor pane and re-populate with the new Nodes
        toolbarBox.getChildren().clear();
        toolbarBox.getChildren().add(vbox);

        // Add extension filters to the image file chooser
        imageFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("Any", "*.*")
        );
    }

    // Adds a Node containing a text field for editing the header title.
    private static Node getHeaderTitleBox(SpokeGraphPromptSceneModel model, SceneGraph graph) {
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
    private static Node getHeaderBodyBox(SpokeGraphPromptSceneModel model, SceneGraph graph) {
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

    private static Node getCareerBox(SpokeGraphPromptSceneModel model, SceneGraph graph) {
        TextField centerTextField = new TextField(model.careerCenterText);

        // Listeners to update the position
        centerTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.careerCenterText = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        VBox vbox = new VBox(new Label("Career Graph Center Text:"), centerTextField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static Node getPromptBox(SpokeGraphPromptSceneModel model, SceneGraph graph) {
        TextField promptTextField = new TextField(model.promptText);

        // Listeners to update the position
        promptTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.promptText = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        VBox vbox = new VBox(new Label("Prompt Graph Center Text:"), promptTextField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    /**
     * Creates a Node with editing controls for all the answers, as well as a button to add
     * additional answers. See createAnswerNode for more information on answer editing controls.
     * @param model The PromptSceneModel being edited.
     * @param graph The SceneGraph of the current survey being edited.
     * @return A Node with editing controls for all the answers and a button to add additional
     *         answers.
     */
    private static Node getAnswersBox(Controller controller,
                                      SpokeGraphPromptSceneModel model, SceneGraph graph) {
        // Add a separator to separate the "Answers:" label from the answer sections
        Separator separator = new Separator();
        separator.setPadding(new Insets(0, 0, 10, 0));
        VBox vbox = new VBox(new Label("Answers:"), separator);

        // Create controls for each answer (and add them to the Node)
        for (ButtonModel answer : model.answers) {
            vbox.getChildren().add(createAnswerNode(controller, answer, vbox, model, graph));
        }

        // Setup the button for adding answers
        Button addButton = new Button("+");
        addButton.setOnAction(event -> {
            if (model.answers.length < 4) {
                ButtonModel newAnswer = new ButtonModel();
                newAnswer.target = controller.createNewScene(false).getId();

                // Add the new answer to the PromptSceneModel's answers
                ArrayList<ButtonModel> answersList = new ArrayList<>(Arrays.asList(model.answers));
                answersList.add(newAnswer);
                model.answers = answersList.toArray(new ButtonModel[0]);
                graph.registerSceneModel(model); // Re-register the model to update the scene

                // Add editing controls for the new answer
                int index = vbox.getChildren().size() - 1;
                // Add controls just before the add button
                vbox.getChildren().add(index,
                        createAnswerNode(controller, newAnswer, vbox, model, graph));
                controller.rebuildSceneGraphTreeView();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText("Unable to Create Answer");
                alert.setContentText("This scene can only support up to four answers");
                alert.showAndWait();
            }
        });

        vbox.getChildren().add(addButton);
        vbox.setPadding(PADDING);
        return vbox;
    }

    /**
     * Creates a Node containing all the controls for editing an answer button, including a field
     * to adjust the text, a color picker, buttons to change the shape and image, a drop down for
     * selecting the target, and a button to remove the answer.
     * @param answersContainer The VBox that will contain the controls for all the answers.
     * @param answer The ButtonModel for the answer controls being created.
     * @param model The PromptSceneModel being edited.
     * @param graph The SceneGraph of the current survey being edited.
     * @return A Node containing all the controls for editing an answer button
     */
    private static Node createAnswerNode(Controller controller,
                                         ButtonModel answer, VBox answersContainer,
                                         SpokeGraphPromptSceneModel model, SceneGraph graph) {
        // Setup the text field for editing the answer
        TextField answerField = new TextField(answer.text);
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

        // Setup the button for adding an image to the answer
        Button imageChooseButton = new Button("Image");
        imageChooseButton.setOnAction(event -> {
            // Open the image file chooser
            File file = imageFileChooser.showOpenDialog(null);

            // If null, no file was chosen
            if (file != null) {
                // Set the chooser to open in the same directory next time
                String imagePath = file.getPath();
                String directoryPath =
                        imagePath.substring(0, imagePath.lastIndexOf(File.separator));
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

        VBox answerVbox = new VBox(); // Contains all the editing controls for this answer

        // Setup the button for removing an answer
        Button removeButton = new Button("x");
        removeButton.setOnAction(event -> {
            // Remove the answer from the PromptSceneModel's answers
            ArrayList<ButtonModel> answersList = new ArrayList<>(Arrays.asList(model.answers));
            answersList.remove(answer);
            model.answers = answersList.toArray(new ButtonModel[0]);
            graph.registerSceneModel(model); // Re-register the model to update the scene

            // Remove the editing controls for this answer from the parent container
            answersContainer.getChildren().remove(answerVbox);
            controller.rebuildSceneGraphTreeView();
        });

        // Setup the combo-box for choosing the answers target scene
        ArrayList<String> sceneIds = new ArrayList<>(graph.getSceneIds());
        sceneIds.remove(model.id); // Prevent a scene from navigating to itself

        ArrayList<SceneTarget> sceneTargets = new ArrayList<>();
        for (String id : sceneIds) {
            sceneTargets.add(new SceneTarget(id, graph.getSceneById(id).getName()));
        }

        ComboBox<SceneTarget> targetComboBox =
                new ComboBox<>(FXCollections.observableList(sceneTargets).sorted());

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

        // Setup combo-box for choosing Riasec categories
        ComboBox<Riasec> riasecComboBox = new ComboBox<>(RIASEC_VALUES);
        riasecComboBox.setValue(answer.category);
        riasecComboBox.setOnAction(event -> {
            Riasec category = riasecComboBox.getValue();
            if (!answer.category.equals(category)) {
                answer.category = category;
                graph.registerSceneModel(model);
            }
        });

        // Create an HBox with a "Target: " label and the combo-box
        HBox targetsBox = new HBox(new Label("Target: "), targetComboBox);
        targetsBox.setPadding(new Insets(0, 0, 0, 5));

        HBox riasecBox = new HBox(new Label("Holland Code: "), riasecComboBox);

        // Add a separator so answers are visually separated
        Separator separator = new Separator();
        separator.setPadding(ANSWER_PADDING);

        // Put all the answer controls together
        HBox editingControls = new HBox(colorPicker, imageChooseButton, removeButton);
        answerVbox.getChildren().addAll(answerField, editingControls, targetsBox, riasecBox,
            separator);
        return answerVbox;
    }

    private static Node getCareerCenterColor(SpokeGraphPromptSceneModel model, SceneGraph graph) {
        VBox vbox = new VBox(new Label("Career Center Color"));
        // Setup the color picker for changing the answer color
        Color initialColor = Color.rgb(model.answerCenterColor[0],
                model.answerCenterColor[1], model.answerCenterColor[2]);
        ColorPicker colorPicker = new ColorPicker(initialColor);
        colorPicker.setOnAction(event -> {
            // Set the answer color to the new color
            Color newColor = colorPicker.getValue();
            model.careersCenterColor[0] = (int) (newColor.getRed() * COLOR_RANGE);
            model.careersCenterColor[1] = (int) (newColor.getGreen() * COLOR_RANGE);
            model.careersCenterColor[2] = (int) (newColor.getBlue() * COLOR_RANGE);

            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        vbox.getChildren().add(colorPicker);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static Node getAnswerCenterColor(SpokeGraphPromptSceneModel model, SceneGraph graph) {
        VBox vbox = new VBox(new Label("Answers Center Color"));
        // Setup the color picker for changing the answer color
        Color initialColor = Color.rgb(model.answerCenterColor[0],
                model.answerCenterColor[1], model.answerCenterColor[2]);
        ColorPicker colorPicker = new ColorPicker(initialColor);
        colorPicker.setOnAction(event -> {
            // Set the answer color to the new color
            Color newColor = colorPicker.getValue();
            model.answerCenterColor[0] = (int) (newColor.getRed() * COLOR_RANGE);
            model.answerCenterColor[1] = (int) (newColor.getGreen() * COLOR_RANGE);
            model.answerCenterColor[2] = (int) (newColor.getBlue() * COLOR_RANGE);

            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        vbox.getChildren().add(colorPicker);
        vbox.setPadding(PADDING);
        return vbox;
    }
}