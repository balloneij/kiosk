package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.TimeoutScene;

public final class TimeoutSceneModel implements SceneModel {

    public String id;
    public String name;
    public String title;
    public String prompt;

    /**
     * Creates an empty PromptSceneModel.
     */
    public TimeoutSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.id = "Timeout Scene";
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
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SceneModel deepCopy() {
        TimeoutSceneModel copy = new TimeoutSceneModel();
        copy.id = this.id;
        copy.name = name;
        copy.title = this.title;
        copy.prompt = this.prompt;

        return copy;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}
