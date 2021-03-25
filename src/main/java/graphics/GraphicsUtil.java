package graphics;

import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import kiosk.models.ImageModel;
import kiosk.scenes.ButtonControl;
import processing.core.PConstants;

public class GraphicsUtil {

    // Pull constants from the settings
    private static final int SCREEN_W = Kiosk.getSettings().screenW;
    private static final int SCREEN_H = Kiosk.getSettings().screenH;

    // Header
    public static final float HEADER_W = SCREEN_W * 3f / 4;
    public static final float HEADER_H = SCREEN_H / 6f;
    public static final float HEADER_X = (SCREEN_W - HEADER_W) / 2;
    public static final float HEADER_Y = SCREEN_H / 32f;
    private static final float HEADER_CENTER_X = HEADER_X + (HEADER_W / 2);
    private static final float HEADER_CENTER_Y = HEADER_Y + (HEADER_H / 2);
    private static final int HEADER_CURVE_RADIUS = 25;

    // Header title
    private static final int HEADER_TITLE_FONT_SIZE = 24;
    private static final float HEADER_TITLE_Y = HEADER_CENTER_Y - HEADER_TITLE_FONT_SIZE;

    // Header body
    private static final int HEADER_BODY_FONT_SIZE = 16;
    private static final float HEADER_BODY_Y = HEADER_CENTER_Y + HEADER_BODY_FONT_SIZE;

    private static final int COMMON_BUTTON_WIDTH = Kiosk.getSettings().screenW / 8;
    private static final int COMMON_BUTTON_HEIGHT = Kiosk.getSettings().screenH / 8;
    private static final int COMMON_BUTTON_PADDING = 20;

    public static final float TextRatioEstimate = 1.5f; // 1.7
    public static final float InnerOuterCircleRatio = 4.f;

    /**
     * Initialize the back button's ButtonControl model.
     * @param sketch to draw to
     * @return back button control
     */
    public static ButtonControl initializeBackButton(Kiosk sketch) {
        ButtonModel backButtonModel = new ButtonModel();
        // Rob fix your checkstyle
        backButtonModel.text = "← Back";
        backButtonModel.rgb = Color.DW_BLACK_RGB;
        return new ButtonControl(backButtonModel,
                COMMON_BUTTON_PADDING, sketch.height
                - (COMMON_BUTTON_HEIGHT * 3 / 4) - COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4, false);
    }

    /**
     * Initialize the home button's ButtonControl model.
     * @return home button control
     */
    public static ButtonControl initializeHomeButton() {
        ButtonModel homeButtonModel = new ButtonModel();
        homeButtonModel.text = "◄◄ Restart";
        homeButtonModel.rgb = Color.DW_MAROON_RGB;
        return new ButtonControl(homeButtonModel,
                COMMON_BUTTON_PADDING, COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4, false);
    }

    /**
     * Initialize the next button's ButtonControl model.
     * @param sketch to create on.
     * @return next button control
     */
    public static ButtonControl initializeNextButton(Kiosk sketch) {
        ButtonModel nextButtonModel = new ButtonModel();
        nextButtonModel.text = "Go! →";
        nextButtonModel.rgb = Color.DW_GREEN_RGB;
        ButtonControl nextButton = new ButtonControl(nextButtonModel,
                sketch.width - COMMON_BUTTON_PADDING - COMMON_BUTTON_WIDTH * 3 / 4,
                sketch.height - (COMMON_BUTTON_HEIGHT * 3 / 4) - COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        return nextButton;
    }

    /**
     * Initialize the MSOE button's ButtonControl model.
     * @param sketch to create on.
     * @return MSOE button control
     */
    public static ButtonControl initializeMsoeButton(Kiosk sketch) {
        ButtonModel msoeButtonModel = new ButtonModel();
        msoeButtonModel.image = new ImageModel("assets/MSOE-U-BK_RD.png", 723 / 6, 883 / 6);
        ButtonControl msoeButton = new ButtonControl(msoeButtonModel,
                sketch.width - COMMON_BUTTON_PADDING * 2 - (723 / 6),
                sketch.height - COMMON_BUTTON_PADDING - (883 / 6),
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_WIDTH * 3 / 4, false);
        msoeButton.setNoButton(true);
        return msoeButton;
    }

    /**
     * Draws the header.
     * @param sketch to draw to
     * @param title text
     * @param body text
     */
    public static void drawHeader(Kiosk sketch, String title, String body) {
        // Draw the white header box
        sketch.fill(255);
        sketch.stroke(255);

        Graphics.drawRoundedRectangle(sketch,
                HEADER_X + HEADER_W / 2, HEADER_Y + HEADER_H / 2,
                HEADER_W, HEADER_H, HEADER_CURVE_RADIUS);

        // Draw the title and body
        sketch.fill(0);
        sketch.stroke(0);

        Graphics.useGothic(sketch, HEADER_TITLE_FONT_SIZE, true);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(title, HEADER_CENTER_X, HEADER_TITLE_Y,
                (int) (HEADER_W * 0.95), HEADER_H / 2);

        Graphics.useGothic(sketch, HEADER_BODY_FONT_SIZE, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(body, HEADER_CENTER_X,
                (int) (HEADER_BODY_Y * 1.15), (int) (HEADER_W * 0.95), HEADER_H / 2);
    }

    /**
     * Draw a text with an black outline.
     * @param text to write
     * @param x location
     * @param y location
     * @param w width bound
     * @param h height bound
     * @param sketch to draw to
     */
    public static void textWithOutline(String text,
                                       float x, float y, float w, float h, Kiosk sketch) {
        // Draw multiple copies of the text shifted by a few pixels to create the outline
        sketch.fill(0, 0, 0);
        for (int delta = -1; delta < 2; delta++) {
            sketch.text(text, x + delta, y, w, h);
            sketch.text(text, x, y + delta, w, h);
        }

        // Draw the text
        sketch.fill(255);
        sketch.text(text, x, y, w, h);
    }
}
