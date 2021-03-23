package kiosk.models;

import kiosk.scenes.CareerPathwayScene;
import kiosk.scenes.Scene;

/**
 * Model for storing information about a CareerPathwayScene.
 */
public class CareerPathwaySceneModel implements SceneModel {
    private FilterGroupModel filter;
    public CareerModel[] careers;

    public String id;
    public String name = "Career Pathway Scene";
    public String centerText = "Center";
    public String headerTitle = "Title";
    public String headerBody = "Heading";

    @Override
    public Scene createScene() {
        return new CareerPathwayScene(this);
    }

    public CareerPathwaySceneModel() {
        super();
        setFilter(new FilterGroupModel("All"));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public FilterGroupModel getFilter() {
        return filter;
    }

    /**
     * Updates the list of careers using the new filter and re-creates the spoke-graph button
     * models.
     * @param filter The new FilterGroupModel to filter careers by.
     */
    public void setFilter(FilterGroupModel filter) {
        this.filter = filter;

        // Use the list of careers from the filter
        careers = filter.getCareers().toArray(new CareerModel[] {});
//        createCareerButtons(Arrays.asList(careers)); TODO
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
        model.setFilter(filter);
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
