package kiosk;

import java.util.LinkedList;
import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;

public class SceneControl {

    private final LinkedList<Scene> scenes;

    public SceneControl() {
        this.scenes = new LinkedList<>();
    }

    public void pushScene(Scene scene) {
        scenes.push(scene);
    }

    public void popScene() {
        scenes.pop();
    }

    /**
     * Retrieves the active scene if there is one. Otherwise,
     * it creates a new EmptyScene.
     * @return active scene or a new EmptyScene
     */
    public Scene currentScene() {
        if (scenes.size() > 0) {
            return scenes.getFirst();
        } else {
            return new EmptyScene();
        }
    }
}
