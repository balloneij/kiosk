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
    private boolean isRoot;

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

    /**
     * Detials Scene show a title, body of text, and a button at the bottom.
     * @param model The model object where we get our information.
     */
    public DetailsScene(DetailsSceneModel model) {
        this.model = model;
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
            this.supplementaryButton = GraphicsUtil.initializeMsoeButtonUpperRight(sketch);
            sketch.hookControl(this.supplementaryButton);
        }

        if (this.model.targets[0].image != null) {
            this.model.targets[0].image.width = buttonImageWidth;
            this.model.targets[0].image.height = buttonImageHeight;
        }

        this.centerButton = new ButtonControl(
            this.model.targets[0],
            (sketchWidth / 2) - (buttonWidth * 5 / 8),
                (int) (foregroundYPadding * 0.85 + foregroundHeight
                - (buttonWidth * 5 / 4 + buttonPadding)),
                buttonWidth * 5 / 4,
                buttonWidth * 5 / 4
        );
        this.centerButton.init(sketch);
        sketch.hookControl(this.centerButton);

        this.nextButton = GraphicsUtil.initializeNextButton(sketch);
        sketch.hookControl(this.nextButton);

        this.isRoot = sketch.getRootSceneModel().getId().equals(this.model.getId());
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (!this.isRoot) {
            if (this.homeButton.wasClicked()) {
                sceneGraph.reset();
            } else if (this.backButton.wasClicked()) {
                sceneGraph.popScene();
            }
        } else {
            if (this.supplementaryButton != null && this.supplementaryButton.wasClicked()) {
                sceneGraph.pushScene(new CreditsSceneModel());
            }
        }
        if (this.centerButton.wasClicked()) {
            sceneGraph.pushScene(this.centerButton.getTarget());
        } else if (this.nextButton.wasClicked()) {
            sceneGraph.pushScene(this.centerButton.getTarget());
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;

        // Draw the white foreground box
        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch,
            foregroundXPadding + foregroundWidth / 2.f,
                foregroundYPadding + foregroundHeight / 2.f,
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
        sketch.text(this.model.title, centerX, (int) (titleY * 1.15),
                (int) (foregroundWidth * 0.95), foregroundHeight / 5);

        // Body
        Graphics.useGothic(sketch, bodyFontSize, false);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.textLeading(25);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.body, centerX, (int) (bodyY * 1.15),
                (int) (foregroundWidth * 0.95), foregroundHeight / 5);


        this.centerButton.draw(sketch);
        this.nextButton.draw(sketch);
        if (!isRoot) {
            this.homeButton.draw(sketch);
            this.backButton.draw(sketch);
        } else {
            supplementaryButton.draw(sketch); //TODO CHECK IF NEXT & MSOE BUTTONS OVERLAP
        }
    }
}
