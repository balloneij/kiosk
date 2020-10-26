package kiosk.scenes;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import kiosk.*;
import kiosk.models.ButtonModel;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class ButtonControl implements Control<MouseEvent> {

    private final ButtonModel model;
    private final Rectangle rect;
    private final Map<InputEvent, EventListener<MouseEvent>> eventListeners;
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
        this.eventListeners.put(InputEvent.MousePressed, this::onMousePressed);
        this.eventListeners.put(InputEvent.MouseReleased, this::onMouseReleased);
    }

    /**
     * Draw the button as a rectangle.
     * @param sketch to draw to
     */
    public void drawRectangle(Kiosk sketch) {
        // Constants
        final int buttonRadius = 10;
        final int textSize = 20;
        final boolean textBold = true;

        // Draw modifiers
        sketch.rectMode(PConstants.CORNER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        // Draw rectangle
        if (this.isPressed) {
            sketch.fill(100, 168, 71);
        } else {
            sketch.fill(112, 191, 76);
        }
        Graphics.drawRoundedRectangle(sketch, this.rect.x, this.rect.y,
                this.rect.width, this.rect.height, buttonRadius);

        // Draw text
        sketch.fill(255);
        Graphics.useSansSerif(sketch, textSize, textBold);
        sketch.text(this.model.text,
                (float) this.rect.getCenterX(),
                (float) this.rect.getCenterY());
    }


    public Map<InputEvent, EventListener<MouseEvent>> getEventListeners() {
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

    public String getTarget() {
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