package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneGraph;

public class ResetScene implements Scene {

    @Override
    public void init(Kiosk sketch) {

    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        sceneGraph.reset();
    }

    @Override
    public void draw(Kiosk sketch) {

    }
}
