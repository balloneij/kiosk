package kiosk.models;

import kiosk.scenes.PromptScene;
import kiosk.scenes.Scene;
import kiosk.scenes.TimeoutScene;

public final class TimeoutSceneModel implements SceneModel {

    public String id;
    public String title;
    public String prompt;
    public String actionPhrase;
    public ButtonModel[] answers;

    /**
     * Creates an empty PromptSceneModel.
     */
    public TimeoutSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.title = "";
        this.prompt = "\n\n";
        this.actionPhrase = "";
        this.answers = new ButtonModel[]{};
    }

    @Override
    public Scene createScene() {
        return new TimeoutScene(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SceneModel deepCopy() {
        ButtonModel[] buttonsCopy = new ButtonModel[this.answers.length];
        for (int i = 0; i < buttonsCopy.length; i++) {
            ButtonModel button = this.answers[i];
            buttonsCopy[i] = button.deepCopy();
        }

        TimeoutSceneModel copy = new TimeoutSceneModel();
        copy.id = this.id;
        copy.title = this.title;
        copy.prompt = this.prompt;
        copy.actionPhrase = this.actionPhrase;
        copy.answers = buttonsCopy;

        return copy;
    }
}
