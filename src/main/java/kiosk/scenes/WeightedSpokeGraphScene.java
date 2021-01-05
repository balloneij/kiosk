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
        SpokeUtil.spokeGraph(
            sketch, model.size, model.centerX, model.centerY, model.padding,
            model.centerText, model.options, model.weights, null
        );
    }
}