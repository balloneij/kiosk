package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SceneAnimationHelper;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.DetailsSceneModel;
import processing.core.PConstants;


public class DetailsScene implements Scene {

    private final DetailsSceneModel model;
    private ButtonControl centerButton;
    private ButtonControl nextButton;
    private ButtonControl homeButton;
    private ButtonControl backButton;
    private ButtonControl supplementaryButton;
    private boolean isRoot;

    private static int screenW =  Kiosk.getSettings().screenW;
    private static int screenH = Kiosk.getSettings().screenH;

    // Buttons
    private static int buttonWidth = Kiosk.getSettings().screenW / 8;
    private static int buttonHeight = Kiosk.getSettings().screenH / 6;
    private static int buttonPadding = 20;

    // White foreground
    private static int foregroundWidth = Kiosk.getSettings().screenW * 2 / 3;
    private static int foregroundHeight = Kiosk.getSettings().screenH * 3 / 4;
    private static int foregroundXPadding = Kiosk.getSettings().screenW / 6;
    private static int foregroundYPadding = Kiosk.getSettings().screenH / 8;
    private static int foregroundCurveRadius = 100;

    // Text
    private static int titleY = Kiosk.getSettings().screenH / 4;
    private static int bodyY = Kiosk.getSettings().screenH * 3 / 8;
    private static int titleFontSize = 24;
    private static int bodyFontSize = 16;

    // Button Image Props
    private static int buttonRadius = Kiosk.getSettings().screenW / 8;
    private static int buttonImageWidth = buttonRadius * 4 / 5;
    private static int buttonImageHeight = buttonRadius * 4 / 5;

    //Animations
    private int sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
    private SceneAnimationHelper.Clicked clicked;
    private float totalTimeOpening = 0;
    private float totalTimeEnding = 0;
    private float dt = 0;

    /**
     * Detials Scene show a title, body of text, and a button at the bottom.
     * @param model The model object where we get our information.
     */
    public DetailsScene(DetailsSceneModel model) {
        this.model = model;

        screenW =  Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;
        // Buttons
        buttonWidth = Kiosk.getSettings().screenW / 8;
        buttonHeight = Kiosk.getSettings().screenH / 6;
        buttonPadding = 20;

        // White foreground
        foregroundWidth = Kiosk.getSettings().screenW * 2 / 3;
        foregroundHeight = Kiosk.getSettings().screenH * 3 / 4;
        foregroundXPadding = Kiosk.getSettings().screenW / 6;
        foregroundYPadding = Kiosk.getSettings().screenH / 8;
        foregroundCurveRadius = 100;

        // Text
        titleY = Kiosk.getSettings().screenH / 4;
        bodyY = Kiosk.getSettings().screenH * 3 / 8;
        titleFontSize = 24;
        bodyFontSize = 16;

        // Button Image Props
        buttonRadius = Kiosk.getSettings().screenW / 8;
        buttonImageWidth = buttonRadius * 4 / 5;
        buttonImageHeight = buttonRadius * 4 / 5;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchHeight = Kiosk.getSettings().screenH;
        final int sketchWidth = Kiosk.getSettings().screenW;

        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton = GraphicsUtil.initializeHomeButton(sketch);
            sketch.hookControl(this.homeButton);
            this.backButton = GraphicsUtil.initializeBackButton(sketch);
            sketch.hookControl(this.backButton);
        } else {
            this.supplementaryButton = GraphicsUtil.initializeMsoeButton(
                    sketch, 0, 0 - (3 * screenH / 4f));
            sketch.hookControl(this.supplementaryButton);
        }

        if (this.model.button.image != null) {
            this.model.button.image.width = buttonImageWidth;
            this.model.button.image.height = buttonImageHeight;
        }

        this.centerButton = new ButtonControl(
            this.model.button,
            (sketchWidth / 2) - (buttonWidth * 5 / 8),
            foregroundYPadding + foregroundHeight
                - (buttonWidth * 5 / 4 + buttonPadding),
                buttonWidth * 5 / 4,
                buttonWidth * 5 / 4
        );
        this.centerButton.init(sketch);
        sketch.hookControl(this.centerButton);

        this.nextButton = GraphicsUtil.initializeNextButton(sketch);
        sketch.hookControl(this.nextButton);

        sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
        totalTimeOpening = 0;
        totalTimeEnding = 0;

        this.isRoot = sketch.getRootSceneModel().getId().equals(this.model.getId());

        clicked = SceneAnimationHelper.Clicked.NONE;
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        this.dt = dt;

        if (!this.isRoot) {
            if (this.homeButton.wasClicked()) {
                clicked = SceneAnimationHelper.Clicked.HOME;
            } else if (this.backButton.wasClicked()) {
                clicked = SceneAnimationHelper.Clicked.BACK;
            }
        } else if (this.supplementaryButton.wasClicked()) {
            clicked = SceneAnimationHelper.Clicked.MSOE;
        }

        if (this.centerButton.wasClicked()) {
            clicked = SceneAnimationHelper.Clicked.NEXT;
        } else if (this.nextButton.wasClicked()) {
            clicked = SceneAnimationHelper.Clicked.NEXT;
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        if ((totalTimeOpening < sceneAnimationMilliseconds) && sceneAnimationMilliseconds != 0) {
            totalTimeOpening += dt * 1000;
        }
        if (!clicked.equals(SceneAnimationHelper.Clicked.NONE)
                && sceneAnimationMilliseconds != 0) {
            totalTimeEnding += dt * 1000;
        }

        int[] returnVals = SceneAnimationHelper.sceneAnimationLogic(sketch,
                clicked,
                null, null, null,
                totalTimeOpening, totalTimeEnding, sceneAnimationMilliseconds,
                screenW, screenH);
        drawThisFrame(sketch, returnVals[0], returnVals[1]);
    }

    private void drawThisFrame(Kiosk sketch, int offsetX, int offsetY) {
        final int centerX = Kiosk.getSettings().screenW / 2;
        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
                foregroundXPadding + offsetX + foregroundWidth / 2.f,
                foregroundYPadding + offsetY + foregroundHeight / 2.f,
                foregroundWidth, foregroundHeight,
                foregroundCurveRadius);

        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);

        // Title
        Graphics.useGothic(sketch, titleFontSize, true);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(33);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.title, centerX + offsetX, (int) (titleY * 1.15) + offsetY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 5);

        // Body
        Graphics.useGothic(sketch, bodyFontSize, false);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(25);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.body, centerX + offsetX, (int) (bodyY * 1.15) + offsetY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 5);

        this.centerButton.draw(sketch, offsetX, offsetY);
        this.nextButton.draw(sketch, offsetX, offsetY);

        if (isRoot) {
            supplementaryButton.draw(sketch, offsetX, offsetY);
        } else {
            if ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.PUSH))
                    || ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.POP))
                    && clicked.equals(SceneAnimationHelper.Clicked.BACK))
                    || clicked.equals(SceneAnimationHelper.Clicked.HOME)) {
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
