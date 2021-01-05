package kiosk.scenes;

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
    private static final int FOREGROUND_CURVE_RADIUS = 50;

    // Text
    private static final int TITLE_Y = Kiosk.getSettings().screenH / 4;
    private static final int TITLE_FONT_SIZE = 24;
    private static final int PROMPT_Y = Kiosk.getSettings().screenH * 3 / 8;
    private static final int PROMPT_FONT_SIZE = 16;
    private static final int ACTION_Y = Kiosk.getSettings().screenH / 2;
    private static final int ACTION_FONT_SIZE = 20;

    // Buttons
    private static final int BUTTON_WIDTH = Kiosk.getSettings().screenW / 8;
    private static final int BUTTON_HEIGHT = Kiosk.getSettings().screenH / 6;
    private static final int BUTTON_RADIUS = Kiosk.getSettings().screenW / 8;
    private static final int BUTTON_IMAGE_WIDTH = BUTTON_RADIUS * 4 / 5;
    private static final int BUTTON_IMAGE_HEIGHT = BUTTON_RADIUS * 4 / 5;
    private static final int BUTTON_PADDING = 20;
    private static final int BUTTON_Y = Kiosk.getSettings().screenH * 7 / 12;

    private final TimeoutSceneModel model;
    private ButtonControl homeButton;
    private ButtonControl backButton;

    public TimeoutScene(TimeoutSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchWidth = Kiosk.getSettings().screenW;
        final int sketchHeight = Kiosk.getSettings().screenH;

        var homeButtonModel = new ButtonModel();
        homeButtonModel.text = "Take me back to the beginning";
        this.homeButton = new ButtonControl(homeButtonModel,
                BUTTON_PADDING * 2, sketchHeight - BUTTON_PADDING * 5,
                BUTTON_WIDTH * 3, BUTTON_HEIGHT * 3 / 4);
        sketch.hookControl(this.homeButton);
        var backButtonModel = new ButtonModel();
        backButtonModel.text = "I'm still here!";
        this.backButton = new ButtonControl(backButtonModel,
                sketchWidth - BUTTON_HEIGHT - BUTTON_PADDING - BUTTON_WIDTH * 2,
                sketchHeight - BUTTON_PADDING * 5,
                BUTTON_WIDTH * 2, BUTTON_HEIGHT * 3 / 4);
        sketch.hookControl(this.backButton);

        this.model.title = "Are You Still There?";
        this.model.prompt = "\n\n\n If you no longer wish to complete the survey, click "
                + "\n \"Take me back to the beginning!\" "
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
                FOREGROUND_X_PADDING, FOREGROUND_Y_PADDING,
                FOREGROUND_WIDTH, FOREGROUND_HEIGHT,
                FOREGROUND_CURVE_RADIUS);

        // Draw text
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);

        // Title
        Graphics.useSansSerifBold(sketch, TITLE_FONT_SIZE);
        sketch.text(this.model.title, centerX, TITLE_Y);

        // Prompt
        Graphics.useSansSerif(sketch, PROMPT_FONT_SIZE);
        sketch.text(this.model.prompt, centerX, PROMPT_Y);

        homeButton.draw(sketch);
        backButton.draw(sketch);
    }
}