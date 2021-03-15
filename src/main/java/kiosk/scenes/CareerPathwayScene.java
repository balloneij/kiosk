package kiosk.scenes;

import kiosk.SceneGraph;
import kiosk.UserScore;
import kiosk.models.CareerModel;
import kiosk.models.CareerPathwaySceneModel;

/**
 * A scene that displays a spoke graph containing buttons for each of the careers currently in
 * the list, weighted based on the career RIASEC type and the UserScore.
 */
public class CareerPathwayScene extends PathwayScene {
    /**
     * Creates a new CareerPathwayScene using the given model.
     * @param model A CareerPathwaySceneModel used to create the scene.
     */
    public CareerPathwayScene(CareerPathwaySceneModel model) {
        super(model);

        // Update weights based on user score
        UserScore userScore = SceneGraph.getUserScore();
        double[] weights = new double[model.careers.length];
        for (int i = 0; i < model.careers.length; i++) {
            CareerModel career = model.careers[i];
            weights[i] = userScore.getCategoryScore(career.riasecCategory);
        }
        spokeGraph.setWeights(weights);
    }
}
