package kiosk.models;

import kiosk.scenes.PromptScene;
import kiosk.scenes.Scene;
import kiosk.scenes.TimeoutScene;

public final class TimeoutSceneModel implements SceneModel {

    public String id;
    public String title;
    public String prompt;

    /**
     * Creates an empty PromptSceneModel.
     */
    public TimeoutSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.title = "";
        this.prompt = "\n\n";
    }

    @Override
    public Scene createScene() {
        return new TimeoutScene(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SceneModel deepCopy() {
        TimeoutSceneModel copy = new TimeoutSceneModel();
        copy.id = this.id;
        copy.title = this.title;
        copy.prompt = this.prompt;

        return copy;
    }
}
