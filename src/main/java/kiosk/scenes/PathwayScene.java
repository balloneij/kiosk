package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
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
    protected final SpokeGraph spokeGraph;
    private ButtonControl backButton;
    private ButtonControl homeButton;

    /**
     * Create a pathway scene.
     * @param model to base the scene off of
     */
    public PathwayScene(PathwaySceneModel model) {
        this.model = model;
        for (ButtonModel careerModel : model.buttonModels) {
            careerModel.isCircle = true;
        }

        // Create the spoke graph
        float size = SCREEN_H - HEADER_Y - HEADER_H;
        this.spokeGraph = new SpokeGraph(size,
                SCREEN_W / 2f - size / 2,
                HEADER_Y + HEADER_H,
                this.model.centerText,
                this.model.buttonModels);

        if (!Kiosk.getRootSceneModel().getId().equals(this.model.getId())) {
            this.backButton = ButtonControl.createBackButton();
            this.homeButton = ButtonControl.createHomeButton();
        }
    }

    @Override
    public void init(Kiosk sketch) {
        if (!Kiosk.getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton = GraphicsUtil.initializeHomeButton();
            sketch.hookControl(this.homeButton);
            this.backButton = GraphicsUtil.initializeBackButton(sketch);
            sketch.hookControl(this.backButton);
        }

        for (ButtonControl careerOption : this.spokeGraph.getButtonControls()) {
            sketch.hookControl(careerOption);
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.spokeGraph.getButtonControls()) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget(), button.getModel().category);
            }
        }

        if (!Kiosk.getRootSceneModel().getId().equals(this.model.getId())) {
            if (this.homeButton.wasClicked()) {
                sceneGraph.reset();
            } else if (this.backButton.wasClicked()) {
                sceneGraph.popScene();
            }
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
        this.spokeGraph.draw(sketch);

        if (!Kiosk.getRootSceneModel().getId().equals(this.model.getId())) {
            this.backButton.draw(sketch);
            this.homeButton.draw(sketch);
        }
    }

    protected void drawHeader(Kiosk sketch) {
        // Draw the white header box
        sketch.fill(255);
        sketch.stroke(255);

        Graphics.drawRoundedRectangle(sketch,
                HEADER_X + HEADER_W / 2, HEADER_Y + HEADER_H / 2,
                HEADER_W, HEADER_H, HEADER_CURVE_RADIUS);

        // Draw the title and body
        sketch.fill(0);
        sketch.stroke(0);

        Graphics.useGothic(sketch, HEADER_TITLE_FONT_SIZE, true);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(model.headerTitle, HEADER_CENTER_X, HEADER_TITLE_Y,
                (int) (HEADER_W * 0.95), HEADER_H / 2);

        Graphics.useGothic(sketch, HEADER_BODY_FONT_SIZE, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(model.headerBody, HEADER_CENTER_X,
                (int) (HEADER_BODY_Y * 1.15), (int) (HEADER_W * 0.95), HEADER_H / 2);
    }
}
