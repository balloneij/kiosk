package kiosk.scenes;

import kiosk.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ErrorSceneModel;
import processing.core.PConstants;

public class ErrorScene implements Scene {

    private final ErrorSceneModel model;

    public ErrorScene(ErrorSceneModel model) {
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
        sketch.fill(255);

        // Set background color depending on intensity of the error
        switch (this.model.intensity) {
            // Cadmium red
            case SEVERE -> sketch.background(227, 0, 34);
            // Safety yellow
            case WARNING -> {
                sketch.fill(0);
                sketch.background(238, 210, 2);
            }
            // Winter wizard blue
            case INFORMATION -> sketch.background(150, 221, 255);
            // Black
            default -> sketch.background(0);
        }

        Graphics.useSansSerif(sketch, 16, true);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.text(this.model.errorMsg, sketch.width / 2f, sketch.height / 2f);
    }
}
