package kiosk.models;

import java.util.Arrays;
import kiosk.scenes.CareerPathwayScene;
import kiosk.scenes.Scene;

/**
 * Model for storing information about a CareerPathwayScene.
 */
public class CareerPathwaySceneModel implements SceneModel {
    public String id;
    public String name;
    public String centerText;
    public String headerTitle;
    public String headerBody;
    public FilterGroupModel filter;
    public CareerModel[] careers;

    @Override
    public Scene createScene() {
        return new CareerPathwayScene(this);
    }

    /**
     * Creates a CareerPathwaySceneModel with default values.
     */
    public CareerPathwaySceneModel() {
        id = IdGenerator.getInstance().getNextId();
        name = "Career Pathway Scene";
        centerText = "Center";
        headerTitle = "Title";
        headerBody = "Heading";
        filter = new FilterGroupModel("All");
        careers = new CareerModel[] {};
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
        model.filter = filter;
        model.careers = Arrays.copyOf(careers, careers.length);
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
