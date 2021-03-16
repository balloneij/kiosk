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
    static Alert alert = new Alert(Alert.AlertType.ERROR);

    protected static Node getNameBox(Controller controller, SceneModel model, SceneGraph graph) {
        var nameField = new TextField(getEditableName(model));

        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !ShowingNameAlert
                    && !getEditableName(model).equals(nameField.getText())) {
                evaluateNameProperty(controller, model, graph, nameField);
            }
        });

        nameField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)
                    && !getEditableName(model).equals(nameField.getText())) {
                evaluateNameProperty(controller, model, graph, nameField);
            }
        });

        var vbox = new VBox(new Label("Name:"), nameField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static void evaluateNameProperty(Controller controller, SceneModel model,
                                             SceneGraph graph, TextField nameField) {
        var oldName = getEditableName(model);

        var newValue = nameField.getText();
        alert.setHeaderText("Duplicate Name");
        alert.setContentText(String.format("There is already a scene with the name %s."
                + "\r\n Please try a different name.", newValue));

        if (graph.getSceneModelByName(newValue) == null) { // No matches
            model.setName(newValue);
            controller.rebuildSceneGraphTreeView();
        } else {
            if (!alert.isShowing()) {
                alert.showAndWait();
            }
            nameField.setText(oldName);
            nameField.positionCaret(oldName.length());
        }
    }

    private static String getEditableName(SceneModel model) {
        String name = model.getName();
        name = name.replaceAll("⦸", "");
        return name.replaceAll("✪", "");
    }
}
