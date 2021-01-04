package kiosk.scenes;

import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.SpokeGraphSceneModel;

public class PathwayScene implements Scene {

    private final SpokeGraphSceneModel model;

    public PathwayScene(SpokeGraphSceneModel model) {
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
                model.padding, model.centerText, model.answers);
    }
}
