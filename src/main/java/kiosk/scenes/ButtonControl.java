package kiosk.scenes;

import graphics.Color;
import graphics.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.input.TouchEvent;
import kiosk.EventListener;
import kiosk.InputEvent;
import kiosk.Kiosk;
import kiosk.Settings;
import kiosk.TouchScreenEvent;
import kiosk.models.ButtonModel;
import processing.core.PConstants;
import processing.event.Event;
import processing.event.MouseEvent;

public class ButtonControl implements Control<MouseEvent, TouchEvent> {

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
    private final Map<TouchScreenEvent, EventListener<TouchEvent>> touchEventListeners;
    private Image image;
    private boolean isPressed;
    private boolean wasClicked;
    private float centerSquareSize = 0;
    private boolean disabled = false;
    private boolean shouldAnimate;
    private boolean wasInit = false;
    private boolean initWarningPrinted = false;

    private int centerX;
    private int centerY;
    private int buttonAnimationFrames;
    private double buttonAnimationIntensity;
    private float buttonAnimationLengthFrames;
    private double animationOffsetX;
    private double animationOffsetY;

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
        centerX = x;
        centerY = y;
        this.rect = new Rectangle(x, y, w, h);
        updateRadius(); // Radius only used when button is circle
        this.image = null;
        this.disabled = false;
        this.shouldAnimate = doesAnimate;

        // Read draw constants from settings
        Settings settings = Kiosk.getSettings();
        screenW = settings.screenW;
        screenH = settings.screenH;
        buttonAnimationFrames = settings.buttonAnimationFrames;
        buttonAnimationIntensity = settings.buttonAnimationIntensity;
        buttonAnimationLengthFrames = settings.buttonAnimationLengthFrames;

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
        this.eventListeners.put(InputEvent.MouseDragged, this::onMouseDragged);

        this.touchEventListeners = new HashMap<>();
        this.touchEventListeners.put(TouchScreenEvent.TouchPressed, this::onTouchPressed);
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
        centerX = x;
        centerY = y;
        this.rect = new Rectangle(x, y, radius * 2, radius * 2);
        updateRadius(); // Radius only used when button is circle
        this.image = null;
        this.disabled = false;
        shouldAnimate = doesAnimate;

        // Read draw constants from settings
        Settings settings = Kiosk.getSettings();
        screenW = settings.screenW;
        screenH = settings.screenH;
        buttonAnimationFrames = settings.buttonAnimationFrames;
        buttonAnimationIntensity = settings.buttonAnimationIntensity;
        buttonAnimationLengthFrames = settings.buttonAnimationLengthFrames;

        fontSize = screenW / 75;
        fontSizeOverwritten = false;
        // Negative will make the color darker on click
        colorDeltaOnClick = -25;

        // Constants for home and back button
        buttonWidth = screenW / 8;
        buttonHeight = screenH / 6;
        // Radius of the rounded edge on rectangle buttons
        defaultCornerRadius = buttonHeight / 5;
        buttonPadding = 20;

        this.eventListeners = new HashMap<>();
        this.eventListeners.put(InputEvent.MousePressed, this::onMousePressed);
        this.eventListeners.put(InputEvent.MouseReleased, this::onMouseReleased);
        this.eventListeners.put(InputEvent.MouseDragged, this::onMouseDragged);

        this.touchEventListeners = new HashMap<>();
        this.touchEventListeners.put(TouchScreenEvent.TouchPressed, this::onTouchPressed);
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

    long lastTime = 0;

    /**
     * Draw's the appropriate button to the sketch using
     * coordinates and information provided upon initialization.
     *
     * @param sketch to draw to
     */
    public void draw(Kiosk sketch, double offsetX, double offsetY) {
        animationOffsetX = offsetX;
        animationOffsetY = offsetY;
        draw(sketch);
    }

