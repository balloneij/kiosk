package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.SpokeGraphPromptScene;

public class SpokeGraphPromptSceneModel implements SceneModel {

    public final String headerTitle;
    public final String headerBody;
    public final String careerCenterText;
    public final String[] careerOptions;
    public final int[] careerWeights;
    public final String promptText;
    public final String[] promptOptions;
    public final int[] optionColors;
    private final String id;

    public SpokeGraphPromptSceneModel(String headerTitle, String headerBody,
            String careerCenterText, String[] careerOptions, int[] careerWeights,
            String promptText, String[] prompOptions, int[] optionColors, String id) {
        this.headerTitle = headerTitle;
        this.headerBody = headerBody;
        this.careerCenterText = careerCenterText;
        this.careerOptions = careerOptions;
        this.careerWeights = careerWeights;
        this.promptText = promptText;
        this.promptOptions = prompOptions;
        this.optionColors = optionColors;
        this.id = id;
    }

    @Override
    public Scene createScene() {
        return new SpokeGraphPromptScene(this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public SceneModel deepCopy() {
        return new SpokeGraphPromptSceneModel(this.headerTitle, this.headerBody,
            this.careerCenterText, this.careerOptions, this.careerWeights, this.promptText,
            this.promptOptions, this.optionColors, this.id);
    }
}
