package kiosk.models;

import kiosk.scenes.PromptScene;
import kiosk.scenes.Scene;

public final class PromptSceneModel implements SceneModel {

    public String id;
    public String name;
    public String title;
    public String prompt;
    public String actionPhrase;
    public ButtonModel[] answers;

    /**
     * Creates an empty PromptSceneModel.
     */
    public PromptSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.name = "Prompt Scene";
        this.title = "";
        this.prompt = "\n\n";
        this.actionPhrase = "";
        this.answers = new ButtonModel[]{};
    }

    @Override
    public Scene createScene() {
        return new PromptScene(this);
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
        ButtonModel[] buttonsCopy = new ButtonModel[this.answers.length];
        for (int i = 0; i < buttonsCopy.length; i++) {
            ButtonModel button = this.answers[i];
            buttonsCopy[i] = button.deepCopy();
        }

        PromptSceneModel copy = new PromptSceneModel();
        copy.id = this.id;
        copy.name = name;
        copy.title = this.title;
        copy.prompt = this.prompt;
        copy.actionPhrase = this.actionPhrase;
        copy.answers = buttonsCopy;

        return copy;
    }

    @Override
    public String[] getTargets() {
        String[] ids = new String[this.answers.length];

        for (int i = 0; i < this.answers.length; i++) {
            ids[i] = this.answers[i].target;
        }

        return ids;
    }

    @Override
    public String toString() {
        return "Prompt Scene";
    }
}
