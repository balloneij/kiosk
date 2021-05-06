package kiosk;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import graphics.Boop;
import graphics.Color;
import graphics.Graphics;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import kiosk.models.ButtonModel;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.stage.Stage;
import kiosk.models.CareerModel;
import kiosk.models.DefaultSceneModel;
import kiosk.models.ErrorSceneModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.SceneModel;
import kiosk.models.TimeoutSceneModel;
import kiosk.scenes.Control;
import kiosk.scenes.Scene;
import kiosk.scenes.TimeoutScene;
import processing.core.PApplet;
import processing.core.PSurface;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Kiosk extends PApplet {

    protected SceneGraph sceneGraph;
    private String surveyPath;
    private Scene lastScene;
    private SceneModel lastSceneModel;
    private boolean currentSceneIsRoot = false;
    private final Map<InputEvent, LinkedList<EventListener<MouseEvent>>> mouseListeners;
    private final Map<TouchScreenEvent, LinkedList<EventListener<TouchEvent>>> touchListeners;
    private TouchPoint touchPoint;
    private long lastNanos = 0;
    protected static Settings settings;
    private Boop boop;
    private long newSceneMillis;
    private boolean timeoutActive = false;
    private boolean hotkeysEnabled = true;
    private boolean shouldTimeout = true;
    private File loadedFile;
    private boolean isFullScreen = false;
    private boolean fontsLoaded = false;

    private static JFileChooser fileChooser;

    /**
     * Create a Kiosk and loads the survey specified in the path provided.
     * @param surveyPath to load from
     */
    public Kiosk(String surveyPath) {
        this(surveyPath, Settings.readSettings());
    }

    /**
     * For use in the editor when the width and height of the kiosk
     * needs to be different than what's specified in the settings file.
     * @param surveyPath the path of the survey XML
     * @param settings the settings to use
     */
    public Kiosk(String surveyPath, Settings settings) {
        // Configure fileChooser style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Could not set the UI for the file chooser");
        }

        // Create the fileChooser (default it to the working directory) and make it always-on-top
        String initialDirectory = System.getProperty("user.dir");
        fileChooser = new JFileChooser(new File(initialDirectory)) {
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog dialog = super.createDialog(parent);
                dialog.setAlwaysOnTop(true); // keeps the dialog from being behind the window
                return dialog;
            }
        };

        // Only allow xml files
        fileChooser.setFileFilter(
                new FileNameExtensionFilter("XML file (*.xml)", "xml"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        Kiosk.settings = settings;

        LoadedSurveyModel survey;
        this.surveyPath = surveyPath;
        if (!surveyPath.isEmpty()) {
            this.loadedFile = new File(surveyPath);
            survey = LoadedSurveyModel.readFromFile(loadedFile);
        } else {
            List<SceneModel> defaultScenes = new ArrayList<>();
            defaultScenes.add(new DefaultSceneModel());
            survey = new LoadedSurveyModel(defaultScenes);
        }

        File careersFile = new File(CareerModelLoader.DEFAULT_CAREERS_CSV_PATH);
        if (!careersFile.exists()) {
            try {
                careersFile.createNewFile();
            } catch (IOException e) {
                // Recoverable without any issues
            }
        }

        CareerModelLoader careerModelLoader = new CareerModelLoader(careersFile);
        this.sceneGraph = new SceneGraph(survey, careerModelLoader);

        if (careerModelLoader.hasIssues()) {
            DefaultSceneModel model = new DefaultSceneModel();
            model.message = careerModelLoader.getIssuesSummary();
            this.sceneGraph.pushScene(model);
        }

        this.mouseListeners = new LinkedHashMap<>();
        for (InputEvent e : InputEvent.values()) {
            this.mouseListeners.put(e, new LinkedList<>());
        }

        this.touchListeners = new LinkedHashMap<>();
        for (TouchScreenEvent e : TouchScreenEvent.values()) {
            this.touchListeners.put(e, new LinkedList<>());
        }

        Color.setSketch(this);

        boop = new Boop();
    }

    @Override
    protected PSurface initSurface() {
        surface = super.initSurface();
        final Canvas canvas = (Canvas) surface.getNative();
        final javafx.scene.Scene oldScene = canvas.getScene();
        Stage stage = (Stage) oldScene.getWindow();

        stage.addEventHandler(TouchEvent.TOUCH_PRESSED, event -> {
            if (touchPoint == null) {
                touchPoint = event.getTouchPoint();
                for (EventListener listener : this.touchListeners.get(TouchScreenEvent.TouchPressed)) {
                    listener.invoke(event);
                }
                boop.checkTap(this, event);
            }
        });

        stage.addEventHandler(TouchEvent.TOUCH_RELEASED, event -> {
            if (touchPoint != null && touchPoint.getId() == event.getTouchPoint().getId()) {
                touchPoint = null;
                for (EventListener listener : this.touchListeners.get(TouchScreenEvent.TouchReleased)) {
                    listener.invoke(event);
                }
            }
        });

        return surface;
    }

    /**
     * Load a survey from the file specified. If the file cannot be loaded,
     * a survey is constructed with an error scene to notify the user.
     * @param file to try loading from
     */
    public void loadSurveyFile(File file) {
        this.loadedFile = file;
        LoadedSurveyModel survey;
        try {
            // Load the survey
            survey = LoadedSurveyModel.readFromFile(file);
        } catch (Exception exception) {

            // Create an error survey
            String errorMsg = "Could not read from survey at '" + file.getPath()
                    + "'\nThe XML is probably deformed in some way."
                    + "\nRefer to the console for more specific details.";
            survey = new LoadedSurveyModel();
            survey.scenes = new SceneModel[]{ new ErrorSceneModel(errorMsg) };

            // Unhandled exception when creating the survey file
            exception.printStackTrace();
        }

        // Create career loader
        CareerModelLoader careerModelLoader =
                new CareerModelLoader(new File(CareerModelLoader.DEFAULT_CAREERS_CSV_PATH));

        // Reload the survey
        sceneGraph.loadSurvey(survey, careerModelLoader);

        // Push any issues as the first scene
        sceneGraph.reset();
        if (careerModelLoader.hasIssues()) {
            DefaultSceneModel model = new DefaultSceneModel();
            model.message = careerModelLoader.getIssuesSummary();
            sceneGraph.pushScene(model);
        }
    }

    public void reloadSettings() {
        settings = Settings.readSettings();
    }

    @Override
    public void settings() {
        if (settings.fullScreenDesired) {
            isFullScreen = true;
            fullScreen();
        } else {
            isFullScreen = false;
        }
        size(settings.screenW, settings.screenH, FX2D);
    }

    public void enableTimeout() {
        shouldTimeout = true;
    }

    public void disableTimeout() {
        shouldTimeout = false;
    }

    @Override
    public void setup() {
        super.setup();
        this.lastNanos = System.nanoTime();
        boop.loadVariables(this);
        if (!fontsLoaded) {
            Graphics.loadFonts();
            fontsLoaded = true;
        }
    }

    @Override
    public void draw() {
        // Clear out the previous frame
        this.background(0);

        // Check for frameCount rollover
        if (this.frameCount <= 0) {
            this.frameCount = this.frameCount - Integer.MIN_VALUE + 1;
        }

        // Compute the time delta in seconds
        long currentNanos = System.nanoTime();
        long currentMillis = currentNanos / 1000000;
        float dt = (float) (currentNanos - this.lastNanos) / 1000000000;
        this.lastNanos = currentNanos;

        // Get the current scene and sceneModel
        Scene currentScene = this.sceneGraph.getCurrentScene();
        SceneModel currentSceneModel = this.sceneGraph.getCurrentSceneModel();

        // Initialize the current scene if it hasn't been
        if (currentScene != this.lastScene) {
            this.clearEventListeners();
            currentScene.init(this);

            if (lastScene != null && lastScene.getClass().getName().contains("TimeoutScene")) {
                timeoutActive = false;
            }

            // Record when a new scene is loaded
            this.newSceneMillis = currentMillis;

            this.lastScene = currentScene;
            this.lastSceneModel = currentSceneModel;

            currentSceneIsRoot =
                    lastSceneModel.getId().equals(sceneGraph.getRootSceneModel().getId());
        }

        // Update and draw the scene
        currentScene.update(dt, this.sceneGraph);
        Graphics.drawBubbleBackground(this, dt);
        currentScene.draw(this);

        // Check for timeout
        long currentSceneMillis = currentMillis - this.newSceneMillis;
        if (!currentSceneIsRoot) {
            if (timeoutActive && currentSceneMillis > Kiosk.settings.gracePeriodMillis) {
                // Clear the timeoutActive flag
                // Needed here because a sceneGraph reset doesn't clear the flag automatically
                timeoutActive = false;
                this.sceneGraph.reset();
            } else if (currentSceneMillis > Kiosk.settings.timeoutMillis
                    && shouldTimeout && !timeoutActive) {
                // Create pop-up
                // note that it gets drawn in the next draw() call
                this.sceneGraph.pushScene(new TimeoutSceneModel());
                ((TimeoutScene) this.sceneGraph.getCurrentScene()).remainingTime =
                        Kiosk.settings.gracePeriodMillis;

                // Set the timeoutActive flag so this doesn't get called twice
                timeoutActive = true;
            } else if (timeoutActive && this.sceneGraph.getCurrentScene() instanceof TimeoutScene) {
                ((TimeoutScene) this.sceneGraph.getCurrentScene()).remainingTime =
                        (Kiosk.settings.gracePeriodMillis - currentSceneMillis);
            }
        }
        boop.movementLogic(this, currentScene);
    }

    /**
     * Clear every event listener.
     */
    public void clearEventListeners() {
        // Clear the list of listeners for each event type
        for (InputEvent e : InputEvent.values()) {
            this.mouseListeners.get(e).clear();
        }
    }

    public UserScore getUserScore() {
        return this.sceneGraph.getUserScore();
    }

    /**
     * Hook a Control's event listeners to the sketch.
     * @param control with event listeners.
     */
    public void hookControl(Control control) {
        Map<InputEvent, EventListener> newListeners = control.getEventListeners();
        for (InputEvent key : newListeners.keySet()) {
            this.mouseListeners.get(key).push(newListeners.get(key));
        }

        Map<TouchScreenEvent, EventListener> newTouchListeners = control.getTouchEventListeners();
        for (TouchScreenEvent key : newTouchListeners.keySet()) {
            this.touchListeners.get(key).push(newTouchListeners.get(key));
        }
    }

    /**
     * Event handler for when any key is pressed. Only certain keys have responses...
     * F2 - Open JFileChooser to select (only) an XML file
     * F5 - Refresh the current view to reflect the chosen file's paths
     * @param event args passed to the listener
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (this.hotkeysEnabled) {
            if (event.getKeyCode() == 113) {
                // F2 Key Press
                File file = showFileOpener();
                if (file != null) {
                    reloadSettings();
                    loadSurveyFile(file);
                }
            } else if (event.getKeyCode() == 116) {
                // F5 Key Press
                if (loadedFile != null) {
                    reloadSettings();
                    loadSurveyFile(loadedFile);
                }
                this.sceneGraph.reset();
            } else if (event.getKeyCode() == 122) {
                // F11 Key Press
                Settings s = Settings.readSettings();
                s.setFullScreen(!isFullScreen);
                Kiosk kioskNew = new Kiosk(this.surveyPath, s);
                kioskNew.setFontsLoaded(true);
                kioskNew.run();
                this.noLoop();
                this.getSurface().setVisible(false);
            }
        }

        for (EventListener listener : this.mouseListeners.get(InputEvent.KeyPressed)) {
            listener.invoke(event);
        }
    }

    /**
     * Opens the file chooser for the user to select a file.
     * @return the File selected, or null
     */
    public static File showFileOpener() {
        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Opens the file chooser for finding a location to save a file.
     * @return File selected, or null
     */
    public static File showFileSaver() {
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        for (EventListener<MouseEvent> listener
                : this.mouseListeners.get(InputEvent.MouseClicked)) {
            listener.invoke(event);
        }
        boop.checkTap(this, event);
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        for (EventListener<MouseEvent> listener
                : this.mouseListeners.get(InputEvent.MouseDragged)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        for (EventListener<MouseEvent> listener
                : this.mouseListeners.get(InputEvent.MouseEntered)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseExited(MouseEvent event) {
        for (EventListener<MouseEvent> listener
                : this.mouseListeners.get(InputEvent.MouseExited)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        for (EventListener<MouseEvent> listener
                : this.mouseListeners.get(InputEvent.MouseMoved)) {
            listener.invoke(event);
        }
    }

    /**
     * Overload Pressings event handler and propagate
     * to the relevant listeners.
     * @param event args passed to the listener
     */
    @Override
    public void mousePressed(MouseEvent event) {
        for (EventListener<MouseEvent> listener
                : this.mouseListeners.get(InputEvent.MousePressed)) {
            listener.invoke(event);
        }
        boop.checkTap(this, event);
    }

    /**
     * Overload Pressings event handler and propagate
     * to the relevant listeners.
     * @param event args passed to the listener
     */
    @Override
    public void mouseReleased(MouseEvent event) {
        for (EventListener<MouseEvent> listener
                : this.mouseListeners.get(InputEvent.MouseReleased)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        for (EventListener<MouseEvent> listener
                : this.mouseListeners.get(InputEvent.MouseWheel)) {
            listener.invoke(event);
        }
    }

    protected void setFontsLoaded(boolean fontsLoaded) {
        this.fontsLoaded = fontsLoaded;
    }

    /**
     * Gets the current settings configuration.
     * @return The current Settings Object
     */
    public static Settings getSettings() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public SceneModel getRootSceneModel() {
        return sceneGraph.getRootSceneModel();
    }

    public void run() {
        this.runSketch();
    }

    protected void setHotkeysEnabled(boolean hotkeysEnabled) {
        this.hotkeysEnabled = hotkeysEnabled;
    }
}
