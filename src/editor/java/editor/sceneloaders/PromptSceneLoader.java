package editor.sceneloaders;

import java.util.ArrayList;
import java.util.Arrays;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.PromptSceneModel;

public class PromptSceneLoader {
    // The default padding to space the editing Nodes
    static final Insets PADDING = new Insets(10, 0, 0, 10);

    /**
     * Populates the editor pane with fields for editing the provided SceneModel.
     * @param model The current scene model we want to modify.
     * @param editorPane The main editor view.
     * @param graph The scene graph used to manage application state.
     */
    public static void loadScene(PromptSceneModel model, AnchorPane editorPane, SceneGraph graph) {
        // Get the editing Nodes for the PromptSceneModel properties
        VBox vbox = new VBox(
                getTitleBox(model, graph),
                getPromptBox(model, graph),
                getActionBox(model, graph),
                getAnswersBox(model, graph)
        );

        // Clear the editor pane and re-populate with the new Nodes
        editorPane.getChildren().clear();
        editorPane.getChildren().add(vbox);
    }

    private static Node getTitleBox(PromptSceneModel model, SceneGraph graph) {
        var titleField = new TextField(model.title);

        // Listener to update the title
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.title = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        var vbox = new VBox(new Label("Title:"), titleField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static Node getPromptBox(PromptSceneModel model, SceneGraph graph) {
        var promptField = new TextField(model.prompt);

        // Listener to update the prompt
        promptField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.prompt = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        var vbox = new VBox(new Label("Prompt:"), promptField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static Node getActionBox(PromptSceneModel model, SceneGraph graph) {
        var actionField = new TextField(model.actionPhrase);

        // Listener to update the action phrase
        actionField.textProperty().addListener((observable, oldValue, newValue) -> {
            model.actionPhrase = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        var vbox = new VBox(new Label("Action Phrase:"), actionField);
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
    private static Node getAnswersBox(PromptSceneModel model, SceneGraph graph) {
        var vbox = new VBox(new Label("Answers:"));

        // Create controls for each answer (and add them to the Node)
        for (ButtonModel answer : model.answers) {
            vbox.getChildren().add(createAnswerNode(answer, vbox, model, graph));
        }

        // Setup the button for adding answers
        Button addButton = new Button("+");
        addButton.setOnAction(event -> {
            ButtonModel newAnswer = new ButtonModel();

            // Add the new answer to the PromptSceneModel's answers
            ArrayList<ButtonModel> answersList = new ArrayList<>(Arrays.asList(model.answers));
            answersList.add(newAnswer);
            model.answers = answersList.toArray(ButtonModel[]::new);
            graph.registerSceneModel(model); // Re-register the model to update the scene

            // Add editing controls for the new answer
            int index = vbox.getChildren().size() - 1; // Add controls just before the add button
            vbox.getChildren().add(index, createAnswerNode(newAnswer, vbox, model, graph));
        });

        vbox.getChildren().add(addButton);
        vbox.setPadding(PADDING);
        return vbox;
    }

    /**
     * Creates a Node containing all the controls for editing an answer button, including... TODO.
     * @param answersContainer The VBox that will contain the controls for all the answers.
     * @param answer The ButtonModel for the answer controls being created.
     * @param model The PromptSceneModel being edited.
     * @param graph The SceneGraph of the current survey being edited.
     * @return A Node containing all the controls for editing an answer button
     */
    private static Node createAnswerNode(ButtonModel answer, VBox answersContainer,
                                         PromptSceneModel model, SceneGraph graph) {
        var answerHbox = new HBox(); // Contains all the editing controls for this answer
        var answerField = new TextField(answer.text);

        // Listener to update the answer
        answerField.textProperty().addListener((observable, oldValue, newValue) -> {
            answer.text = newValue;
            graph.registerSceneModel(model); // Re-register the model to update the scene
        });

        // Setup the button for removing an answer
        Button removeButton = new Button("x");
        removeButton.setOnAction(event -> {
            // Remove the answer from the PromptSceneModel's answers
            ArrayList<ButtonModel> answersList = new ArrayList<>(Arrays.asList(model.answers));
            answersList.remove(answer);
            model.answers = answersList.toArray(ButtonModel[]::new);
            graph.registerSceneModel(model); // Re-register the model to update the scene

            // Remove the editing controls for this answer from the parent container
            answersContainer.getChildren().remove(answerHbox);
        });

        answerHbox.getChildren().addAll(answerField, removeButton);
        return answerHbox;
    }
}
