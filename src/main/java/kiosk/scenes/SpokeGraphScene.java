package kiosk.scenes;

import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;

import java.util.Arrays;

public class SpokeGraphScene implements Scene {

    private final SpokeGraphSceneModel model;

    public SpokeGraphScene(SpokeGraphSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {

    }

    @Override
    public void draw(Kiosk sketch) {
        var weights = new int[model.options.length];
        Arrays.fill(weights, 1);
        GraphicsUtil.spokeGraph(sketch, model.size, model.xpos, model.ypos,
                model.padding, model.centerText, model.options, weights);
    }
}
