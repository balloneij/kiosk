package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.CreditsSceneModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.PromptSceneModel;
import processing.core.PConstants;


public class PromptScene implements Scene {

    private static int screenW =  Kiosk.getSettings().screenW;
    private static int screenH = Kiosk.getSettings().screenH;

    // White foreground
    private static int foregroundWidth = screenW * 2 / 3;
    private static int foregroundHeight = screenH * 3 / 4;
    private static int foregroundXPadding
            = screenW / 6 + foregroundWidth / 2;
    private static int foregroundYPadding
            = screenH / 8 + foregroundHeight / 2;
    private static int foregroundCurveRadius = 100;

    // Text
    private static int titleY = screenH / 5;
    private static int titleFontSize = screenW / 55;
    private static int promptY = screenH * 3 / 8;
    private static int promptFontSize = screenW / 60;
    private static int actionY = screenH / 2;
    private static int actionFontSize = screenW / 58;

    // Buttons
    private static int buttonWidth = screenW / 8;
    private static int buttonHeight = screenH / 6;
    private static int buttonRadius = screenW / 8;
    private static int buttonImageWidth = buttonRadius * 4 / 5;
    private static int buttonImageHeight = buttonRadius * 4 / 5;
    private static int buttonPadding = 20;
    private static int buttonY = screenH * 7 / 12;

    //Animations
    private int startFrame = 0;
    private int sceneAnimationFrames = Kiosk.getSettings().sceneAnimationFrames;
    private boolean clickedBack = false;
    private boolean clickedHome = false;
    private boolean clickedNext = false;
    private boolean clickedMsoe = false;
    private String sceneToGoTo;
    private Riasec riasecToGoTo;
    private FilterGroupModel filterToGoTo;

    private final PromptSceneModel model;
    private final ButtonControl[] buttons;
    private ButtonControl homeButton;
    private ButtonControl backButton;
    private ButtonControl supplementaryButton;

    /**
     * Default constructor.
     * @param model the model to make
     */
    public PromptScene(PromptSceneModel model) {
        this.model = model;
        this.buttons = new ButtonControl[this.model.answers.length];
        screenW =  Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        // White foreground
        foregroundWidth = screenW * 2 / 3;
        foregroundHeight = screenH * 3 / 4;
        foregroundXPadding
                = screenW / 6 + foregroundWidth / 2;
        foregroundYPadding
                = screenH / 8 + foregroundHeight / 2;
        foregroundCurveRadius = 100;

        // Text
        titleY = screenH / 5;
        titleFontSize = screenW / 55;
        promptY = screenH * 3 / 8;
        promptFontSize = screenW / 60;
        actionY = screenH / 2;
        actionFontSize = screenW / 58;

        // Buttons
        buttonWidth = screenW / 8;
        buttonHeight = screenH / 6;
        buttonRadius = screenW / 8;
        buttonImageWidth = buttonRadius * 4 / 5;
        buttonImageHeight = buttonRadius * 4 / 5;
        buttonPadding = 20;
        buttonY = screenH * 7 / 12;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchHeight = Kiosk.getSettings().screenH;

        // Start the X on the far left so we simply need to add
        // button width and padding to get the next X
        int x = Kiosk.getSettings().screenW / 2
                - (buttonWidth * this.buttons.length
                + buttonPadding * (this.buttons.length - 1)) / 2;
        for (int i = 0; i < this.model.answers.length; i++) {
            ButtonModel model = this.model.answers[i];

            int width;
            int height;

            if (model.isCircle) {
                width = buttonRadius;
                height = buttonRadius;
            } else {
                width = buttonWidth;
                height = buttonHeight;
            }

            // Modify the image so it fits inside the button
            if (model.image != null) {
                model.image.width = buttonImageWidth;
                model.image.height = buttonImageHeight;
            }

            ButtonControl button = new ButtonControl(model, x, buttonY, width, height);
            button.init(sketch);

            sketch.hookControl(button);
            this.buttons[i] = button;

            x += buttonWidth + buttonPadding;
        }

        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton = GraphicsUtil.initializeHomeButton(sketch);
            sketch.hookControl(this.homeButton);
            this.backButton = GraphicsUtil.initializeBackButton(sketch);
            sketch.hookControl(this.backButton);
        } else {
            this.supplementaryButton = GraphicsUtil.initializeMsoeButton(sketch);
            sketch.hookControl(this.supplementaryButton);
        }

