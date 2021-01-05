package kiosk.models;

import kiosk.scenes.ErrorScene;
import kiosk.scenes.Scene;

public class ErrorSceneModel implements SceneModel {

    public enum ErrorIntensity {
        // Blue
        INFORMATION,
        // Yellow
        WARNING,
        // RED
        SEVERE,
    }

    public String errorMsg;
    public String id;
    public ErrorIntensity intensity;

    /**
     * Create a default error scene model with no error message
     * and a severe intensity.
     */
    public ErrorSceneModel() {
        this.errorMsg = "null";
        this.id = IdGenerator.getInstance().getNextId();
        this.intensity = ErrorIntensity.SEVERE;
    }

    /**
     * Create an error scene model with the specified message
     * and a severe intensity.
     * @param errorMsg message to display on the screen.
     */
    public ErrorSceneModel(String errorMsg) {
        this.errorMsg = errorMsg;
        this.id = IdGenerator.getInstance().getNextId();
        this.intensity = ErrorIntensity.SEVERE;
    }

    /**
     * Create an error scene model.
     * @param errorMsg to display on the screen
     * @param intensity intensity level
     */
    public ErrorSceneModel(String errorMsg, ErrorIntensity intensity) {
        this.errorMsg = errorMsg;
        this.id = IdGenerator.getInstance().getNextId();
        this.intensity = intensity;
    }

    @Override
    public Scene createScene() {
        return new ErrorScene(this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public SceneModel deepCopy() {
        var copy = new ErrorSceneModel();
        copy.id = id;
        copy.errorMsg = errorMsg;
        copy.intensity = intensity;
        return copy;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}