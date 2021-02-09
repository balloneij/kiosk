package editor;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import kiosk.SceneGraph;
import kiosk.models.SceneModel;

public class SceneModelTreeCell extends TreeCell<SceneModel> {
    private TextField textField;
    private Controller controller;
    private static Alert alert = new Alert(Alert.AlertType.ERROR);
    public static SceneGraph sceneGraph;

    public SceneModelTreeCell(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            createTextField();
        }

        textField.setText(getName());
        setText(getName());
        setGraphic(textField);
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem().getName());
        setGraphic(getTreeItem().getGraphic());
    }

    @Override
    public void updateItem(SceneModel item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getName());
            setGraphic(getTreeItem().getGraphic());
        }
    }

    private void createTextField() {
        textField = new TextField(getName());
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                if (sceneGraph.getSceneModelByName(textField.getText()) == null) {
                    getItem().setName(textField.getText());
                } else {
                    alert.setContentText(String.format("There is already a scene with the name %s."
                            + "\r\n Please try a different name.", textField.getText()));
                    if (!alert.isShowing()) {
                        alert.showAndWait();
                    }
                    textField.setText(getItem().getName());
                    textField.positionCaret(getItem().getName().length());
                }
                controller.rebuildToolbar(getItem());
                controller.rebuildSceneGraphTreeView();
                commitEdit(getItem());
            } else if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }

    // We want the SceneModel's actual name, not the toString()
    private String getName() {
        return getItem() == null ? "" : getItem().getName();
    }
}
