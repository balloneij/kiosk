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

        this.backButton = GraphicsUtil.initializeBackButton(sketch);
        this.backButton.init(sketch);
        sketch.hookControl(this.backButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.backButton.wasClicked()) {
            //TODO STAY ON THIS SCENE FOR A FEW FRAMES
            sceneGraph.popScene();
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;

        // Draw bubble background
        Graphics.drawBubbleBackground(sketch);

        //If this scene is new, animate the items to gradually show up on screen
        if (sketch.frameCount - startFrame <= Kiosk.getSettings().sceneAnimationFrames && !sketch.isEditor) {
            // Draw the white foreground box
            sketch.fill(255);
            Graphics.drawRoundedRectangle(sketch,
                    (float) (foregroundXPadding + screenW + screenW
                            * (1 - ((sketch.frameCount - startFrame)
                            * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1))),
                    foregroundYPadding,
                    foregroundWidth,
                    foregroundHeight,
                    foregroundCurveRadius);
            // Draw text
            sketch.rectMode(PConstants.CENTER);
            sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
            sketch.fill(0);
            // Title
            Graphics.useGothic(sketch, titleFontSize, true);
            sketch.text(this.model.title, (int) (centerX + screenW + screenW
                            * (1 - ((sketch.frameCount - startFrame) * 1.0
                            / Kiosk.getSettings().sceneAnimationFrames + 1))), titleY,
                    sketch.width / 2f, sketch.height / 5f);
            //Creators & Supporters
            Graphics.useGothic(sketch, promptFontSize, false);
            sketch.text(this.model.creatorTitle, (int) (creatorX + screenW + screenW
                            * (1 - ((sketch.frameCount - startFrame) * 1.0
                            / Kiosk.getSettings().sceneAnimationFrames + 1))), creatorY,
                    sketch.width / 1.75f, sketch.height / 5f);
            sketch.text(this.model.creators, (int) (creatorX + screenW + screenW
                            * (1 - ((sketch.frameCount - startFrame) * 1.0
                            / Kiosk.getSettings().sceneAnimationFrames + 1))),
                    (int) (creatorY * 1.35),
                    sketch.width / 1.75f, sketch.height / 2f);
            sketch.text(this.model.supporterTitle, (int) (supporterX + screenW + screenW
                            * (1 - ((sketch.frameCount - startFrame) * 1.0
                            / Kiosk.getSettings().sceneAnimationFrames + 1))), supporterY,
                    sketch.width / 1.75f, sketch.height / 5f);
            sketch.text(this.model.supporters, (int) (supporterX + screenW + screenW
                            * (1 - ((sketch.frameCount - startFrame) * 1.0
                            / Kiosk.getSettings().sceneAnimationFrames + 1))),
                    (int) (supporterY * 1.45),
                    sketch.width / 1.75f, sketch.height / 2f);
        } else { //If it's already a second-or-two old, draw the scene normally
            // Draw the white foreground box
            sketch.fill(255);
            Graphics.drawRoundedRectangle(sketch,
                    foregroundXPadding, foregroundYPadding,
                    foregroundWidth, foregroundHeight,
                    foregroundCurveRadius);

            // Draw text
            sketch.rectMode(PConstants.CENTER);
            sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
            sketch.fill(0);

            // Title
            Graphics.useGothic(sketch, (int) (titleFontSize * 1.15f), true);
            sketch.text(this.model.title, centerX, titleY,
                    sketch.width / 2f, sketch.height / 5f);

            // Creator Title
            Graphics.useGothic(sketch, actionFontSize, true);
            sketch.text(this.model.creatorTitle, creatorX, creatorY,
                    sketch.width / 1.75f, sketch.height / 5f);

            // Creators
            Graphics.useGothic(sketch, actionFontSize, false);
            sketch.text(this.model.creators, creatorX, (int) (creatorY * 1.35),
                    sketch.width / 1.75f, sketch.height);

            // Supporter Title
            Graphics.useGothic(sketch, actionFontSize, true);
            sketch.text(this.model.supporterTitle, supporterX, supporterY,
                    sketch.width / 1.75f, sketch.height / 5f);

            // Supporters
            Graphics.useGothic(sketch, actionFontSize, false);
            sketch.text(this.model.supporters, supporterX, (int) (supporterY * 1.45),
                    sketch.width / 1.75f, sketch.height);
        }

        backButton.draw(sketch);
    }
}
