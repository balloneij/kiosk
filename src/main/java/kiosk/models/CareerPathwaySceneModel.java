package kiosk.models;

import java.util.Arrays;
import java.util.List;
import kiosk.scenes.CareerPathwayScene;
import kiosk.scenes.Scene;

/**
 * Model for storing information about a CareerPathwayScene.
 */
public class CareerPathwaySceneModel extends PathwaySceneModel {
    private FilterGroupModel filter;
    public CareerModel[] careers;

    public CareerPathwaySceneModel() {
        super();
        setFilter(new FilterGroupModel("All"));
    }

    /**
     * Creates buttons for each of the careers in the provided list.
     * @param careerModels A list of CareerModels to create buttons for.
     */
    public void createCareerButtons(List<CareerModel> careerModels) {
        this.buttonModels = new ButtonModel[careerModels.size()];

        for (int i = 0; i < careerModels.size(); i++) {
            String careerName = careerModels.get(i).name;
            buttonModels[i] = new ButtonModel(careerName, careerName);
        }
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
        createCareerButtons(Arrays.asList(careers));
    }

    @Override
    public Scene createScene() {
        return new CareerPathwayScene(this);
    }

    @Override
    public SceneModel deepCopy() {
        CareerPathwaySceneModel copy = new CareerPathwaySceneModel();

        ButtonModel[] copiedButtonModels = new ButtonModel[buttonModels.length];
        for (int i = 0; i < buttonModels.length; i++) {
            copiedButtonModels[i] = buttonModels[i].deepCopy();
        }

        copy.buttonModels = copiedButtonModels;
        copy.id = id;
        copy.name = name;
        copy.headerTitle = headerTitle;
        copy.headerBody = headerBody;
        copy.centerText = centerText;
        copy.setFilter(filter);

        return copy;
    }

    @Override
    public String toString() {
        return "Career Pathway Scene";
    }
}
