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
        this.headerTitle = "Now for a few questions about you.";
        this.headerBody = "You can go back and change your answers, if you want to.";
        this.careerCenterText = "Build Resilient Cities";
        this.careerOptions = new String[]{"Civil\nEngineer", "Environmental\nEngineer", "Structural\nEngineer",
                "Mechanical\nEngineer", "Architect", "Urban\nPlanner", "Construction\n& Traces",
                "Communications", "Public\nPolicy\nLeader", "Data\nScientist"
        };
        this.careerWeights = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        this.promptText = "How much do you love to play with numbers?";
        this.promptOptions = new String[]{"I love playing with numbers!", "Math is fun and useful.",
                "Math is not really my thing."};
        this.optionColors = new int[]{0, 1, 2};
        this.id = "spokeGraphId";
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
