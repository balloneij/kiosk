package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.SpokeGraphPromptScene;

public final class SpokeGraphPromptSceneModel implements SceneModel {

    public String id;
    public String headerTitle;
    public String headerBody;
    public String careerCenterText;
    public String[] careerOptions;
    public int[] careerWeights;
    public String promptText;
    public ButtonModel[] answers;

    /**
     * Creates a new prompt scene model.
     * Composed of:
     * - Title
     * - Unweighted spoke graph on the right
     * - Weighted spoke graph on the left
     */
    public SpokeGraphPromptSceneModel() {
        this.headerTitle = IdGenerator.getInstance().getNextId();
        this.headerBody = "";
        this.careerCenterText = "";
        this.careerOptions = new String[]{ };
        this.careerWeights = new int[]{ };
        this.promptText = "";
        this.answers = new ButtonModel[]{};
        this.id = "";
    }

    @Override
    public Scene createScene() {
        return new SpokeGraphPromptScene(this);
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
    public SceneModel deepCopy() {
        var copy = new SpokeGraphPromptSceneModel();
        copy.headerTitle = this.headerTitle;
        copy.headerBody = this.headerBody;
        copy.careerCenterText = this.careerCenterText;
        copy.careerOptions = this.careerOptions;
        copy.careerWeights = this.careerWeights;
        copy.promptText = this.promptText;
        copy.answers = this.answers;
        copy.id = this.id;
        return copy;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}
