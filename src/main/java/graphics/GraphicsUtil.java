package graphics;

import java.util.Arrays;

import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import kiosk.scenes.ButtonControl;
import processing.core.PConstants;

public class GraphicsUtil {

    private static final int COMMON_BUTTON_WIDTH = Kiosk.getSettings().screenW / 8;
    private static final int COMMON_BUTTON_HEIGHT = Kiosk.getSettings().screenH / 8;
    private static final int COMMON_BUTTON_PADDING = 20;

    public static final float TextRatioEstimate = 1.5f; // 1.7
    public static final float InnerOuterCircleRatio = 4.f;

    public static ButtonControl initializeBackButton(Kiosk sketch) {
        var backButtonModel = new ButtonModel();
        backButtonModel.text = "\uD83E\uDC78 Back";
        backButtonModel.rgb = Color.DW_BLACK_RGB;
        ButtonControl backButton = new ButtonControl(backButtonModel,
                COMMON_BUTTON_PADDING, sketch.height - (COMMON_BUTTON_HEIGHT * 3 / 4) - COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        return backButton;
    }

    public static ButtonControl initializeHomeButton() {
        var homeButtonModel = new ButtonModel();
        homeButtonModel.text = "⭯ Restart";
        homeButtonModel.rgb = Color.DW_MAROON_RGB;
        ButtonControl homeButton = new ButtonControl(homeButtonModel,
                COMMON_BUTTON_PADDING, COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        return homeButton;
    }

    public static ButtonControl initializeNextButton(Kiosk sketch) {
        var nextButtonModel = new ButtonModel();
        nextButtonModel.text = "Go! \uD83E\uDC7A";
        nextButtonModel.rgb = Color.DW_GREEN_RGB;
        ButtonControl nextButton = new ButtonControl(nextButtonModel,
                sketch.width - COMMON_BUTTON_PADDING - COMMON_BUTTON_WIDTH * 3 / 4,
                sketch.height - (COMMON_BUTTON_HEIGHT * 3 / 4) - COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        return nextButton;
    }
}
