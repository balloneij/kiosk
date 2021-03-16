package kiosk.models;

import java.util.Arrays;
import java.util.List;
import kiosk.scenes.CareerPathwayScene;
import kiosk.scenes.Scene;

/**
 * Model for storing information about a CareerPathwayScene.
 */
public class CareerPathwaySceneModel extends PathwaySceneModel {

    public CareerPathwaySceneModel() {
        super();
        createCareerButtons(Arrays.asList(LoadedSurveyModel.careers));
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

        return copy;
    }

    @Override
    public String toString() {
        return "Career Pathway Scene";
    }
}
