package kiosk;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import kiosk.models.ButtonModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.ResetModel;
import kiosk.models.WaveTransitionSceneModel;
import kiosk.scenes.Scene;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class Kiosk extends PApplet {

    private final SceneGraph sceneGraph;
    private Scene lastScene;
    private Map<String, LinkedList<EventCallback>> callbacks;
    private int lastMillis = 0;

    /**
     * Draws scenes.
     */
    public Kiosk() {
        this.sceneGraph = Kiosk.createExampleSceneGraph();
        this.callbacks = new LinkedHashMap<>();
        this.callbacks.put("mouseReleased", new LinkedList<>());
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
            this.clearCallbacks();
            currentScene.init(this);
            this.lastScene = currentScene;
        }

        // Update and draw the scene
        currentScene.update(dt, this.sceneGraph);
        currentScene.draw(this);
    }

    public void clearCallbacks() {
        this.callbacks.get("mouseReleased").clear();
    }

    public void addMouseReleasedCallback(EventCallback callback) {
        this.callbacks.get("mouseReleased").push(callback);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        for (EventCallback callback : this.callbacks.get("mouseReleased")) {
            callback.invoke(event);
        }
    }

    private static SceneGraph createExampleSceneGraph() {
        // TODO: Replace this manually object construction with XML

        var reset = new WaveTransitionSceneModel(new ResetModel(), false);

        var catsOrDogs = new PromptSceneModel("Which do you prefer?", new ButtonModel[]{
            new ButtonModel("Cats", reset),
            new ButtonModel("Dogs", reset)
        }, false);
        var transitionToCatsOrDogs = new WaveTransitionSceneModel(catsOrDogs, true);

        var coffee = new PromptSceneModel("How do you like your coffee?", new ButtonModel[]{
            new ButtonModel("Black", transitionToCatsOrDogs),
            new ButtonModel("Blacker", transitionToCatsOrDogs)
        }, true);
        var transitionToCoffee = new WaveTransitionSceneModel(coffee, false);

        var yogurt = new PromptSceneModel("Are you supposed to stir greek yogurt?",
                new ButtonModel[] {
                    new ButtonModel("No", transitionToCoffee)
                }, false);
        var transitionToYogurt = new WaveTransitionSceneModel(yogurt, true);

        var caps = new PromptSceneModel("Caps! Caps for sale!", new ButtonModel[]{
            new ButtonModel("Fifty", transitionToYogurt),
            new ButtonModel("cents", transitionToYogurt),
            new ButtonModel("a", transitionToYogurt),
            new ButtonModel("cap", transitionToYogurt),
            new ButtonModel("!", transitionToYogurt)
        }, true);

        return new SceneGraph(caps);
    }

    public static void main(String[] args) {
        Kiosk kiosk = new Kiosk();
        kiosk.runSketch();
    }
}
