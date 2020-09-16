package kiosk;

import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;

import java.util.LinkedList;

public class SceneControl {

    private final LinkedList<Scene> scenes;

    public SceneControl()
    {
        this.scenes = new LinkedList<>();
    }

    public void pushScene(Scene scene)
    {
        scenes.push(scene);
    }

    public void popScene()
    {
        scenes.pop();
    }

    public Scene currentScene()
    {
        if (scenes.size() > 0)
        {
            return scenes.getFirst();
        }
        else
        {
            return new EmptyScene();
        }
    }
}
