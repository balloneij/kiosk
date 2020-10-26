package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.WaveTransitionScene;

public final class WaveTransitionSceneModel implements SceneModel {

    public String target;
    public boolean invertedColors;
    public String id;

    /**
     * Creates a default WaveTransitionSceneModel with a bad target.
     */
    public WaveTransitionSceneModel() {
        this.target = "null";
        this.invertedColors = false;
        this.id = IdGenerator.getInstance().getNextId();
    }

    /**
     * Constructs a wave transition model.
     * @param target scene the transition links to
     * @param invertedColors true for white, black otherwise
     */
    public WaveTransitionSceneModel(String target, boolean invertedColors) {
        this(target, invertedColors, IdGenerator.getInstance().getNextId());
    }

    /**
     * Constructs a wave transition model.
     * @param target scene the transition links to
     * @param invertedColors true for white, black otherwise
     * @param uniqueId An id unique to this specific model.
     */
    public WaveTransitionSceneModel(String target, boolean invertedColors, String uniqueId) {
        this.target = target;
        this.invertedColors = invertedColors;
        this.id = uniqueId;
    }

    @Override
    public Scene createScene() {
        return new WaveTransitionScene(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SceneModel deepCopy() {
        return new WaveTransitionSceneModel(target, invertedColors, id);
    }
}
