package kiosk.scenes;

import graphics.Color;
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

    private static final int SCREEN_H = Kiosk.getSettings().screenH;

    private static final int FONT_SIZE = 16;
    private static boolean FONT_SIZE_OVERWRITTEN = false;
    // Radius of the rounded edge on rectangle buttons
    private static final int DEFAULT_RADIUS = 20;
    // Negative will make the color darker on click
    private static final int COLOR_DELTA_ON_CLICK = -25;
    private static float TEXT_SIZE_MULTIPLIER = 1;

    // Constants for home and back button
    private static final int BUTTON_WIDTH = Kiosk.getSettings().screenW / 8;
    private static final int BUTTON_HEIGHT = Kiosk.getSettings().screenH / 6;
    private static final int BUTTON_PADDING = 20;

    private final ButtonModel model;
    private final Rectangle rect;
    private final int radius;
    private final Map<InputEvent, EventListener<MouseEvent>> eventListeners;
    private Image image;
    private boolean isPressed;
    private boolean wasClicked;
    private boolean isClickable;
    private float centerSquareSize = 0;
    private boolean disabled = false;

    /**
     * Button UI control. Visual representation of a ButtonModel.
     *
     * @param model with button data
     * @param x     of the top-right corner
     * @param y     of the top-right corner
     * @param w     width
     * @param h     height
     */
    public ButtonControl(ButtonModel model, int x, int y, int w, int h) {
        this.model = model;
        this.rect = new Rectangle(x, y, w, h);
        this.radius = DEFAULT_RADIUS;
        this.image = null;
        this.isClickable = true;
        this.centerSquareSize = (float) Math.sqrt(Math.pow(Math.min(w, h) * 2, 2) / 2);

        this.eventListeners = new HashMap<>();
        this.eventListeners.put(InputEvent.MousePressed, this::onMousePressed);
        this.eventListeners.put(InputEvent.MouseReleased, this::onMouseReleased);
    }

    /**
     * Button UI control. Visual representation of a ButtonModel.
     *
     * @param model  with button data
     * @param x      of the top-right corner
     * @param y      of the top-right corner
     * @param w      width
     * @param h      height
     * @param radius radius (in circular case)
     */
    public ButtonControl(ButtonModel model, int x, int y, int w, int h, int radius) {
        this.model = model;
        this.rect = new Rectangle(x, y, w, h);
        this.radius = radius;
        this.image = null;
        this.isClickable = true;

        // The text has to fit inside the largest square possible inside the circle
        // so we're using the Pythagorean theorem to get the sides of the square, and
        // the diameter is the hypotenuse.
        // Additionally, we're using the diameter of the minimumButtonRadius in
        // order to keep all the font sizes consistent
        // Images must fit inside this circle too
        this.centerSquareSize = (float) Math.sqrt(Math.pow(radius * 2, 2) / 2);

        this.eventListeners = new HashMap<>();
        this.eventListeners.put(InputEvent.MousePressed, this::onMousePressed);
        this.eventListeners.put(InputEvent.MouseReleased, this::onMouseReleased);
    }

    /**
     * Initialize the button for loading images.
     *
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
     *
     * @param sketch to draw to
     */
    public void draw(Kiosk sketch) {
        if (!FONT_SIZE_OVERWRITTEN) {
            Graphics.useGothic(sketch, FONT_SIZE, true);
            TEXT_SIZE_MULTIPLIER = 1;
        }
        FONT_SIZE_OVERWRITTEN = false;
        if (this.model.isCircle) {
            this.drawCircle(sketch, 1);
        } else {
            this.drawRectangle(sketch, 1);
        }
        if (this.model.image != null) {
            sketch.imageMode(PConstants.CENTER);
            if (this.isPressed) {
                this.image.draw(sketch, (float) rect.getCenterX(),
                        (float) rect.getCenterY() + this.rect.height / 10.f);
            } else {
                this.image.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());
            }
        }
    }

    /**
     * Draw the button.
     * @param sketch to draw to
     * @param multiplier to change the font's size for this drawing only
     */
    public void draw(Kiosk sketch, float multiplier) {
        Graphics.useGothic(sketch, (int) (FONT_SIZE * multiplier), true);
        FONT_SIZE_OVERWRITTEN = true;
        TEXT_SIZE_MULTIPLIER = multiplier;
        draw(sketch);
    }

    public void draw(Kiosk sketch, boolean isClickable) {
        this.setClickable(isClickable);
        draw(sketch);
    }

    /**
     * Draw the button.
     * @param sketch to draw to
     * @param sizeMultiplier to change the button's overall size, for animation purposes
     */
    public void draw(Kiosk sketch, double sizeMultiplier) {
        Graphics.useGothic(sketch, (int) (FONT_SIZE * sizeMultiplier), true);
        TEXT_SIZE_MULTIPLIER = (float) sizeMultiplier;
        if (this.model.isCircle) {
            this.drawCircle(sketch, sizeMultiplier);
        } else {
            this.drawRectangle(sketch, sizeMultiplier);
        }
        if (this.model.image != null) {
            sketch.imageMode(PConstants.CENTER);
            if (this.isPressed) {
                this.image.draw(sketch, (float) rect.getCenterX(),
                        (float) rect.getCenterY() + this.rect.height / 10.f);
            } else {
                this.image.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());
            }
        }
    }

    /**
     * Draw the button as a rectangle.
     *
     * @param sketch to draw to
     */
    private void drawRectangle(Kiosk sketch, double sizeMultiplier) {
        //TODO MAKE HOME & BACK BUTTONS NOT ANIMATED
        // Draw modifiers
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        if (this.isClickable) {
            //Draw the darker button behind the button to add 3D effects
            sketch.fill(clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK),
                    clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK),
                    clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK));
            sketch.stroke(59, 58, 57, 63f);
            if (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames < Kiosk.getSettings().buttonAnimationLengthFrames) {
                double offset = ((0 - (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) / (850.0)) + ((sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) * ((Kiosk.getSettings().buttonAnimationLengthFrames - 1) / 850.0))); //TODO MOVE THE VARIABLE 850 TO SETTINGS
                Graphics.drawRoundedRectangle(sketch, this.rect.x + this.rect.width / 2.f,
                        this.rect.y + this.rect.height / 2.f + this.rect.height / 10.f,
                        (int) (this.rect.width * sizeMultiplier * (1 + offset)),
                        (int) (this.rect.height * sizeMultiplier * (1 + offset)),
                        (int) (DEFAULT_RADIUS * sizeMultiplier));
            } else {
                Graphics.drawRoundedRectangle(sketch, this.rect.x + this.rect.width / 2.f,
                        this.rect.y + this.rect.height / 2.f + this.rect.height / 10.f,
                        (int) (this.rect.width * sizeMultiplier),
                        (int) (this.rect.height * sizeMultiplier),
                        (int) (DEFAULT_RADIUS * sizeMultiplier));
            }
        }

        // Set the color and draw the shape for when the button is clicked or not clicked
        if (this.isPressed && this.isClickable) {
            int r = clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK);
            int g = clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK);
            int b = clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK);

            sketch.fill(r, g, b);
            sketch.stroke(59, 58, 57, 63f);
            Graphics.drawRoundedRectangle(sketch, this.rect.x + this.rect.width / 2.f,
                    this.rect.y + this.rect.height / 2.f + this.rect.height / 10.f,
                    (int) (this.rect.width * sizeMultiplier),
                    (int) (this.rect.height * sizeMultiplier),
                    (int) (DEFAULT_RADIUS * sizeMultiplier));

            // Draw the text, including the text outline
            //TODO ANIMATE TEXT TO MOVE AS WELL
            sketch.fill(0);
            sketch.stroke(0);
            for (int x = -1; x < 2; x++) {
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() + (this.rect.width / 2.f) - (this.rect.width / 2.f) + x,
                        (float) this.rect.getCenterY() + (this.rect.height / 2.f)
                                - (this.rect.height / 2.f)
                                + this.rect.height / 10.f,
                        (float) this.rect.width,
                        (float) this.rect.height);
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() + (this.rect.width / 2.f) - (this.rect.width / 2.f),
                        (float) this.rect.getCenterY() + (this.rect.height / 2.f)
                                - (this.rect.height / 2.f)
                                + x + this.rect.height / 10.f,
                        (float) this.rect.width,
                        (float) this.rect.height);
            }
            sketch.fill(255 + COLOR_DELTA_ON_CLICK);
            sketch.stroke(255 + COLOR_DELTA_ON_CLICK);
            sketch.text(this.model.text,
                    (float) this.rect.getCenterX() + (this.rect.width / 2.f) - (this.rect.width / 2.f),
                    (float) this.rect.getCenterY() + (this.rect.height / 2.f)
                            - (this.rect.height / 2.f)
                            + this.rect.height / 10.f,
                    (float) this.rect.width,
                    (float) this.rect.height);
        } else {
            sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            sketch.stroke(59, 58, 57, 63f);
            //Every 110 frames, play an animation lasting 10 frames
            if (isClickable && sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames < Kiosk.getSettings().buttonAnimationLengthFrames) {
                double offset = ((0 - (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) / (850.0)) + ((sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) * ((Kiosk.getSettings().buttonAnimationLengthFrames - 1) / 850.0))); //TODO MOVE THE VARIABLE 850 TO SETTINGS
                Graphics.drawRoundedRectangle(sketch, this.rect.x + this.rect.width / 2.f, this.rect.y + this.rect.height / 2.f,
                        (int) (this.rect.width * sizeMultiplier * (1 + offset)),
                        (int) (this.rect.height * sizeMultiplier * (1 + offset)),
                        (int) (DEFAULT_RADIUS * sizeMultiplier));
            } else {
                Graphics.drawRoundedRectangle(sketch, this.rect.x + this.rect.width / 2.f, this.rect.y + this.rect.height / 2.f,
                        (int) (this.rect.width * sizeMultiplier),
                        (int) (this.rect.height * sizeMultiplier),
                        (int) (DEFAULT_RADIUS * sizeMultiplier));
            }

            // Draw text, including the text outline
            sketch.fill(0);
            sketch.stroke(0);
            for (int x = -1; x < 2; x++) {
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() + (this.rect.width / 2.f) - (this.rect.width / 2.f) + x,
                        (float) this.rect.getCenterY() + (this.rect.height / 2.f) - (this.rect.height / 2.f),
                        (float) this.rect.width,
                        (float) this.rect.height);
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() + (this.rect.width / 2.f) - (this.rect.width / 2.f),
                        (float) this.rect.getCenterY() + (this.rect.height / 2.f) - (this.rect.height / 2.f) + x,
                        (float) this.rect.width,
                        (float) this.rect.height);
            }
            sketch.fill(255);
            sketch.stroke(255);
            sketch.text(this.model.text,
                    (float) this.rect.getCenterX() + (this.rect.width / 2.f) - (this.rect.width / 2.f),
                    (float) this.rect.getCenterY() + (this.rect.height / 2.f) - (this.rect.height / 2.f),
                    (float) this.rect.width,
                    (float) this.rect.height);
        }
    }

    private void drawCircle(Kiosk sketch, double sizeMultiplier) {
        //TODO MAKE SPOKE GRAPHS NOT-CLICKABLE BUTTONS NOT ANIMATED
        // Draw modifiers
        sketch.rectMode(PConstants.CORNER);
        sketch.ellipseMode(PConstants.CENTER);
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        if (this.isClickable && !this.disabled) {
            //Draw the darker button behind the button to add 3D effects
            sketch.fill(clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK),
                    clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK),
                    clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK));
            sketch.stroke(59, 58, 57, 63f);
            if (isClickable && sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames < Kiosk.getSettings().buttonAnimationLengthFrames) {
                double offset = ((0 - (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) / (850.0)) + ((sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) * ((Kiosk.getSettings().buttonAnimationLengthFrames - 1) / 850.0))); //TODO MOVE THE VARIABLE 850 TO SETTINGS
                sketch.ellipse(this.rect.x + this.rect.width / 2.f,
                        this.rect.y + this.rect.height / 2.f + this.rect.height / 10.f,
                        (int) (this.rect.width * sizeMultiplier * (1 + offset)),
                        (int) (this.rect.height * sizeMultiplier * (1 + offset)));
            } else {
                sketch.ellipse(this.rect.x + this.rect.width / 2.f,
                        this.rect.y + this.rect.height / 2.f + this.rect.height / 10.f,
                        (int) (this.rect.width * sizeMultiplier),
                        (int) (this.rect.height * sizeMultiplier));
            }
        }

        // Set the color and draw the shape
        sketch.rectMode(PConstants.CENTER);

        if (this.isPressed && this.isClickable) {
            int r = clampColor(this.model.rgb[0] + COLOR_DELTA_ON_CLICK);
            int g = clampColor(this.model.rgb[1] + COLOR_DELTA_ON_CLICK);
            int b = clampColor(this.model.rgb[2] + COLOR_DELTA_ON_CLICK);

            sketch.fill(r, g, b);
            sketch.stroke(59, 58, 57, 63f);
            sketch.ellipse(this.rect.x + this.rect.width / 2.f, this.rect.y + this.rect.height / 2.f + this.rect.height / 10.f,
                    (float) (this.rect.width * sizeMultiplier), (float) (this.rect.height * sizeMultiplier));

            // Draw the text, including the text outline
            sketch.fill(59, 58, 57);
            sketch.stroke(59, 58, 57);

            if (!this.model.text.isBlank()) {
                for (int x = -1; x < 2; x++) {
                    sketch.text(this.model.text,
                            (float) this.rect.getCenterX() - (this.rect.width / 2.f)  + (this.rect.width / 2.f) + x,
                            (float) this.rect.getCenterY() - (this.rect.height / 2.f)  + (this.rect.height / 2.f) + this.rect.height / 10.f,
                            centerSquareSize,
                            centerSquareSize);
                    sketch.text(this.model.text,
                            (float) this.rect.getCenterX() - (this.rect.width / 2.f)  + (this.rect.width / 2.f),
                            (float) this.rect.getCenterY() - (this.rect.height / 2.f)  + (this.rect.height / 2.f) + x + this.rect.height / 10.f,
                            centerSquareSize,
                            centerSquareSize);
                }
                sketch.fill(255 + COLOR_DELTA_ON_CLICK);
                sketch.stroke(255 + COLOR_DELTA_ON_CLICK);
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f)  + (this.rect.width / 2.f),
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f)  + (this.rect.height / 2.f) + this.rect.height / 10.f,
                        centerSquareSize,
                        centerSquareSize);
            }
        } else {
            sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            sketch.stroke(59, 58, 57, 63f);
            if (isClickable && sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames < Kiosk.getSettings().buttonAnimationLengthFrames) {
                double offset = ((0 - (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) / (850.0)) + ((sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames) * ((Kiosk.getSettings().buttonAnimationLengthFrames - 1) / 850.0))); //TODO MOVE THE VARIABLE 850 SETTINGS
                sketch.ellipse(this.rect.x + this.rect.width / 2.f,
                        this.rect.y + this.rect.height / 2.f,
                        (float) (this.rect.width * sizeMultiplier * (1 + offset)),
                        (float) (this.rect.height * sizeMultiplier * (1 + offset)));
            } else {
                sketch.ellipse(this.rect.x + this.rect.width / 2.f,
                        this.rect.y + this.rect.height / 2.f,
                        (float) (this.rect.width * sizeMultiplier),
                        (float) (this.rect.height * sizeMultiplier));
            }

            // Draw text
            sketch.fill(255);
            sketch.stroke(255);
            Graphics.useSansSerifBold(sketch, FONT_SIZE);
            if (!this.model.text.isBlank()) {
                // Draw text, including the text outline
                sketch.fill(59, 58, 57);
                sketch.stroke(59, 58, 57);
                for (int x = -1; x < 2; x++) {
                    sketch.text(this.model.text,
                            (float) this.rect.getCenterX() - (this.rect.width / 2.f)  + (this.rect.width / 2.f) + x,
                            (float) this.rect.getCenterY() - (this.rect.height / 2.f)  + (this.rect.height / 2.f),
                            centerSquareSize,
                            centerSquareSize);
                    sketch.text(this.model.text,
                            (float) this.rect.getCenterX() - (this.rect.width / 2.f)  + (this.rect.width / 2.f),
                            (float) this.rect.getCenterY() - (this.rect.height / 2.f)  + (this.rect.height / 2.f) + x,
                            centerSquareSize,
                            centerSquareSize);
                }
                sketch.fill(255);
                sketch.stroke(255);
                sketch.text(this.model.text,
                        (float) this.rect.getCenterX() - (this.rect.width / 2.f)  + (this.rect.width / 2.f),
                        (float) this.rect.getCenterY() - (this.rect.height / 2.f)  + (this.rect.height / 2.f),
                        centerSquareSize,
                        centerSquareSize);
            }
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
        return temp && this.isClickable;
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

    /**
     * Moves the button to the specified Coordinates.
     * @param x The X Location from the left of the screen.
     * @param y The Y Location from the top of the screen.
     */
    public void setLocation(int x, int y) {
        this.rect.x = x;
        this.rect.y = y;
    }

    /**
     * By default, button models are clickable. You can turn that off here.
     * @param isClickable True if we can click it, false if not.
     */
    public void setClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    /**
     * Sets the width and the height of the button for rendering purposes.
     * This is a stop sign.
     * @param width The width of the button. (This is a stop sign)
     * @param height The height of the button. (This is a stop sign)
     */
    public void setWidthAndHeight(int width, int height) {
        this.rect.width = width;
        this.rect.height = height;
    }

    public float getCenterX() {
        return (float) this.rect.getCenterX();
    }

    public float getCenterY() {
        return (float) this.rect.getCenterY();
    }

    private static int clampColor(int c) {
        return Math.max(Math.min(c, 255), 0);
    }

    /**
     * Create ButtonControl representing the home button.
     * @return ButtonControl in the position of the model
     */
    public static ButtonControl createHomeButton() {
        var homeButtonModel = new ButtonModel();
        homeButtonModel.text = "Home";
        homeButtonModel.rgb = Color.DW_BLACK_RGB;
        return new ButtonControl(homeButtonModel,
                BUTTON_PADDING, BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, BUTTON_HEIGHT * 3 / 4);
    }

    /**
     * Create ButtonControl representing the back button.
     * @return ButtonControl in the position of the model
     */
    public static ButtonControl createBackButton() {
        var backButtonModel = new ButtonModel();
        backButtonModel.text = "Back";
        backButtonModel.rgb = Color.DW_BLACK_RGB;
        return new ButtonControl(backButtonModel,
                BUTTON_PADDING, SCREEN_H - (BUTTON_HEIGHT * 3 / 4) - BUTTON_PADDING,
                BUTTON_WIDTH * 3 / 4, BUTTON_HEIGHT * 3 / 4);
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}