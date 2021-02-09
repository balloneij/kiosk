package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.WaveTransitionScene;

public final class WaveTransitionSceneModel implements SceneModel {

    public String target;
    public boolean invertedColors;
    public String id;
    public String name;

    /**
     * Creates a default WaveTransitionSceneModel with a bad target.
     */
    public WaveTransitionSceneModel() {
        this.target = "null";
        this.name = "Wave Transition";
        this.invertedColors = false;
        this.id = IdGenerator.getInstance().getNextId();
    }

    /**
     * Constructs a wave transition model.
     * @param target scene the transition links to
     * @param invertedColors true for white, black otherwise
     */
    public WaveTransitionSceneModel(String target, boolean invertedColors) {
        this(target, invertedColors, IdGenerator.getInstance().getNextId(), "Wave Transition");
    }

    /**
     * Constructs a wave transition model.
     * @param target scene the transition links to
     * @param invertedColors true for white, black otherwise
     * @param uniqueId An id unique to this specific model.
     */
<<<<<<< HEAD
    public WaveTransitionSceneModel(String target,
                                    boolean invertedColors, String uniqueId, String name) {
=======
    public WaveTransitionSceneModel(String target, boolean invertedColors,
            String uniqueId, String name) {
>>>>>>> dev
        this.target = target;
        this.invertedColors = invertedColors;
        this.id = uniqueId;
        this.name = name;
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
        return new WaveTransitionSceneModel(target, invertedColors, id, name);
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}
