package kiosk.scenes;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import kiosk.EventListener;
import kiosk.Graphics;
import kiosk.InputEvent;
import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class ButtonControl implements Control<MouseEvent> {

    public static final int FONT_SIZE = 20;
    private static boolean FONT_SIZE_OVERWRITTEN = false;
    // Radius of the rounded edge on rectangle buttons
    private static final int RADIUS = 20;
    // Negative will make the color darker on click
    private static final int COLOR_DELTA_ON_CLICK = -25;
    private static float TEXT_SIZE_MULTIPLIER = 1;

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

    public void setColor(int r, int g, int b) {
        if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
            this.model.rgb[0] = r;
            this.model.rgb[1] = g;
            this.model.rgb[2] = b;
        }
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
        if (!FONT_SIZE_OVERWRITTEN) {
            Graphics.useGothic(sketch, FONT_SIZE, true);
            TEXT_SIZE_MULTIPLIER = 1;
        }
        FONT_SIZE_OVERWRITTEN = false;
        if (this.model.isCircle) {
            this.drawCircle(sketch);
        } else {
            this.drawRectangle(sketch);
        }
        if (this.model.image != null) {
            sketch.imageMode(PConstants.CENTER);
            if (this.isPressed) {
                this.image.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY() + this.rect.height / 10.f);
            } else {
                this.image.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());
            }
        }
    }

    public void draw(Kiosk sketch, float multiplier) {
        Graphics.useGothic(sketch, (int) (FONT_SIZE * multiplier), true);
        FONT_SIZE_OVERWRITTEN = true;
        TEXT_SIZE_MULTIPLIER = multiplier;
        draw(sketch);
    }

    /**
     * Draw the button as a rectangle.
     * @param sketch to draw to
     */
    private void drawRectangle(Kiosk sketch) {
        // Draw modifiers
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        //Draw the darker button behind the button to add 3D effects
        sketch.fill(clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK),
                clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK),
                clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK));
        sketch.stroke(59, 58, 57, 63f);
//        sketch.stroke(clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK),
//                clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK),
//                clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK));
        Graphics.drawRoundedRectangle(sketch, this.rect.x, this.rect.y + this.rect.height / 10.f,
                this.rect.width, this.rect.height, RADIUS);

        // Set the color and draw the shape for when the button is clicked or not clicked
        if (this.isPressed) {
            int r = clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK);
            int g = clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK);
            int b = clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK);

            sketch.fill(r, g, b);
            sketch.stroke(59, 58, 57, 63f);
//            sketch.stroke(r, g, b);
            Graphics.drawRoundedRectangle(sketch, this.rect.x, this.rect.y + this.rect.height / 10.f,
                    this.rect.width, this.rect.height, RADIUS);

            // Draw the text, including the text outline
            sketch.fill(0);
            sketch.stroke(0);
            for (int x = -1; x < 2; x++) {
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f) + x,
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f) + this.rect.height / 10.f,
                        (float) this.rect.width,
                        (float) this.rect.height);
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f),
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f) + x + this.rect.height / 10.f,
                        (float) this.rect.width,
                        (float) this.rect.height);
            }
            sketch.fill(255 + COLOR_DELTA_ON_CLICK);
            sketch.stroke(255 + COLOR_DELTA_ON_CLICK);
            sketch.text(this.model.text,
                    (float) this.rect.getCenterX() - (this.rect.width / 2.f),
                    (float) this.rect.getCenterY() - (this.rect.height / 2.f) + this.rect.height / 10.f,
                    (float) this.rect.width,
                    (float) this.rect.height);
        } else {
            sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            sketch.stroke(59, 58, 57, 63f);
//            sketch.stroke(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            Graphics.drawRoundedRectangle(sketch, this.rect.x, this.rect.y,
                    this.rect.width, this.rect.height, RADIUS);

            // Draw text, including the text outline
            sketch.fill(0);
            sketch.stroke(0);
            for (int x = -1; x < 2; x++) {
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f) + x,
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f),
                        (float) this.rect.width,
                        (float) this.rect.height);
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f),
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f) + x,
                        (float) this.rect.width,
                        (float) this.rect.height);
            }
            sketch.fill(255);
            sketch.stroke(255);
            sketch.text(this.model.text,
                    (float) this.rect.getCenterX() - (this.rect.width / 2.f),
                    (float) this.rect.getCenterY() - (this.rect.height / 2.f),
                    (float) this.rect.width,
                    (float) this.rect.height);
        }
    }

    private void drawCircle(Kiosk sketch) {
        // Draw modifiers
        sketch.ellipseMode(PConstants.CORNER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        //Draw the darker button behind the button to add 3D effects
        sketch.fill(clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK),
                clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK),
                clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK));
        sketch.stroke(59, 58, 57, 63f);
//        sketch.stroke(clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK),
//                clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK),
//                clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK));
        sketch.ellipse(this.rect.x, this.rect.y + this.rect.height / 10.f,
                this.rect.width, this.rect.height);

        // Set the color and draw the shape
        if (this.isPressed) {
            int r = clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK);
            int g = clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK);
            int b = clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK);

            sketch.fill(r, g, b);
            sketch.stroke(59, 58, 57, 63f);
//            sketch.stroke(r, g, b);
            sketch.ellipse(this.rect.x, this.rect.y + this.rect.height / 10.f, this.rect.width, this.rect.height);

            // Draw the text, including the text outline
            sketch.fill(59, 58, 57);
            sketch.stroke(59, 58, 57);
            for (int x = -1; x < 2; x++) {
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f) + x,
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f) + this.rect.height / 10.f,
                        (float) this.rect.width,
                        (float) this.rect.height);
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f),
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f) + x + this.rect.height / 10.f,
                        (float) this.rect.width,
                        (float) this.rect.height);
            }
            sketch.fill(255 + COLOR_DELTA_ON_CLICK);
            sketch.stroke(255 + COLOR_DELTA_ON_CLICK);
            sketch.text(this.model.text,
                    (float) this.rect.getCenterX() - (this.rect.width / 2.f),
                    (float) this.rect.getCenterY() - (this.rect.height / 2.f) + this.rect.height / 10.f,
                    (float) this.rect.width,
                    (float) this.rect.height);
        } else {
            sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            sketch.stroke(59, 58, 57, 63f);
//            sketch.stroke(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            sketch.ellipse(this.rect.x, this.rect.y, this.rect.width, this.rect.height);

            // Draw text, including the text outline
            sketch.fill(59, 58, 57);
            sketch.stroke(59, 58, 57);
            for (int x = -1; x < 2; x++) {
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f) + x,
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f),
                        (float) this.rect.width,
                        (float) this.rect.height);
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f),
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f) + x,
                        (float) this.rect.width,
                        (float) this.rect.height);
            }
            sketch.fill(255);
            sketch.stroke(255);
            sketch.text(this.model.text,
                    (float) this.rect.getCenterX() - (this.rect.width / 2.f),
                    (float) this.rect.getCenterY() - (this.rect.height / 2.f),
                    (float) this.rect.width,
                    (float) this.rect.height);
        }
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