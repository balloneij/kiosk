package kiosk.scenes;

import graphics.Color;
import graphics.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.PromptSceneModel;
import processing.core.PConstants;


public class PromptScene implements Scene {

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

    private final PromptSceneModel model;
    private final ButtonControl[] buttons;
    private ButtonControl homeButton;
    private ButtonControl backButton;

    public PromptScene(PromptSceneModel model) {
        this.model = model;
        this.buttons = new ButtonControl[this.model.answers.length];
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchHeight = Kiosk.getSettings().screenH;

        // Start the X on the far left so we simply need to add
        // button width and padding to get the next X
        int x = Kiosk.getSettings().screenW / 2
                - (BUTTON_WIDTH * this.buttons.length
                + BUTTON_PADDING * (this.buttons.length - 1)) / 2;
        for (int i = 0; i < this.model.answers.length; i++) {
            ButtonModel model = this.model.answers[i];

            int width;
            int height;

            if (model.isCircle) {
                width = BUTTON_RADIUS;
                height = BUTTON_RADIUS;
            } else {
                width = BUTTON_WIDTH;
                height = BUTTON_HEIGHT;
            }

            // Modify the image so it fits inside the button
            if (model.image != null) {
                model.image.width = BUTTON_IMAGE_WIDTH;
                model.image.height = BUTTON_IMAGE_HEIGHT;
            }

            var button = new ButtonControl(model, x, BUTTON_Y, width, height);
            button.init(sketch);

            sketch.hookControl(button);
            this.buttons[i] = button;

            x += BUTTON_WIDTH + BUTTON_PADDING;
        }

        var homeButtonModel = new ButtonModel();
        homeButtonModel.text = "Home";
        homeButtonModel.rgb = Color.DW_BLACK_RGB;
        this.homeButton = new ButtonControl(homeButtonModel,
                BUTTON_PADDING, BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, BUTTON_HEIGHT * 3 / 4);
        sketch.hookControl(this.homeButton);
        var backButtonModel = new ButtonModel();
        backButtonModel.text = "Back";
        backButtonModel.rgb = Color.DW_BLACK_RGB;
        this.backButton = new ButtonControl(backButtonModel,
                BUTTON_PADDING, sketchHeight - (BUTTON_HEIGHT * 3 / 4) - BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, BUTTON_HEIGHT * 3 / 4);
        sketch.hookControl(this.backButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.buttons) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget(), button.getModel().category);
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

        // Action
        Graphics.useSansSerifBold(sketch, ACTION_FONT_SIZE);
        sketch.text(this.model.actionPhrase, centerX, ACTION_Y);

        // Draw buttons
        for (ButtonControl button : this.buttons) {
            button.draw(sketch);
        }

        homeButton.draw(sketch);
        backButton.draw(sketch);
    }
}
