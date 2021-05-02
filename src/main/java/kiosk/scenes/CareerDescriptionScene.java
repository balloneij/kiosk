package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.CareerDescriptionModel;
import kiosk.models.CreditsSceneModel;
import processing.core.PConstants;


public class CareerDescriptionScene implements Scene {

    private final CareerDescriptionModel model;
    private ButtonControl centerButton;
    private ButtonControl homeButton;
    private ButtonControl backButton;
    private ButtonControl supplementaryButton;

    private static int screenW = Kiosk.getSettings().screenW;
    private static int screenH = Kiosk.getSettings().screenH;

    // Button
    private static int buttonWidth = screenW / 8;
    private static int buttonHeight = screenH / 8;
    private static int buttonPadding = 20;

    // White foreground
    private static int foregroundWidth = screenW * 2 / 3;
    private static int foregroundHeight = screenH * 3 / 4;
    private static int foregroundXPadding
            = screenW / 6 + foregroundWidth / 2;
    private static int foregroundYPadding
            = screenH / 8 + foregroundHeight / 2;
    private static int foregroundCurveRadius = 100;

    // Text
    private static int titleY = screenH / 4;
    private static int instructionsY = screenH * 3 / 8;
    private static int descriptionY = screenH * 4 / 8;
    private static int titleFontSize = screenW / 55;
    private static int instructionsFontSize = screenW / 60;
    private static int descriptionFontSize = screenW / 58;

    //Animations
    private int sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
    private boolean clickedBack = false;
    private boolean clickedHome = false;
    private boolean clickedMsoe = false;
    private float totalTimeOpening = 0;
    private float totalTimeEnding = 0;
    private float dt = 0;

    /**
     * Career Description Scene shows a title, body of text, and a button at the bottom.
     * @param model The model object where we get our information.
     */
    public CareerDescriptionScene(CareerDescriptionModel model) {
        this.model = model;
        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        // Button
        buttonWidth = screenW / 8;
        buttonHeight = screenH / 8;
        buttonPadding = 20;

        // White foreground
        foregroundWidth = screenW * 2 / 3;
        foregroundHeight = screenH * 3 / 4;
        foregroundXPadding
                = screenW / 6 + foregroundWidth / 2;
        foregroundYPadding
                = screenH / 8 + foregroundHeight / 2;
        foregroundCurveRadius = 100;

        // Text
        titleY = screenH / 4;
        instructionsY = screenH * 3 / 8;
        descriptionY = screenH * 4 / 8;
        titleFontSize = screenW / 55;
        instructionsFontSize = screenW / 60;
        descriptionFontSize = screenW / 58;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchHeight = Kiosk.getSettings().screenH;
        final int sketchWidth = Kiosk.getSettings().screenW;

        this.homeButton = GraphicsUtil.initializeHomeButton(sketch);
        sketch.hookControl(this.homeButton);
        this.backButton = GraphicsUtil.initializeBackButton(sketch);
        sketch.hookControl(this.backButton);

        if (this.model.button.image != null) {
            this.model.button.image.width = buttonWidth;
            this.model.button.image.height = buttonHeight;
        }

        this.centerButton = new ButtonControl(
            this.model.button,
            (sketchWidth / 2) - (buttonWidth / 2),
            sketchHeight * 2 / 3,
                buttonWidth,
                buttonHeight
        );
        this.centerButton.init(sketch);
        sketch.hookControl(this.centerButton);

        sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
        totalTimeOpening = 0;
        totalTimeEnding = 0;

        this.supplementaryButton = GraphicsUtil.initializeMsoeButton(sketch);
        this.supplementaryButton.init(sketch);
        sketch.hookControl(this.supplementaryButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        this.dt = dt;

        if (this.homeButton.wasClicked()) {
            clickedHome = true;
        } else if (this.backButton.wasClicked()) {
            clickedBack = true;
        } else if (this.centerButton.wasClicked()) {
            clickedHome = true;
        } else if (this.supplementaryButton.wasClicked()) {
            clickedMsoe = true;
            System.out.println("MSOE CLICKED");
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        if (sketch.isEditor) {
            if (clickedBack) {
                sketch.getSceneGraph().popScene();
            } else if (clickedHome) {
                sketch.getSceneGraph().reset();
            } else if (clickedMsoe) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        }

        if (totalTimeOpening < sceneAnimationMilliseconds) {
            totalTimeOpening += dt * 1000;
        }
        if (clickedBack || clickedHome || clickedMsoe) {
            totalTimeEnding += dt * 1000;
        }

        if (clickedMsoe && !sketch.isEditor) {
            System.out.println("HERE STEP 2");
            drawThisFrame(sketch, (int) (screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1))), 0);
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                System.out.println("FINALLY STEP 3");
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        } else if (clickedBack && !sketch.isEditor) {
            drawThisFrame(sketch, (int) (0 - screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1))), 0);
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedHome && !sketch.isEditor) {
            drawThisFrame(sketch, 0, (int) (screenH
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1))));
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.contains("RESET")
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            drawThisFrame(sketch, 0, (int) (screenH + screenH
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1))));
        } else if (sketch.getSceneGraph().recentActivity.contains("POP")
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            drawThisFrame(sketch, (int) (0 - screenW - screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1))), 0);
        } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            drawThisFrame(sketch, (int) (screenW + screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1))), 0);
        } else {
            drawThisFrame(sketch, 0, 0);
        }

        this.homeButton.draw(sketch);
        this.backButton.draw(sketch);
    }

    private void drawThisFrame(Kiosk sketch, int offsetX, int offsetY) {
        final int centerX = Kiosk.getSettings().screenW / 2;
        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
                foregroundXPadding + offsetX, foregroundYPadding + offsetY,
                foregroundWidth, foregroundHeight,
                foregroundCurveRadius);

        // Text Properties
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);


        // Career Name
        Graphics.useGothic(sketch, titleFontSize, true);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.careerModel.name, centerX + offsetX, (int) (titleY * 1.15) + offsetY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 5);


        // What to do next
        Graphics.useGothic(sketch, instructionsFontSize, false);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.body, centerX + offsetX, (int) (instructionsY * 1.15) + offsetY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 5);

        // Career Description
        Graphics.useGothic(sketch, descriptionFontSize, false);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.careerModel.description, centerX + offsetX,
                (int) (descriptionY * 1.15) + offsetY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 5);

        this.centerButton.draw(sketch, offsetX, offsetY);

        this.supplementaryButton.draw(sketch, offsetX, offsetY);
    }
}
