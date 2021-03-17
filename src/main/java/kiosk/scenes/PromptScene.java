package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.PromptSceneModel;
import processing.core.PConstants;


public class PromptScene implements Scene {

    // White foreground
    private static final int FOREGROUND_WIDTH = Kiosk.getSettings().screenW * 2 / 3;
    private static final int FOREGROUND_HEIGHT = Kiosk.getSettings().screenH * 3 / 4;
    private static final int FOREGROUND_X_PADDING = Kiosk.getSettings().screenW / 6 + FOREGROUND_WIDTH / 2;
    private static final int FOREGROUND_Y_PADDING = Kiosk.getSettings().screenH / 8 + FOREGROUND_HEIGHT / 2;
    private static final int FOREGROUND_CURVE_RADIUS = 100;

    // Text
    private static final int TITLE_Y = Kiosk.getSettings().screenH / 5;
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

    //Animations
    private int startFrame = 0;

    private final PromptSceneModel model;
    private final ButtonControl[] buttons;
    private ButtonControl homeButton;
    private ButtonControl backButton;
    private ButtonControl supplementaryButton;

    public PromptScene(PromptSceneModel model) {
        this.model = model;
        this.buttons = new ButtonControl[this.model.answers.length];
    }

    @Override
    public void init(Kiosk sketch) {
        startFrame = sketch.frameCount;
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

        if (!Kiosk.getSceneGraph().getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton = GraphicsUtil.initializeHomeButton();
            sketch.hookControl(this.homeButton);
            this.backButton = GraphicsUtil.initializeBackButton(sketch);
            sketch.hookControl(this.backButton);
        } else {
            this.supplementaryButton = GraphicsUtil.initializeMSOEButton(sketch);
            sketch.hookControl(this.supplementaryButton);
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.buttons) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget(), button.getModel().category);
            }
        }

        if (!Kiosk.getSceneGraph().getRootSceneModel().getId().equals(this.model.getId())) {
            if (this.homeButton.wasClicked()) {
                sceneGraph.reset();
            } else if (this.backButton.wasClicked()) {
                sceneGraph.popScene();
            }
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;

        // Draw bubble background
        Graphics.drawBubbleBackground(sketch);

        //TODO MAKE ANIMATION LESS CHOPPY WHEN LESS FRAMES DESIRED
        //If this scene is new, animate the items to gradually show up on screen
        if (sketch.frameCount - startFrame < Kiosk.getSettings().sceneAnimationFrames) {
            // Draw the white foreground box
            sketch.fill(255);
            Graphics.drawRoundedRectangle(sketch,
                    FOREGROUND_X_PADDING, FOREGROUND_Y_PADDING,
                    (float) (FOREGROUND_WIDTH * ((sketch.frameCount - startFrame) * 1.0
                            / Kiosk.getSettings().sceneAnimationFrames)),
                    (float) (FOREGROUND_HEIGHT * ((sketch.frameCount - startFrame) * 1.0
                            / Kiosk.getSettings().sceneAnimationFrames)),
                    (float) (FOREGROUND_CURVE_RADIUS * ((sketch.frameCount - startFrame) * 1.0
                            / Kiosk.getSettings().sceneAnimationFrames)));
            // Draw text
            sketch.rectMode(PConstants.CENTER);
            sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
            sketch.fill(0);
            if (sketch.frameCount - startFrame > (Kiosk.getSettings().sceneAnimationFrames / 2)) {
                // Title
                Graphics.useGothic(sketch, (int) (TITLE_FONT_SIZE * ((sketch.frameCount - startFrame) * 1.0
                        / (Kiosk.getSettings().sceneAnimationFrames + 1))), true);
                sketch.text(this.model.title, centerX, TITLE_Y,
                        sketch.width / 1.5f, sketch.height / 5f);
                // Prompt
                Graphics.useGothic(sketch, (int) (PROMPT_FONT_SIZE * ((sketch.frameCount - startFrame) * 1.0
                        / (Kiosk.getSettings().sceneAnimationFrames + 1))), false);
                sketch.text(this.model.prompt, centerX, PROMPT_Y,
                        sketch.width / 1.5f, sketch.height / 5f);
                // Action
                Graphics.useGothic(sketch, (int) (ACTION_FONT_SIZE * ((sketch.frameCount - startFrame) * 1.0
                        / (Kiosk.getSettings().sceneAnimationFrames + 1))), true);
                sketch.text(this.model.actionPhrase, centerX, ACTION_Y,
                        sketch.width / 1.5f, sketch.height / 6f);
            }
            if (sketch.frameCount - startFrame > (Kiosk.getSettings().sceneAnimationFrames / 2)) {
                for (ButtonControl button : this.buttons) {
                    button.draw(sketch, ((sketch.frameCount - startFrame) * 1.0
                            / (Kiosk.getSettings().sceneAnimationFrames + 1)));
                }
            }
        } else { //If it's already a second-or-two old, draw the scene normally
            // Draw the white foreground box
            sketch.fill(255);
            Graphics.drawRoundedRectangle(sketch,
                    FOREGROUND_X_PADDING, FOREGROUND_Y_PADDING,
                    FOREGROUND_WIDTH, FOREGROUND_HEIGHT,
                    FOREGROUND_CURVE_RADIUS);

            // Draw text
            sketch.rectMode(PConstants.CENTER);
            sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
            sketch.fill(0);

            // Title
            Graphics.useGothic(sketch, TITLE_FONT_SIZE, true);
            sketch.text(this.model.title, centerX, TITLE_Y,
                    sketch.width / 1.5f, sketch.height / 5f);

            // Prompt
            Graphics.useGothic(sketch, PROMPT_FONT_SIZE, false);
            sketch.text(this.model.prompt, centerX, PROMPT_Y,
                    sketch.width / 1.5f, sketch.height / 5f);

            // Action
            Graphics.useGothic(sketch, ACTION_FONT_SIZE, true);
            sketch.text(this.model.actionPhrase, centerX, ACTION_Y,
                    sketch.width / 1.5f, sketch.height / 6f);

            // Draw buttons
            for (ButtonControl button : this.buttons) {
                button.draw(sketch);
            }
        }

        if (!Kiosk.getSceneGraph().getRootSceneModel().getId().equals(this.model.getId())) {
            homeButton.draw(sketch);
            backButton.draw(sketch);
        }
    }
}
