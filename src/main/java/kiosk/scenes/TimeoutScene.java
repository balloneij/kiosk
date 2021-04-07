package kiosk.scenes;

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
    private static int titleFontSize = 24;
    private static int promptY = Kiosk.getSettings().screenH * 3 / 6;
    private static int promptFontSize = 16;
    private static int actionY = Kiosk.getSettings().screenH / 2;
    private static int actionFontSize = 20;

    // Buttons
    private static int buttonWidth = Kiosk.getSettings().screenW / 8;
    private static int buttonHeight = Kiosk.getSettings().screenH / 6;
    private static int buttonRadius = Kiosk.getSettings().screenW / 8;
    private static int buttonImageWidth = buttonRadius * 4 / 5;
    private static int buttonImageHeight = buttonRadius * 4 / 5;
    private static int buttonPadding = 20;
    private static int buttonY = Kiosk.getSettings().screenH * 7 / 12;

    private final TimeoutSceneModel model;
    private ButtonControl homeButton;
    private ButtonControl backButton;

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
        titleFontSize = 24;
        promptY = Kiosk.getSettings().screenH * 3 / 6;
        promptFontSize = 16;
        actionY = Kiosk.getSettings().screenH / 2;
        actionFontSize = 20;

        // Buttons
        buttonWidth = Kiosk.getSettings().screenW / 8;
        buttonHeight = Kiosk.getSettings().screenH / 6;
        buttonRadius = Kiosk.getSettings().screenW / 8;
        buttonImageWidth = buttonRadius * 4 / 5;
        buttonImageHeight = buttonRadius * 4 / 5;
        buttonPadding = 20;
        buttonY = Kiosk.getSettings().screenH * 7 / 12;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchWidth = Kiosk.getSettings().screenW;
        final int sketchHeight = Kiosk.getSettings().screenH;

        ButtonModel homeButtonModel = new ButtonModel();
        homeButtonModel.text = "Take me back to the beginning!";
        sketch.rectMode(PConstants.CENTER);
        this.homeButton = new ButtonControl(homeButtonModel,
                buttonPadding * 2, sketchHeight - buttonPadding * 5,
                buttonWidth * 3, buttonHeight * 3 / 4);
        sketch.hookControl(this.homeButton);
        ButtonModel backButtonModel = new ButtonModel();
        backButtonModel.text = "I'm still here!";
        sketch.rectMode(PConstants.CENTER);
        this.backButton = new ButtonControl(backButtonModel,
                sketchWidth - buttonPadding * 2 - buttonWidth * 3,
                sketchHeight - buttonPadding * 5,
                buttonWidth * 3, buttonHeight * 3 / 4);
        sketch.hookControl(this.backButton);

        this.model.title = "Are You Still There?";
        this.model.prompt = "\n\n\n If you still want to complete the survey, click "
                + "\n \"I'm still here!\" "
                + "\n Careful, if you don't choose an option, "
                + "\n the survey will automatically reset "
                + "\n after a little while!";
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

        // Prompt
        Graphics.useGothic(sketch, promptFontSize, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.prompt, centerX, promptY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 2);

        homeButton.draw(sketch);
        backButton.draw(sketch);
    }
}
