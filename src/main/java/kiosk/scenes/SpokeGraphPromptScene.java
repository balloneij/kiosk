package kiosk.scenes;

import graphics.GraphicsUtil;
import kiosk.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.SpokeGraphPromptSceneModel;

import java.util.Arrays;

public class SpokeGraphPromptScene implements Scene {

    private final SpokeGraphPromptSceneModel model;
    private float size;
    private float x;
    private float y;
    private float centerX;
    private float centerY;
    
    final static int PADDING = 1;

    public SpokeGraphPromptScene(SpokeGraphPromptSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
        size = sketch.width * .4f;
        x = sketch.width * .05f + 2 * sketch.width / 5.f;
        y = sketch.height * .25f;
        centerX = (size / 2) + x;
        centerY = (size / 2) + y;

        initializeButtons(model, sketch, size, centerX, centerY);
    }

    private static void initializeButtons(SpokeGraphPromptSceneModel model, Kiosk sketch, float size,
            float centerX, float centerY) {
        var degrees = 0.f;
        var weights = model.careerWeights;
        var totalWeight = (float) Arrays.stream(weights).sum();
        var maxValue = (float) Arrays.stream(weights).max().getAsInt();
        var maxRatio = maxValue / totalWeight;

        // for each answer find the degrees and position
        for (var i = 0; i < model.answerButtons.length; i++) {
            var btnModel = model.answerButtons[i].getModel();
            var degOffset = 180 * weights[i] / totalWeight;
            var maxRad = .125f * size;
            var smRad = .5f * size * (float) Math.sin(Math.toRadians(degOffset))
                / (1 + (float) Math.sin(Math.toRadians(degOffset)));
            var colorSelection = model.optionColors[i % model.optionColors.length];

            smRad = Math.min(smRad, maxRad) - PADDING;
            degrees += degOffset;

            var upperLeftX = centerX + (.5f * size - smRad) * Math.cos(Math.toRadians(degrees)) - smRad;
            var upperLeftY = centerY + (.5f * size - smRad) * Math.sin(Math.toRadians(degrees)) - smRad;

            btnModel.isCircle = true;
            btnModel.rgb = new int[]{colorSelection >> 16 & 0xFF, colorSelection >> 8 & 0xFF, colorSelection & 0xFF};
            model.answerButtons[i] = new ButtonControl(btnModel, (int) upperLeftX,
                (int) upperLeftY, 2 * (int) smRad, 2 * (int) smRad);
            degrees += degOffset;
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useSerif(sketch);
        Graphics.drawBubbleBackground(sketch);
        drawHeader(sketch);
        drawCareerGraph(sketch);
        drawPromptGraph(sketch);
    }

    private void drawHeader(Kiosk sketch) {
        sketch.textSize(18.f);
        sketch.fill(256, 256, 256);
        sketch.stroke(256, 256, 256);
        var boxX = sketch.width / 8.f;
        var boxY = sketch.height / 32.f;
        var boxWidth = sketch.width - (sketch.width / 5.f);
        var boxHeight = sketch.height - .8f * sketch.height;

        Graphics.drawRoundedRectangle(sketch, boxX, boxY, boxWidth, boxHeight, 25);
        sketch.fill(0, 0, 0);
        sketch.stroke(0, 0, 0);

        var boxCenterX = (boxX + boxWidth * .5f);
        var boxCenterY = (boxY + boxHeight * .5f);
        var headerWidth = sketch.textWidth(model.headerTitle);
        sketch.textAscent();
        sketch.textSize(52);
        sketch.text(model.headerTitle, boxCenterX, boxCenterY - boxHeight / 3);

        sketch.textSize(34);
        sketch.text(model.headerBody, boxCenterX,boxCenterY + boxHeight / 4f);
    }

    private void drawCareerGraph(Kiosk sketch) {
        GraphicsUtil.spokeGraph(
            sketch,
            sketch.width / 3.f,
            sketch.width * .05f,
            sketch.height * .25f,
            1.f,
            model.careerCenterText,
            model.careerOptions,
            model.careerWeights,
            null
        );
    }

    private void drawPromptGraph(Kiosk sketch) {
//        GraphicsUtil.spokeGraph(
//            sketch,
//            sketch.width * .4f,
//            sketch.width * .05f + 2 * sketch.width / 5.f,
//            sketch.height * .25f,
//            1.f,
//            model.promptText,
//            model.promptOptions,
//            model.optionColors
//        );
        GraphicsUtil.drawInnerCircle(sketch, centerX, centerY, size / 4.f, model.promptText);
        for (var button : model.answerButtons) {
            button.draw(sketch);
        }
    }
}
