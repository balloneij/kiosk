package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.Settings;
import kiosk.models.PathwaySceneModel;
import processing.core.PConstants;

public class PathwayScene implements Scene {

    // Pull constants from the settings
    private static final int SCREEN_W = Kiosk.getSettings().screenW;
    private static final int SCREEN_H = Kiosk.getSettings().screenH;

    // Header
    private static final float HEADER_W = SCREEN_W * 3f / 4;
    private static final float HEADER_H = SCREEN_H / 6f;
    private static final float HEADER_X = (SCREEN_W - HEADER_W) / 2;
    private static final float HEADER_Y = SCREEN_H / 32f;
    private static final float HEADER_CENTER_X = HEADER_X + (HEADER_W / 2);
    private static final float HEADER_CENTER_Y = HEADER_Y + (HEADER_H / 2);
    private static final int HEADER_CURVE_RADIUS = 25;

    // Header title
    private static final int HEADER_TITLE_FONT_SIZE = 24;
    private static final float HEADER_TITLE_Y = HEADER_CENTER_Y - HEADER_TITLE_FONT_SIZE;

    // Header body
    private static final int HEADER_BODY_FONT_SIZE = 16;
    private static final float HEADER_BODY_Y = HEADER_CENTER_Y + HEADER_BODY_FONT_SIZE;

    private final PathwaySceneModel model;
    private ButtonControl[] careerOptions;
    private ButtonControl homeButton;
    private ButtonControl backButton;

    float size;
    float centerX;
    float centerY;

    public PathwayScene(PathwaySceneModel model) {
        this.model = model;
        this.careerOptions = new ButtonControl[this.model.careers.length];
    }

    @Override
    public void init(Kiosk sketch) {
        centerX = sketch.width / 2.f;
        centerY = (sketch.height  * .57f);
        size = sketch.height * .75f;

        this.homeButton = GraphicsUtil.initializeHomeButton();
        sketch.hookControl(this.homeButton);
        this.backButton = GraphicsUtil.initializeBackButton(sketch);
        sketch.hookControl(this.backButton);

        this.careerOptions = new ButtonControl[this.model.careers.length];
        for (int i = 0; i < careerOptions.length; i++) {
            this.careerOptions[i] = new ButtonControl(this.model.careers[i], 0, 0, 0, 0);
            this.model.careers[i].isCircle = true;
        }

        for (ButtonControl careerOption : this.careerOptions) {
            sketch.hookControl(careerOption);
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.careerOptions) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget());
            }
        }
        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.backButton.wasClicked()) {
            sceneGraph.popScene();
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useGothic(sketch, 48, true);
        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.fill(0);
        Graphics.drawBubbleBackground(sketch);
        drawHeader(sketch);
        SpokeUtil.spokeGraph(sketch, size, centerX, centerY, 5, model.centerText, careerOptions, true);
        this.homeButton.draw(sketch);
        this.backButton.draw(sketch);
    }

    private void drawHeader(Kiosk sketch) {
        // Draw the white header box
        sketch.fill(255);
        sketch.stroke(255);

        Graphics.drawRoundedRectangle(sketch,
                HEADER_X, HEADER_Y, HEADER_W, HEADER_H, HEADER_CURVE_RADIUS);

        // Draw the title and body
        sketch.fill(0);
        sketch.stroke(0);

        Graphics.useGothic(sketch, HEADER_TITLE_FONT_SIZE, true);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(model.headerTitle, HEADER_CENTER_X, HEADER_TITLE_Y, (int) (HEADER_W * 0.95), HEADER_H / 2);

        Graphics.useGothic(sketch, HEADER_BODY_FONT_SIZE, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(model.headerBody, HEADER_CENTER_X, (int)(HEADER_BODY_Y * 1.15), (int) (HEADER_W * 0.95), HEADER_H / 2);
    }
}
