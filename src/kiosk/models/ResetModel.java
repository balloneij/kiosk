package kiosk.models;

import kiosk.scenes.ResetScene;
import kiosk.scenes.Scene;

public final class ResetModel implements SceneModel {

    private final String id;

    /**
     * Constructs a new reset model.
     */
    public ResetModel() {
        this(IdGenerator.getInstance().getNextId());
    }

    /**
     * Constructs a new reset model.
     * @param uniqueId An id unique to this specific model.
     */
    public ResetModel(String uniqueId) {
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
}
