package kiosk;

import graphics.Color;
import graphics.Graphics;
import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
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
import kiosk.models.CareerModel;
import kiosk.models.DefaultSceneModel;
import kiosk.models.ErrorSceneModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.SceneModel;
import kiosk.models.TimeoutSceneModel;
import kiosk.scenes.Control;
import kiosk.scenes.Scene;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Kiosk extends PApplet {

    protected SceneGraph sceneGraph;
    private String surveyPath;
    private CareerModel[] careers;
    private final FilterGroupModel[] filters;
    private Scene lastScene;
    private SceneModel lastSceneModel;
    private final Map<InputEvent, LinkedList<EventListener<MouseEvent>>> mouseListeners;
    private int lastMillis = 0;
    protected static Settings settings;
    private int newSceneMillis;
    private boolean timeoutActive = false;
    private boolean hotkeysEnabled = true;
    private boolean shouldTimeout = true;
    private boolean isFullScreen = false;

    private static JFileChooser fileChooser;

    /**
     * Create a Kiosk and loads the survey specified in the path provided.
     * @param surveyPath to load from
     */
    public Kiosk(String surveyPath) {
        this(surveyPath, Settings.readSettings());
    }

    /**
     * Create a kiosk and loads the survey specified in the path provided.
     * @param surveyPath to load from
     * @param fullScreenDesired if this kiosk should be in fullscreen
     */
    public Kiosk(String surveyPath, boolean fullScreenDesired) {
        this(surveyPath, Settings.readSettings(fullScreenDesired));
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
            survey = LoadedSurveyModel.readFromFile(new File(surveyPath));
        } else {
            List<SceneModel> defaultScenes = new ArrayList<>();
            defaultScenes.add(new DefaultSceneModel());
            survey = new LoadedSurveyModel(defaultScenes);
        }
        this.sceneGraph = new SceneGraph(survey);
        this.careers = survey.careers;
        this.filters = survey.filters;

        this.mouseListeners = new LinkedHashMap<>();

        for (InputEvent e : InputEvent.values()) {
            this.mouseListeners.put(e, new LinkedList<>());
        }

        Color.setSketch(this);
    }

    /**
     * Load a survey from the file specified. If the file cannot be loaded,
     * a survey is constructed with an error scene to notify the user.
     * @param file to try loading from
     */
    public void loadSurveyFile(File file) {
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

        // Update the scene graph
        sceneGraph.loadSurvey(survey);
        this.careers = survey.careers;
        sceneGraph.reset();
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
        size(settings.screenW, settings.screenH);
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
        this.lastMillis = millis();
        Graphics.loadFonts();
    }

    @Override
    public void draw() {
        // Compute the time delta in seconds
        int currMillis = millis();
        float dt = (float) (currMillis - this.lastMillis) / 1000;
        this.lastMillis = currMillis;

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
            this.newSceneMillis = currMillis;

            this.lastScene = currentScene;
            this.lastSceneModel = currentSceneModel;
        }

        // Update and draw the scene
        currentScene.update(dt, this.sceneGraph);
        currentScene.draw(this);

        int currentSceneMillis = currMillis - this.newSceneMillis;

        // Check for timeout (since the current scene has been loaded)
        // Make sure it's not the root scene though first
        // Also make sure that we weren't previously on the root scene,
        // otherwise we get the timeout popup immediately
        currentSceneModel = this.sceneGraph.getCurrentSceneModel();
        if (lastSceneModel.getId().equals(sceneGraph.getRootSceneModel().getId())) {
            currentSceneMillis = 0;
        }
        if (!currentSceneModel.getId().equals(sceneGraph.getRootSceneModel().getId())
                && currentSceneMillis > Kiosk.settings.timeoutMillis && shouldTimeout) {
            if (timeoutActive) {
                // Clear the timeoutActive flag
                // Needed here because a sceneGraph reset doesn't clear the flag automatically
                timeoutActive = false;
                this.sceneGraph.reset();
            } else {
                // Create pop-up
                // note that it gets drawn in the next draw() call
                this.sceneGraph.pushScene(new TimeoutSceneModel());

                // Set the timeoutActive flag so this doesn't get called twice
                timeoutActive = true;
            }
        }
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

    public CareerModel[] getAllCareers() {
        return careers;
    }

    public FilterGroupModel[] getFilters() {
        return filters;
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
    }

    /**
     * Hook a map of event listeners to the kiosk.
     * @param listeners to attach
     */
    public void hookControl(Map<InputEvent, EventListener> listeners) {
        for (InputEvent key : listeners.keySet()) {
            this.mouseListeners.get(key).push(listeners.get(key));
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
                this.sceneGraph.reset();
            } else if (event.getKeyCode() == 122) {
                // F11 Key Press
                Settings s = new Settings(!isFullScreen);
                Kiosk kioskNew = new Kiosk(this.surveyPath, s);
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

    public static Settings getSettings() {
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