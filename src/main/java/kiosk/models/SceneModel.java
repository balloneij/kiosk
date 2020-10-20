package kiosk.models;

import kiosk.scenes.Scene;

/**
 * A model containing any data necessary to create a Scene instance.
 */
public interface SceneModel {
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
     * Creates a deep copy of the current SceneModel.
     * @return new copy of the same type
     */
    SceneModel deepCopy();
}
