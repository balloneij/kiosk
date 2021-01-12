package kiosk.models;

import kiosk.scenes.ButtonControl;
import kiosk.scenes.DetailsScene;
import kiosk.scenes.Image;
import kiosk.scenes.Scene;

public class DetailsSceneModel implements SceneModel {

    public String id;
    public String title;
    public String body;
    public ButtonModel button;

    public DetailsSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.title = "";
        this.body = "";
        this.button = new ButtonModel();
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
    public SceneModel deepCopy() {
        var copy = new DetailsSceneModel();
        copy.id = this.id;
        copy.title = title;
        copy.button = this.button.deepCopy();
        copy.body = this.body;

        return copy;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }

    @Override
    public String toString() {
        return "Details Scene";
    }
}
