package graphics;

import kiosk.Kiosk;
import kiosk.models.ButtonModel;
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
     * Create home button.
     * @param sketch to draw to
     * @return back button control
     */
    public static ButtonControl initializeBackButton(Kiosk sketch) {
        ButtonModel backButtonModel = new ButtonModel();
        // Rob fix your checkstyle
        backButtonModel.text = "\uD83E\uDC78 Back";
        backButtonModel.rgb = Color.DW_BLACK_RGB;
        return new ButtonControl(backButtonModel,
                COMMON_BUTTON_PADDING, sketch.height
                - (COMMON_BUTTON_HEIGHT * 3 / 4) - COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
    }

    /**
     * Create home button.
     * @return back button control
     */
    public static ButtonControl initializeHomeButton() {
        ButtonModel homeButtonModel = new ButtonModel();
        homeButtonModel.text = "⭯ Restart";
        homeButtonModel.rgb = Color.DW_MAROON_RGB;
        ButtonControl homeButton = new ButtonControl(homeButtonModel,
                COMMON_BUTTON_PADDING, COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        return homeButton;
    }

    /**
     * Create next button.
     * @param sketch to create on.
     * @return next button
     */
    public static ButtonControl initializeNextButton(Kiosk sketch) {
        ButtonModel nextButtonModel = new ButtonModel();
        nextButtonModel.text = "Go! \uD83E\uDC7A";
        nextButtonModel.rgb = Color.DW_GREEN_RGB;
        ButtonControl nextButton = new ButtonControl(nextButtonModel,
                sketch.width - COMMON_BUTTON_PADDING - COMMON_BUTTON_WIDTH * 3 / 4,
                sketch.height - (COMMON_BUTTON_HEIGHT * 3 / 4) - COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        return nextButton;
    }

    public static void drawHeader(Kiosk sketch, String title, String body) {
        // Draw the white header box
        sketch.fill(255);
        sketch.stroke(255);

        Graphics.drawRoundedRectangle(sketch,
                HEADER_X, HEADER_Y, HEADER_W, HEADER_H, HEADER_CURVE_RADIUS);

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
}
