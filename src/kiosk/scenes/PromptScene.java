package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.ImageModel;
import kiosk.models.PromptSceneModel;
import processing.core.PConstants;
import processing.core.PFont;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class PromptScene implements Scene {

    private PromptSceneModel model;
    private final ButtonControl[] buttons;
    private Image penguinImage;

    private float penguinX = 0;
    private float penguinRotation = 0;
    private PFont spookyFont = null;

    public PromptScene(PromptSceneModel model) {
        this.model = model;
        this.buttons = new ButtonControl[this.model.answers.length];
    }

    @Override
    public void init(Kiosk sketch) {
        int width = sketch.width;
        int height = sketch.height;
        // Add 4 to the length so the buttons aren't a tight fit
        int buttonWidth = width / (this.buttons.length + 4);
        int buttonHeight = 50;
        // Add 1 to the length to account for both the left and right sides of the buttons
        int buttonPadding = (width - buttonWidth * this.buttons.length) / (this.buttons.length + 1);

        int y = height * 3 / 4;
        int x = buttonPadding;
        for (int i = 0; i < this.model.answers.length; i++) {
            ButtonModel model = this.model.answers[i];
            var button = new ButtonControl(model, x, y, buttonWidth, buttonHeight);

            sketch.hookControl(button);
            this.buttons[i] = button;

            x += buttonWidth + buttonPadding;
        }

        // Spooky font
        try {
            File file = new File("assets/spooky.ttf");
            this.spookyFont = new PFont(Font.createFont(Font.TRUETYPE_FONT, file), true);
        } catch (FontFormatException | IOException exception) {
            // TODO: Find a graceful way to load fonts w/ fallback fonts so we can avoid null checks
            throw new RuntimeException("Font could not be loaded: " + exception.getMessage());
        }

        // Penguin
        this.penguinImage = Image.createImage(sketch, new ImageModel("assets/penguin.png", 64, 64));
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.buttons) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget());
            }
        }

        // Penguin
        this.penguinX += 35 * dt;
        this.penguinRotation += (Math.PI / 2) * dt;
    }

    @Override
    public void draw(Kiosk sketch) {
        // TODO: Set the font
        sketch.rectMode(PConstants.CORNER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        sketch.background(this.model.invertedColors ? 255 : 0);
        sketch.fill(this.model.invertedColors ? 0 : 255);
        sketch.textFont(this.spookyFont,24);
        sketch.text(this.model.question, sketch.width / 2.0f, sketch.height / 4.0f);

        for (ButtonControl button : this.buttons) {
            button.drawRectangle(sketch);
        }

        // Penguin
        final float penguinY = (float) (Math.sin(this.penguinX * 0.1) * 20 + 20);
        this.penguinImage.rotate(this.penguinRotation);
        this.penguinImage.draw(sketch, this.penguinX, penguinY);
    }
}
