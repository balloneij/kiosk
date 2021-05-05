package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SceneAnimationHelper;
import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
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
    private int sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
    private SceneAnimationHelper.Clicked clicked;

    private String sceneToGoTo;
    private Riasec riasecToGoTo;
    private FilterGroupModel filterToGoTo;
    private float totalTimeOpening = 0;
    private float totalTimeEnding = 0;
    private float dt = 0;

    private final PromptSceneModel model;
    private final ButtonControl[] buttons;
    private ButtonControl homeButton;
    private ButtonControl backButton;
    private ButtonControl supplementaryButton;
    private boolean isRoot = false;

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

            clicked = SceneAnimationHelper.Clicked.NONE;
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

        sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
        totalTimeOpening = 0;
        totalTimeEnding = 0;

        this.isRoot = sketch.getRootSceneModel().getId().equals(this.model.getId());
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        this.dt = dt;

        for (ButtonControl button : this.buttons) {
            if (button.wasClicked()) {
                clicked = SceneAnimationHelper.Clicked.NEXT;
                sceneToGoTo = button.getTarget();
                riasecToGoTo = button.getModel().category;
                filterToGoTo = button.getModel().filter;
                break;
            }
        }

        if (!isRoot) {
            if (this.homeButton.wasClicked()) {
                clicked = SceneAnimationHelper.Clicked.HOME;
            } else if (this.backButton.wasClicked()) {
                clicked = SceneAnimationHelper.Clicked.BACK;
            }
        } else if (this.supplementaryButton.wasClicked()) {
            clicked = SceneAnimationHelper.Clicked.MSOE;
        }
    }

    @Override
    public void draw(Kiosk sketch) {

        if ((totalTimeOpening < sceneAnimationMilliseconds)
                && sceneAnimationMilliseconds != 0) {
            totalTimeOpening += dt * 1000;
        }
        if (!clicked.equals(SceneAnimationHelper.Clicked.NONE)
                && sceneAnimationMilliseconds != 0) {
            totalTimeEnding += dt * 1000;
        }

        int[] returnVals = SceneAnimationHelper.sceneAnimationLogic(sketch,
                clicked,
                sceneToGoTo, riasecToGoTo, filterToGoTo,
                totalTimeOpening, totalTimeEnding, sceneAnimationMilliseconds,
                screenW, screenH);
        drawThisFrame(sketch, returnVals[0], returnVals[1]);

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

        if (isRoot) {
            supplementaryButton.draw(sketch, offsetX, offsetY);
        } else {
            if (((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity
                    .equals(SceneGraph.RecentActivity.PUSH))
                    && !clicked.equals(SceneAnimationHelper.Clicked.NEXT)
                    || ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity
                    .equals(SceneGraph.RecentActivity.POP))
                    && clicked.equals(SceneAnimationHelper.Clicked.BACK)
                    || clicked.equals(SceneAnimationHelper.Clicked.HOME)))) {
                homeButton.draw(sketch, offsetX, offsetY);
                backButton.draw(sketch, offsetX, offsetY);
            } else if (clicked.equals(SceneAnimationHelper.Clicked.MSOE)) {
                homeButton.draw(sketch, offsetX, offsetY);
                backButton.draw(sketch);
            } else {
                homeButton.draw(sketch);
                backButton.draw(sketch);
            }
        }
    }
}
