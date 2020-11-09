package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.SpokeGraphPromptScene;

public final class SpokeGraphPromptSceneModel implements SceneModel {

    public String headerTitle;
    public String headerBody;
    public String careerCenterText;
    public String[] careerOptions;
    public int[] careerWeights;
    public String promptText;
    public String[] promptOptions;
    public int[] optionColors;
    public String id;

    public SpokeGraphPromptSceneModel() {
        this.headerTitle = "";
        this.headerBody = "";
        this.careerCenterText = "";
        this.careerOptions = new String[]{ };
        this.careerWeights = new int[]{ };
        this.promptText = "";
        this.promptOptions = new String[]{ };
        this.optionColors = new int[]{ };
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
    public SceneModel deepCopy() {
        var copy = new SpokeGraphPromptSceneModel();
        copy.headerTitle = this.headerTitle;
        copy.headerTitle = this.headerTitle;
        copy.careerCenterText = this.careerCenterText;
        copy.careerOptions = this.careerOptions;
        copy.careerWeights = this.careerWeights;
        copy.promptText = this.promptText;
        copy.promptOptions = this.promptOptions;
        copy.optionColors = this.optionColors;
        copy.id = this.id;
        return copy;
    }
}
