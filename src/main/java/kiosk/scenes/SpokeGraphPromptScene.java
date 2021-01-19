package kiosk.scenes;

import graphics.Graphics;
import graphics.SpokeUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.Settings;
import kiosk.models.SpokeGraphPromptSceneModel;


public class SpokeGraphPromptScene implements Scene {

    // Pull constants from the settings
    private static final int SCREEN_W = Settings.readSettings().screenW;
    private static final int SCREEN_H = Settings.readSettings().screenH;

    // Header
    private static final float HEADER_W = SCREEN_W * 3f / 4;
    private static final float HEADER_H = SCREEN_H / 6f;
    private static final float HEADER_X = (SCREEN_W - HEADER_W) / 2;
    private static final float HEADER_Y = SCREEN_H / 32f;
    private static final float HEADER_CENTER_X = HEADER_X + (HEADER_W / 2);
    private static final float HEADER_CENTER_Y = HEADER_Y + (HEADER_H / 2);
    private static final int HEADER_CURVE_RADIUS = 25;

    // Header title
    private static final int HEADER_TITLE_FONT_SIZE = 24;
    private static final float HEADER_TITLE_Y = HEADER_CENTER_Y - HEADER_TITLE_FONT_SIZE;

    // Header body
    private static final int HEADER_BODY_FONT_SIZE = 16;
    private static final float HEADER_BODY_Y = HEADER_CENTER_Y + HEADER_BODY_FONT_SIZE;

    private final SpokeGraphPromptSceneModel model;
    private ButtonControl[] careerOptions;
    private ButtonControl[] answerButtons;

    private float careerSize;
    private float answerSize;
    private float centerX;
    private float centerY;

    /**
     * Creates a Spoke Graph Prompt Scene
     * It has a spoke graph of all the careers in the upper left corner
     * and a spoke graph in the bottom right for taking input.
     * @param model The model to pull data from.
     */
    public SpokeGraphPromptScene(SpokeGraphPromptSceneModel model) {
        this.model = model;
        this.careerOptions = new ButtonControl[this.model.careers.length];
        this.answerButtons = new ButtonControl[this.model.answers.length];
    }

    @Override
    public void init(Kiosk sketch) {
        centerX = sketch.width / 2.f;
        centerY = (sketch.height  * .57f);
        answerSize = sketch.height * .75f;
        careerSize = answerSize / 2;

        this.careerOptions = new ButtonControl[this.model.careers.length];
        for (int i = 0; i < careerOptions.length; i++) {
            this.careerOptions[i] = new ButtonControl(this.model.careers[i], 0, 0, 0, 0);
            this.model.careers[i].isCircle = true;
        }

        //initialize weights for testing purposes
        this.model.careerWeights = new int[this.careerOptions.length];
        for (int i = 0; i < this.model.careerWeights.length; i++) {
            this.model.careerWeights[i] = i + 1;
        }

        this.answerButtons = new ButtonControl[this.model.answers.length];
        for (int i = 0; i < answerButtons.length; i++) {
            this.answerButtons[i] = new ButtonControl(this.model.answers[i], 0, 0, 0, 0);
            this.model.answers[i].isCircle = true;
        }

        for (ButtonControl answerButton : this.answerButtons) {
            sketch.hookControl(answerButton);
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.answerButtons) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget());
            }
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useSansSerifBold(sketch, 48);
        Graphics.drawBubbleBackground(sketch);
        drawHeader(sketch);
        SpokeUtil.spokeGraph(sketch, careerSize, centerX - careerSize, centerY - careerSize / 2,
                1, model.careerCenterText, careerOptions, this.model.careerWeights);
        SpokeUtil.spokeGraph(sketch, answerSize / 2, centerX + answerSize / 2,
                centerY, 5, model.promptText, answerButtons);
    }

    private void drawHeader(Kiosk sketch) {
        // Draw the white header box
        sketch.fill(255);
        sketch.stroke(255);

        Graphics.drawRoundedRectangle(sketch,
                HEADER_X, HEADER_Y, HEADER_W, HEADER_H, HEADER_CURVE_RADIUS);

        // Draw the title and body
        sketch.fill(0);
        sketch.stroke(0);

        Graphics.useSansSerifBold(sketch, HEADER_TITLE_FONT_SIZE);
        sketch.text(model.headerTitle, HEADER_CENTER_X, HEADER_TITLE_Y);

        Graphics.useSansSerif(sketch, HEADER_BODY_FONT_SIZE);
        sketch.text(model.headerBody, HEADER_CENTER_X, HEADER_BODY_Y);
    }
}
