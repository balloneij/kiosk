package kiosk.scenes;

import kiosk.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import processing.core.PConstants;

public class EmptyScene implements Scene {

    private float textX;
    private float textY;

    @Override
    public void init(Kiosk sketch) {
        this.textX = sketch.width / 2.0f;
        this.textY = sketch.height / 2.0f;
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {

    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useGothic(sketch, 24);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(255);

        sketch.background(0);
        sketch.rectMode(PConstants.CENTER);
        sketch.text("Empty scene", this.textX, this.textY, sketch.width / 2f, sketch.height / 3f);
        sketch.text("How did you get here?", this.textX, this.textY + 32, sketch.width / 2f, sketch.height / 3f);
    }
}
