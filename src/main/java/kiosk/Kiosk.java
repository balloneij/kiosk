package kiosk;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import kiosk.models.DefaultSceneModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.SceneModel;
import kiosk.models.TimeoutSceneModel;
import kiosk.scenes.Control;
import kiosk.scenes.DefaultScene;
import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class Kiosk extends PApplet {

    protected SceneGraph sceneGraph;
    private Scene lastScene;
    private String surveyFile = "";
    private final Map<InputEvent, LinkedList<EventListener<MouseEvent>>> mouseListeners;
    private int lastMillis = 0;
    protected static Settings settings;
    private int newSceneMillis;
    private boolean timeoutActive = false;

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
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("Could not set the UI for the file chooser");
        }

        Kiosk.settings = settings;

        if (!surveyPath.isEmpty()) {
            surveyFile = surveyPath;
            var loadedSurveyModel = LoadedSurveyModel.readFromFile(new File(surveyPath));
            this.sceneGraph = new SceneGraph(loadedSurveyModel);
        } else {
            List<SceneModel> defaultScenes = new ArrayList<>();
            defaultScenes.add(new DefaultSceneModel());

            this.sceneGraph = new SceneGraph(new LoadedSurveyModel(defaultScenes));
        }

        this.mouseListeners = new LinkedHashMap<>();

        for (InputEvent e : InputEvent.values()) {
            this.mouseListeners.put(e, new LinkedList<>());
        }
    }

    /**
     * Draws scenes.
     */
    public void updateSurveyPath(String surveyPath) {
        settings = Settings.readSettings();

        if (!surveyPath.isEmpty()) {
            surveyFile = surveyPath;
            this.sceneGraph = new SceneGraph(LoadedSurveyModel.readFromFile(new File(surveyPath)));
        } else {
            List<SceneModel> defaultScenes = new ArrayList<>();
            defaultScenes.add(new DefaultSceneModel());

            this.sceneGraph = new SceneGraph(new LoadedSurveyModel(defaultScenes));
        }
    }

    @Override
    public void settings() {
        size(settings.screenW, settings.screenH);
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

            if(lastScene!= null && lastScene.getClass().getName().contains("TimeoutScene")){
                timeoutActive = false;
            }

            // Record when a new scene is loaded
            this.newSceneMillis = currMillis;

            this.lastScene = currentScene;
        }

        // Update and draw the scene
        currentScene.update(dt, this.sceneGraph);
        currentScene.draw(this);

        int currentSceneMillis = currMillis - this.newSceneMillis;

        // Check for timeout (since the current scene has been loaded)
        // Make sure it's not the intro scene though first
        if (!currentSceneModel.getId().equals(sceneGraph.getRootSceneModel().getId())
                && currentSceneMillis > settings.timeoutMillis) {
            if(timeoutActive) {
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
     * Event handler for when any key is pressed. Only certain keys have responses...
     * F2 - Open JFileChooser to select (only) an XML file
     * F5 - Refresh the current view to reflect the chosen file's paths
     * @param event args passed to the listener
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == 113) { //F2 Key Press
            System.out.println("Opening the file explorer...");
            final JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter =
                    new FileNameExtensionFilter("XML file (*.xml)", "xml", "XML");
            fc.setFileFilter(filter);
            fc.setAcceptAllFileFilterUsed(false);
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                surveyFile = fc.getSelectedFile().getPath();
                System.out.println(
                        "Getting " + surveyFile + " in the background for the next refresh\n");
            } else {
                System.out.println("There was an error getting the file.\n");
            }
        } else if (event.getKeyCode() == 116) { //F5 Key Press
            System.out.println("Refreshing the view...\n");
            try {
                updateSurveyPath(surveyFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (EventListener listener : this.mouseListeners.get(InputEvent.KeyPressed)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        for (var listener : this.mouseListeners.get(InputEvent.MouseClicked)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        for (var listener : this.mouseListeners.get(InputEvent.MouseDragged)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        for (var listener : this.mouseListeners.get(InputEvent.MouseEntered)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseExited(MouseEvent event) {
        for (var listener : this.mouseListeners.get(InputEvent.MouseExited)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        for (var listener : this.mouseListeners.get(InputEvent.MouseMoved)) {
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
        for (var listener : this.mouseListeners.get(InputEvent.MousePressed)) {
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
        for (var listener : this.mouseListeners.get(InputEvent.MouseReleased)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        for (var listener : this.mouseListeners.get(InputEvent.MouseWheel)) {
            listener.invoke(event);
        }
    }

    public static Settings getSettings() {
        return settings;
    }

    public void run() {
        this.runSketch();
    }
}