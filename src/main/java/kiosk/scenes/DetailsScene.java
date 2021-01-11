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
    private ButtonControl homeButton;
    private ButtonControl backButton;

    // Buttons
    private static final int BUTTON_WIDTH = Kiosk.getSettings().screenW / 8;
    private static final int BUTTON_HEIGHT = Kiosk.getSettings().screenH / 6;
    private static final int BUTTON_PADDING = 20;

    public DetailsScene(DetailsSceneModel model) {
        this.model = model;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchHeight = Kiosk.getSettings().screenH;

        var homeButtonModel = new ButtonModel();
        homeButtonModel.text = "Home";
        homeButtonModel.rgb = Color.DW_BLACK_RGB;
        homeButton = new ButtonControl(homeButtonModel,
                BUTTON_PADDING, BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, BUTTON_HEIGHT * 3 / 4);
        sketch.hookControl(this.homeButton);

        var backButtonModel = new ButtonModel();
        backButtonModel.text = "Back";
        backButtonModel.rgb = Color.DW_BLACK_RGB;
        this.backButton = new ButtonControl(backButtonModel,
                BUTTON_PADDING, sketchHeight - (BUTTON_HEIGHT * 3 / 4) - BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, BUTTON_HEIGHT * 3 / 4);
        sketch.hookControl(this.backButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.backButton.wasClicked()) {
            sceneGraph.popScene();
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.drawBubbleBackground(sketch);

        homeButton.draw(sketch);
        backButton.draw(sketch);
    }
}
