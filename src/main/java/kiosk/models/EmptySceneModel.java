package kiosk.models;

import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;

public class EmptySceneModel implements SceneModel {

    public String id;
    public String name;
    public String message;

    /**
     * Constructs a new empty scene model.
     */
    public EmptySceneModel() { //todo this is different than normal
        this(IdGenerator.getInstance().getNextId(), "How did you get here?");
    }

    /**
     * Constructs a new empty scene model.
     * @param uniqueId An id unique to this specific model.
     */
    public EmptySceneModel(String uniqueId, String message) {
        this.id = uniqueId;
        this.name = "Empty Scene";
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
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SceneModel deepCopy() {
        var copy = new EmptySceneModel();
        copy.id = id;
        copy.name = name;
        copy.message = message;
        return copy;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }

    @Override
    public String toString() {
        return "Empty Scene";
    }
}