    /**
     * Draw's the appropriate button to the sketch using
     * coordinates and information provided upon initialization.
     *
     * @param sketch to draw to
     */
    public void draw(Kiosk sketch) {
        // Set the Font
        if (!fontSizeOverwritten) {
            Graphics.useGothic(sketch, fontSize, true);
            checkInit(); // Prints a warning if the button wasn't initialized
            textSizeMultiplier = 1;
        }
        fontSizeOverwritten = false;

        // Draw the shape
        if (!this.model.noButton) {
            if (this.model.isCircle) {
                this.drawCircle(sketch);
            } else {
                this.drawRectangle(sketch);
            }
        } else if (this.model.image != null) { // Draw the image, if it exists
            sketch.imageMode(PConstants.CENTER);
            if (this.isPressed) {
                this.image.draw(sketch, (float) (rect.getCenterX() + animationOffsetX),
                        (float) (rect.getCenterY() + animationOffsetY + this.rect.height / 10.f));
            } else {
                this.image.draw(sketch, (float) (rect.getCenterX()
                        + animationOffsetX), !this.model.noButton
                        ? (float) (rect.getCenterY() + animationOffsetY + this.rect.height / 10.f)
                        : (float) (rect.getCenterY() + animationOffsetY));
            }
        }

        if (isSnapping) {
            long deltaTime = 0;
            if (lastTime != 0) {
                deltaTime = System.currentTimeMillis() - lastTime;
                lastTime = System.currentTimeMillis();
            } else {
                lastTime = System.currentTimeMillis();
            }

            float distX = centerX - this.rect.x;
            float distY = centerY - this.rect.y;
            if (Math.sqrt(distX * distX + distY * distY) < 10) {
                this.isSnapping = false;
                this.isDragged = false;
                this.rect.x = centerX;
                this.rect.y = centerY;
                draggedButtonModel = null;
                this.lastTime = 0;
            } else {
                this.rect.y += (int) (distY * deltaTime / 100f);
                this.rect.x += (int) (distX * deltaTime / 100f);
            }
        }
    }

    /**
     * Draw the button as a rectangle.
     *
     * @param sketch to draw to
     */
    private void drawRectangle(Kiosk sketch) {
        setDrawModifiers(sketch);
        drawOutline(sketch, 1);

        // If pressed, draw the text lower and don't draw the main button
        // This makes it look like the button is pushed into the screen
        if (this.isPressed) {
            drawText(sketch);
            drawImage(sketch);
        } else {
            if (sketch.frameCount % buttonAnimationFrames
                    < buttonAnimationLengthFrames
                    && !this.disabled && this.shouldAnimate) {
                double offset = calculateAnimationOffset(sketch);
                setFill(sketch);
                sketch.stroke(59, 58, 57, 63f);
                Graphics.drawRoundedRectangle(sketch, (float)
                                (this.rect.x + animationOffsetX + this.rect.width / 2.f),
                        (float) (this.rect.y + animationOffsetY + (float) (this.rect.height / 2.f
                                + (this.rect.height / 10.f * offset))),
                        (int) (this.rect.width), (int)
                                (this.rect.height), defaultCornerRadius);
                textWithOutline(this.model.text,
                        (float) (this.rect.getCenterX() + animationOffsetX),
                        (float) (this.rect.getCenterY() + animationOffsetY
                                + (this.rect.height / 10.f * offset)),
                        (float) this.rect.width,
                        (float) this.rect.height,
                        sketch, isLightButton());
                drawImage(sketch, offset);
            } else {
                setNormalFillAndStroke(sketch);
                Graphics.drawRoundedRectangle(sketch, (float)
                                (this.rect.x + animationOffsetX + this.rect.width / 2.f),
                        (float) (this.rect.y + animationOffsetY + this.rect.height / 2.f),
                        this.rect.width, this.rect.height, defaultCornerRadius);
                textWithOutline(this.model.text,
                        (float) (this.rect.getCenterX() + animationOffsetX),
                        (float) (this.rect.getCenterY() + animationOffsetY),
                        (float) this.rect.width,
                        (float) this.rect.height,
                        sketch, isLightButton());
                drawImage(sketch);
            }
        }
    }

    private void drawCircle(Kiosk sketch) {
        setDrawModifiers(sketch);
        drawOutline(sketch, 1);

        // If pressed, draw the text lower and don't draw the main button
        // This makes it look like the button is pushed into the screen
        if (this.isPressed) {
            drawText(sketch);
            drawImage(sketch);
        } else {
            if (sketch.frameCount % buttonAnimationFrames
                    < buttonAnimationLengthFrames
                    && !this.disabled && this.shouldAnimate) {
                double offset = calculateAnimationOffset(sketch);
                setFill(sketch);
                sketch.ellipse((float) (this.rect.x + animationOffsetX + this.rect.width / 2.f),
                        (float) (this.rect.y + animationOffsetY + (float) (this.rect.height / 2.f
                                + (this.rect.height / 10.f * offset))),
                        (float) (this.rect.width),
                        (float) (this.rect.height));
                textWithOutline(this.model.text,
                        (float) (this.rect.getCenterX() + animationOffsetX),
                        (float) (this.rect.getCenterY() + animationOffsetY
                                + (this.rect.height / 10.f * offset)),
                        (float) this.rect.width,
                        (float) this.rect.height,
                        sketch, isLightButton());
                drawImage(sketch, offset);
            } else {
                setNormalFillAndStroke(sketch);
                sketch.ellipse((float) (this.rect.x + animationOffsetX + this.rect.width / 2.f),
                        (float) (this.rect.y + animationOffsetY + this.rect.height / 2.f),
                        (float) (this.rect.width),
                        (float) (this.rect.height));
                textWithOutline(this.model.text,
                        (float) (this.rect.getCenterX() + animationOffsetX),
                        (float) (this.rect.getCenterY() + animationOffsetY),
                        this.rect.width, this.rect.height,
                        sketch, isLightButton());
                drawImage(sketch);
            }
        }
    }

