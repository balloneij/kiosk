package kiosk.models;

import kiosk.scenes.CareerPathwayScene;
import kiosk.scenes.Scene;

/**
 * Model for storing information about a CareerPathwayScene.
 */
public class CareerPathwaySceneModel implements SceneModel {

    public String id;
    public String name = "Career Pathway Scene";
    public String centerText = "Center";
    public String headerTitle = "Title";
    public String headerBody = "Heading";

    @Override
    public Scene createScene() {
        return new CareerPathwayScene(this);
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
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SceneModel deepCopy() {
        CareerPathwaySceneModel model = new CareerPathwaySceneModel();
        model.id = id;
        model.name = name;
        model.centerText = centerText;
        model.headerTitle = headerTitle;
        model.headerBody = headerBody;
        return model;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }

    @Override
    public String toString() {
        return "Career Pathway Scene";
    }
}
