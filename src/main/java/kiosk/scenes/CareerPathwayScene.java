package kiosk.scenes;

import graphics.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.CareerModel;
import kiosk.models.CareerPathwaySceneModel;
import kiosk.models.LoadedSurveyModel;

/**
 * A scene that displays a spoke graph containing buttons for each of the careers currently in
 * the list, weighted based on the career RIASEC type and the UserScore.
 */
public class CareerPathwayScene extends PathwayScene {
    private final CareerPathwaySceneModel model;
    private final int[] weights;

    /**
     * Creates a new CareerPathwayScene using the given model.
     * @param model A CareerPathwaySceneModel used to create the scene.
     */
    public CareerPathwayScene(CareerPathwaySceneModel model) {
        super(model);
        this.model = model;
        weights = new int[LoadedSurveyModel.careers.length];
    }

    // TODO
//    @Override
//    public void draw(Kiosk sketch) {
//        Graphics.useSansSerifBold(sketch, 48);
//        Graphics.drawBubbleBackground(sketch);
//        drawHeader(sketch);
//        SpokeUtil.spokeGraph(sketch, size, centerX, centerY, 5, model.centerText,
//            buttons, weights);
//    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        super.update(dt, sceneGraph);
        // TODO I don't like having this here since it only needs to run once but this is the
        //  only place where we have access to the SceneGraph (which has the UserScore)
        // Update the career weights based on the career's Riasec category and the user's score
        for (int i = 0; i < weights.length; i++) {
            CareerModel career = LoadedSurveyModel.careers[i];
            weights[i] = sceneGraph.getUserScore().getCategoryScore(career.riasecCategory);
        }
    }
}
