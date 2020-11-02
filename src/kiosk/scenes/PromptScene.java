package kiosk.scenes;

import kiosk.Graphics;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.PromptSceneModel;
import processing.core.PConstants;


public class PromptScene implements Scene {

    private final PromptSceneModel model;
    private final ButtonControl[] buttons;

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
        int buttonHeight = 30;
        // Add 1 to the length to account for both the left and right sides of the buttons
        int buttonPadding = (width - buttonWidth * this.buttons.length) / (this.buttons.length + 1);

        float y = (height / 5f) * (1f / 3) + // Box height
                ((height * 4 / 5f) * (2f / 3)); // Box y
        int x = buttonPadding;
        for (int i = 0; i < this.model.answers.length; i++) {
            ButtonModel model = this.model.answers[i];
            var button = new ButtonControl(model, x, (int)y, buttonWidth, buttonHeight);

            sketch.hookControl(button);
            this.buttons[i] = button;

            x += buttonWidth + buttonPadding;
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
        final int width = sketch.width;
        final int height = sketch.height;

        Graphics.drawBubbleBackground(sketch);

        // Draw the white foreground box
        final int curveRadius = 50;
        float boxX = width * 1f / 8;
        float boxWidth = width * 3f / 4;
        float boxY = height / 5f;
        float boxHeight = (height - boxY) * 2 / 3;


        sketch.fill(255);
        Graphics.drawRoundedRectangle(sketch, boxX, boxY, boxWidth, boxHeight, curveRadius);

        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);

        Graphics.useSerif(sketch, 24);
        sketch.text("What Challenge Do you Want to Take On?", boxX + boxWidth / 2, boxY + boxHeight / 5);

        float textY = boxY + boxHeight * 1 / 3;
        Graphics.useSansSerif(sketch, 16, false);
        sketch.text("Everyone asks what you want to be when you grow up.", boxX + boxWidth / 2, textY);
        textY += 18;
        sketch.text("We're asking what challenges you want to take on.", boxX + boxWidth / 2, textY);
        textY += 18;
        sketch.text("What big problem do you want to help solve?", boxX + boxWidth / 2, textY);
        textY += 20;

        Graphics.useSansSerif(sketch, 20, true);
        sketch.text("Ready?", boxX + boxWidth / 2, textY);

//        sketch.rectMode(PConstants.CORNER);
//        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
//
//        sketch.background(this.model.invertedColors ? 255 : 0);
//        sketch.fill(this.model.invertedColors ? 0 : 255);
//
//        FontManager.useSerif(sketch);
//        sketch.text(this.model.question, sketch.width / 2.0f, sketch.height / 4.0f);
//
        for (ButtonControl button : this.buttons) {
            button.drawRectangle(sketch);
        }
//
//        // Penguin
//        final float penguinY = (float) (Math.sin(this.penguinX * 0.1) * 20 + 20);
//        this.penguinImage.rotate(this.penguinRotation);
//        this.penguinImage.draw(sketch, this.penguinX, penguinY);
    }
}
