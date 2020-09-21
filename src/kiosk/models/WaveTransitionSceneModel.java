package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.WaveTransitionScene;

public final class WaveTransitionSceneModel implements SceneModel {

    public final SceneModel target;
    public final boolean invertedColors;

    /**
     * Constructs a wave transition model.
     * @param target scene the transition links to
     * @param invertedColors true for white, black otherwise
     */
    public WaveTransitionSceneModel(SceneModel target, boolean invertedColors) {
        this.target = target;
        this.invertedColors = invertedColors;
    }

    @Override
    public Scene createScene() {
        return new WaveTransitionScene(this);
    }
}
