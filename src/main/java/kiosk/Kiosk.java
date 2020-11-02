package kiosk;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.SpokeGraphPromptSceneModel;
import kiosk.models.WeightedSpokeGraphSceneModel;
import kiosk.scenes.Control;
import kiosk.scenes.Scene;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Kiosk extends PApplet {

    protected final SceneGraph sceneGraph;
    private Scene lastScene;
    private final Map<InputEvent, LinkedList<EventListener<MouseEvent>>> mouseListeners;
    private int lastMillis = 0;
    private final int timeoutMillis = 30000; //TODO replace with info from settings xml
    private int newSceneMillis;

    /**
     * Draws scenes.
     */
    public Kiosk() {
        this.sceneGraph = new SceneGraph(LoadedSurveyModel.readFromFile(new File("survey.xml")));
        this.sceneGraph.pushScene(new SpokeGraphPromptSceneModel(
                "Now for a few questions about you.",
                "You can go back and change your answers. If you want to.",
                "Build\nResilian\nCities",
                new String[] {
                    "Civil\nEngineer", "Environmental\nEngineer", "Structural\nEngineer",
                    "Mechanical\nEngineer,", "Architect", "Urban\nPlanner", "Construction\n& Traces",
                    "Communications", "Public\nPolicy\nLeader", "Data\nScientist"
                },
                new int[] {2, 3, 2, 2, 5, 2, 4, 2, 2, 3},
                "How much do you\nlove to play\nwith numbers?",
                new String[]{"I love\nplaying\nwith\nnumbers!", "Math is fun\nand\nuseful.",
                        "Math is not\nreally my\nthing."},
                "WeightedSpokeGraph"
        ));
        this.mouseListeners = new LinkedHashMap<>();

        for (InputEvent e : InputEvent.values()) {
            this.mouseListeners.put(e, new LinkedList<>());
        }
    }

    @Override
    public void settings() {
        size(640, 360);
    }

    @Override
    public void setup() {
        super.setup();
        this.lastMillis = millis();
    }

    @Override
    public void draw() {
        // Compute the time delta in seconds
        int currMillis = millis();
        float dt = (float) (currMillis - this.lastMillis) / 1000;
        this.lastMillis = currMillis;

        // Get the current scene
        Scene currentScene = this.sceneGraph.getCurrentScene();

        // Initialize the current scene if it hasn't been
        if (currentScene != this.lastScene) {
            this.clearEventListeners();
            currentScene.init(this);
            this.lastScene = currentScene;
            // Record when a new scene is loaded
            this.newSceneMillis = millis();
        }

        // Update and draw the scene
        currentScene.update(dt, this.sceneGraph);
        currentScene.draw(this);

        // Check for timeout (since the current scene has been loaded)
        int currentSceneMillis = millis() - this.newSceneMillis;
        if (currentSceneMillis > this.timeoutMillis) {
            // Reset the kiosk
            this.sceneGraph.reset();
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
    public void hookControl(Control<MouseEvent> control) {
        var newListeners = control.getEventListeners();

        for (InputEvent key : newListeners.keySet()) {
            this.mouseListeners.get(key).push(newListeners.get(key));
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

    public void run() {
        this.runSketch();
    }
}
