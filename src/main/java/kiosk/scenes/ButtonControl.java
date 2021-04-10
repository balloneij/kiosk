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
import kiosk.models.ImageModel;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class ButtonControl implements Control<MouseEvent> {

    private int screenW = Kiosk.getSettings().screenW;
    private int screenH = Kiosk.getSettings().screenH;

    private int fontSize = screenW / 75;
    private boolean fontSizeOverwritten = false;
    // Negative will make the color darker on click
    private int colorDeltaOnClick = -25;

    // Constants for home and back button
    private int buttonWidth = Kiosk.getSettings().screenW / 8;
    private int buttonHeight = Kiosk.getSettings().screenH / 6;
    // Radius of the rounded edge on rectangle buttons
    private int defaultCornerRadius = buttonHeight / 5;
    private int buttonPadding = 20;

    private float textSizeMultiplier = 1;
    private final ButtonModel model;
    private final Rectangle rect;
    private int radius;
    private final Map<InputEvent, EventListener<MouseEvent>> eventListeners;
    private Image image;
    private boolean isPressed;
    private boolean wasClicked;
    private float centerSquareSize = 0;
    private boolean disabled = false;
    private boolean shouldAnimate;
    private boolean wasInit = false;
    private boolean initWarningPrinted = false;

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
        this(model, x, y, w, h, true);
    }

    /**
     * Button UI control. Visual representation of a ButtonModel.
     *
     * @param model with button data
     * @param x     of the top-right corner
     * @param y     of the top-right corner
     * @param w     width
     * @param h     height
     */
    public ButtonControl(ButtonModel model, int x, int y, int w, int h, boolean doesAnimate) {
        this.model = model;
        this.rect = new Rectangle(x, y, w, h);
        updateRadius(); // Radius only used when button is circle
        this.image = null;
        this.disabled = false;
        this.shouldAnimate = doesAnimate;

        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        fontSize = screenW / 75;
        fontSizeOverwritten = false;
        // Negative will make the color darker on click
        colorDeltaOnClick = -25;

        // Constants for home and back button
        buttonWidth = Kiosk.getSettings().screenW / 8;
        buttonHeight = Kiosk.getSettings().screenH / 6;
        // Radius of the rounded edge on rectangle buttons
        defaultCornerRadius = buttonHeight / 5;
        buttonPadding = 20;

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
     * @param radius radius (in circular case)
     */
    public ButtonControl(ButtonModel model, int x, int y, int radius) {
        this(model, x, y, radius, true);
    }

    /**
     * Button UI control. Visual representation of a ButtonModel.
     *
     * @param model  with button data
     * @param x      of the top-right corner
     * @param y      of the top-right corner
     * @param radius radius (in circular case)
     */
    public ButtonControl(ButtonModel model, int x, int y, int radius, boolean doesAnimate) {
        this.model = model;
        this.rect = new Rectangle(x, y, radius * 2, radius * 2);
        updateRadius(); // Radius only used when button is circle
        this.image = null;
        this.disabled = false;
        shouldAnimate = doesAnimate;

        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        fontSize = screenW / 75;
        fontSizeOverwritten = false;
        // Negative will make the color darker on click
        colorDeltaOnClick = -25;

        // Constants for home and back button
        buttonWidth = Kiosk.getSettings().screenW / 8;
        buttonHeight = Kiosk.getSettings().screenH / 6;
        // Radius of the rounded edge on rectangle buttons
        defaultCornerRadius = buttonHeight / 5;
        buttonPadding = 20;

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
        wasInit = true;
    }

    /**
     * Draw's the appropriate button to the sketch using
     * coordinates and information provided upon initialization.
     *
     * @param sketch to draw to
     */
    public void draw(Kiosk sketch) {
        if (!fontSizeOverwritten) {
            Graphics.useGothic(sketch, fontSize, true);
            checkInit(); // Prints a warning if the button wasn't initialized
            textSizeMultiplier = 1;
        }
        fontSizeOverwritten = false;
        if (!this.model.noButton) {
            if (this.model.isCircle) {
                this.drawCircle(sketch, 1);
            } else {
                this.drawRectangle(sketch, 1);
            }
        } else {
            if (this.model.image != null) {
                sketch.imageMode(PConstants.CENTER);
                if (this.isPressed && !this.disabled) {
                    this.image.draw(sketch, (float) rect.getCenterX(),
                            (float) rect.getCenterY() + this.rect.height / 10.f);
                } else {
                    this.image.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());
                }
            }
        }
    }

    /**
     * Draw the button.
     * @param sketch to draw to
     * @param multiplier to change the font's size for this drawing only
     */
    public void draw(Kiosk sketch, float multiplier) {
        Graphics.useGothic(sketch, (int) (fontSize * multiplier), true);
        fontSizeOverwritten = true;
        textSizeMultiplier = multiplier;
        draw(sketch);
    }

    public void draw(Kiosk sketch, boolean isClickable) {
        this.setDisabled(!isClickable);
        draw(sketch);
    }

    /**
     * Draw the button.
     * @param sketch to draw to
     * @param sizeMultiplier to change the button's overall size, for animation purposes
     */
    public void draw(Kiosk sketch, double sizeMultiplier) {
        Graphics.useGothic(sketch, (int) (fontSize * sizeMultiplier), true);
        checkInit(); // Prints a warning if the button wasn't initialized
        textSizeMultiplier = (float) sizeMultiplier;
        if (!this.model.noButton) {
            if (this.model.isCircle) {
                this.drawCircle(sketch, sizeMultiplier);
            } else {
                this.drawRectangle(sketch, sizeMultiplier);
            }
        } else {
            if (this.model.image != null) {
                sketch.imageMode(PConstants.CENTER);
                if (this.isPressed && !this.disabled) {
                    this.image.draw(sketch, (float) rect.getCenterX(),
                            (float) rect.getCenterY() + this.rect.height / 10.f);
                } else {
                    this.image.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());
                }
            }
        }
    }

    /**
     * Draw the button as a rectangle.
     *
     * @param sketch to draw to
     */
    private void drawRectangle(Kiosk sketch, double sizeMultiplier) {
        // Draw modifiers
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        // If it's clickable, draw the darker button behind the main one to add 3D effect
        if (!this.disabled) {

            //Draw a shadow behind the clickable button to add 3D effects
            sketch.imageMode(PConstants.CENTER);
            ImageModel imgModel = new ImageModel("assets/shadow.png",
                    this.rect.width * 2,
                    this.rect.width * 2);
            Image img = Image.createImage(sketch, imgModel);
            img.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());

            //Draw the darker button behind the button to add 3D effects
            sketch.fill(clampColor(this.model.rgb[0] + colorDeltaOnClick),
                    clampColor(this.model.rgb[1] + colorDeltaOnClick),
                    clampColor(this.model.rgb[2] + colorDeltaOnClick));
            sketch.stroke(Color.DW_BLACK_RGB[0], Color.DW_BLACK_RGB[1],
                    Color.DW_BLACK_RGB[2], 63f);
            Graphics.drawRoundedRectangle(sketch, this.rect.x + this.rect.width / 2.f,
                    this.rect.y + this.rect.height / 2.f + this.rect.height / 10.f,
                    this.rect.width, this.rect.height, defaultCornerRadius);
        }

        // If pressed, draw the text lower and don't draw the main button
        // This makes it look like the button is pushed into the screen
        if (this.isPressed) {
            textWithOutline(this.model.text,
                    (float) this.rect.getCenterX(),
                    (float) this.rect.getCenterY()
                            + this.rect.height / 10.f,
                    (float) this.rect.width,
                    (float) this.rect.height,
                    sketch, isLightButton());
            if (this.model.image != null) {
                sketch.imageMode(PConstants.CENTER);
                if (this.isPressed && !this.disabled) {
                    this.image.draw(sketch, (float) rect.getCenterX(),
                            (float) rect.getCenterY() + this.rect.height / 10.f);
                } else {
                    this.image.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());
                }
            }
        } else {
            if (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                    < Kiosk.getSettings().buttonAnimationLengthFrames
                    && !this.disabled && this.shouldAnimate) {
                double offset = calculateAnimationOffset(sketch);
                if (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                        < (Kiosk.getSettings().buttonAnimationLengthFrames / 2)
                        && !this.disabled && this.shouldAnimate) {
                    sketch.fill((clampColor((int) (this.model.rgb[0] + colorDeltaOnClick
                            * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames)))),
                            (clampColor((int) (this.model.rgb[1] + colorDeltaOnClick
                                    * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames)))),
                            (clampColor((int) (this.model.rgb[2] + colorDeltaOnClick
                                    * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames)))));
                } else {
                    sketch.fill(clampColor((int) (this.model.rgb[0] + colorDeltaOnClick
                                    * ((Kiosk.getSettings().buttonAnimationLengthFrames
                                    - (sketch.frameCount
                                    % Kiosk.getSettings().buttonAnimationFrames))
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames))),
                            (clampColor((int) (this.model.rgb[1] + colorDeltaOnClick
                                    * ((Kiosk.getSettings().buttonAnimationLengthFrames
                                    - (sketch.frameCount
                                    % Kiosk.getSettings().buttonAnimationFrames))
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames)))),
                            (clampColor((int) (this.model.rgb[2] + colorDeltaOnClick
                                    * ((Kiosk.getSettings().buttonAnimationLengthFrames
                                    - (sketch.frameCount
                                    % Kiosk.getSettings().buttonAnimationFrames))
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames)))));
                }
                sketch.stroke(59, 58, 57, 63f);
                Graphics.drawRoundedRectangle(sketch, this.rect.x + this.rect.width / 2.f,
                        this.rect.y + (float) (this.rect.height / 2.f
                                + (this.rect.height / 10.f * offset)),
                        (int) (this.rect.width), (int)
                                (this.rect.height), defaultCornerRadius);
                textWithOutline(this.model.text,
                        (float) this.rect.getCenterX(),
                        (float) (this.rect.getCenterY() + (this.rect.height / 10.f * offset)),
                        (float) this.rect.width,
                        (float) this.rect.height,
                        sketch, isLightButton());
                if (this.model.image != null) {
                    sketch.imageMode(PConstants.CENTER);
                    if (this.isPressed && !this.disabled) {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                            (float) rect.getCenterY() + this.rect.height / 10.f);
                    } else {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                            (float) (this.rect.getCenterY() + (this.rect.height / 10.f * offset)));
                    }
                }
            } else {
                sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
                sketch.stroke(59, 58, 57, 63f);
                Graphics.drawRoundedRectangle(sketch, this.rect.x + this.rect.width / 2.f,
                        this.rect.y + this.rect.height / 2.f,
                        this.rect.width, this.rect.height, defaultCornerRadius);
                textWithOutline(this.model.text,
                        (float) this.rect.getCenterX(),
                        (float) this.rect.getCenterY(),
                        (float) this.rect.width,
                        (float) this.rect.height,
                        sketch, isLightButton());
                if (this.model.image != null) {
                    sketch.imageMode(PConstants.CENTER);
                    if (this.isPressed && !this.disabled) {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                            (float) rect.getCenterY() + this.rect.height / 10.f);
                    } else {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                            (float) rect.getCenterY());
                    }
                }
            }
        }
    }

    private void drawCircle(Kiosk sketch, double sizeMultiplier) {
        // Draw modifiers
        sketch.rectMode(PConstants.CORNER);
        sketch.ellipseMode(PConstants.CENTER);
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);

        // If it's clickable, draw the darker button behind the main one to add 3D effect
        if (!disabled) {

            //Draw a shadow behind the clickable button to add 3D effects
            sketch.imageMode(PConstants.CENTER);
            ImageModel imgModel = new ImageModel("assets/shadow.png",
                    this.rect.width * 2,
                    this.rect.width * 2);
            Image img = Image.createImage(sketch, imgModel);
            img.draw(sketch, (float) rect.getCenterX(), (float) rect.getCenterY());

            sketch.fill(clampColor(this.model.rgb[0] + colorDeltaOnClick),
                    clampColor(this.model.rgb[1] + colorDeltaOnClick),
                    clampColor(this.model.rgb[2] + colorDeltaOnClick));
            sketch.stroke(59, 58, 57, 63f);
            sketch.ellipse(this.rect.x + this.rect.width / 2.f,
                    this.rect.y + this.rect.height / 2.f + this.rect.height / 10.f,
                    (int) (this.rect.width * sizeMultiplier),
                    (int) (this.rect.height * sizeMultiplier));
        }

        // If pressed, draw the text lower and don't draw the main button
        // This makes it look like the button is pushed into the screen
        if (this.isPressed) {
            if (!this.model.text.equals("")) {
                textWithOutline(this.model.text,
                        (float) this.rect.getCenterX(),
                        (float) this.rect.getCenterY() + this.rect.height / 10.f,
                        this.rect.width, this.rect.height,
                        sketch, isLightButton());
                if (this.model.image != null) {
                    sketch.imageMode(PConstants.CENTER);
                    if (this.isPressed && !this.disabled) {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                                (float) rect.getCenterY() + this.rect.height / 10.f);
                    } else {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                                (float) (rect.getCenterY()));
                    }
                }
            }
        } else {
            sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
            sketch.stroke(59, 58, 57, 63f);
            if (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                    < Kiosk.getSettings().buttonAnimationLengthFrames
                    && !this.disabled && this.shouldAnimate) {
                double offset = calculateAnimationOffset(sketch);
                if (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                        < (Kiosk.getSettings().buttonAnimationLengthFrames / 2)
                        && !this.disabled && this.shouldAnimate) {
                    sketch.fill(clampColor((int) (this.model.rgb[0] + colorDeltaOnClick
                            * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames))),
                            clampColor((int) (this.model.rgb[1] + colorDeltaOnClick
                                    * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames))),
                            clampColor((int) (this.model.rgb[2] + colorDeltaOnClick
                                    * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames))));
                } else {
                    sketch.fill(clampColor((int) (this.model.rgb[0] + colorDeltaOnClick
                                    * ((Kiosk.getSettings().buttonAnimationLengthFrames
                                    - (sketch.frameCount
                                    % Kiosk.getSettings().buttonAnimationFrames))
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames))),
                            clampColor((int) (this.model.rgb[1] + colorDeltaOnClick
                                    * ((Kiosk.getSettings().buttonAnimationLengthFrames
                                    - (sketch.frameCount
                                    % Kiosk.getSettings().buttonAnimationFrames))
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames))),
                            clampColor((int) (this.model.rgb[2] + colorDeltaOnClick
                                    * ((Kiosk.getSettings().buttonAnimationLengthFrames
                                    - (sketch.frameCount
                                    % Kiosk.getSettings().buttonAnimationFrames))
                            / (float) Kiosk.getSettings().buttonAnimationLengthFrames))));
                }
                sketch.ellipse(this.rect.x + this.rect.width / 2.f,
                        this.rect.y + (float) (this.rect.height / 2.f
                                + (this.rect.height / 10.f * offset)),
                        (float) (this.rect.width * sizeMultiplier),
                        (float) (this.rect.height * sizeMultiplier));
                textWithOutline(this.model.text,
                        (float) this.rect.getCenterX(),
                        (float) (this.rect.getCenterY() + (this.rect.height / 10.f * offset)),
                        (float) this.rect.width,
                        (float) this.rect.height,
                        sketch, isLightButton());
                if (this.model.image != null) {
                    sketch.imageMode(PConstants.CENTER);
                    if (this.isPressed && !this.disabled) {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                                (float) (rect.getCenterY() + this.rect.height / 10.f));
                    } else {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                                (float) (rect.getCenterY() + (this.rect.height / 10.f * offset)));
                    }
                }
            } else {
                sketch.ellipse(this.rect.x + this.rect.width / 2.f,
                        this.rect.y + this.rect.height / 2.f,
                        (float) (this.rect.width * sizeMultiplier),
                        (float) (this.rect.height * sizeMultiplier));
                textWithOutline(this.model.text,
                        (float) this.rect.getCenterX(),
                        (float) this.rect.getCenterY(),
                        this.rect.width, this.rect.height,
                        sketch, isLightButton());
                if (this.model.image != null) {
                    sketch.imageMode(PConstants.CENTER);
                    if (this.isPressed && !this.disabled) {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                                (float) rect.getCenterY() + this.rect.height / 10.f);
                    } else {
                        this.image.draw(sketch, (float) rect.getCenterX(),
                                (float) (rect.getCenterY()));
                    }
                }
            }
        }
    }

    /**
     * Calculates how much the animation should change a button's height by.
     * Is essentially a parabola to ensure it pulses up and back down at a constant rate
     * @param sketch to draw to
     * @return the percentage difference between the normal value and this frame's value
     */
    private double calculateAnimationOffset(Kiosk sketch) {
        return (8) * ((0 - (sketch.frameCount
                % Kiosk.getSettings().buttonAnimationFrames)
                * (sketch.frameCount % Kiosk.getSettings().buttonAnimationFrames)
                / Kiosk.getSettings().buttonAnimationIntensity) + ((sketch.frameCount
                % Kiosk.getSettings().buttonAnimationFrames)
                * ((Kiosk.getSettings().buttonAnimationLengthFrames - 1)
                / Kiosk.getSettings().buttonAnimationIntensity)));
    }

    private boolean isLightButton() {
        return ((this.model.rgb[0] + this.model.rgb[1] + this.model.rgb[2]) / 3) >= 225;
    }

    // TODO maybe this should be extracted to a graphics class
    private void textWithOutline(String text, float x, float y, float w, float h, Kiosk sketch, boolean blackTextDesired) {
        if (blackTextDesired) {
            // Draw multiple copies of the text shifted by a few pixels to create the outline
            sketch.fill(Color.DW_WHITE_RGB[0], Color.DW_WHITE_RGB[1], Color.DW_WHITE_RGB[2]);
            for (int delta = -1; delta < 2; delta++) {
                sketch.text(text, x + delta, y, w, h);
                sketch.text(text, x, y + delta, w, h);
            }

            // Draw the text
            sketch.fill(Color.DW_BLACK_RGB[0], Color.DW_BLACK_RGB[1], Color.DW_BLACK_RGB[2]);
            sketch.text(text, x, y, w, h);
        } else {
            // Draw multiple copies of the text shifted by a few pixels to create the outline
            sketch.fill(Color.DW_BLACK_RGB[0], Color.DW_BLACK_RGB[1], Color.DW_BLACK_RGB[2]);
            for (int delta = -1; delta < 2; delta++) {
                sketch.text(text, x + delta, y, w, h);
                sketch.text(text, x, y + delta, w, h);
            }

            // Draw the text
            sketch.fill(Color.DW_WHITE_RGB[0], Color.DW_WHITE_RGB[1], Color.DW_WHITE_RGB[2]);
            sketch.text(text, x, y, w, h);
        }
    }

    public void setNoButton(boolean isButton) {
        model.noButton = isButton;
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
        return temp && !this.disabled;
    }

    public String getTarget() {
        return this.model.target;
    }

    public ButtonModel getModel() {
        return this.model;
    }

    public void setWidth(int width) {
        this.rect.width = width;
        updateRadius();
    }

    public void setHeight(int height) {
        this.rect.height = height;
        updateRadius();
    }

    // Helper method for updating the radius and calculating new centerSquareSize
    private void updateRadius() {
        this.radius = Math.min(this.rect.width / 2, this.rect.height / 2);

        // The text has to fit inside the largest square possible inside the circle
        // so we're using the Pythagorean theorem to get the sides of the square, and
        // the diameter is the hypotenuse.
        // Additionally, we're using the diameter of the minimumButtonRadius in
        // order to keep all the font sizes consistent
        // Images must fit inside this circle too
        this.centerSquareSize = (float) Math.sqrt(Math.pow(this.radius * 2, 2) / 2);
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

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Prints a warning & stack trace if the button has not been initialized. Meant to be called
     * in the draw method.
     */
    private void checkInit() {
        // Print warning if button was not init and warning hasn't ben printed
        if (!wasInit && !initWarningPrinted) {
            initWarningPrinted = true;
            throw new IllegalStateException("Button was not init! Call ButtonControl.init() "
                + "in the init method of the scene!");
        }
    }
}