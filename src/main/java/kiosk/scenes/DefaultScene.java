package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.DefaultSceneModel;
import processing.core.PConstants;

/**
 * The DefaultScene that is displayed when no survey is loaded. It displays instructions on how to
 * load a survey.
 */
public class DefaultScene implements Scene {
    private final DefaultSceneModel model;
    private ButtonControl homeButton;

    public DefaultScene(DefaultSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
        this.homeButton = GraphicsUtil.initializeHomeButton(sketch);
        sketch.hookControl(this.homeButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        sketch.background(99, 144, 197);
        sketch.fill(255);

        Graphics.useGothic(sketch, 16, false);
        sketch.textSize(16);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.message,
                sketch.width / 2f,
                sketch.height / 2f, sketch.width, sketch.height);

        this.homeButton.draw(sketch);
    }
}
