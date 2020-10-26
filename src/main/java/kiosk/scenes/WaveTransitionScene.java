package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.WaveTransitionSceneModel;
import processing.core.PApplet;

public class WaveTransitionScene implements Scene {

    // Horizontal "scroll" speed
    private static final int HORIZONTAL_WAVE_SPEED = 500;
    // How fast a bump will reach the top of the wave to the bottom
    private static final int VERTICAL_WAVE_SPEED = 10;
    // How many bumps there are
    private static final float WAVINESS = (float) 0.05;
    // How big the bumps are
    private static final float WAVE_MAGNITUDE = 10;

    private float thetaI = 0;
    private float locationX;
    private final WaveTransitionSceneModel model;

    public WaveTransitionScene(WaveTransitionSceneModel model) {
        this.model = model;
    }

    public void init(Kiosk sketch) {
        this.locationX = sketch.width + WAVE_MAGNITUDE;
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        // Check if the animation is complete
        if (this.locationX < -WAVE_MAGNITUDE) {
            sceneGraph.pushScene(this.model.target);
        }
        this.thetaI += VERTICAL_WAVE_SPEED * dt;
        this.locationX -= HORIZONTAL_WAVE_SPEED * dt;
    }

    @Override
    public void draw(Kiosk sketch) {
        // Note that this sketch doesn't fill the background on purpose

        sketch.stroke(this.model.invertedColors ? 0 : 255);
        sketch.fill(this.model.invertedColors ? 0 : 255);

        float y = 0;
        float theta = this.thetaI;

        // Draw the wave by creating a custom shape
        sketch.beginShape();
        sketch.vertex(sketch.width, 0);
        while (y < sketch.height) {
            sketch.vertex(locationX + WAVE_MAGNITUDE * PApplet.sin(theta), y);
            y += 1;
            theta += WAVINESS;
        }
        sketch.vertex(sketch.width, sketch.height);
        sketch.endShape();
    }
}
