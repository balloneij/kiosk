package kiosk;

import kiosk.scenes.PromptScene;
import kiosk.scenes.Scene;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class Kiosk extends PApplet {

    private final SceneControl sceneControl;
    private int lastMillis;
    private Scene lastScene;
    private Map<String, LinkedList<EventCallback>> callbacks;

    public Kiosk()
    {
        this.sceneControl = new SceneControl();
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
        this.sceneControl.pushScene(new PromptScene("Which do you like best?", new String[]{
                "Dogs",
                "Cats"
        }, false));
    }

    @Override
    public void draw() {
        int currMillis = millis();
        float dt = (float)(currMillis - this.lastMillis) / 1000;
        this.lastMillis = currMillis;

        Scene currentScene = this.sceneControl.currentScene();
        if (currentScene != this.lastScene)
        {
            this.clearCallbacks();
            currentScene.init(this);
            this.lastScene = currentScene;
        }
        currentScene.update(dt, this.sceneControl);
        currentScene.draw(this);
    }

    public void clearCallbacks()
    {
        this.callbacks.get("mouseReleased").clear();
    }

    public void addMouseReleasedCallback(EventCallback callback)
    {
        this.callbacks.get("mouseReleased").push(callback);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        for (EventCallback callback : this.callbacks.get("mouseReleased")) {
            callback.invoke(event);
        }
    }

    public static void main(String[] args)
    {
        Kiosk kiosk = new Kiosk();
        kiosk.runSketch();
    }
}
