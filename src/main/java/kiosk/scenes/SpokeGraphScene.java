package kiosk.scenes;

import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;

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
        GraphicsUtil.spokeGraph(sketch, model.size, model.xpos, model.ypos,
                model.padding, model.centerText, model.options, model.colors);
    }
}
