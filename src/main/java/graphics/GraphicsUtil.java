package graphics;

import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import kiosk.scenes.ButtonControl;

public class GraphicsUtil {

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
}
