package kiosk.scenes;

import graphics.Color;
import graphics.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.DetailsSceneModel;
import processing.core.PConstants;


public class DetailsScene implements Scene {

    private final DetailsSceneModel model;
    private ButtonControl centerButton;
    private ButtonControl nextButton;
    private ButtonControl homeButton;
    private ButtonControl backButton;

    // Buttons
    private static final int BUTTON_WIDTH = Kiosk.getSettings().screenW / 8;
    private static final int BUTTON_PADDING = 20;

    // White foreground
    private static final int FOREGROUND_WIDTH = Kiosk.getSettings().screenW * 2 / 3;
    private static final int FOREGROUND_HEIGHT = Kiosk.getSettings().screenH * 3 / 4;
    private static final int FOREGROUND_X_PADDING = Kiosk.getSettings().screenW / 6;
    private static final int FOREGROUND_Y_PADDING = Kiosk.getSettings().screenH / 8;
    private static final int FOREGROUND_CURVE_RADIUS = 50;

    // Text
    private static final int TITLE_Y = Kiosk.getSettings().screenH / 5;
    private static final int BODY_Y = Kiosk.getSettings().screenH * 3 / 9;
    private static final int TITLE_FONT_SIZE = 36;
    private static final int BODY_FONT_SIZE = 30;

    /**
     * Detials Scene show a title, body of text, and a button at the bottom.
     * @param model The model object where we get our information.
     */
    public DetailsScene(DetailsSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchHeight = Kiosk.getSettings().screenH;
        final int sketchWidth = Kiosk.getSettings().screenW;

        var homeButtonModel = new ButtonModel();
        homeButtonModel.text = "Home";
        homeButtonModel.rgb = Color.DW_BLACK_RGB;
        homeButton = new ButtonControl(homeButtonModel,
                BUTTON_PADDING, BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, BUTTON_WIDTH * 3 / 4);
        sketch.hookControl(this.homeButton);

        var backButtonModel = new ButtonModel();
        backButtonModel.text = "Back";
        backButtonModel.rgb = Color.DW_BLACK_RGB;
        this.backButton = new ButtonControl(backButtonModel,
                BUTTON_PADDING, sketchHeight - (BUTTON_WIDTH * 3 / 4) - BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, BUTTON_WIDTH * 3 / 4);
        sketch.hookControl(this.backButton);

        this.centerButton = new ButtonControl(
            this.model.button,
            (sketchWidth / 2) - (BUTTON_WIDTH * 5 / 8),
            FOREGROUND_Y_PADDING + FOREGROUND_HEIGHT
                - (BUTTON_WIDTH * 5 / 4 + BUTTON_PADDING),
                BUTTON_WIDTH * 5 / 4,
                BUTTON_WIDTH * 5 / 4
        );
        this.centerButton.init(sketch);
        sketch.hookControl(this.centerButton);

        var nextButtonModel = new ButtonModel();
        nextButtonModel.rgb = Color.DW_GREEN_RGB;
        nextButtonModel.text = "Go!";

        this.nextButton = new ButtonControl(
            nextButtonModel,
            FOREGROUND_X_PADDING + FOREGROUND_WIDTH + BUTTON_PADDING,
            sketchHeight - (BUTTON_WIDTH * 3 / 4) - BUTTON_PADDING,
            BUTTON_WIDTH * 3 / 4,
            BUTTON_WIDTH * 3 / 4
        );
        this.nextButton.init(sketch);
        sketch.hookControl(this.nextButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.backButton.wasClicked()) {
            sceneGraph.popScene();
        } else if (this.centerButton.wasClicked()) {
            sceneGraph.pushScene(this.centerButton.getTarget());
        } else if (this.nextButton.wasClicked()) {
            sceneGraph.pushScene(this.centerButton.getTarget());
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;
        Graphics.drawBubbleBackground(sketch);

        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
            FOREGROUND_X_PADDING, FOREGROUND_Y_PADDING,
            FOREGROUND_WIDTH, FOREGROUND_HEIGHT,
            FOREGROUND_CURVE_RADIUS);

        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);


        // Title
        Graphics.useSansSerifBold(sketch, TITLE_FONT_SIZE);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(33);
        sketch.text(this.model.title, centerX, TITLE_Y);

        // Body
        Graphics.useSansSerif(sketch, BODY_FONT_SIZE);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(40);
        sketch.text(this.model.body, centerX, BODY_Y);

        this.centerButton.draw(sketch);
        this.homeButton.draw(sketch);
        this.backButton.draw(sketch);
        this.nextButton.draw(sketch);
    }
}
