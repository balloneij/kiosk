package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.WaveTransitionScene;

public final class WaveTransitionSceneModel implements SceneModel {

    public final SceneModel target;
    public final boolean invertedColors;
    private final String id;

    /**
     * Constructs a wave transition model.
     * @param target scene the transition links to
     * @param invertedColors true for white, black otherwise
     */
    public WaveTransitionSceneModel(SceneModel target, boolean invertedColors) {
        this(target, invertedColors, IdGenerator.getInstance().getNextId());
    }

    /**
     * Constructs a wave transition model.
     * @param target scene the transition links to
     * @param invertedColors true for white, black otherwise
     * @param uniqueId An id unique to this specific model.
     */
    public WaveTransitionSceneModel(SceneModel target, boolean invertedColors, String uniqueId) {
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
}
