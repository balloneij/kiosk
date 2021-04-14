package editor;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class UnsavedChangesAlert {

    public static final ButtonType SAVE = new ButtonType("Save");
    public static final ButtonType NO_SAVE = new ButtonType("Don't Save");
    public static final ButtonType CANCEL = new ButtonType("Cancel");

    public static Optional<ButtonType> showAndWait() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("You have unsaved changes. If you reload all of those changes will be lost!");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(SAVE, CANCEL, NO_SAVE);

        return alert.showAndWait();
    }
}
