package kiosk.scenes;

import graphics.Graphics;
import graphics.SpokeUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.Settings;
import kiosk.models.PathwaySceneModel;

public class PathwayScene implements Scene {

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

    private final PathwaySceneModel model;
    private float size;
    private float centerX;
    private float centerY;
    private int[] buttonLocations;
    private ButtonControl[] careerOptions;

    public PathwayScene(PathwaySceneModel model) {
        this.model = model;
        this.careerOptions = new ButtonControl[this.model.answers.length];
    }

    @Override
    public void init(Kiosk sketch) {
        size = sketch.width * .4f;
        var x = sketch.width * .05f + 2 * sketch.width / 5.f;
        var y = sketch.height * .25f;
        centerX = (size / 2) + x;
        centerY = (size / 2) + y;
        this.careerOptions = new ButtonControl[this.model.answers.length];

        initializeButtons(model, sketch, size, centerX, centerY);
    }

    private void initializeButtons(PathwaySceneModel model, Kiosk sketch, float size,
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

            this.careerOptions[i] = new ButtonControl(btnModel, (int) (upperLeftX - .5 * radius),
                    (int) (upperLeftY - .125 * size), (int) radius, (int) radius);
            degrees += 120;
            sketch.hookControl(this.careerOptions[i]);
        }
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.careerOptions) {
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
        drawPathwayGraph(sketch);
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

    private void drawPathwayGraph(Kiosk sketch) {
        for (int i = 0; i < model.answers.length; i++) {
            sketch.stroke(255);
            sketch.line(centerX, centerY, buttonLocations[2 * i], buttonLocations[2 * i + 1]);
            this.careerOptions[i].draw(sketch);
        }
        SpokeUtil.drawInnerCircle(sketch, centerX, centerY, size / 4.f, model.centerText);
    }
}
