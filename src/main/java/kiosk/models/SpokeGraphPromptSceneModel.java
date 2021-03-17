package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.SpokeGraphPromptScene;

public final class SpokeGraphPromptSceneModel implements SceneModel {

    public String id;
    public String name;
    public String headerTitle;
    public String headerBody;
    public String careerCenterText;
    public String promptText;
    public ButtonModel[] answers;

    /**
     * Creates a new SG prompt scene model.
     * Composed of:
     * - Title
     * - Unweighted spoke graph on the right
     * - Weighted spoke graph on the left
     */
    public SpokeGraphPromptSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.name = "Spoke Graph Prompt Scene";
        this.headerTitle = "";
        this.headerBody = "";
        this.careerCenterText = "";
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
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SceneModel deepCopy() {
        ButtonModel[] answersCopy = new ButtonModel[this.answers.length];
        for (int i = 0; i < answersCopy.length; i++) {
            ButtonModel answer = this.answers[i];
            answersCopy[i] = answer.deepCopy();
        }

        SpokeGraphPromptSceneModel copy = new SpokeGraphPromptSceneModel();
        copy.id = this.id;
        copy.name = name;
        copy.headerTitle = this.headerTitle;
        copy.headerBody = this.headerBody;
        copy.careerCenterText = this.careerCenterText;
        copy.promptText = this.promptText;
        copy.answers = answersCopy;
        return copy;
    }

    @Override
    public String[] getTargets() {
        // Career graph is not clickable; therefore, it has no targets

        String[] ids = new String[this.answers.length];

        for (int i = 0; i < this.answers.length; i++) {
            ids[i] = this.answers[i].target;
        }

        return ids;
    }

    @Override
    public String toString() {
        return "Spoke Graph Prompt Scene";
    }
}
