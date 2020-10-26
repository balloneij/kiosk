package kiosk.models;

import kiosk.scenes.ResetScene;
import kiosk.scenes.Scene;

public final class ResetSceneModel implements SceneModel {

    public String id;

    /**
     * Constructs a new reset model.
     */
    public ResetSceneModel() {
        this(IdGenerator.getInstance().getNextId());
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
    public SceneModel deepCopy() {
        return new ResetSceneModel(id);
    }
}
