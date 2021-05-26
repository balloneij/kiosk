package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SceneAnimationHelper;
import java.io.File;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.CareerDescriptionModel;
import kiosk.models.ImageModel;
import processing.core.PConstants;


public class CareerDescriptionScene implements Scene {

    // Button
    private static int buttonWidth;
    private static int buttonHeight;

    private static int screenW;
    private static int screenH;

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

    //Animations
    private int sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
    private SceneAnimationHelper.Clicked clicked;
    private float totalTimeOpening = 0;
    private float totalTimeEnding = 0;
    private float dt = 0;

    /**
     * Career Description Scene shows a title, body of text, and a button at the bottom.
     * @param model The model object where we get our information.
     */
    public CareerDescriptionScene(CareerDescriptionModel model) {
        this.model = model;
        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

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
        if (this.model.image == null) {
            this.model.image = new ImageModel();
        }
        this.model.image.width = imageSize;
        this.model.image.height = imageSize;

        File imageFile = new File(this.model.image.path);
        if (!imageFile.exists()) {
            this.model.image.path = "assets/default.png";
        }
        image = Image.createImage(sketch, this.model.image);

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

        sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
        totalTimeOpening = 0;
        totalTimeEnding = 0;

        this.supplementaryButton = GraphicsUtil.initializeMsoeButton(sketch);
        this.supplementaryButton.init(sketch);
        sketch.hookControl(this.supplementaryButton);

        clicked = SceneAnimationHelper.Clicked.NONE;
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        this.dt = dt;

        if (this.homeButton.wasClicked()) {
            clicked = SceneAnimationHelper.Clicked.HOME;
        } else if (this.centerButton.wasClicked()) {
            clicked = SceneAnimationHelper.Clicked.BACK;
        } else if (this.supplementaryButton.wasClicked()) {
            clicked = SceneAnimationHelper.Clicked.MSOE;
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        if ((totalTimeOpening < sceneAnimationMilliseconds) && sceneAnimationMilliseconds != 0) {
            totalTimeOpening += dt * 1000;
        }
        if (!clicked.equals(SceneAnimationHelper.Clicked.NONE) && sceneAnimationMilliseconds != 0) {
            totalTimeEnding += dt * 1000;
        }

        int[] returnVals = SceneAnimationHelper.sceneAnimationLogic(sketch,
                clicked,
                null, null, null,
                totalTimeOpening, totalTimeEnding, sceneAnimationMilliseconds,
                screenW, screenH);
        drawThisFrame(sketch, returnVals[0], returnVals[1]);

    }

    private void drawThisFrame(Kiosk sketch, int offsetX, int offsetY) {
        final int centerX = Kiosk.getSettings().screenW / 2;
        // Draw the white foreground box
        sketch.fill(255);
        sketch.rectMode(PConstants.CORNER);
        sketch.rect(foregroundCornerX + offsetX, foregroundCornerY + offsetY,
                foregroundWidth + offsetX, foregroundHeight + offsetY,
                foregroundCurveRadius);

        // Career Name
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);
        Graphics.useGothic(sketch, titleFontSize, true);
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(this.model.careerModel.name, centerX + offsetX, (int) (titleY * 1.15) + offsetY,
                (int) (foregroundWidth * 0.95), foregroundHeight / 5f);

        // Image
        sketch.imageMode(PConstants.CORNER);

        float imageX = foregroundCornerX + padding;
        float imageY = foregroundCornerY + (foregroundHeight / 2f) - (imageSize / 2f);
        this.image.draw(sketch, imageX + offsetX, imageY + offsetY);

        // Body
        Graphics.useGothic(sketch, descriptionFontSize, false);
        sketch.textAlign(PConstants.LEFT, PConstants.TOP);
        sketch.rectMode(PConstants.CORNER);

        float textX = imageX + imageSize + padding;
        float textW = foregroundWidth
                - (textX - foregroundCornerX)
                - padding;
        sketch.text(this.model.careerModel.description,
                textX + offsetX, imageY + offsetY, textW, imageSize);

        this.centerButton.draw(sketch, offsetX, offsetY);

        if ((sketch.getSceneGraph().getHistorySize() == 2
                && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.PUSH))
                || ((sketch.getSceneGraph().getHistorySize() == 2
                && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.POP))
                && clicked.equals(SceneAnimationHelper.Clicked.BACK))
                || clicked.equals(SceneAnimationHelper.Clicked.HOME)) {
            centerButton.draw(sketch, offsetX, offsetY);
            homeButton.draw(sketch, offsetX, offsetY);
            supplementaryButton.draw(sketch, offsetX, offsetY);
        } else if (clicked.equals(SceneAnimationHelper.Clicked.MSOE)) {
            centerButton.draw(sketch, offsetX, offsetY);
            homeButton.draw(sketch, offsetX, offsetY);
            supplementaryButton.draw(sketch, offsetX, 0);
        } else {
            centerButton.draw(sketch);
            homeButton.draw(sketch);
            supplementaryButton.draw(sketch, offsetX, 0);
        }
    }
}
