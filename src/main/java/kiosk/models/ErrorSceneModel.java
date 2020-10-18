package kiosk.models;

import kiosk.scenes.ErrorScene;
import kiosk.scenes.Scene;

public class ErrorSceneModel implements SceneModel {

    public String errorMsg;
    public String id;

    public ErrorSceneModel() {
        this.errorMsg = "null";
        this.id = IdGenerator.getInstance().getNextId();
    }

    public ErrorSceneModel(String errorMsg) {
        this.errorMsg = errorMsg;
        this.id = IdGenerator.getInstance().getNextId();
    }

    @Override
    public Scene createScene() {
        return new ErrorScene(this);
    }

    @Override
    public String getId() {
        return this.id;
    }
}
