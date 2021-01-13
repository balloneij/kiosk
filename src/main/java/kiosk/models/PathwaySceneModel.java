package kiosk.models;

import kiosk.Settings;
import kiosk.scenes.Scene;
import kiosk.scenes.PathwayScene;

public class PathwaySceneModel implements SceneModel {

    public String id;
    public String headerTitle;
    public String headerBody;
    public float xpos;
    public float ypos;
    public float size;
    public float padding;
    public String centerText;
    public ButtonModel[] answers;

    /**
     * Creates a new pathway scene model.
     * Composed of:
     * - Title
     * - Unweighted spoke graph on the right
     * - Weighted spoke graph on the left
     */
    public PathwaySceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.headerTitle = "";
        this.headerBody = "";
        this.xpos = 0;
        this.ypos = 0;
        this.size = 50;
        this.padding = 20;
        this.centerText = "";
        this.answers = new ButtonModel[]{};
    }

    @Override
    public Scene createScene() {
        return new PathwayScene(this);
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
        ButtonModel[] answersCopy = new ButtonModel[this.answers.length];
        for (int i = 0; i < answersCopy.length; i++) {
            ButtonModel answer = this.answers[i];
            answersCopy[i] = answer.deepCopy();
        }

        var copy = new PathwaySceneModel();
        copy.id = this.id;
        copy.headerTitle = this.headerTitle;
        copy.headerBody = this.headerBody;
        copy.xpos = this.xpos;
        copy.ypos = this.ypos;
        copy.size = this.size;
        copy.padding = this.padding;
        copy.centerText = this.centerText;
        copy.answers = answersCopy;
        return copy;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}
