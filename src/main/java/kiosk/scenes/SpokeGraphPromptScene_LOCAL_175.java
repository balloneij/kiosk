package kiosk.scenes;

import graphics.GraphicsUtil;
import kiosk.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.SpokeGraphPromptSceneModel;


public class SpokeGraphPromptScene implements Scene {

    private final SpokeGraphPromptSceneModel model;
    private float size;
    private float centerX;
    private float centerY;
    private int[] buttonLocations;
    private ButtonControl[] buttons;

    public SpokeGraphPromptScene(SpokeGraphPromptSceneModel model) {
        this.model = model;
        this.buttons = new ButtonControl[this.model.answers.length];
    }

    @Override
    public void init(Kiosk sketch) {
        size = sketch.width * .4f;
        var x = sketch.width * .05f + 2 * sketch.width / 5.f;
        var y = sketch.height * .25f;
        centerX = (size / 2) + x;
        centerY = (size / 2) + y;

        initializeButtons(model, sketch, size, centerX, centerY);
    }

    private void initializeButtons(SpokeGraphPromptSceneModel model, Kiosk sketch, float size,
            float centerX, float centerY) {
        var degrees = 0.f;
        var radius = .25 * size;
        buttonLocations = new int[2 * model.answers.length];

        // for each answer find the degrees and position
        for (var i = 0; i < model.answers.length; i++) {
            var btnModel = model.answers[i];
            btnModel.isCircle = true;

            var upperLeftX = centerX + (.62 * size - radius) * Math.cos(Math.toRadians(degrees));
            var upperLeftY = centerY + (.62 * size - radius) * Math.sin(Math.toRadians(degrees));
            buttonLocations[2 * i] = (int) upperLeftX;
            buttonLocations[2 * i + 1] = (int) upperLeftY;

            this.buttons[i] = new ButtonControl(btnModel, (int) (upperLeftX - .5 * radius),
                (int) (upperLeftY - .125 * size), (int) radius, (int) radius);
            degrees += 120;
            sketch.hookControl(this.buttons[i]);
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.buttons) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget());
            }
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useSerif(sketch);
        Graphics.drawBubbleBackground(sketch);
        drawHeader(sketch);
        drawCareerGraph(sketch);
        drawPromptGraph(sketch);
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
        sketch.textSize(52);
        sketch.text(model.headerTitle, boxCenterX, boxCenterY - boxHeight / 3);

        sketch.textSize(34);
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
            buttons
        );
    }

    private void drawPromptGraph(Kiosk sketch) {
        for (int i = 0; i < model.answers.length; i++) {
            sketch.stroke(0, 0, 0);
            sketch.line(centerX, centerY, buttonLocations[2 * i], buttonLocations[2 * i + 1]);
            this.buttons[i].draw(sketch);
        }
        GraphicsUtil.drawInnerCircle(sketch, centerX, centerY, size / 4.f, model.promptText);
    }
}
