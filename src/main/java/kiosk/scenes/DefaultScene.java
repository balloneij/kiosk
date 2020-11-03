package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.DefaultSceneModel;
import processing.core.PConstants;

/**
 * The DefaultScene that is displayed when no survey is loaded. It displays instructions on how to load a survey.
 */
public class DefaultScene implements Scene {
    private final DefaultSceneModel model;

    public DefaultScene(DefaultSceneModel model) {
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
        sketch.background(99, 144, 197);
        sketch.fill(255);

        // TODO: Set the font
        sketch.textSize(16);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.text(this.model.message, sketch.width / 2f, sketch.height / 2f);
    }
}
