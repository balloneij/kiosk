package kiosk.scenes;

import graphics.SpokeUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.PathwaySceneModel;

public class PathwayScene implements Scene {

    private final PathwaySceneModel model;

    public PathwayScene(PathwaySceneModel model) {
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
<<<<<<< HEAD:src/main/java/kiosk/scenes/PathwayScene.java
        GraphicsUtil.spokeGraph(sketch, model.size, model.xpos, model.ypos,
                model.padding, model.centerText, model.answers);
=======
        SpokeUtil.spokeGraph(sketch, model.size, model.xpos, model.ypos,
                model.padding, model.centerText, model.options, model.colors);
>>>>>>> 9c1e2bf314320e0a99f73d1be3111c29c7cc133d:src/main/java/kiosk/scenes/SpokeGraphScene.java
    }
}