    private void setDrawModifiers(Kiosk sketch) {
        sketch.rectMode(PConstants.CORNER);
        sketch.ellipseMode(PConstants.CENTER);
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
    }

    private void drawOutline(Kiosk sketch, double sizeMultiplier) {
        if (!this.disabled) {
            //Draw the darker button behind the button to add 3D effects
            sketch.fill(clampColor(this.model.rgb[0] + colorDeltaOnClick),
                    clampColor(this.model.rgb[1] + colorDeltaOnClick),
                    clampColor(this.model.rgb[2] + colorDeltaOnClick));
            sketch.stroke(Color.DW_BLACK_RGB[0], Color.DW_BLACK_RGB[1],
                    Color.DW_BLACK_RGB[2], 63f);
            if (model.isCircle) {
                sketch.ellipse((float) (this.rect.x + animationOffsetX + this.rect.width / 2.f),
                        (float) (this.rect.y + animationOffsetY + this.rect.height
                                / 2.f + this.rect.height / 10.f),
                        (int) (this.rect.width * sizeMultiplier),
                        (int) (this.rect.height * sizeMultiplier));
            } else {
                Graphics.drawRoundedRectangle(sketch, (float) (this.rect.x
                                + animationOffsetX + this.rect.width / 2.f),
                        (float) (this.rect.y + animationOffsetY + this.rect.height
                                / 2.f + this.rect.height / 10.f),
                        this.rect.width, this.rect.height, defaultCornerRadius);
            }
        }
    }

    private void drawText(Kiosk sketch) {
        if (!this.model.text.equals("")) {
            textWithOutline(this.model.text,
                    (float) (this.rect.getCenterX() + animationOffsetX),
                    (float) (this.rect.getCenterY() + animationOffsetY + this.rect.height / 10.f),
                    this.rect.width, this.rect.height,
                    sketch, isLightButton());
        }
    }

    private void drawImage(Kiosk sketch) {
        if (this.model.image != null) {
            sketch.imageMode(PConstants.CENTER);
            if (this.isPressed && !this.disabled) {
                this.image.draw(sketch, (float) (rect.getCenterX() + animationOffsetX),
                        (float) (rect.getCenterY() + animationOffsetY + this.rect.height / 10.f));
            } else {
                this.image.draw(sketch, (float) (rect.getCenterX() + animationOffsetX),
                        (float) (rect.getCenterY() + animationOffsetY));
            }
        }
    }

    private void drawImage(Kiosk sketch, double offset) {
        if (this.model.image != null) {
            sketch.imageMode(PConstants.CENTER);
            if (this.isPressed && !this.disabled) {
                this.image.draw(sketch, (float) (rect.getCenterX() + animationOffsetX),
                        (float) (rect.getCenterY() + animationOffsetY + this.rect.height / 10.f));
            } else {
                this.image.draw(sketch, (float) (rect.getCenterX() + animationOffsetX),
                        (float) (rect.getCenterY() + animationOffsetY
                                + (this.rect.height / 10.f * offset)));
            }
        }
    }

    private void setFill(Kiosk sketch) {
        int frameCount = sketch.frameCount;

        // Determine the fill color
        int r;
        int g;
        int b;
        if (frameCount % buttonAnimationFrames
                < (buttonAnimationLengthFrames / 2)
                && !this.disabled && this.shouldAnimate) {
            r = clampColor((int) (this.model.rgb[0] + colorDeltaOnClick
                    * (frameCount % buttonAnimationFrames
                    / buttonAnimationLengthFrames)));
            g = clampColor((int) (this.model.rgb[1] + colorDeltaOnClick
                    * (frameCount % buttonAnimationFrames
                    / buttonAnimationLengthFrames)));
            b = clampColor((int) (this.model.rgb[2] + colorDeltaOnClick
                    * (frameCount % buttonAnimationFrames
                    / buttonAnimationLengthFrames)));
        } else {
            r = clampColor((int) (this.model.rgb[0] + colorDeltaOnClick
                    * ((buttonAnimationLengthFrames
                    - (frameCount
                    % buttonAnimationFrames))
                    / buttonAnimationLengthFrames)));
            g = clampColor((int) (this.model.rgb[1] + colorDeltaOnClick
                    * ((buttonAnimationLengthFrames
                    - (frameCount
                    % buttonAnimationFrames))
                    / buttonAnimationLengthFrames)));
            b = clampColor((int) (this.model.rgb[2] + colorDeltaOnClick
                    * ((buttonAnimationLengthFrames
                    - (frameCount
                    % buttonAnimationFrames))
                    / buttonAnimationLengthFrames)));
        }

        sketch.fill(r, g, b);
    }

