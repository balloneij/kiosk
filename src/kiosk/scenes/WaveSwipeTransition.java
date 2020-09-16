package kiosk.scenes;

import kiosk.EventCallback;
import kiosk.Kiosk;
import kiosk.SceneControl;
import processing.core.PApplet;
import processing.event.MouseEvent;

public class WaveSwipeTransition implements Scene {

    // The higher the wave speed, the faster vertical speed the waves move
    private static final int WAVE_SPEED = 10;
    // The higher the waviness, the more peaks/troughs there are in the line
    private static final float WAVINESS = (float) 0.05;

    private float thetaI = 0;
    private float x;
    private boolean invertedColors;

    public WaveSwipeTransition(boolean invertedColors)
    {
        this.invertedColors = invertedColors;
    }

    public void init(Kiosk app)
    {
        this.x = app.width + 10;
    }

    @Override
    public void update(float dt, SceneControl sceneControl) {
        if (this.x < -10)
        {
            sceneControl.popScene();
        }
        this.thetaI += WaveSwipeTransition.WAVE_SPEED * dt;
        this.x -= 500 * dt;
    }

    @Override
    public void draw(Kiosk app) {
        app.stroke(this.invertedColors ? 0 : 255);
        app.fill(this.invertedColors ? 0 : 255);

        float y = 0;
        float theta = thetaI;

        app.beginShape();
        app.vertex(app.width, 0);
        while (y < app.height)
        {
            app.vertex(x + 10 * PApplet.sin(theta), y);
            y += 1;
            theta += WaveSwipeTransition.WAVINESS;
        }
        app.vertex(app.width, app.height);
        app.endShape();
    }
}
