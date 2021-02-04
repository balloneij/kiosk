package editor.sceneloaders;

import editor.Controller;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import kiosk.SceneGraph;
import kiosk.models.SceneModel;

public class SceneLoader {
    // The default padding to space the editing Nodes
    static final Insets PADDING = new Insets(0, 0, 10, 10);
    static boolean ShowingNameAlert = false;

    protected static Node getNameBox(Controller controller, SceneModel model, SceneGraph graph) {
        var nameField = new TextField(model.getName());

        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !ShowingNameAlert) {
                evaluateNameProperty(controller, model, graph, nameField);
            }
        });

        nameField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                evaluateNameProperty(controller, model, graph, nameField);
            }
        });

        var vbox = new VBox(new Label("Name:"), nameField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static void evaluateNameProperty(Controller controller, SceneModel model,
            SceneGraph graph, TextField nameField) {
        var newValue = nameField.getText();
        var oldName = model.getName();
        model.setName(newValue);
        if (graph.containsDuplicateSceneWithName(model)) {
            nameField.setText(oldName);
            nameField.positionCaret(oldName.length());
            if (!ShowingNameAlert) {
                ShowingNameAlert = true;
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        String.format("There is already a scene with the name %s."
                                + "\r\n Please try a different name.", newValue)
                );
                alert.showAndWait();
                ShowingNameAlert = false;
            }
        }
        controller.rebuildSceneGraphTreeView();
    }

}
