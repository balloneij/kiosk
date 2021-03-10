package editor.sceneloaders;

import editor.Controller;
import javafx.application.Platform;
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
        String name = model.getName();
        name = name.replaceAll("⇱", "");
        name = name.replaceAll("√", "");

        var nameField = new TextField(name);

        // just so lambda doesn't yell at us
        String finalName = name;
        nameField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)
                    && !finalName.equals(nameField.getText())) {
                evaluateNameProperty(controller, model, graph, nameField);
            }
        });

        var vbox = new VBox(new Label("Name:"), nameField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    private static void evaluateNameProperty(Controller controller, SceneModel model,
                                             SceneGraph graph, TextField nameField) {
        var oldName = model.getName();
        oldName = oldName.replaceAll("⇱", "");
        oldName = oldName.replaceAll("√", "");

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

}
