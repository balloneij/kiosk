package kiosk.models;

import kiosk.scenes.DetailsScene;
import kiosk.scenes.Scene;

public class DetailsSceneModel implements SceneModel {

    public String id;
    public String name;
    public String title;
    public String body;
    public ButtonModel[] targets;

    /**
     * Stores all relevant information needed for the Details Scene.
     * Which is composed of an ID, main title, body of text, and a button
     * at the bottom.
     */
    public DetailsSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.name = "Details Scene";
        this.title = "";
        this.body = "";
        this.targets = new ButtonModel[] {new ButtonModel()};
    }

    @Override
    public Scene createScene() {
        return new DetailsScene(this);
    }

    @Override
    public String getId() {
        return this.id;
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
        DetailsSceneModel copy = new DetailsSceneModel();
        ButtonModel[] targetsCopy = new ButtonModel[this.targets.length];
        for (int i = 0; i < targetsCopy.length; i++) {
            ButtonModel button = this.targets[i];
            targetsCopy[i] = button.deepCopy();
        }
        copy.id = this.id;
        copy.name = name;
        copy.title = title;
        copy.body = this.body;
        copy.targets = targetsCopy;

        return copy;
    }

    @Override
    public String[] getTargets() {
        String[] ids = new String[this.targets.length];

        for (int i = 0; i < this.targets.length; i++) {
            ids[i] = this.targets[i].target;
        }
        return ids;
    }

    @Override
    public String toString() {
        return "Details Scene";
    }
}
