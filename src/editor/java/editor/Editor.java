package editor;

import java.io.File;
import javafx.application.Application;
import kiosk.Kiosk;
import kiosk.Settings;
import kiosk.models.LoadedSurveyModel;
import processing.core.PSurface;
import processing.javafx.PSurfaceFX;

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

    @Override
    protected PSurface initSurface() {
        g = createPrimaryGraphics();
        PSurface genericSurface = g.createSurface();
        PSurfaceFX fxSurface = (PSurfaceFX) genericSurface;

        fxSurface.sketch = this;
        App.surface = fxSurface;
        Controller.surface = fxSurface;
        Controller.sceneGraph = this.sceneGraph;

        new Thread(() -> Application.launch(App.class)).start();

        while (fxSurface.stage == null) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Error sleeping");
            }
        }

        this.surface = fxSurface;
        return surface;
    }

    /**
     *  Writes the new settings out to a file and chooses which settings to
     *  apply depending on whether or not they can be applied without a restart.
     * @param newSettings The new settings class containing the newly set settings.
     * @return True indicates that the user needs to restart the program for
     *          some of the settings to apply.
     */
    protected static boolean applySettings(Settings newSettings) {
        newSettings.writeSettings();
        boolean restartRequired =
            newSettings.screenH != Kiosk.settings.screenH
            || newSettings.screenW != Kiosk.settings.screenW;
        Kiosk.settings.timeoutMillis = newSettings.timeoutMillis;
        return restartRequired;
    }

    /**
     * Starts the editor.
     * @param args unused
     */
    public static void main(String[] args) {
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

        Editor editor = new Editor("survey.xml", editorSettings);
        editor.runSketch();
    }
}
