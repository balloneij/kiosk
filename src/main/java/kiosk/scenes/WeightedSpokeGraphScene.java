package kiosk.scenes;

import java.util.Arrays;

import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.WeightedSpokeGraphSceneModel;

public class WeightedSpokeGraphScene implements Scene {

    private final WeightedSpokeGraphSceneModel model;
    private float totalWeights;
    private float centerX;
    private float centerY;

    public WeightedSpokeGraphScene(WeightedSpokeGraphSceneModel model) {
        this.model = model;
    }

    private final int[][] colorIntensity = {
        {212, 177, 0},  // Dull orange
        {201, 212, 0},  // Dull green
        {152, 212, 0},  // Medium green
        {53, 212, 0},   // Dark green
        {33, 133, 0},   // Darker green
        {0, 79, 0}      // Intense green
    };

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

        var bigCircleRad = this.model.size / 4;
        sketch.fill(219, 146, 0);
        sketch.stroke(219, 146, 0);
        sketch.ellipse(centerX, centerY, bigCircleRad, bigCircleRad);

        sketch.fill(256, 256, 256);
        sketch.stroke(256, 256, 256);
        sketch.textSize(.5f * this.model.size
                / (GraphicsUtil.TextRatioEstimate * largestTextLine(model.centerText)));
        var textWidth = sketch.textWidth(model.centerText);
        sketch.text(this.model.centerText, centerX - (textWidth / 2), centerY);

        float deg = 0;
        for (int i = 0; i < model.options.length; i++) {
            var text = model.options[i];
            var weight = model.weights[i];
            sketch.fill(0, 0, 0);

            var degOffSet = 180 * weight / this.totalWeights;
            deg += degOffSet;

            var smRad = (.5f * this.model.size * Math.sin(Math.toRadians(degOffSet)))
                    / (1 + Math.sin(Math.toRadians(degOffSet)));
            var maxRad = .125f * model.size;

            if (smRad > maxRad) {
                smRad = maxRad;
            }

            smRad -= this.model.padding;


            var smX = (-smRad + this.model.size * .5) * Math.cos(Math.toRadians(deg)) + centerX;
            var smY = (-smRad + this.model.size * .5) * Math.sin(Math.toRadians(deg)) + centerY;

            sketch.stroke(256, 256, 256);
            sketch.line(
                centerX + (float) Math.cos(Math.toRadians(deg)) * this.model.size * .125f,
                centerY + (float) Math.sin(Math.toRadians(deg)) * this.model.size * .125f,
                (float) Math.cos(Math.toRadians(deg)) * .5f * this.model.size + centerX,
                (float) Math.sin(Math.toRadians(deg)) * .5f * this.model.size + centerY
            );

            var colorSelection = model.weights[i];
            sketch.stroke(colorIntensity[colorSelection][0], colorIntensity[colorSelection][1], colorIntensity[colorSelection][2]);
            sketch.fill(colorIntensity[colorSelection][0], colorIntensity[colorSelection][1], colorIntensity[colorSelection][2]);

            sketch.ellipse((float) smX, (float) smY, (float) smRad * 2, (float) smRad * 2);
            deg += degOffSet;

            sketch.stroke(256, 256, 256);
            sketch.fill(256, 256, 256);

            textWidth = sketch.textWidth(text);
            sketch.textSize(2 * (float) smRad / (GraphicsUtil.TextRatioEstimate * largestTextLine(text)));
            sketch.text(text, (float) smX - (textWidth / 2), (float) smY);
        }
    }
    private static int largestTextLine(String text) {
        var lineList = text.split("\n");
        int largestLineSize = Integer.MIN_VALUE;
        for (var line : lineList) {
            if (line.length() > largestLineSize) {
                largestLineSize = line.length();
            }
        }
        return largestLineSize;
    }
}
