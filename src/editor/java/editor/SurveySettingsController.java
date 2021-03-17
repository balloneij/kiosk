package editor;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import kiosk.Kiosk;
import kiosk.Settings;

public class SurveySettingsController implements Initializable {

    @FXML
    private Spinner<Integer> widthSpinner;
    @FXML
    private Spinner<Integer> heightSpinner;
    @FXML
    private Spinner<Integer> timeOutSpinner;
    @FXML
    public Button okButton;
    @FXML
    public static Stage root;

    public static Editor editor;

    protected static Settings currentSettings = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentSettings = Settings.readSettings();
        widthSpinner.setEditable(true);
        heightSpinner.setEditable(true);
        widthSpinner.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int i) {
                setValue(getValue() - i);
                heightSpinner.getValueFactory().setValue(9 * getValue() / 16);
            }

            @Override
            public void increment(int i) {
                setValue(getValue() + i);
                heightSpinner.getValueFactory().setValue(9 * getValue() / 16);
            }
        });
        heightSpinner.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int i) {
                setValue(getValue() - i);
                widthSpinner.getValueFactory().setValue(16 * getValue() / 9);
            }

            @Override
            public void increment(int i) {
                setValue(getValue() + i);
                widthSpinner.getValueFactory().setValue(16 * getValue() / 9);
            }
        });
        widthSpinner.addEventHandler(Event.ANY, e -> {
            if (e.getEventType().getName().equals("KEY_RELEASED")
                    && !((KeyEvent) e).getText().isEmpty()) {
                int newValue = 0;
                try {
                    String textValue = ((Spinner) e.getSource()).getEditor().getText();
                    newValue = Integer.parseInt(textValue);
                } catch (NumberFormatException exception) {
                    newValue = widthSpinner.getValue();
                }
                int caretPosition = widthSpinner.getEditor().getCaretPosition();
                widthSpinner.getValueFactory().setValue(newValue);
                heightSpinner.getValueFactory().setValue(9 * newValue / 16);
                widthSpinner.getEditor().positionCaret(caretPosition);
            }
        });
        heightSpinner.addEventHandler(Event.ANY, e -> {
            if (e.getEventType().getName().equals("KEY_RELEASED")) {
                int newValue = 0;
                try {
                    String textValue = ((Spinner) e.getSource()).getEditor().getText();
                    newValue = Integer.parseInt(textValue);
                } catch (NumberFormatException exception) {
                    newValue = heightSpinner.getValue();
                }
                int caretPosition = heightSpinner.getEditor().getCaretPosition();
                heightSpinner.getValueFactory().setValue(newValue);
                widthSpinner.getValueFactory().setValue(16 * newValue / 9);
                heightSpinner.getEditor().positionCaret(caretPosition);
            }
        });

        Settings settings = currentSettings != null ? currentSettings : Kiosk.getSettings();
        widthSpinner.getValueFactory().setValue(settings.screenW);
        heightSpinner.getValueFactory().setValue(settings.screenH);
        timeOutSpinner.setEditable(true);
        timeOutSpinner.increment(settings.timeoutMillis / 1000);
    }

    /**
     * When the user clicks OK, this will apply the new settings and close the dialog.
     */
    @FXML
    public void onSubmit() {
        int width = widthSpinner.getValue();
        int height = heightSpinner.getValue();
        int timeOut = timeOutSpinner.getValue();

        Settings settings = new Settings();
        settings.screenH = height;
        settings.screenW = width;
        settings.timeoutMillis = timeOut * 1000; // Convert ms to seconds
        settings.writeSettings();

        boolean restartNeeded;
        restartNeeded = currentSettings == null
            ? settings.screenH != Editor.getSettings().screenH
                || settings.screenW != Editor.getSettings().screenW :
            currentSettings.screenH != settings.screenH
                || settings.screenW != currentSettings.screenW;
        if (restartNeeded) {
            ButtonType restartLater = new ButtonType(
                    "Close Later", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType restartNow = new ButtonType("Close Now", ButtonBar.ButtonData.OK_DONE);
            Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "In order for all of these settings to apply, "
                        + "the editor must be restarted.",
                restartNow,
                restartLater);
            alert.setTitle("Restart Required");
            alert.setContentText("In order for all of these settings to apply, "
                + "the editor must be closed and re-opened.");
            Optional<ButtonType> result = alert.showAndWait();
            Editor.applySettings(settings);
            SurveySettingsController.currentSettings = settings;
            if (result.get() == restartNow) {
                System.exit(0);
            }
        } else {
            Editor.applySettings(settings);
            SurveySettingsController.currentSettings = settings;
        }

        if (root != null) {
            root.close();
        }
    }
}
