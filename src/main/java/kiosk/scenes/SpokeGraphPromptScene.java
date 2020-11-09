package kiosk.scenes;

import graphics.GraphicsUtil;
import kiosk.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.SpokeGraphPromptSceneModel;

public class SpokeGraphPromptScene implements Scene {

    private final SpokeGraphPromptSceneModel model;

    public SpokeGraphPromptScene(SpokeGraphPromptSceneModel model) {
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

        sketch.rect(boxX, boxY, boxWidth, boxHeight);
        sketch.fill(0, 0, 0);
        sketch.stroke(0, 0, 0);

        var boxCenterX = (boxX + boxWidth * .5f);
        var boxCenterY = (boxY + boxHeight * .5f);
        var headerWidth = sketch.textWidth(model.headerTitle);
        sketch.textAscent();
        sketch.text(model.headerTitle, boxCenterX - (.5f * headerWidth), boxCenterY);

        var bodyWidth = sketch.textWidth(model.headerBody);
        sketch.text(model.headerBody, boxCenterX - (.5f * bodyWidth), boxCenterY + sketch.textAscent());
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
        GraphicsUtil.spokeGraph(
            sketch,
            sketch.width * .43f,
            sketch.width * .05f + 2 * sketch.width / 5.f,
            sketch.height * .25f,
            1.f,
            model.promptText,
            model.promptOptions,
            model.optionColors
        );
    }
}
