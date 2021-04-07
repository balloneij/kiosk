package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.CareerDescriptionModel;
import kiosk.models.CreditsSceneModel;
import processing.core.PConstants;


public class CareerDescriptionScene implements Scene {

    private final CareerDescriptionModel model;
    private ButtonControl centerButton;
    private ButtonControl homeButton;
    private ButtonControl backButton;
    private ButtonControl supplementaryButton;

    private static int SCREEN_W = Kiosk.getSettings().screenW;
    private static int SCREEN_H = Kiosk.getSettings().screenH;

    // Button
    private static int BUTTON_WIDTH = SCREEN_W / 8;
    private static int BUTTON_HEIGHT = SCREEN_H / 8;
    private static int BUTTON_PADDING = 20;

    // White foreground
    private static int FOREGROUND_WIDTH = SCREEN_W * 2 / 3;
    private static int FOREGROUND_HEIGHT = SCREEN_H * 3 / 4;
    private static int FOREGROUND_X_PADDING
            = SCREEN_W / 6 + FOREGROUND_WIDTH / 2;
    private static int FOREGROUND_Y_PADDING
            = SCREEN_H / 8 + FOREGROUND_HEIGHT / 2;
    private static int FOREGROUND_CURVE_RADIUS = 100;

    // Text
    private static int TITLE_Y = SCREEN_H / 4;
    private static int INSTRUCTIONS_Y = SCREEN_H * 3 / 8;
    private static int DESCRIPTION_Y = SCREEN_H * 4 / 8;
    private static int TITLE_FONT_SIZE = SCREEN_W / 55;
    private static int INSTRUCTIONS_FONT_SIZE = SCREEN_W / 60;
    private static int DESCRIPTION_FONT_SIZE = SCREEN_W / 58;

    /**
     * Career Description Scene shows a title, body of text, and a button at the bottom.
     * @param model The model object where we get our information.
     */
    public CareerDescriptionScene(CareerDescriptionModel model) {
        this.model = model;
        SCREEN_W = Kiosk.getSettings().screenW;
        SCREEN_H = Kiosk.getSettings().screenH;

        // Button
        BUTTON_WIDTH = SCREEN_W / 8;
        BUTTON_HEIGHT = SCREEN_H / 8;
        BUTTON_PADDING = 20;

        // White foreground
        FOREGROUND_WIDTH = SCREEN_W * 2 / 3;
        FOREGROUND_HEIGHT = SCREEN_H * 3 / 4;
        FOREGROUND_X_PADDING
                = SCREEN_W / 6 + FOREGROUND_WIDTH / 2;
        FOREGROUND_Y_PADDING
                = SCREEN_H / 8 + FOREGROUND_HEIGHT / 2;
        FOREGROUND_CURVE_RADIUS = 100;

        // Text
        TITLE_Y = SCREEN_H / 4;
        INSTRUCTIONS_Y = SCREEN_H * 3 / 8;
        DESCRIPTION_Y = SCREEN_H * 4 / 8;
        TITLE_FONT_SIZE = SCREEN_W / 55;
        INSTRUCTIONS_FONT_SIZE = SCREEN_W / 60;
        DESCRIPTION_FONT_SIZE = SCREEN_W / 58;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchHeight = Kiosk.getSettings().screenH;
        final int sketchWidth = Kiosk.getSettings().screenW;

        this.homeButton = GraphicsUtil.initializeHomeButton();
        sketch.hookControl(this.homeButton);
        this.backButton = GraphicsUtil.initializeBackButton(sketch);
        sketch.hookControl(this.backButton);

        if (this.model.button.image != null) {
            this.model.button.image.width = BUTTON_WIDTH;
            this.model.button.image.height = BUTTON_HEIGHT;
        }

        this.centerButton = new ButtonControl(
            this.model.button,
            (sketchWidth / 2) - (BUTTON_WIDTH / 2),
            sketchHeight * 2 / 3,
                BUTTON_WIDTH,
                BUTTON_HEIGHT
        );
        this.centerButton.init(sketch);
        sketch.hookControl(this.centerButton);

        this.supplementaryButton = GraphicsUtil.initializeMsoeButton(sketch);
        this.supplementaryButton.init(sketch);
        sketch.hookControl(this.supplementaryButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.backButton.wasClicked()) {
            sceneGraph.popScene();
        } else if (this.centerButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.supplementaryButton.wasClicked()) {
            sceneGraph.pushScene(new CreditsSceneModel());
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;
        Graphics.drawBubbleBackground(sketch);

        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
            FOREGROUND_X_PADDING, FOREGROUND_Y_PADDING,
            FOREGROUND_WIDTH, FOREGROUND_HEIGHT,
            FOREGROUND_CURVE_RADIUS);

        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);


        // Career Name
        Graphics.useGothic(sketch, TITLE_FONT_SIZE, true);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(33);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.careerModel.name, centerX, (int) (TITLE_Y * 1.15),
                (int) (FOREGROUND_WIDTH * 0.95), FOREGROUND_HEIGHT / 5);


        // What to do next
        Graphics.useGothic(sketch, INSTRUCTIONS_FONT_SIZE, false);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(25);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.body, centerX, (int) (INSTRUCTIONS_Y * 1.15),
                (int) (FOREGROUND_WIDTH * 0.95), FOREGROUND_HEIGHT / 5);

        // Career Description
        Graphics.useGothic(sketch, DESCRIPTION_FONT_SIZE, false);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(25);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.careerModel.description, centerX, (int) (DESCRIPTION_Y * 1.15),
                (int) (FOREGROUND_WIDTH * 0.95), FOREGROUND_HEIGHT / 5);




        this.centerButton.draw(sketch);
        this.homeButton.draw(sketch);
        this.backButton.draw(sketch);
        this.supplementaryButton.draw(sketch);
    }
}
