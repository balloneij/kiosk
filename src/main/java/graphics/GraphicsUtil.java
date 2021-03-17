package graphics;

import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import kiosk.models.ImageModel;
import kiosk.scenes.ButtonControl;

public class GraphicsUtil {

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
        ButtonControl backButton = new ButtonControl(backButtonModel,
                COMMON_BUTTON_PADDING, sketch.height
                - (COMMON_BUTTON_HEIGHT * 3 / 4) - COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        return backButton;
    }

    /**
     * Initialize the home button's ButtonControl model.
     * @return home button control
     */
    public static ButtonControl initializeHomeButton() {
        ButtonModel homeButtonModel = new ButtonModel();
        homeButtonModel.text = "◄◄ Restart";
        homeButtonModel.rgb = Color.DW_MAROON_RGB;
        ButtonControl homeButton = new ButtonControl(homeButtonModel,
                COMMON_BUTTON_PADDING, COMMON_BUTTON_PADDING,
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_HEIGHT * 3 / 4);
        return homeButton;
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
                COMMON_BUTTON_WIDTH * 3 / 4, COMMON_BUTTON_WIDTH * 3 / 4);
        msoeButton.setNoButton(true);
        //TODO If we make some type of credits scene, I imagine that this button takes you there.
        //     Then this should be enabled and lead to a new scene.
        msoeButton.setDisabled(true);
        return msoeButton;
    }
}