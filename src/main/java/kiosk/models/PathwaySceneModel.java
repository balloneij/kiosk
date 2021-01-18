package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.PathwayScene;

public class PathwaySceneModel implements SceneModel {

    public String id;
    public String headerTitle;
    public String headerBody;
    public String centerText;
    public ButtonModel[] careers;

    /**
     * Creates a new pathway scene model.
     * Composed of:
     * - Title
     * - Body
     * - Career spoke graph consisting of weighted buttons
     */
    public PathwaySceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.headerTitle = "";
        this.headerBody = "";
        this.centerText = "";
        this.careers = new ButtonModel[]{};
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
        ButtonModel[] answersCopy = new ButtonModel[this.careers.length];
        for (int i = 0; i < answersCopy.length; i++) {
            ButtonModel answer = this.careers[i];
            answersCopy[i] = answer.deepCopy();
        }

        var copy = new PathwaySceneModel();
        copy.id = this.id;
        copy.headerTitle = this.headerTitle;
        copy.headerBody = this.headerBody;
        copy.centerText = this.centerText;
        copy.careers = answersCopy;
        return copy;
    }

    @Override
    public String[] getTargets() {
        String[] ids = new String[this.careers.length];

        for (int i = 0; i < this.careers.length; i++) {
            ids[i] = this.careers[i].target;
        }

        return ids;
    }

    @Override
    public String toString() {
        return "Pathway Scene";
    }
}
