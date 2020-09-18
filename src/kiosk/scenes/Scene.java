package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneGraph;

/**
 * A Scene the user sees and possibly interacts with.
 * Usually the visual side of the SceneModel interface.
 */
public interface Scene {
    /**
     * Init Scene. Useful for adding input handlers and doing
     * calculations that depend on runtime information such as width/height
     * of the window and children nodes.
     * @param sketch the scene will be drawn to
     */
    void init(Kiosk sketch);

    /**
     * Update method called each time the sketch is redrawn.
     * @param dt time in seconds since the last frame
     * @param sceneGraph to push or pop new scenes
     */
    void update(float dt, SceneGraph sceneGraph);

    /**
     * Draw method called each time the sketch is redrawn.
     * @param sketch to draw to
     */
    void draw(Kiosk sketch);
}
