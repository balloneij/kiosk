package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.CreditsSceneModel;
import processing.core.PConstants;

public class CreditsScene implements Scene {

    private static int screenW =  Kiosk.getSettings().screenW;
    private static int screenH = Kiosk.getSettings().screenH;

    // White foreground
    private static int foregroundWidth = Kiosk.getSettings().screenW * 2 / 3;
    private static int foregroundHeight = Kiosk.getSettings().screenH * 3 / 4;
    private static int foregroundXPadding
            = Kiosk.getSettings().screenW / 6 + foregroundWidth / 2;
    private static int foregroundYPadding
            = Kiosk.getSettings().screenH / 8 + foregroundHeight / 2;
    private static int foregroundCurveRadius = 100;

    // Text
    private static int titleY = Kiosk.getSettings().screenH / 4;
    private static int titleFontSize = screenW / 55;
    private static int creatorX = Kiosk.getSettings().screenW / 3;
    private static int creatorY = Kiosk.getSettings().screenH * 4 / 9;
    private static int promptFontSize = screenW / 60;
    private static int supporterX = Kiosk.getSettings().screenW * 2 / 3;
    private static int supporterY = Kiosk.getSettings().screenH * 4 / 9;
    private static int actionFontSize = screenW / 58;

    //Animations
    private int startFrame = 0;
    private int sceneAnimationFrames = Kiosk.getSettings().sceneAnimationFrames;
    private boolean clickedBack = false;

    private final CreditsSceneModel model;
    private ButtonControl backButton;

    /**
     * Default constructor.
     * @param model the model to make
     */
    public CreditsScene(CreditsSceneModel model) {
        this.model = model;

        // White foreground
        foregroundWidth = Kiosk.getSettings().screenW * 2 / 3;
        foregroundHeight = Kiosk.getSettings().screenH * 3 / 4;
        foregroundXPadding
                = Kiosk.getSettings().screenW / 6 + foregroundWidth / 2;
        foregroundYPadding
                = Kiosk.getSettings().screenH / 8 + foregroundHeight / 2;
        foregroundCurveRadius = 100;

        // Text
        titleY = Kiosk.getSettings().screenH / 4;
        titleFontSize = Kiosk.getSettings().screenW / 55;
        creatorX = Kiosk.getSettings().screenW / 3;
        creatorY = Kiosk.getSettings().screenH * 4 / 9;
        promptFontSize = Kiosk.getSettings().screenW / 60;
        supporterX = Kiosk.getSettings().screenW * 2 / 3;
        supporterY = Kiosk.getSettings().screenH * 4 / 9;
        actionFontSize = Kiosk.getSettings().screenW / 58;
    }

    @Override
    public void init(Kiosk sketch) {
        this.model.title = "This kiosk was made thanks to the\n"
                + "work of these wonderful people!";
        this.model.creatorTitle = "MSOE Development Team";
        this.model.creators = "Isaac Ballone\nSeth Fenske\n"
                + "Rob Retzlaff\nLucas Stenzel\nJoshua Vogt";
        this.model.supporterTitle = "Additional Support";
        this.model.supporters = "The Lighthouse Kids\nJohn Emmerich\n"
                + "Jodi Schomaker\nDavid Mancl\nRyan Kresse\n"
                + "Dr. Taylor\nOur friends & families";

        startFrame = sketch.frameCount;
        sceneAnimationFrames = Kiosk.getSettings().sceneAnimationFrames;

        this.backButton = GraphicsUtil.initializeBackButton(sketch);
        this.backButton.init(sketch);
        sketch.hookControl(this.backButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.backButton.wasClicked()) {
            clickedBack = true;
        }
    }

    @Override
    public void draw(Kiosk sketch) {

        // Draw bubble background
        Graphics.drawBubbleBackground(sketch);

        if (sketch.isEditor) {
            if (clickedBack) {
                sketch.getSceneGraph().popScene();
            }
        }

        if (clickedBack && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, (int) (0 - screenW
                    * (1 - ((sketch.frameCount - startFrame) * 1.0
                    / sceneAnimationFrames + 1))), 0);
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().popScene();
            }
        } else if (sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch, (int) (screenW + screenW
                    * (1 - ((sketch.frameCount - startFrame) * 1.0
                    / sceneAnimationFrames + 1))), 0);
        } else { //If it's already a second-or-two old, draw the scene normally
            drawThisFrame(sketch, 0, 0);
        }
    }

    private void drawThisFrame(Kiosk sketch, int offsetX, int offsetY) {
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
        Graphics.useGothic(sketch, (int) (titleFontSize * 1.15f), true);
        sketch.text(this.model.title, centerX + offsetX, titleY + offsetY,
                sketch.width / 2f, sketch.height / 5f);

        // Creator Title
        Graphics.useGothic(sketch, actionFontSize, true);
        sketch.text(this.model.creatorTitle, creatorX + offsetX, creatorY + offsetY,
                sketch.width / 1.75f, sketch.height / 5f);

        // Creators
        Graphics.useGothic(sketch, actionFontSize, false);
        sketch.text(this.model.creators, creatorX + offsetX, (int) (creatorY * 1.35) + offsetY,
                sketch.width / 1.75f, sketch.height);

        // Supporter Title
        Graphics.useGothic(sketch, actionFontSize, true);
        sketch.text(this.model.supporterTitle, supporterX + offsetX, supporterY + offsetY,
                sketch.width / 1.75f, sketch.height / 5f);

        // Supporters
        Graphics.useGothic(sketch, actionFontSize, false);
        sketch.text(this.model.supporters, supporterX + offsetX, (int) (supporterY * 1.45) + offsetY,
                sketch.width / 1.75f, sketch.height);

        backButton.draw(sketch, offsetX, offsetY);
    }
}
