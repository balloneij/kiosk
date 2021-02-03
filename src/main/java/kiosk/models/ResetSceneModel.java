package kiosk.models;

import kiosk.scenes.ResetScene;
import kiosk.scenes.Scene;

public final class ResetSceneModel implements SceneModel {

    public String id;
    public String name;

    /**
     * Constructs a new reset model.
     */
    public ResetSceneModel() {
        this(IdGenerator.getInstance().getNextId());
        this.name = "Reset Scene";
    }

    /**
     * Constructs a new reset model.
     * @param uniqueId An id unique to this specific model.
     */
    public ResetSceneModel(String uniqueId) {
        this.id = uniqueId;
    }

    @Override
    public Scene createScene() {
        return new ResetScene();
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
        return new ResetSceneModel(id);
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}
