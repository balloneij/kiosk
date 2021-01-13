package kiosk.models;

import kiosk.scenes.PromptScene;
import kiosk.scenes.Scene;

public final class PromptSceneModel implements SceneModel {

    public String id;
    public String title;
    public String prompt;
    public String actionPhrase;
    public ButtonModel[] careers;

    /**
     * Creates an empty PromptSceneModel.
     */
    public PromptSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.title = "";
        this.prompt = "\n\n";
        this.actionPhrase = "";
        this.careers = new ButtonModel[]{};
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
    public SceneModel deepCopy() {
        ButtonModel[] buttonsCopy = new ButtonModel[this.careers.length];
        for (int i = 0; i < buttonsCopy.length; i++) {
            ButtonModel button = this.careers[i];
            buttonsCopy[i] = button.deepCopy();
        }

        PromptSceneModel copy = new PromptSceneModel();
        copy.id = this.id;
        copy.title = this.title;
        copy.prompt = this.prompt;
        copy.actionPhrase = this.actionPhrase;
        copy.careers = buttonsCopy;

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
        return "Prompt Scene";
    }
}
