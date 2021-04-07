package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.CreditsSceneModel;
import processing.core.PConstants;

public class CreditsScene implements Scene {

    private static int SCREEN_W =  Kiosk.getSettings().screenW;
    private static int SCREEN_H = Kiosk.getSettings().screenH;

    // White foreground
    private static int FOREGROUND_WIDTH = Kiosk.getSettings().screenW * 2 / 3;
    private static int FOREGROUND_HEIGHT = Kiosk.getSettings().screenH * 3 / 4;
    private static int FOREGROUND_X_PADDING
            = Kiosk.getSettings().screenW / 6 + FOREGROUND_WIDTH / 2;
    private static int FOREGROUND_Y_PADDING
            = Kiosk.getSettings().screenH / 8 + FOREGROUND_HEIGHT / 2;
    private static int FOREGROUND_CURVE_RADIUS = 100;

    // Text
    private static int TITLE_Y = Kiosk.getSettings().screenH / 4;
    private static int TITLE_FONT_SIZE = SCREEN_W / 55;
    private static int CREATOR_X = Kiosk.getSettings().screenW / 3;
    private static int CREATOR_Y = Kiosk.getSettings().screenH * 4 / 9;
    private static int PROMPT_FONT_SIZE = SCREEN_W / 60;
    private static int SUPPORTER_X = Kiosk.getSettings().screenW * 2 / 3;
    private static int SUPPORTER_Y = Kiosk.getSettings().screenH * 4 / 9;
    private static int ACTION_FONT_SIZE = SCREEN_W / 58;

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
        FOREGROUND_WIDTH = Kiosk.getSettings().screenW * 2 / 3;
        FOREGROUND_HEIGHT = Kiosk.getSettings().screenH * 3 / 4;
        FOREGROUND_X_PADDING
                = Kiosk.getSettings().screenW / 6 + FOREGROUND_WIDTH / 2;
        FOREGROUND_Y_PADDING
                = Kiosk.getSettings().screenH / 8 + FOREGROUND_HEIGHT / 2;
        FOREGROUND_CURVE_RADIUS = 100;

        // Text
        TITLE_Y = Kiosk.getSettings().screenH / 4;
        TITLE_FONT_SIZE = Kiosk.getSettings().screenW / 55;
        CREATOR_X = Kiosk.getSettings().screenW / 3;
        CREATOR_Y = Kiosk.getSettings().screenH * 4 / 9;
        PROMPT_FONT_SIZE = Kiosk.getSettings().screenW / 60;
        SUPPORTER_X = Kiosk.getSettings().screenW * 2 / 3;
        SUPPORTER_Y = Kiosk.getSettings().screenH * 4 / 9;
        ACTION_FONT_SIZE = Kiosk.getSettings().screenW / 58;
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
        sketch.hookControl(this.backButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.backButton.wasClicked()) {
            sceneGraph.popScene();
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;

        // Draw bubble background
        Graphics.drawBubbleBackground(sketch);

        //TODO MAKE ANIMATION LESS CHOPPY WHEN LESS FRAMES DESIRED
        //     Implement Seth's idea of white box gradually revealing text
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
                Graphics.useGothic(sketch, (int) (TITLE_FONT_SIZE
                        * ((sketch.frameCount - startFrame) * 1.0
                        / (Kiosk.getSettings().sceneAnimationFrames + 1))), true);
                sketch.text(this.model.title, centerX, TITLE_Y,
                        sketch.width / 2f, sketch.height / 5f);
                //Creators & Supporters
                Graphics.useGothic(sketch, (int) (PROMPT_FONT_SIZE
                        * ((sketch.frameCount - startFrame) * 1.0
                        / (Kiosk.getSettings().sceneAnimationFrames + 1))), false);
                sketch.text(this.model.creatorTitle, CREATOR_X, CREATOR_Y,
                        sketch.width / 1.75f, sketch.height / 5f);
                sketch.text(this.model.creators, CREATOR_X, (int) (CREATOR_Y * 1.35),
                        sketch.width / 1.75f, sketch.height / 2f);
                sketch.text(this.model.supporterTitle, SUPPORTER_X, SUPPORTER_Y,
                        sketch.width / 1.75f, sketch.height / 5f);
                sketch.text(this.model.supporters, SUPPORTER_X, (int) (SUPPORTER_Y * 1.45),
                        sketch.width / 1.75f, sketch.height / 2f);
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
            Graphics.useGothic(sketch, (int) (TITLE_FONT_SIZE * 1.15f), true);
            sketch.text(this.model.title, centerX, TITLE_Y,
                    sketch.width / 2f, sketch.height / 5f);

            // Creator Title
            Graphics.useGothic(sketch, ACTION_FONT_SIZE, true);
            sketch.text(this.model.creatorTitle, CREATOR_X, CREATOR_Y,
                    sketch.width / 1.75f, sketch.height / 5f);

            // Creators
            Graphics.useGothic(sketch, ACTION_FONT_SIZE, false);
            sketch.text(this.model.creators, CREATOR_X, (int) (CREATOR_Y * 1.35),
                    sketch.width / 1.75f, sketch.height);

            // Supporter Title
            Graphics.useGothic(sketch, ACTION_FONT_SIZE, true);
            sketch.text(this.model.supporterTitle, SUPPORTER_X, SUPPORTER_Y,
                    sketch.width / 1.75f, sketch.height / 5f);

            // Supporters
            Graphics.useGothic(sketch, ACTION_FONT_SIZE, false);
            sketch.text(this.model.supporters, SUPPORTER_X, (int) (SUPPORTER_Y * 1.45),
                    sketch.width / 1.75f, sketch.height);
        }

        backButton.draw(sketch);
    }
}
