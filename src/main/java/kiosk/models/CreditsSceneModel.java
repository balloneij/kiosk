package kiosk.models;

import kiosk.scenes.CreditsScene;
import kiosk.scenes.Scene;

public class CreditsSceneModel implements SceneModel {

    public String id;
    public String name;
    public String title;
    public String creatorTitle;
    public String creators;
    public String supporterTitle;
    public String supporters;

    /**
     * Creates an empty CreditsSceneModel.
     */
    public CreditsSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.name = "Credits Scene";
        this.title = "";
        this.creatorTitle = "";
        this.creators = "";
        this.supporterTitle = "";
        this.supporters = "";
    }

    @Override
    public Scene createScene() {
        return new CreditsScene(this);
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
        CreditsSceneModel copy = new CreditsSceneModel();
        copy.id = this.id;
        copy.name = name;
        copy.title = this.title;
        copy.creatorTitle = this.creatorTitle;
        copy.creators = this.creators;
        copy.supporterTitle = this.supporterTitle;
        copy.supporters = this.supporters;

        return copy;
    }

    @Override
    public String[] getTargets() {
        return null;
    }

    @Override
    public String toString() {
        return "Credits Scene";
    }
}

