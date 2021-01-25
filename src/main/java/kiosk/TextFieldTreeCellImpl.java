package kiosk;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import kiosk.models.SceneModel;

public class TextFieldTreeCellImpl extends TreeCell<SceneModel> {
    private TextField textField;

    public TextFieldTreeCellImpl() {

    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            createTextField();
        }
        setText(getItem().getName());
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
                    textField.setText(getString());
                    getItem().setName(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(getTreeItem().getGraphic());
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(getItem());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            }
        });
    }

    // We want the SceneModel's actual name, not the toString()
    private String getString() {
        return getItem() == null ? "" : getItem().getName();
    }
}
