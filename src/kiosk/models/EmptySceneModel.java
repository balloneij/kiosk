package kiosk.models;

import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;

public class EmptySceneModel implements SceneModel {
    @Override
    public Scene createScene() {
        return new EmptyScene();
    }
}
