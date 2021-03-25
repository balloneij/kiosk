package kiosk.models;

import kiosk.scenes.CareerDescriptionScene;
import kiosk.scenes.Scene;

public class CareerDescriptionModel implements SceneModel {

    public String id;
    public String name;
    public String title;
    public String body;
    public CareerModel careerModel;
    public ButtonModel button;

    /**
     * Stores all relevant information needed for the final Career
     * Description Scene, which is composed of an ID, title, body of text
     * (instructions,) the careerModel, and a button at the bottom.
     */
    public CareerDescriptionModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.name = "Career Description Scene";
        this.title = "Career Title Here";
        this.body = "Read about your selected career here. Then,"
                + " you can either go back and explore other careers, or"
                + " start over from the beginning.";
        this.careerModel = new CareerModel();
        this.button = new ButtonModel("Go to the beginning", "null");
    }

    @Override
    public Scene createScene() {
        return new CareerDescriptionScene(this);
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
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SceneModel deepCopy() {
        CareerDescriptionModel copy = new CareerDescriptionModel();
        copy.id = this.id;
        copy.title = this.title;
        copy.body = this.body;
        copy.button = this.button.deepCopy();
        copy.careerModel = this.careerModel;

        return copy;
    }

    @Override
    public String[] getTargets() {
        return new String[] {button.target};
    }

    @Override
    public String toString() {
        return "Career Description Scene";
    }
}