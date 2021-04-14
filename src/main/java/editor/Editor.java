package editor;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kiosk.Kiosk;
import kiosk.Settings;
import kiosk.models.LoadedSurveyModel;
import processing.core.PSurface;

public class Editor extends Kiosk {

    /**
     * Aspect ratio of the preview window.
     */
    public static final double PREVIEW_ASPECT_RATIO = 16.0 / 9;

    /**
     * Width of the preview window. Height is calculated based off of
     * the preview window
     */
    public static final int PREVIEW_WIDTH = 1280;

    /**
     * Width of the side toolbar.
     */
    public static final int TOOLBAR_WIDTH = 320;

    /**
     * The instance of the stage. Used to set the title window.
     */
    private static Stage stage;

    /**
     * Instantiates the editor and starts the sketch.
     */
    public Editor(String surveyPath, Settings settings) {
        super(surveyPath, settings);
        this.setHotkeysEnabled(false);
        SurveySettingsController.editor = this;
    }

    @Override
    public void settings() {
        size(Kiosk.settings.screenW, Kiosk.settings.screenH, FX2D);
    }

    /**
     * Uses the current stage to set the title of the editor window.
     * @param newTitle The new title.
     */
    public static void setTitle(String newTitle) {
        if (stage != null) {
            stage.setTitle(newTitle);
        }
    }

    /**
     * Gets the current title of the window.
     * @return The title.
     */
    public static String getTitle() {
        if (stage != null) {
            return stage.getTitle();
        } else {
            return "No stage set";
        }
    }

    @Override
    protected PSurface initSurface() {
        // Pull the secret sauce out of Processing 3
        surface = super.initSurface();
        final Canvas canvas = (Canvas) surface.getNative();
        final Scene oldScene = canvas.getScene();
        stage = (Stage) oldScene.getWindow();
        stage.setOnCloseRequest(new EventHandler<javafx.stage.WindowEvent>() {
            @Override
            public void handle(javafx.stage.WindowEvent event) {
                if (Controller.hasPendingChanges) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "You have unsaved changes! Are you sure you want to exit?");
                    Optional<ButtonType> optional = alert.showAndWait();
                    if (!optional.isPresent()
                            || optional.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) {
                        event.consume();
                    }
                }
            }
        });

        // Attach the scene graph before initialization
        Controller.sceneGraph = sceneGraph;
        Controller.careers = getAllCareers();
        Controller.filters = getFilters();

        // Load FXML and the controller
        FXMLLoader loader = null;
        Parent root = null;
        try {
            File editorFxml = new File("src/main/java/editor/Editor.fxml");
            loader = new FXMLLoader(editorFxml.toURI().toURL());
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Attach the sketch canvas to the preview pane
        Map<String, Object> namespace = loader.getNamespace();  // Map of fx:id's
        StackPane previewPane = (StackPane) namespace.get("surveyPreviewPane");
        previewPane.getChildren().add(canvas);

        // Constrain the canvas by the preview size
        canvas.widthProperty().bind(previewPane.widthProperty());
        canvas.heightProperty().bind(previewPane.heightProperty());

        // Create the new scene
        Scene scene = new Scene(root,
                Editor.PREVIEW_WIDTH + Editor.TOOLBAR_WIDTH,
                (int) (Editor.PREVIEW_WIDTH / Editor.PREVIEW_ASPECT_RATIO));

        // Delays these actions and runs them on the "correct" thread
        Platform.runLater(() -> {
            stage.setScene(scene);
            // A platypus?
            stage.setResizable(false);
        });

        return surface;
    }

    /**
     *  Writes the new settings out to a file and chooses which settings to
     *  apply depending on whether or not they can be applied without a restart.
     * @param newSettings The new settings class containing the newly set settings.
     * @return True indicates that the user needs to restart the program for
     *          some of the settings to apply.
     */
    public static boolean applySettings(Settings newSettings) {
        newSettings.writeSettings();
        boolean restartRequired = true;
        if (Kiosk.settings != null) {
            restartRequired =
                    newSettings.screenH != Kiosk.settings.screenH
                            || newSettings.screenW != Kiosk.settings.screenW;
            Kiosk.settings.timeoutMillis = newSettings.timeoutMillis;
            Kiosk.settings.gracePeriodMillis = newSettings.gracePeriodMillis;
        } else {
            Kiosk.settings = newSettings;
        }
        return restartRequired;
    }

    /**
     * Starts the editor.
     * @param args unused
     */
    public static void main(String[] args) {
        //Kiosk.disableTimeout();
        // Create a sample survey file for demonstrating scenes
        File sampleSurveyFile = new File("sample_survey.xml");
        if (!sampleSurveyFile.exists()) {
            LoadedSurveyModel surveyModel = LoadedSurveyModel.createSampleSurvey();
            surveyModel.writeToFile(sampleSurveyFile);
        }

        // Run the editor
        Settings settings = Settings.readSettings();
        Settings editorSettings = new Settings();
        editorSettings.timeoutMillis = settings.timeoutMillis;
        editorSettings.gracePeriodMillis = settings.gracePeriodMillis;

        Editor editor = new Editor("survey.xml", editorSettings);
        editor.runSketch();
    }
}
