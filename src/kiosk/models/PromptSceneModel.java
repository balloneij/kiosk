package kiosk.models;

import kiosk.scenes.PromptScene;
import kiosk.scenes.Scene;

public class PromptSceneModel implements SceneModel {

    public String question;
    public ButtonModel[] answers;
    public boolean invertedColors;

    /**
     * Constructs a prompt scene model.
     * @param question to ask
     * @param answers buttons with the answers and targets
     * @param invertedColors set to true for white primary, black secondary
     */
    public PromptSceneModel(String question, ButtonModel[] answers, boolean invertedColors) {
        this.question = question;
        this.answers = answers;
        this.invertedColors = invertedColors;
    }

    @Override
    public Scene createScene() {
        return new PromptScene(this);
    }
}
