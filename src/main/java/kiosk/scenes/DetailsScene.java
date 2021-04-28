package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.CreditsSceneModel;
import kiosk.models.DetailsSceneModel;
import processing.core.PConstants;


public class DetailsScene implements Scene {

    private final DetailsSceneModel model;
    private ButtonControl centerButton;
    private ButtonControl nextButton;
    private ButtonControl homeButton;
    private ButtonControl backButton;
    private ButtonControl supplementaryButton;

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
    private int startFrame = 0;
    private int sceneAnimationFrames = Kiosk.getSettings().sceneAnimationFrames;
    private boolean clickedBack = false;
    private boolean clickedHome = false;
    private boolean clickedNext = false;
    private boolean clickedMsoe = false;

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

        startFrame = sketch.frameCount;
        sceneAnimationFrames = Kiosk.getSettings().sceneAnimationFrames;
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (!sceneGraph.getRootSceneModel().getId().equals(this.model.getId())) {
            if (this.homeButton.wasClicked()) {
                clickedHome = true;
            } else if (this.backButton.wasClicked()) {
                clickedBack = true;
            }
        } else if (this.supplementaryButton.wasClicked()) {
            clickedMsoe = true;
        }

        if (this.centerButton.wasClicked()) {
            clickedNext = true;
        } else if (this.nextButton.wasClicked()) {
            clickedNext = true;
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.drawBubbleBackground(sketch);

        if (sketch.isEditor) {
            if (clickedNext) {
                sketch.getSceneGraph().pushScene(this.centerButton.getTarget());
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
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))), 0);
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                if (clickedNext) {
                    sketch.getSceneGraph().pushScene(this.centerButton.getTarget());
                } else if (clickedMsoe) {
                    sketch.getSceneGraph().pushScene(new CreditsSceneModel());
                }
            }
        } else if (clickedBack && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, (int) (0 - screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))), 0);
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedHome && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, 0, (int) (screenH
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))));
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().reset();
            }
        }
        if (sketch.getSceneGraph().recentActivity.contains("RESET")
                && sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch, 0, (int) (screenH + screenH
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))));
        } else if (sketch.getSceneGraph().recentActivity.contains("POP")
                && sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch,  (int) (0 - screenW - screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))), 0);
        } else if (sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch, (int) (screenW + screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))), 0);
        } else {
            drawThisFrame(sketch, 0, 0);
        }

        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton.draw(sketch);
            this.backButton.draw(sketch);
        }
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

        if (sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            supplementaryButton.draw(sketch, offsetX, offsetY);
        }
    }
}
