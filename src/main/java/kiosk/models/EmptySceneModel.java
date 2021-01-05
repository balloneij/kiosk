package kiosk.models;

import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;

public class EmptySceneModel implements SceneModel {

    public String id;
    public String message;

    /**
     * Constructs a new empty scene model.
     */
    public EmptySceneModel() {
        this(IdGenerator.getInstance().getNextId(), "How did you get here?");
    }

    /**
     * Constructs a new empty scene model.
     * @param uniqueId An id unique to this specific model.
     */
    public EmptySceneModel(String uniqueId, String message) {
        this.id = uniqueId;
        this.message = message;
    }

    @Override
    public Scene createScene() {
        return new EmptyScene(this);
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
    public SceneModel deepCopy() {
        var copy = new EmptySceneModel();
        copy.id = id;
        copy.message = message;
        return copy;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}
