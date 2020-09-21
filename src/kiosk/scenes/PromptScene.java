package kiosk.scenes;

import java.awt.Point;
import java.awt.Rectangle;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.EmptySceneModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.SceneModel;
import processing.core.PConstants;
import processing.event.MouseEvent;


public class PromptScene implements Scene {

    private PromptSceneModel model;
    private final Button[] buttons;

    private boolean selectionMade = false;
    private SceneModel selectionSceneModel = new EmptySceneModel();

    public PromptScene(PromptSceneModel model) {
        this.model = model;
        this.buttons = new Button[this.model.answers.length];
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
            Rectangle rect = new Rectangle(x, y, buttonWidth, buttonHeight);

            this.buttons[i] = new Button(model, rect);
            x += buttonWidth + buttonPadding;
        }

        // Attach mouse click callback
        sketch.addMouseReleasedCallback(arg -> this.mouseClicked((MouseEvent) arg));
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        if (this.selectionMade) {
            sceneGraph.pushScene(this.selectionSceneModel);
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

        for (Button button : this.buttons) {
            // Draw button
            sketch.fill(this.model.invertedColors ? 0 : 255);
            sketch.stroke(this.model.invertedColors ? 0 : 255);
            sketch.rect(button.rect.x, button.rect.y, button.rect.width, button.rect.height);
            // Draw button text
            sketch.fill(this.model.invertedColors ? 255 : 0);
            sketch.text(button.model.text,
                    (float) button.rect.getCenterX(), (float) button.rect.getCenterY());
        }
    }

    private void mouseClicked(MouseEvent event) {
        Point point = new Point(event.getX(), event.getY());

        for (Button button : this.buttons) {
            if (button.rect.contains(point)) {
                this.selectionMade = true;
                this.selectionSceneModel = button.model.target;
                break;
            }
        }
    }

    private static class Button {

        public ButtonModel model;
        public Rectangle rect;

        public Button(ButtonModel model, Rectangle rect) {
            this.model = model;
            this.rect = rect;
        }
    }
}
