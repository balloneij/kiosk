package kiosk.scenes;

import graphics.GraphicsUtil;
import kiosk.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.SpokeGraphPromptSceneModel;


public class SpokeGraphPromptScene implements Scene {

    private static final int TITLE_FONT_SIZE = 24;
    private static final int PROMPT_FONT_SIZE = 16;

    // Buttons
    private static final int BUTTON_WIDTH = Kiosk.getSettings().screenW / 8;
    private static final int COMMON_BUTTON_HEIGHT = Kiosk.getSettings().screenH / 10;
    private static final int BUTTON_PADDING = 20;

    private final SpokeGraphPromptSceneModel model;
    private float size;
    private float centerX;
    private float centerY;
    private int[] buttonLocations;
    private ButtonControl[] answerButtons;
    private ButtonControl homeButton;
    private ButtonControl backButton;

    public SpokeGraphPromptScene(SpokeGraphPromptSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
        size = sketch.width * .4f;
        var x = sketch.width * .05f + 2 * sketch.width / 5.f;
        var y = sketch.height * .25f;
        centerX = (size / 2) + x;
        centerY = (size / 2) + y;
        this.answerButtons = new ButtonControl[model.answerButtons.length];

        initializeButtons(model, sketch, size, centerX, centerY);

        final int sketchHeight = Kiosk.getSettings().screenH;
        var homeButtonModel = new ButtonModel();
        homeButtonModel.text = "⌂";
        this.homeButton = new ButtonControl(homeButtonModel,
                BUTTON_PADDING, BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        sketch.hookControl(this.homeButton);
        var backButtonModel = new ButtonModel();
        backButtonModel.text = "← Back";
        this.backButton = new ButtonControl(backButtonModel,
                BUTTON_PADDING, sketchHeight - (COMMON_BUTTON_HEIGHT * 3 / 4) - BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        sketch.hookControl(this.backButton);
    }

    private void initializeButtons(SpokeGraphPromptSceneModel model, Kiosk sketch, float size,
            float centerX, float centerY) {
        var degrees = 0.f;
        var radius = .25 * size;
        buttonLocations = new int[2 * model.promptOptions.length];

        // for each answer find the degrees and position
        for (var i = 0; i < model.answerButtons.length; i++) {
            var btnModel = model.answerButtons[i];
            var colorSelection = model.optionColors[i % model.optionColors.length];
            btnModel.isCircle = true;
            // Colors represented as a single int have their RGB values spread along an int.
            btnModel.rgb = new int[]{
                colorSelection >> 16 & 0xFF,    // Shift 16 bits for Red
                colorSelection >> 8 & 0xFF,     // Shift 8 Bits for Blue
                colorSelection & 0xFF};         // Shift 0 bits for Green

            var upperLeftX = centerX + (.62 * size - radius) * Math.cos(Math.toRadians(degrees));
            var upperLeftY = centerY + (.62 * size - radius) * Math.sin(Math.toRadians(degrees));
            buttonLocations[2 * i] = (int) upperLeftX;
            buttonLocations[2 * i + 1] = (int) upperLeftY;

            this.answerButtons[i] = new ButtonControl(btnModel, (int) (upperLeftX - .5 * radius),
                (int) (upperLeftY - .125 * size), (int) radius, (int) radius);
            degrees += 120;
            sketch.hookControl(this.answerButtons[i]);
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.answerButtons) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget());
            }
        }

        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.backButton.wasClicked()) {
            sceneGraph.popScene();
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useGothic(sketch);
        Graphics.drawBubbleBackground(sketch);
        drawHeader(sketch);
        drawCareerGraph(sketch);
        drawPromptGraph(sketch);

        homeButton.setColor(138, 37, 93);
        homeButton.draw(sketch, 2);
        backButton.setColor(59, 58, 57);
        backButton.draw(sketch);
    }

    private void drawHeader(Kiosk sketch) {
        sketch.textSize(18.f);
        sketch.fill(256, 256, 256);
        sketch.stroke(256, 256, 256);
        var boxX = sketch.width / 8.f;
        var boxY = sketch.height / 32.f;
        var boxWidth = sketch.width - (sketch.width / 5.f);
        var boxHeight = sketch.height - .8f * sketch.height;

        Graphics.drawRoundedRectangle(sketch, boxX, boxY, boxWidth, boxHeight, 25);
        sketch.fill(0, 0, 0);
        sketch.stroke(0, 0, 0);

        var boxCenterX = (boxX + boxWidth * .5f);
        var boxCenterY = (boxY + boxHeight * .5f);
        var headerWidth = sketch.textWidth(model.headerTitle);
        sketch.textAscent();
        Graphics.useGothic(sketch, TITLE_FONT_SIZE, true);
        sketch.text(model.headerTitle, boxCenterX, boxCenterY - boxHeight / 3);

        Graphics.useGothic(sketch, PROMPT_FONT_SIZE, false);
        sketch.text(model.headerBody, boxCenterX, boxCenterY + boxHeight / 4f);
    }

    private void drawCareerGraph(Kiosk sketch) {
        GraphicsUtil.spokeGraph(
            sketch,
            sketch.width / 3.f,
            sketch.width * .05f,
            sketch.height * .25f,
            1.f,
            model.careerCenterText,
            model.careerOptions,
            model.careerWeights,
            null
        );
    }

    private void drawPromptGraph(Kiosk sketch) {
        for (int i = 0; i < model.answerButtons.length; i++) {
            sketch.stroke(0, 0, 0);
            sketch.line(centerX, centerY, buttonLocations[2 * i], buttonLocations[2 * i + 1]);
            this.answerButtons[i].draw(sketch);
        }
        GraphicsUtil.drawInnerCircle(sketch, centerX, centerY, size / 4.f, model.promptText);
    }
}
