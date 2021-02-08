package editor;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import kiosk.models.SceneModel;

public class SceneModelTreeCell extends TreeCell<SceneModel> {
    private Controller controller;
    private TextField textField;
    private final ContextMenu editMenu = new ContextMenu();

    /**
     * Creates a new TreeCell used for displaying SceneModels.
     * @param controller the editor's controller
     */
    public SceneModelTreeCell(Controller controller) {
        this.controller = controller;

        // Creating ContextMenu
        MenuItem rootMenuItem = new MenuItem("Make This Scene the Root");
        rootMenuItem.setOnAction(t -> {
            controller.setRootScene(getItem());
        });
        MenuItem deleteMenuItem = new MenuItem("Delete This Scene");
        deleteMenuItem.setOnAction(t -> {
            controller.deleteScene(getItem());
        });
        // Determine if scene is the root; if so, disable options
        // todo Why does't this work? Why are we FILLED with nulls?
        // todo In the Controller's initialization, this is called like 11 times.
        if ((getItem() != null)
            && (getItem().getId().equals(Controller.sceneGraph.getRootSceneModel().getId()))) {
            rootMenuItem.setDisable(true);
            deleteMenuItem.setDisable(true);
        }
        editMenu.getItems().addAll(rootMenuItem, deleteMenuItem);

        // Create tooltip, if needed
        // todo Same as above, this doesn't work. Once the above is fixed,
        // todo the first check can be removed here too (hopefully)
        if ((getItem() != null)
                && getItem().getName().contains("⇱")) {
            Tooltip orphanInfo = new Tooltip("This scene cannot be reached "
                    + "by any other scenes");
            orphanInfo.setHideDelay(new Duration(.5));
            setTooltip(orphanInfo);
        }

        // todo testing code; prints out Null every time!
        // todo When a new cell is created, apparently it isn't?
        System.out.println(getItem());
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
                setContextMenu(editMenu);
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
