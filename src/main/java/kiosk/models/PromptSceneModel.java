package kiosk.models;

import kiosk.scenes.PromptScene;
import kiosk.scenes.Scene;

public final class PromptSceneModel implements SceneModel {

    public String question;
    public ButtonModel[] answers;
    public boolean invertedColors;
    public String id;

    /**
     * Creates an empty PromptSceneModel.
     */
    public PromptSceneModel() {
        this.question = "null";
        this.answers = new ButtonModel[]{};
        this.invertedColors = false;
        this.id = IdGenerator.getInstance().getNextId();
    }

    /**
     * Constructs a prompt scene model.
     * @param question to ask
     * @param answers buttons with the answers and targets
     * @param invertedColors set to true for white primary, black secondary
     */
    public PromptSceneModel(String question, ButtonModel[] answers, boolean invertedColors) {
        this(question, answers, invertedColors, IdGenerator.getInstance().getNextId());
    }

    /**
     * Constructs a prompt scene model.
     * @param question to ask
     * @param answers buttons with the answers and targets
     * @param invertedColors set to true for white primary, black secondary
     * @param uniqueId An id unique to this specific model.
     */
    public PromptSceneModel(String question, ButtonModel[] answers,
                            boolean invertedColors, String uniqueId) {
        this.question = question;
        this.answers = answers;
        this.invertedColors = invertedColors;
        this.id = uniqueId;
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
    public SceneModel deepCopy() {
        ButtonModel[] buttonsCopy = new ButtonModel[this.answers.length];
        for (int i = 0; i < buttonsCopy.length; i++) {
            ButtonModel button = this.answers[i];
            buttonsCopy[i] = new ButtonModel(button.text, button.target);
        }
        return new PromptSceneModel(question, buttonsCopy, invertedColors, id);
    }
}
