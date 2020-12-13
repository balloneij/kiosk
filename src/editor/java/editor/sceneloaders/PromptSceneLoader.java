package editor.sceneloaders;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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

    private static Node getAnswersBox(PromptSceneModel model, SceneGraph graph) {
        var vbox = new VBox(new Label("Answers:"));

        // Create fields for each answer (and add them to the Node)
        for (ButtonModel answer : model.answers) {
            var answerField = new TextField(answer.text);
            vbox.getChildren().add(answerField);

            // Listener to update the answer
            answerField.textProperty().addListener((observable, oldValue, newValue) -> {
                answer.text = newValue;
                graph.registerSceneModel(model); // Re-register the model to update the scene
            });
        }

        vbox.setPadding(PADDING);
        return vbox;
    }
}
