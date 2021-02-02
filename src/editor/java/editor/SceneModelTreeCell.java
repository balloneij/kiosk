package editor;

import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import kiosk.models.SceneModel;

public class SceneModelTreeCell extends TreeCell<SceneModel> {
    private TextField textField;
    private Controller controller;

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
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getName());
                    item.setName(getText());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getName());
                setGraphic(getTreeItem().getGraphic());
                item.setName(getText());
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getName());
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ENTER) {
                getItem().setName(textField.getText());
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
