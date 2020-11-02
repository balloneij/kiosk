package kiosk.scenes;

import kiosk.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.PromptSceneModel;
import processing.core.PConstants;


public class PromptScene implements Scene {

    // White foreground
    private static final int FOREGROUND_WIDTH = Kiosk.WIDTH * 2 / 3;
    private static final int FOREGROUND_HEIGHT = Kiosk.HEIGHT * 3 / 4;
    private static final int FOREGROUND_X_PADDING = Kiosk.WIDTH / 6;
    private static final int FOREGROUND_Y_PADDING = Kiosk.HEIGHT / 8;
    private static final int FOREGROUND_CURVE_RADIUS = 50;

    // Text
    private static final int TITLE_Y = Kiosk.HEIGHT / 4;
    private static final int TITLE_FONT_SIZE = 24;
    private static final int PROMPT_Y = Kiosk.HEIGHT * 3 / 8;
    private static final int PROMPT_FONT_SIZE = 16;
    private static final int ACTION_Y = Kiosk.HEIGHT / 2;
    private static final int ACTION_FONT_SIZE = 20;

    // Buttons
    private static final int BUTTON_WIDTH = Kiosk.WIDTH / 8;
    private static final int BUTTON_HEIGHT = Kiosk.HEIGHT / 6;
    private static final int BUTTON_RADIUS = Kiosk.WIDTH / 8;
    private static final int BUTTON_IMAGE_WIDTH = BUTTON_RADIUS * 4 / 5;
    private static final int BUTTON_IMAGE_HEIGHT = BUTTON_RADIUS * 4 / 5;
    private static final int BUTTON_X_PADDING = 20;
    private static final int BUTTON_Y = Kiosk.HEIGHT * 7 / 12;

    private final PromptSceneModel model;
    private final ButtonControl[] buttons;

    public PromptScene(PromptSceneModel model) {
        this.model = model;
        this.buttons = new ButtonControl[this.model.answers.length];
    }

    @Override
    public void init(Kiosk sketch) {
        // Start the X on the far left so we simply need to add
        // button width and padding to get the next X
        int x = Kiosk.WIDTH / 2
                - (BUTTON_WIDTH * this.buttons.length
                + BUTTON_X_PADDING * (this.buttons.length - 1)) / 2;
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

            x += BUTTON_WIDTH + BUTTON_X_PADDING;
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.buttons) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget());
            }
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.WIDTH / 2;

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
        Graphics.useSerif(sketch, TITLE_FONT_SIZE);
        sketch.text(this.model.title, centerX, TITLE_Y);

        // Prompt
        Graphics.useSansSerif(sketch, PROMPT_FONT_SIZE, false);
        sketch.text(this.model.prompt, centerX, PROMPT_Y);

        // Action
        Graphics.useSansSerif(sketch, ACTION_FONT_SIZE, true);
        sketch.text(this.model.actionPhrase, centerX, ACTION_Y);

        // Draw buttons
        for (ButtonControl button : this.buttons) {
            button.draw(sketch);
        }
    }
}
