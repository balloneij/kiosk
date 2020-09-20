package kiosk.models;

import kiosk.scenes.ResetScene;
import kiosk.scenes.Scene;

public final class ResetModel implements SceneModel {

    @Override
    public Scene createScene() {
        return new ResetScene();
    }
}
