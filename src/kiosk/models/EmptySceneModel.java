package kiosk.models;

import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;

public class EmptySceneModel implements SceneModel {

    public String id;

    /**
     * Constructs a new empty scene model.
     */
    public EmptySceneModel() {
        this(IdGenerator.getInstance().getNextId());
    }

    /**
     * Constructs a new empty scene model.
     * @param uniqueId An id unique to this specific model.
     */
    public EmptySceneModel(String uniqueId) {
        this.id = uniqueId;
    }

    @Override
    public Scene createScene() {
        return new EmptyScene();
    }

    @Override
    public String getId() {
        return id;
    }
}
