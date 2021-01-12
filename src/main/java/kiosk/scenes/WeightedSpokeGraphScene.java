package kiosk.scenes;

import graphics.SpokeUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.WeightedSpokeGraphSceneModel;

public class WeightedSpokeGraphScene implements Scene {

    private final WeightedSpokeGraphSceneModel model;

    public WeightedSpokeGraphScene(WeightedSpokeGraphSceneModel model) {
        this.model = model;
    }


    @Override
    public void init(Kiosk sketch) { }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {}

    @Override
    public void draw(Kiosk sketch) {
<<<<<<< HEAD
        GraphicsUtil.weightedSpokeGraph(
=======
        SpokeUtil.spokeGraph(
>>>>>>> 9c1e2bf314320e0a99f73d1be3111c29c7cc133d
            sketch, model.size, model.centerX, model.centerY, model.padding,
            model.centerText, model.answers, model.weights
        );
    }
}
