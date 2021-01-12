package kiosk.models;

import kiosk.scenes.ButtonControl;
import kiosk.scenes.Scene;
import kiosk.scenes.WeightedSpokeGraphScene;

public class WeightedSpokeGraphSceneModel
        implements SceneModel {

    public String id;
    public final int centerX;
    public final int centerY;
    public final String centerText;
    public final int size;
    public final int padding;
    public final ButtonControl[] answers;
    public final int[] weights;

    /**
     * Creates a new weighted spoke graph scene model. The outer text circles are going
     * to be larger or smaller depending on their weights.
     * @param centerText Text in the center circle.
     * @param answers Text appearing in outer circles.
     * @param weights Governs how large circles are relative to each other.
     * @param id The unique id of the scene.
     */
    public WeightedSpokeGraphSceneModel(String centerText, int centerX, int centerY,
            ButtonControl[] answers, int[] weights, int size, int padding, String id) {
        this.id = id;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerText = centerText;
        this.size = size;
        this.padding = padding;
        this.answers = answers;
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
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public SceneModel deepCopy() {
        return new WeightedSpokeGraphSceneModel(this.centerText, this.centerX, this.centerY,
            this.answers, this.weights, this.size, this.padding, this.id);
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}
