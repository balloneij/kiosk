package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.WeightedSpokeGraphSceneModel;

import java.util.Arrays;

public class WeightedSpokeGraphScene implements Scene {

    private final WeightedSpokeGraphSceneModel model;
    private float totalWeights;
    private float centerX;
    private float centerY;

    public WeightedSpokeGraphScene(WeightedSpokeGraphSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
        this.totalWeights = Arrays.stream(this.model.weights).sum();
        this.centerX = model.size / 2.f;
        this.centerY = model.size / 2.f;
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {}

    @Override
    public void draw(Kiosk sketch) {
        sketch.fill(256, 0, 0);
        sketch.rect(0, 0, model.size, model.size);

        var bigCircleRad = this.model.size / 4;
        sketch.ellipse(centerX, centerY, bigCircleRad, bigCircleRad);

        float deg = 0;
        for (int i = 0; i < model.options.length; i++) {
            var text = model.options[i];
            var weight = model.weights[i];
            sketch.fill(0, 0, 0);

            var degOffSet = 180 * weight / this.totalWeights; // Subtract from old deg to get delta theta
            deg += degOffSet;

            var smRad = (.5f * this.model.size * Math.sin(Math.toRadians(degOffSet))) / (1 + Math.sin(Math.toRadians(degOffSet)));
            var maxRad = .125f * model.size;

            if (smRad > maxRad)
                smRad = maxRad;


            var smX = (-smRad + this.model.size * .5) * Math.cos(Math.toRadians(deg)) + centerX;
            var smY = (-smRad + this.model.size * .5) * Math.sin(Math.toRadians(deg)) + centerY;

            sketch.line(
                    centerX + (float) Math.cos(Math.toRadians(deg)) * this.model.size * .125f,
                    centerY + (float) Math.sin(Math.toRadians(deg)) * this.model.size * .125f,
                    (float) Math.cos(Math.toRadians(deg)) * .5f * this.model.size + centerX,
                    (float) Math.sin(Math.toRadians(deg)) * .5f * this.model.size + centerY
            );

            sketch.ellipse((float) smX, (float) smY, (float) smRad * 2, (float) smRad * 2);
            deg += degOffSet;

        }
    }
}
