package graphics;

import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import kiosk.models.ImageModel;
import kiosk.scenes.ButtonControl;
import processing.core.PConstants;

public class GraphicsUtil {

    // Pull constants from the settings
    private static int screenW = Kiosk.getSettings().screenW;
    private static int screenH = Kiosk.getSettings().screenH;

    // Header
    public static float headerW = screenW * 3f / 4;
    public static float headerH = screenH / 6f;
    public static float headerX = (screenW - headerW) / 2;
    public static float headerY = screenH / 32f;
    private static float headerCenterX = headerX + (headerW / 2);
    private static float headerCenterY = headerY + (headerH / 2);
    private static int headerCurveRadius = (int) (headerH);

    // Header title
    private static int headerTitleFontSize = screenW / 55;
    private static float headerTitleY = headerCenterY - headerTitleFontSize;

    // Header body
    private static int headerBodyFontSize = screenW / 60;
    private static float headerBodyY = headerCenterY + headerBodyFontSize;

    private static int commonButtonWidth = screenW / 8;
    private static int commonButtonHeight = screenH / 8;
    private static int commonButtonPadding = screenH / 20;

    public static float TextRatioEstimate = 1.5f; // 1.7
    public static float InnerOuterCircleRatio = 4.f;

    /**
     * Initialize the back button's ButtonControl model.
     * @param sketch to draw to
     * @return back button control
     */
    public static ButtonControl initializeBackButton(Kiosk sketch) {

        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        commonButtonWidth = screenW / 8;
        commonButtonHeight = screenH / 8;
        commonButtonPadding = screenH / 20;

        ButtonModel backButtonModel = new ButtonModel();
        // Rob fix your checkstyle
        backButtonModel.text = "← Back";
        backButtonModel.rgb = Color.DW_BLACK_RGB;
        return new ButtonControl(backButtonModel,
                commonButtonPadding, sketch.height
                - (commonButtonHeight * 3 / 4) - commonButtonPadding,
                commonButtonWidth * 3 / 4, commonButtonHeight * 3 / 4, false);
    }

    /**
     * Initialize the home button's ButtonControl model.
     * @return home button control
     */
    public static ButtonControl initializeHomeButton() {

        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        commonButtonWidth = screenW / 8;
        commonButtonHeight = screenH / 8;
        commonButtonPadding = screenH / 20;

        ButtonModel homeButtonModel = new ButtonModel();
        homeButtonModel.text = "◄◄ Restart";
        homeButtonModel.rgb = Color.DW_MAROON_RGB;
        return new ButtonControl(homeButtonModel,
                commonButtonPadding, commonButtonPadding,
                commonButtonWidth * 3 / 4, commonButtonHeight * 3 / 4, false);
    }

    /**
     * Initialize the next button's ButtonControl model.
     * @param sketch to create on.
     * @return next button control
     */
    public static ButtonControl initializeNextButton(Kiosk sketch) {

        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        commonButtonWidth = screenW / 8;
        commonButtonHeight = screenH / 8;
        commonButtonPadding = screenH / 20;

        ButtonModel nextButtonModel = new ButtonModel();
        nextButtonModel.text = "Go! →";
        nextButtonModel.rgb = Color.DW_GREEN_RGB;
        return new ButtonControl(nextButtonModel,
                sketch.width - commonButtonPadding - commonButtonWidth * 3 / 4,
                sketch.height - (commonButtonHeight * 3 / 4) - commonButtonPadding,
                commonButtonWidth * 3 / 4, commonButtonHeight * 3 / 4);
    }

    /**
     * Initialize the MSOE button's ButtonControl model.
     * @param sketch to create on.
     * @return MSOE button control
     */
    public static ButtonControl initializeMsoeButton(Kiosk sketch) {

        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        commonButtonWidth = screenW / 8;
        commonButtonHeight = screenH / 8;
        commonButtonPadding = screenH / 20;

        ButtonModel msoeButtonModel = new ButtonModel();
        msoeButtonModel.image = new ImageModel("assets/MSOE-U-BK_RD.png", 723 / 6, 883 / 6);
        ButtonControl msoeButton = new ButtonControl(msoeButtonModel,
                sketch.width - (commonButtonWidth * 3 / 4) - commonButtonPadding,
                sketch.height - (commonButtonHeight * 5 / 4) - commonButtonPadding,
                commonButtonWidth * 3 / 4, commonButtonWidth * 3 / 4, false);
        msoeButton.setNoButton(true);
        msoeButton.init(sketch);
        return msoeButton;
    }

    /**
     * Draws the header.
     * @param sketch to draw to
     * @param title text
     * @param body text
     */
    public static void drawHeader(Kiosk sketch, String title, String body) {

        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        headerW = screenW * 3f / 4;
        headerH = screenH / 6f;
        headerX = (screenW - headerW) / 2;
        headerY = screenH / 32f;
        headerCenterX = headerX + (headerW / 2);
        headerCenterY = headerY + (headerH / 2);
        headerCurveRadius = (int) (headerH);

        // Header title
        headerTitleFontSize = screenW / 55;
        headerTitleY = headerCenterY - headerTitleFontSize;

        // Header body
        headerBodyFontSize = screenW / 60;
        headerBodyY = headerCenterY + headerBodyFontSize;

        // Draw the white header box
        sketch.fill(255);
        sketch.stroke(255);

        Graphics.drawRoundedRectangle(sketch,
                headerX + headerW / 2, headerY + headerH / 2,
                headerW, headerH, headerCurveRadius);

        // Draw the title and body
        sketch.fill(0);
        sketch.stroke(0);

        Graphics.useGothic(sketch, headerTitleFontSize, true);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(title, headerCenterX, headerTitleY,
                (int) (headerW * 0.95), headerH / 2);

        Graphics.useGothic(sketch, headerBodyFontSize, false);
        sketch.rectMode(PConstants.CENTER);
        sketch.text(body, headerCenterX,
                (int) (headerBodyY * 1.15), (int) (headerW * 0.95), headerH / 2);
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
