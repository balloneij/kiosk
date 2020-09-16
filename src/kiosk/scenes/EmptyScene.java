package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneControl;

public class EmptyScene implements Scene {
    @Override
    public void init(Kiosk app) {

    }

    @Override
    public void update(float dt, SceneControl sceneControl) {

    }

    @Override
    public void draw(Kiosk app) {
        app.background(0);
        app.fill(255);
        app.text("Empty scene", 32, 32);
    }
}
