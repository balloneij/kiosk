package kiosk.models;

import java.util.Arrays;
import kiosk.scenes.PathwayScene;
import kiosk.scenes.Scene;

public class PathwaySceneModel implements SceneModel {

    public String id;
    public String name;
    public String headerTitle;
    public String headerBody;
    public String centerText;
    public ButtonModel[] buttonModels;
    public int[] centerColor;

    /**
     * Creates a new pathway scene model.
     * Composed of:
     * - Title
     * - Body
     * - Career spoke graph consisting of weighted buttons
     */
    public PathwaySceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.name = "Pathway Scene";
        this.headerTitle = "";
        this.headerBody = "";
        this.centerText = "";
        this.buttonModels = new ButtonModel[]{};
        this.centerColor = new int[] { 0, 0, 0 };
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
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SceneModel deepCopy() {
        ButtonModel[] answersCopy = new ButtonModel[this.buttonModels.length];
        for (int i = 0; i < answersCopy.length; i++) {
            ButtonModel answer = this.buttonModels[i];
            answersCopy[i] = answer.deepCopy();
        }

        PathwaySceneModel copy = new PathwaySceneModel();
        copy.id = this.id;
        copy.name = name;
        copy.headerTitle = this.headerTitle;
        copy.headerBody = this.headerBody;
        copy.centerText = this.centerText;
        copy.buttonModels = answersCopy;
        copy.centerColor = Arrays.copyOf(this.centerColor, 3);
        return copy;
    }

    @Override
    public String[] getTargets() {
        String[] ids = new String[this.buttonModels.length];

        for (int i = 0; i < this.buttonModels.length; i++) {
            ids[i] = this.buttonModels[i].target;
        }

        return ids;
    }

    @Override
    public String toString() {
        return "Pathway Scene";
    }
}