        startFrame = sketch.frameCount;
        sceneAnimationFrames = Kiosk.getSettings().sceneAnimationFrames;
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.buttons) {
            if (button.wasClicked()) {
                clickedNext = true;
                sceneToGoTo = button.getTarget();
                riasecToGoTo = button.getModel().category;
                filterToGoTo = button.getModel().filter;
                break;
            }
        }

        if (!sceneGraph.getRootSceneModel().getId().equals(this.model.getId())) {
            if (this.homeButton.wasClicked()) {
                clickedHome = true;
            } else if (this.backButton.wasClicked()) {
                clickedBack = true;
            }
        } else if (this.supplementaryButton.wasClicked()) {
            clickedMsoe = true;
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;

        // Draw bubble background
        Graphics.drawBubbleBackground(sketch);

        if (sketch.isEditor) {
            if (clickedNext) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            } else if (clickedBack) {
                sketch.getSceneGraph().popScene();
            } else if (clickedHome) {
                sketch.getSceneGraph().reset();
            } else if (clickedMsoe) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        }

        if ((clickedNext || clickedMsoe) && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, (int) (screenW
                    * (1 - ((sketch.frameCount - startFrame) * 1.0
                    / sceneAnimationFrames + 1))), 0);
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                if (clickedNext) {
                    sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
                } else if (clickedMsoe) {
                    sketch.getSceneGraph().pushScene(new CreditsSceneModel());
                }
            }
        } else if (clickedBack && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, (int) (0 - screenW
                    * (1 - ((sketch.frameCount - startFrame) * 1.0
                    / sceneAnimationFrames + 1))), 0);
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedHome && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, 0, (int) (screenH
                    * (1 - ((sketch.frameCount - startFrame) * 1.0
                    / sceneAnimationFrames + 1))));
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.contains("RESET")
                && sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch, 0, (int) (screenH + screenH
                    * (1 - ((sketch.frameCount - startFrame) * 1.0
                    / sceneAnimationFrames + 1))));
        } else if (sketch.getSceneGraph().recentActivity.contains("POP")
                && sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch, (int) (0 - screenW - screenW
                    * (1 - ((sketch.frameCount - startFrame) * 1.0
                    / sceneAnimationFrames + 1))), 0);
        } else if (sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch, (int) ((screenW + screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1)))), 0);
        } else { //If it's already a second-or-two old, draw the scene normally
            drawThisFrame(sketch, 0, 0);
        }

        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            homeButton.draw(sketch);
            backButton.draw(sketch);
        }
    }

    private void drawThisFrame(Kiosk sketch, float offsetX, float offsetY) {
        final int centerX = Kiosk.getSettings().screenW / 2;
        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
                foregroundXPadding + offsetX, foregroundYPadding + offsetY,
                foregroundWidth, foregroundHeight,
                foregroundCurveRadius);

        // Draw text
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);

        // Title
        Graphics.useGothic(sketch, titleFontSize, true);
        sketch.text(this.model.title, centerX + offsetX, titleY + offsetY,
                sketch.width / 1.5f, sketch.height / 5f);

        // Prompt
        Graphics.useGothic(sketch, promptFontSize, false);
        sketch.text(this.model.prompt, centerX + offsetX, promptY + offsetY,
                sketch.width / 1.5f, sketch.height / 5f);

        // Action
        Graphics.useGothic(sketch, actionFontSize, true);
        sketch.text(this.model.actionPhrase, centerX + offsetX, actionY + offsetY,
                sketch.width / 1.5f, sketch.height / 6f);

        // Draw buttons
        for (ButtonControl button : this.buttons) {
            button.draw(sketch, offsetX, offsetY);
        }

        if (sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            supplementaryButton.draw(sketch, offsetX, offsetY);
        }
    }
}
