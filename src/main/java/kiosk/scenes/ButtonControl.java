package kiosk.scenes;

import graphics.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import kiosk.EventListener;
import kiosk.InputEvent;
import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class ButtonControl implements Control<MouseEvent> {

    private static final int FONT_SIZE = 20;
    // Radius of the rounded edge on rectangle buttons
    private static final int RADIUS = 20;
    // Negative will make the color darker on click
    private static final int COLOR_DELTA_ON_CLICK = -25;

    private final ButtonModel model;
    private final Rectangle rect;
    private final Map<InputEvent, EventListener<MouseEvent>> eventListeners;
    private Image image;
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
        this.image = null;

        this.eventListeners = new HashMap<>();
        this.eventListeners.put(InputEvent.MousePressed, this::onMousePressed);
        this.eventListeners.put(InputEvent.MouseReleased, this::onMouseReleased);
    }

    /**
     * Initialize the button for loading images.
     * @param sketch to load images to
     */
    public void init(Kiosk sketch) {
        if (this.model.image != null) {
            this.image = Image.createImage(sketch, model.image);
        }
    }

    /**
     * Draw's the appropriate button to the sketch using
     * coordinates and information provided upon initialization.
     * @param sketch to draw to
     */
    public void draw(Kiosk sketch) {
        if (this.model.isCircle) {
            this.drawCircle(sketch);
        } else {
            this.drawRectangle(sketch);
        }
        if (this.model.image != null) {
            sketch.imageMode(PConstants.CENTER);
            this.image.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());
        }
    }

    /**
     * Draw the button as a rectangle.
     * @param sketch to draw to
     */
    private void drawRectangle(Kiosk sketch) {
        // Draw modifiers
        sketch.rectMode(PConstants.CORNER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        // Set the color and draw the shape
        if (this.isPressed) {
            int r = clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK);
            int g = clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK);
            int b = clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK);

            sketch.fill(r, g, b);
            sketch.stroke(r, g, b);
        } else {
            sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            sketch.stroke(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
        }
        Graphics.drawRoundedRectangle(sketch, this.rect.x, this.rect.y,
                this.rect.width, this.rect.height, RADIUS);

        // Draw text
        sketch.fill(255);
        sketch.stroke(255);
        Graphics.useSansSerifBold(sketch, FONT_SIZE);
        sketch.text(this.model.text,
                (float) this.rect.getCenterX(),
                (float) this.rect.getCenterY());
    }

    private void drawCircle(Kiosk sketch) {
        // Draw modifiers
        sketch.ellipseMode(PConstants.CORNER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        // Set the color and draw the shape
        if (this.isPressed) {
            int r = clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK);
            int g = clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK);
            int b = clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK);

            sketch.fill(r, g, b);
            sketch.stroke(r, g, b);
        } else {
            sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            sketch.stroke(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
        }
        sketch.ellipse(this.rect.x, this.rect.y, this.rect.width, this.rect.height);

        // Draw text
        sketch.fill(255);
        sketch.stroke(255);
        Graphics.useSansSerifBold(sketch, FONT_SIZE);
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

    public ButtonModel getModel() {
        return this.model;
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

    private static int clampColor(int c) {
        return Math.max(Math.min(c, 255), 0);
    }
}