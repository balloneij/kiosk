package editor.sceneloaders;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.PromptSceneModel;

public class PromptSceneLoader {
    /**
     * Loads the scene from the model. Sets up the editor pane by getting properties from the graph.
     * @param model The current scene model we want to modify.
     * @param editorPane The main editor view.
     * @param graph The scene graph used to manage application state.
     */
    public static void loadScene(PromptSceneModel model, AnchorPane editorPane, SceneGraph graph) {
        editorPane.getChildren().clear();

        var questionBox = getQuestionBox(model, graph);
        var invertColorBox = getInvertColorBox(model, graph);
        var answersBox = getButtonBox(model, graph);
        var vbox = new VBox();

        vbox.getChildren().addAll(questionBox, invertColorBox, answersBox);
        editorPane.getChildren().add(vbox);
    }

    private static HBox getQuestionBox(PromptSceneModel model, SceneGraph graph) {
        var hbox = new HBox();
        var questionLabel = new Label("Primary Question: ");
        var textArea = new TextField(model.question);

        textArea.textProperty().addListener((observable, oldvalue, newValue) -> {
            var newModel = new PromptSceneModel(
                    newValue,
                    model.answers,
                    model.invertedColors, model.getId()
            );
            graph.registerSceneModel(newModel);
        });

        var mainContent = new VBox();
        mainContent.getChildren().addAll(questionLabel, textArea);
        hbox.getChildren().add(mainContent);
        hbox.setPadding(new Insets(10, 0, 0, 10));
        return hbox;
    }

    private static Node getInvertColorBox(PromptSceneModel model, SceneGraph graph) {
        var invertCheckBox = new CheckBox("Invert Colors: ");
        invertCheckBox.setSelected(model.invertedColors);

        invertCheckBox.selectedProperty().addListener((observable, oldVal, newVal) -> {
            var newModel = new PromptSceneModel(
                    model.question,
                    model.answers,
                    newVal,
                    model.getId()
            );
            graph.registerSceneModel(newModel);
        });

        var box = new HBox();
        box.getChildren().add(invertCheckBox);
        box.setPadding(new Insets(10,  0, 0, 10));
        return box;
    }

    private static Node getButtonBox(PromptSceneModel model, SceneGraph graph) {
        var hbox = new HBox();
        hbox.getChildren().add(new Label("Answers:"));
        for (int i = 0; i < model.answers.length; i++) {
            var answer = model.answers[i];
            var textField = new TextField(answer.text);
            hbox.getChildren().add(textField);

            var index = i;
            textField.textProperty().addListener((observable, oldVal, newVal) -> {
                model.answers[index] = new ButtonModel(newVal, answer.target);
                var newModel = new PromptSceneModel(
                        model.question,
                        model.answers,
                        model.invertedColors,
                        model.getId()
                );
                graph.registerSceneModel(newModel);
            });
        }
        return hbox;
    }
}
