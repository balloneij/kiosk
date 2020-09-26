package kiosk.scenes;

import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.EmptySceneModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.SceneModel;
import processing.core.PConstants;


public class PromptScene implements Scene {

    private PromptSceneModel model;
    private final ButtonControl[] buttons;

    private boolean selectionMade = false;
    private SceneModel selectionSceneModel = new EmptySceneModel();

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
        // TODO: Set the font
        sketch.rectMode(PConstants.CORNER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        sketch.background(this.model.invertedColors ? 255 : 0);
        sketch.fill(this.model.invertedColors ? 0 : 255);
        sketch.text(this.model.question, sketch.width / 2.0f, sketch.height / 4.0f);

        for (ButtonControl button : this.buttons) {
            button.drawRectangle(sketch);
        }
    }
}
