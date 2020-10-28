package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.WeightedSpokeGraphScene;

public class WeightedSpokeGraphSceneModel
        implements SceneModel {

    public final String id;
    public final String centerText;
    public final int size;
    public final String[] options;
    public final int[] weights;

    /**
     * Creates a new weighted spoke graph scene model. The outer text circles are going
     * to be larger or smaller depending on their weights.
     * @param centerText Text in the center circle.
     * @param options Text appearing in outer circles.
     * @param weights Governs how large circles are relative to each other.
     * @param id The unique id of the scene.
     */
    public WeightedSpokeGraphSceneModel(String centerText, String[] options,
                                        int[] weights, int size, String id) {
        this.id = id;
        this.centerText = centerText;
        this.size = size;
        this.options = options;
        this.weights = weights;
    }

    @Override
    public Scene createScene() {
        return new WeightedSpokeGraphScene(this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public SceneModel deepCopy() {
        return new WeightedSpokeGraphSceneModel(this.centerText, this.options,
                this.weights, this.size, this.id);
    }
}
