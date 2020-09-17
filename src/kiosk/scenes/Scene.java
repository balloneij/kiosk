package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneControl;

public interface Scene {
    void init(final Kiosk app);

    void update(float dt, SceneControl sceneControl);

    void draw(Kiosk app);
}
