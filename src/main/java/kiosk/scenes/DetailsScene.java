package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.DetailsSceneModel;
import processing.core.PConstants;


public class DetailsScene implements Scene {

    private final DetailsSceneModel model;
    private ButtonControl centerButton;
    private ButtonControl nextButton;
    private ButtonControl homeButton;
    private ButtonControl backButton;

    // Buttons
    private static final int BUTTON_WIDTH = Kiosk.getSettings().screenW / 8;
    private static final int BUTTON_HEIGHT = Kiosk.getSettings().screenH / 6;
    private static final int BUTTON_PADDING = 20;

    // White foreground
    private static final int FOREGROUND_WIDTH = Kiosk.getSettings().screenW * 2 / 3;
    private static final int FOREGROUND_HEIGHT = Kiosk.getSettings().screenH * 3 / 4;
    private static final int FOREGROUND_X_PADDING = Kiosk.getSettings().screenW / 6;
    private static final int FOREGROUND_Y_PADDING = Kiosk.getSettings().screenH / 8;
    private static final int FOREGROUND_CURVE_RADIUS = 100;

    // Text
    private static final int TITLE_Y = Kiosk.getSettings().screenH / 4;
    private static final int BODY_Y = Kiosk.getSettings().screenH * 3 / 8;
    private static final int TITLE_FONT_SIZE = 24;
    private static final int BODY_FONT_SIZE = 16;

    // Button Image Props
    private static final int BUTTON_RADIUS = Kiosk.getSettings().screenW / 8;
    private static final int BUTTON_IMAGE_WIDTH = BUTTON_RADIUS * 4 / 5;
    private static final int BUTTON_IMAGE_HEIGHT = BUTTON_RADIUS * 4 / 5;

    /**
     * Detials Scene show a title, body of text, and a button at the bottom.
     * @param model The model object where we get our information.
     */
    public DetailsScene(DetailsSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchHeight = Kiosk.getSettings().screenH;
        final int sketchWidth = Kiosk.getSettings().screenW;

        if (!Kiosk.getSceneGraph().getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton = GraphicsUtil.initializeHomeButton();
            this.homeButton.setHomeOrBack(true);
            sketch.hookControl(this.homeButton);
            this.backButton = GraphicsUtil.initializeBackButton(sketch);
            this.backButton.setHomeOrBack(true);
            sketch.hookControl(this.backButton);
        }

        if (this.model.button.image != null) {
            this.model.button.image.width = BUTTON_IMAGE_WIDTH;
            this.model.button.image.height = BUTTON_IMAGE_HEIGHT;
        }

        this.centerButton = new ButtonControl(
            this.model.button,
            (sketchWidth / 2) - (BUTTON_WIDTH * 5 / 8),
            FOREGROUND_Y_PADDING + FOREGROUND_HEIGHT
                - (BUTTON_WIDTH * 5 / 4 + BUTTON_PADDING),
                BUTTON_WIDTH * 5 / 4,
                BUTTON_WIDTH * 5 / 4
        );
        this.centerButton.init(sketch);
        sketch.hookControl(this.centerButton);

        this.nextButton = GraphicsUtil.initializeNextButton(sketch);
        sketch.hookControl(this.nextButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (!Kiosk.getSceneGraph().getRootSceneModel().getId().equals(this.model.getId())) {
            if (this.homeButton.wasClicked()) {
                sceneGraph.reset();
            } else if (this.backButton.wasClicked()) {
                sceneGraph.popScene();
            }
        }
        if (this.centerButton.wasClicked()) {
            sceneGraph.pushScene(this.centerButton.getTarget());
        } else if (this.nextButton.wasClicked()) {
            sceneGraph.pushScene(this.centerButton.getTarget());
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;
        Graphics.drawBubbleBackground(sketch);

        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
            FOREGROUND_X_PADDING + FOREGROUND_WIDTH / 2.f,
                FOREGROUND_Y_PADDING + FOREGROUND_HEIGHT / 2.f,
            FOREGROUND_WIDTH, FOREGROUND_HEIGHT,
            FOREGROUND_CURVE_RADIUS);

        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);


        // Title
        Graphics.useGothic(sketch, TITLE_FONT_SIZE, true);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(33);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.title, centerX, (int) (TITLE_Y * 1.15),
                (int) (FOREGROUND_WIDTH * 0.95), FOREGROUND_HEIGHT / 5);

        // Body
        Graphics.useGothic(sketch, BODY_FONT_SIZE, false);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(25);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.body, centerX, (int) (BODY_Y * 1.15),
                (int) (FOREGROUND_WIDTH * 0.95), FOREGROUND_HEIGHT / 5);


        this.centerButton.draw(sketch);
        this.nextButton.draw(sketch);
        if (!Kiosk.getSceneGraph().getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton.draw(sketch);
            this.backButton.draw(sketch);
        }
    }
}
