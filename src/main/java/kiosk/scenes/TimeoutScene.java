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
    private static final int FOREGROUND_WIDTH = Kiosk.getSettings().screenW * 2 / 3;
    private static final int FOREGROUND_HEIGHT = Kiosk.getSettings().screenH * 3 / 4;
    private static final int FOREGROUND_X_PADDING = Kiosk.getSettings().screenW / 6;
    private static final int FOREGROUND_Y_PADDING = Kiosk.getSettings().screenH / 8;
    private static final int FOREGROUND_CURVE_RADIUS = 100;

    // Text
    private static final int TITLE_Y = Kiosk.getSettings().screenH * 1 / 4;
    private static final int TITLE_FONT_SIZE = 28;
    private int warningY = Kiosk.getSettings().screenH * 3 / 6;
    private int warningFontSize = 16;
    private int timerY = Kiosk.getSettings().screenH * 5 / 8;
    private int timerFontSize = 20;

    // Buttons
    private static final int BUTTON_WIDTH = Kiosk.getSettings().screenW / 4;
    private static final int BUTTON_HEIGHT = Kiosk.getSettings().screenH / 6;
    private static final int BUTTON_RADIUS = Kiosk.getSettings().screenW / 8;
    private static final int BUTTON_IMAGE_WIDTH = BUTTON_RADIUS * 4 / 5;
    private static final int BUTTON_IMAGE_HEIGHT = BUTTON_RADIUS * 4 / 5;
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

    public TimeoutScene(TimeoutSceneModel model) {
        this.model = model;
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
                FOREGROUND_X_PADDING + buttonPadding * 2, buttonY,
                BUTTON_WIDTH, BUTTON_HEIGHT * 3 / 4);
        this.homeButton.init(sketch);
        sketch.hookControl(this.homeButton);
        ButtonModel backButtonModel = new ButtonModel();
        backButtonModel.text = "I'm still here!";
        sketch.rectMode(PConstants.CENTER);
        this.backButton = new ButtonControl(backButtonModel,
                sketchWidth - buttonPadding * 2 - BUTTON_WIDTH - FOREGROUND_X_PADDING,
                buttonY,
                BUTTON_WIDTH, BUTTON_HEIGHT * 3 / 4);
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

        // Draw bubble background
        Graphics.drawBubbleBackground(sketch);

        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
                FOREGROUND_X_PADDING + FOREGROUND_WIDTH / 2.f,
                FOREGROUND_Y_PADDING + FOREGROUND_HEIGHT / 2.f,
                FOREGROUND_WIDTH, FOREGROUND_HEIGHT,
                FOREGROUND_CURVE_RADIUS);

        // Draw text
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);

        // Title
        Graphics.useGothic(sketch, TITLE_FONT_SIZE, true);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.title, centerX, TITLE_Y,
                (int) (FOREGROUND_WIDTH * 0.95), FOREGROUND_HEIGHT / 2);

        // Warning
        Graphics.useGothic(sketch, warningFontSize, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.warning, centerX, warningY,
                (int) (FOREGROUND_WIDTH * 0.95), FOREGROUND_HEIGHT / 2);

        // Timer
        Graphics.useGothic(sketch, timerFontSize, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.fill(256, 0, 0);
        sketch.text(this.model.timerText + ((remainingTime / 1000) + 1) + " seconds",
                centerX, timerY, (int) (FOREGROUND_WIDTH * 0.95), FOREGROUND_HEIGHT / 2);

        // Image
        image.draw(sketch, imageX, imageY);

        homeButton.draw(sketch);
        backButton.draw(sketch);
    }
}
