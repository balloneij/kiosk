package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.CareerDescriptionModel;
import kiosk.models.CreditsSceneModel;
import kiosk.models.ImageModel;
import processing.core.PConstants;


public class CareerDescriptionScene implements Scene {

    // Button
    private static int buttonWidth;
    private static int buttonHeight;

    // White foreground
    private static int foregroundWidth;
    private static int foregroundHeight;
    private static int foregroundCornerX;
    private static int foregroundCornerY;
    private static int foregroundCurveRadius;

    // Text & Image
    private static int titleY;
    private static int titleFontSize;
    private static int descriptionFontSize;
    private static int imageSize;
    private static int padding;

    private final CareerDescriptionModel model;
    private ButtonControl centerButton;
    private ButtonControl homeButton;
    private ButtonControl supplementaryButton;
    private Image image;

    /**
     * Career Description Scene shows a title, body of text, and a button at the bottom.
     * @param model The model object where we get our information.
     */
    public CareerDescriptionScene(CareerDescriptionModel model) {
        this.model = model;
        int screenW = Kiosk.getSettings().screenW;
        int screenH = Kiosk.getSettings().screenH;

        // Button
        buttonWidth = screenW / 8;
        buttonHeight = screenH / 8;

        // White foreground
        foregroundWidth = screenW * 2 / 3;
        foregroundHeight = screenH * 3 / 4;
        foregroundCornerX = screenW / 6;
        foregroundCornerY = screenH / 8;
        foregroundCurveRadius = 100;

        // Text & Image
        titleY = screenH / 4;
        titleFontSize = screenW / 55;
        descriptionFontSize = screenW / 68;
        imageSize = foregroundHeight / 2;
        padding = foregroundWidth / 16;
    }

    @Override
    public void init(Kiosk sketch) {
        final int sketchWidth = Kiosk.getSettings().screenW;

        // Images
        this.model.image = new ImageModel();
        this.model.image.width = imageSize;
        this.model.image.height = imageSize;
        this.image = Image.createImage(sketch, this.model.image);

        // Buttons
        this.homeButton = GraphicsUtil.initializeHomeButton(sketch);
        sketch.hookControl(this.homeButton);

        if (this.model.button.image != null) {
            this.model.button.image.width = buttonWidth;
            this.model.button.image.height = buttonHeight;
        }

        float imageY = foregroundCornerY + (foregroundHeight / 2f) - (imageSize / 2f);
        this.centerButton = new ButtonControl(
            this.model.button,
            (sketchWidth / 2) - (buttonWidth / 2),
                (int) (imageY + imageSize + padding - (buttonHeight / 2)),
                buttonWidth,
                buttonHeight
        );
        this.centerButton.init(sketch);
        sketch.hookControl(this.centerButton);

        this.supplementaryButton = GraphicsUtil.initializeMsoeButton(sketch);
        this.supplementaryButton.init(sketch);
        sketch.hookControl(this.supplementaryButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.centerButton.wasClicked()) {
            sceneGraph.popScene();
        } else if (this.supplementaryButton.wasClicked()) {
            sceneGraph.pushScene(new CreditsSceneModel());
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        final int centerX = Kiosk.getSettings().screenW / 2;

        // Draw the white foreground box
        sketch.fill(255);
        sketch.rectMode(PConstants.CORNER);
        sketch.rect(foregroundCornerX, foregroundCornerY,
                foregroundWidth, foregroundHeight,
                foregroundCurveRadius);

        // Career Name
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);
        Graphics.useGothic(sketch, titleFontSize, true);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.careerModel.name, centerX, (int) (titleY * 1.15),
                (int) (foregroundWidth * 0.95), foregroundHeight / 5f);

        // Image
        sketch.imageMode(PConstants.CORNER);

        float imageX = foregroundCornerX + padding;
        float imageY = foregroundCornerY + (foregroundHeight / 2f) - (imageSize / 2f);
        this.image.draw(sketch, imageX, imageY);

        // Body
        Graphics.useGothic(sketch, descriptionFontSize, false);
        sketch.textAlign(PConstants.LEFT, PConstants.TOP);
        sketch.rectMode(PConstants.CORNER);

        float textX = imageX + imageSize + padding;
        float textW = foregroundWidth
                - (textX - foregroundCornerX)
                - padding;
        sketch.text(this.model.careerModel.description, textX, imageY, textW, imageSize);

        // Buttons
        this.centerButton.draw(sketch);
        this.homeButton.draw(sketch);
        this.supplementaryButton.draw(sketch);
    }
}
