package kiosk.scenes;

import graphics.Color;
import graphics.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.TimeoutSceneModel;
import processing.core.PConstants;


public class TimeoutScene implements Scene {

    // White foreground
    private static int foregroundWidth = Kiosk.getSettings().screenW * 2 / 3;
    private static int foregroundHeight = Kiosk.getSettings().screenH * 3 / 4;
    private static int foregroundXPadding = Kiosk.getSettings().screenW / 6;
    private static int foregroundYPadding = Kiosk.getSettings().screenH / 8;
    private static int foregroundCurveRadius = 100;

    // Text
    private static int titleY = Kiosk.getSettings().screenH / 4;
    private static int titleFontSize = Kiosk.getSettings().screenW / 55;
    private int warningY = Kiosk.getSettings().screenH * 3 / 6;
    private int warningFontSize = Kiosk.getSettings().screenW / 60;
    private int timerY = Kiosk.getSettings().screenH * 5 / 8;
    private int timerFontSize = Kiosk.getSettings().screenW / 58;

    // Buttons
    private static int buttonWidth = Kiosk.getSettings().screenW / 4;
    private static int buttonHeight = Kiosk.getSettings().screenH / 6;
    private static int buttonRadius = Kiosk.getSettings().screenW / 8;
    private static int buttonImageWidth = buttonRadius * 4 / 5;
    private static int buttonImageHeight = buttonRadius * 4 / 5;
    private int buttonPadding = Kiosk.getSettings().screenW / 60;
    private int buttonY = Kiosk.getSettings().screenH * 17 / 24;

    // Image
    private int imageY = Kiosk.getSettings().screenH * 9 / 24;
    private int imageX = Kiosk.getSettings().screenW / 2;

    private final TimeoutSceneModel model;
    private ButtonControl homeButton;
    private ButtonControl backButton;
    private Image image;

    public int remainingTime = 0;

    /**
     * Default constructor.
     * @param model the model to make
     */
    public TimeoutScene(TimeoutSceneModel model) {
        this.model = model;
        // White foreground
        foregroundWidth = Kiosk.getSettings().screenW * 2 / 3;
        foregroundHeight = Kiosk.getSettings().screenH * 3 / 4;
        foregroundXPadding = Kiosk.getSettings().screenW / 6;
        foregroundYPadding = Kiosk.getSettings().screenH / 8;
        foregroundCurveRadius = 100;

        // Text
        titleY = Kiosk.getSettings().screenH / 4;
        titleFontSize = Kiosk.getSettings().screenW / 55;
        warningY = Kiosk.getSettings().screenH * 3 / 6;
        warningFontSize = Kiosk.getSettings().screenW / 60;
        timerY = Kiosk.getSettings().screenH * 5 / 8;
        timerFontSize = Kiosk.getSettings().screenW / 58;

        // Buttons
        buttonWidth = Kiosk.getSettings().screenW / 4;
        buttonHeight = Kiosk.getSettings().screenH / 6;
        buttonRadius = Kiosk.getSettings().screenW / 8;
        buttonImageWidth = buttonRadius * 4 / 5;
        buttonImageHeight = buttonRadius * 4 / 5;
        buttonPadding = Kiosk.getSettings().screenW / 60;
        buttonY = Kiosk.getSettings().screenH * 17 / 24;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchWidth = Kiosk.getSettings().screenW;
        final int sketchHeight = Kiosk.getSettings().screenH;

        ButtonModel homeButtonModel = new ButtonModel();
        homeButtonModel.text = "Let's Start Over";
        homeButtonModel.rgb = Color.DW_MAROON_RGB;
        sketch.rectMode(PConstants.CENTER);
        this.homeButton = new ButtonControl(homeButtonModel,
                foregroundXPadding + buttonPadding * 2, buttonY,
                buttonWidth, buttonHeight * 3 / 4);
        this.homeButton.init(sketch);
        sketch.hookControl(this.homeButton);
        ButtonModel backButtonModel = new ButtonModel();
        backButtonModel.text = "I'm still here!";
        sketch.rectMode(PConstants.CENTER);
        this.backButton = new ButtonControl(backButtonModel,
                sketchWidth - buttonPadding * 2 - buttonWidth - foregroundXPadding,
                buttonY, buttonWidth, buttonHeight * 3 / 4);
        this.backButton.init(sketch);
        sketch.hookControl(this.backButton);

        this.model.title = "Are You Still There?";
        this.model.warning = "When the survey starts over, all your progress will be lost!";
        this.model.timerText = "The survey will start over in ";

        image = Image.createImage(sketch, model.imageModel);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.backButton.wasClicked()) {
            sceneGraph.popScene();
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;

        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
                foregroundXPadding + foregroundWidth / 2.f,
                foregroundYPadding + foregroundHeight / 2.f,
                foregroundWidth, foregroundHeight,
                foregroundCurveRadius);

        // Draw text
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);

        // Title
        Graphics.useGothic(sketch, titleFontSize, true);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.title, centerX, titleY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 2);

        // Warning
        Graphics.useGothic(sketch, warningFontSize, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.warning, centerX, warningY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 2f);

        // Timer
        Graphics.useGothic(sketch, timerFontSize, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.fill(256, 0, 0);
        if (((remainingTime / 1000) + 1) == 1) {
            sketch.text(this.model.timerText + ((remainingTime / 1000) + 1) + " second",
                    centerX, timerY, (int) (foregroundWidth * 0.95), foregroundHeight / 2f);
        } else {
            sketch.text(this.model.timerText + ((remainingTime / 1000) + 1) + " seconds",
                    centerX, timerY, (int) (foregroundWidth * 0.95), foregroundHeight / 2f);
        }

        // Image
        sketch.imageMode(PConstants.CENTER);
        image.draw(sketch, imageX, imageY);

        homeButton.draw(sketch);
        backButton.draw(sketch);
    }
}
