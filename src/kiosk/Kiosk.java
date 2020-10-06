package kiosk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import kiosk.models.ButtonModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.ResetModel;
import kiosk.models.SceneModel;
import kiosk.models.WaveTransitionSceneModel;
import kiosk.scenes.Control;
import kiosk.scenes.Scene;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Kiosk extends PApplet {

    private final SceneGraph sceneGraph;
    private Scene lastScene;
    private final Map<InputEvent, LinkedList<EventListener>> listeners;
    private int lastMillis = 0;

    /**
     * Draws scenes.
     */
    public Kiosk() {
        this.sceneGraph = Kiosk.createExampleSceneGraph();
        this.listeners = new LinkedHashMap<>();

        for (InputEvent e : InputEvent.values()) {
            this.listeners.put(e, new LinkedList<>());
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
        }

        // Update and draw the scene
        currentScene.update(dt, this.sceneGraph);
        currentScene.draw(this);
    }

    /**
     * Clear every event listener.
     */
    public void clearEventListeners() {
        // Clear the list of listeners for each event type
        for (InputEvent e : InputEvent.values()) {
            this.listeners.get(e).clear();
        }
    }

    /**
     * Hook a Control's event listeners to the sketch.
     * @param control with event listeners.
     */
    public void hookControl(Control control) {
        var newListeners = control.getEventListeners();

        for (InputEvent key : newListeners.keySet()) {
            this.listeners.get(key).push(newListeners.get(key));
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        for (EventListener listener : this.listeners.get(InputEvent.MouseClicked)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        for (EventListener listener : this.listeners.get(InputEvent.MouseDragged)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        for (EventListener listener : this.listeners.get(InputEvent.MouseEntered)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseExited(MouseEvent event) {
        for (EventListener listener : this.listeners.get(InputEvent.MouseExited)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        for (EventListener listener : this.listeners.get(InputEvent.MouseMoved)) {
            listener.invoke(event);
        }
    }

    /**
     * Overload Processing's event handler and propagate
     * to the relevant listeners.
     * @param event args passed to the listener
     */
    @Override
    public void mousePressed(MouseEvent event) {
        for (EventListener listener : this.listeners.get(InputEvent.MousePressed)) {
            listener.invoke(event);
        }
    }

    /**
     * Overload Processing's event handler and propagate
     * to the relevant listeners.
     * @param event args passed to the listener
     */
    @Override
    public void mouseReleased(MouseEvent event) {
        for (EventListener listener : this.listeners.get(InputEvent.MouseReleased)) {
            listener.invoke(event);
        }
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        for (EventListener listener : this.listeners.get(InputEvent.MouseWheel)) {
            listener.invoke(event);
        }
    }

    private static SceneGraph createExampleSceneGraph() {
        // TODO: Replace this manually object construction with XML

        var reset = new WaveTransitionSceneModel(new ResetModel(), false, "001");

        var catsOrDogs = new PromptSceneModel("Which do you prefer?", new ButtonModel[]{
            new ButtonModel("Cats", "001"),
            new ButtonModel("Dogs", "001")
        }, false);
        var transitionToCatsOrDogs = new WaveTransitionSceneModel(catsOrDogs, true, "002");

        var coffee = new PromptSceneModel("How do you like your coffee?", new ButtonModel[]{
            new ButtonModel("Black", "002"),
            new ButtonModel("Blacker", "002")
        }, true);
        var transitionToCoffee = new WaveTransitionSceneModel(coffee, false, "003");

        var yogurt = new PromptSceneModel("Are you supposed to stir greek yogurt?",
                new ButtonModel[] {
                    new ButtonModel("No", "003")
                }, false);
        var transitionToYogurt = new WaveTransitionSceneModel(yogurt, true, "004");

        var caps = new PromptSceneModel("Caps! Caps for sale!", new ButtonModel[]{
            new ButtonModel("Fifty", "004"),
            new ButtonModel("cents", "004"),
            new ButtonModel("a", "004"),
            new ButtonModel("cap", "004"),
            new ButtonModel("!", "004")
        }, true);

        var initialScenes = new ArrayList<SceneModel>();
        initialScenes.add(caps);
        initialScenes.add(transitionToYogurt);
        initialScenes.add(yogurt);
        initialScenes.add(transitionToCoffee);
        initialScenes.add(coffee);
        initialScenes.add(transitionToCatsOrDogs);
        initialScenes.add(catsOrDogs);
        initialScenes.add(reset);

        return new SceneGraph(new LoadedSurveyModel(initialScenes));
    }

    public static void main(String[] args) {
        Kiosk kiosk = new Kiosk();
        kiosk.runSketch();
    }
}
