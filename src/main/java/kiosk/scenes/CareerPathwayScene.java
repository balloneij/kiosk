package kiosk.scenes;

import kiosk.SceneGraph;
import kiosk.UserScore;
import kiosk.models.CareerModel;
import kiosk.models.CareerPathwaySceneModel;
import kiosk.models.LoadedSurveyModel;

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

        CareerModel[] careers = LoadedSurveyModel.careers;
        UserScore userScore = SceneGraph.getUserScore();

        double[] weights = new double[careers.length];
        for (int i = 0; i < careers.length; i++) {
            CareerModel career = careers[i];
            weights[i] = userScore.getCategoryScore(career.riasecCategory);
        }
        spokeGraph.setWeights(weights);
    }

//    // TODO
//    @Override
//    public void draw(Kiosk sketch) {
//        Graphics.useSansSerifBold(sketch, 48);
//        Graphics.drawBubbleBackground(sketch);
//        drawHeader(sketch);
//    }

//    @Override
//    public void update(float dt, SceneGraph sceneGraph) {
//        super.update(dt, sceneGraph);
//        // TODO I don't like having this here since it only needs to run once but this is the
//        //  only place where we have access to the SceneGraph (which has the UserScore)
//        // Update the career weights based on the career's Riasec category and the user's score
//        for (int i = 0; i < weights.length; i++) {
//            CareerModel career = LoadedSurveyModel.careers[i];
//            weights[i] = sceneGraph.getUserScore().getCategoryScore(career.riasecCategory);
//        }
//    }
}
