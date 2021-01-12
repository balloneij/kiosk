package kiosk.models;

import java.io.Serializable;
import kiosk.scenes.Scene;

/**
 * A model containing any data necessary to create a Scene instance.
 */
public interface SceneModel extends Serializable {
    /**
     * Creates a new scene. Presumably uses data from the model.
     * @return a new Scene
     */
    Scene createScene();

    /**
     * Get this models unique ID.
     * @return The unique ID which points to this model.
     */
    String getId();

    /**
     * Set this models unique ID.
     * @param id to set
     */
    void setId(String id);

    /**
     * Creates a deep copy of the current SceneModel.
     * @return new copy of the same type
     */
    SceneModel deepCopy();

    String[] getTargets();
}
