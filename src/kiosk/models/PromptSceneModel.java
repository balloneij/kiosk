package kiosk.models;

import kiosk.scenes.PromptScene;
import kiosk.scenes.Scene;

public final class PromptSceneModel implements SceneModel {

    public final String question;
    public final ButtonModel[] answers;
    public final boolean invertedColors;
    private final String id;

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
}