    private void setNormalFillAndStroke(Kiosk sketch) {
        sketch.fill(this.model.rgb[0], this.model.rgb[1], this.model.rgb[2]);
        sketch.stroke(59, 58, 57, 63f);
    }

    /**
     * Calculates how much the animation should change a button's height by.
     * Is essentially a parabola to ensure it pulses up and back down at a constant rate
     * @param sketch to draw to
     * @return the percentage difference between the normal value and this frame's value
     */
    private double calculateAnimationOffset(Kiosk sketch) {
        return (8) * ((0 - (sketch.frameCount
                % buttonAnimationFrames)
                * (sketch.frameCount % buttonAnimationFrames)
                / buttonAnimationIntensity) + ((sketch.frameCount
                % buttonAnimationFrames)
                * ((buttonAnimationLengthFrames - 1)
                / buttonAnimationIntensity)));
    }

    private boolean isLightButton() {
        return ((this.model.rgb[0] + this.model.rgb[1] + this.model.rgb[2]) / 3) >= 225;
    }


    // TODO maybe this should be extracted to a graphics class
    private void textWithOutline(String text, float x, float y, float w,
                                 float h, Kiosk sketch, boolean blackTextDesired) {
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

    @Override
    public Map<TouchScreenEvent, EventListener<TouchEvent>> getTouchEventListeners() {
        return this.touchEventListeners;
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

    private static ButtonModel draggedButtonModel;
    private boolean isDragged = false;

    private int pressX;
    private int pressY;
    private int offsetX;
    private int offsetY;
    private boolean isSnapping;

    private double dragDistance(int x, int y) {
        int distX = pressX - x;
        int distY = pressY - y;
        return Math.sqrt(distX * distX + distY * distY);
    }

    private void onMousePressed(MouseEvent event) {
        if (!this.isPressed && this.rect.contains(event.getX(), event.getY())) {
            this.isPressed = true;
            pressX = event.getX();
            pressY = event.getY();
            offsetX = this.rect.x - pressX;
            offsetY = this.rect.y - pressY;
        }
    }

    private void onTouchPressed(TouchEvent touchEvent) {
        int x = (int) touchEvent.getTouchPoint().getX();
        int y = (int) touchEvent.getTouchPoint().getY();
        if (!this.isPressed && this.rect.contains(x, y)) {
            this.isPressed = true;
            pressX = x;
            pressY = y;
            offsetX = this.rect.x - pressX;
            offsetY = this.rect.y - pressY;
        }
    }

    private void onMouseReleased(MouseEvent event) {
        // Mouse was pressed and released inside the button
        if (this.isPressed && this.rect.contains(event.getX(), event.getY())
            && !isDragged) {
            this.wasClicked = true;
            this.isSnapping = false;
            draggedButtonModel = null;
        }
        this.isPressed = false;
        // TODO: Move this into some drag complete logic
        if (this.model.equals(draggedButtonModel)) {
            this.isDragged = false;
            this.isSnapping = true;
        }
    }

    private void onMouseDragged(MouseEvent event) {
        if (dragDistance(event.getX(), event.getY()) > 10
                && this.rect.contains(event.getX(), event.getY())
        ) {
            if (draggedButtonModel == null && this.isPressed) {
                this.isDragged = true;
                draggedButtonModel = this.model;
            }
            if (this.model.equals(draggedButtonModel)) {
                this.rect.x = event.getX() + offsetX;
                this.rect.y = event.getY() + offsetY;
            }
        } else if (draggedButtonModel == this.model) {
            // This case covers when we drag a button a few pixels from the edge.
            // Without this case we have to move very slowly toward  the edge in
            // order to drag the button. With this case, once we have the button
            // confirmed we can move it without checking the bounds.
            this.rect.x = event.getX() + offsetX;
            this.rect.y = event.getY() + offsetY;
        }
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
