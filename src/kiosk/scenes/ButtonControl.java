package kiosk.scenes;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import kiosk.EventListener;
import kiosk.InputEvent;
import kiosk.models.ButtonModel;
import kiosk.models.SceneModel;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class ButtonControl implements Control {

    private final ButtonModel model;
    private final Rectangle rect;
    private final Map<InputEvent, EventListener> eventListeners;
    private boolean isPressed;
    private boolean wasClicked;

    /**
     * Button UI control. Visual representation of a ButtonModel.
     * @param model with button data
     * @param x of the top-right corner
     * @param y of the top-right corner
     * @param w width
     * @param h height
     */
    public ButtonControl(ButtonModel model, int x, int y, int w, int h) {
        this.model = model;
        this.rect = new Rectangle(x, y, w, h);

        this.eventListeners = new HashMap<>();
        this.eventListeners.put(InputEvent.MousePressed,
            arg -> this.onMousePressed((MouseEvent) arg));
        this.eventListeners.put(InputEvent.MouseReleased,
            arg -> this.onMouseReleased((MouseEvent) arg));
    }

    /**
     * Draw the button as a rectangle.
     * @param sketch to draw to
     */
    public void drawRectangle(PApplet sketch) {
        // TODO: Set the font
        sketch.rectMode(PConstants.CORNER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        // Draw button
        sketch.fill(0);
        sketch.stroke(0);
        sketch.rect(this.rect.x, this.rect.y, this.rect.width, this.rect.height);

        // Draw text
        sketch.fill(255);
        sketch.text(this.model.text,
                (float) this.rect.getCenterX(),
                (float) this.rect.getCenterY());
    }

    public Map<InputEvent, EventListener> getEventListeners() {
        return this.eventListeners;
    }

    /**
     * Provides the value of the wasClicked flag.
     * A click is when the user presses and releases a mouse click inside
     * the button's rectangle.
     * IMPORTANT NOTE: The flag is reset when this method is invoked.
     * @return true if button was just clicked
     */
    public boolean wasClicked() {
        boolean temp = this.wasClicked;

        // Reset the flag so the caller won't think the user
        // clicked the same button twice if wasClicked() is called
        // in a subsequent update tick.
        this.wasClicked = false;
        return temp;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public SceneModel getTarget() {
        return this.model.target;
    }

    private void onMousePressed(MouseEvent event) {
        if (this.rect.contains(event.getX(), event.getY())) {
            this.isPressed = true;
        }
    }

    private void onMouseReleased(MouseEvent event) {
        // Mouse was pressed and released inside the button
        if (this.isPressed && this.rect.contains(event.getX(), event.getY())) {
            this.wasClicked = true;
        }
        this.isPressed = false;
    }
}