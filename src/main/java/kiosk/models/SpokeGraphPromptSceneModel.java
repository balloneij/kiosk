package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.SpokeGraphPromptScene;

public class SpokeGraphPromptSceneModel implements SceneModel {

    private final String id;
    public final String headerTitle;
    public final String headerBody;
    public final String[] careerOptions;

    public SpokeGraphPromptSceneModel(String headerTitle, String headerBody, String[] careerOptions,
              String id) {
        this.headerTitle = headerTitle;
        this.headerBody = headerBody;
        this.careerOptions = careerOptions;
        this.id = id;
    }

    @Override
    public Scene createScene() {
        return new SpokeGraphPromptScene(this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public SceneModel deepCopy() {
        return new SpokeGraphPromptSceneModel(this.headerTitle, this.headerBody, this.careerOptions,
                this.id);
    }
}
