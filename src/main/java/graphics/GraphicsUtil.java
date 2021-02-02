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

    /**
     * Draws a new spoke graph. Draws a large circle in the middle with text and smaller circles
     * on the outside with the options in each one.
     * @param sketch The graphics context used to draw the spoke graph.
     * @param size The side length of the square the graph will fit into.
     * @param x The x location of the upper left-hand corner.
     * @param y The y location of the upper left-hand corner.
     * @param padding The gap space between each outer circle.
     * @param centerText The text that appears in the center circle.
     * @param options The text that appears in each outer circle.
     */
    public static void spokeGraph(Kiosk sketch, float size, float x, float y, float padding,
          String centerText, String[] options, int[] colors) {
        var weights = new int[options.length];
        Arrays.fill(weights, 1);
        spokeGraph(sketch, size, x, y, padding, centerText, options, weights, colors);
    }

    /**
     * Draws a new spoke graph. Draws a large circle in the middle with text and smaller circles
     * on the outside with the options in each one.
     * @param sketch The graphics context used to draw the spoke graph.
     * @param size The side length of the square the graph will fit into.
     * @param x The x location of the upper left-hand corner.
     * @param y The y location of the upper left-hand corner.
     * @param padding The gap space between each outer circle.
     * @param centerText The text that appears in the center circle.
     * @param options The text that appears in each outer circle.
     * @param weights The relative ratio and weight of each option.
     */
    public static void spokeGraph(Kiosk sketch, float size, float x, float y, float padding,
            String centerText, String[] options, int[] weights, int[] colors) {
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        var centerX = x + size / 2.f;
        var centerY = y + size / 2.f;
        drawInnerCircle(sketch, centerX, centerY, size / InnerOuterCircleRatio, centerText);

        float deg = 0.f;
        var totalWeight = (float) Arrays.stream(weights).sum();
        var maxValue = (float) Arrays.stream(weights).max().getAsInt();
        var minValue = (float) Arrays.stream(weights).min().getAsInt();

        for (var i = 0; i < options.length; i++) {
            var degOffSet = 180 * weights[i] / totalWeight;
            var maxRad = .125f * size;
            var smRad = .5f * size * (float) Math.sin(Math.toRadians(degOffSet))
                / (1 + (float) Math.sin(Math.toRadians(degOffSet)));
            var colorSelection = colors != null
                    ? colors[i]
                    : getColor(weights[i], maxValue, minValue, sketch);

            smRad = Math.min(smRad, maxRad) - padding; // Make sure circle is small enough to fit
            deg += degOffSet;
            drawOuterCircle(sketch, centerX, centerY, smRad, size, deg, colorSelection, options[i]);
            deg += degOffSet;
        }
        Graphics.useGothic(sketch, 18, true);
    }

    /**
     * Draws a circle with text inside of it.
     * @param sketch The graphics context to draw with.
     * @param centerX The x coordinate of the center of the circle.
     * @param centerY The y coordinate of the center of the circle.
     * @param diameter The diameter of the circle.
     * @param text The text which will be rendered inside of the circle.
     */
    public static void drawInnerCircle(Kiosk sketch, float centerX, float centerY,
            float diameter, String text) {
        sketch.fill(246, 139, 31);
        sketch.stroke(246, 139, 31);
        sketch.ellipse(centerX - .5f * diameter, centerY - .5f * diameter,
                diameter, diameter);
        sketch.stroke(256, 256, 256);
        sketch.fill(256, 256, 256);

        // Figure out the optimal size of the text to fit in the circles
        boolean sizeFlag = true;
        float buffer = 1.f;
        float textSize = 0;
        while (sizeFlag) {
            sketch.textSize(buffer * diameter / (2 * TextRatioEstimate * largestTextLine(text)));
            float width = sketch.textWidth(text);
            if (((width / .5 * diameter) < 1.30) && ((width / .5 * diameter) > 1.20)) {
                textSize = (buffer * diameter / (2 * TextRatioEstimate * largestTextLine(text)));
                sizeFlag = false;
            } else {
                buffer += 0.05f;
            }
        }

        sketch.textLeading(textSize * 0.95f);
        Graphics.useGothic(sketch, (int)(50), true);
        //Change to be the largest size to fit all text in all circles
        //AKA all text in circles is the same size, which is the largest possible size for the smallest circle
        sketch.text(text, centerX, centerY, diameter, diameter);
    }

    /**
     * Draws a line from (centerX, centerY) to the specified length pointing in the given direction.
     * @param sketch The graphics context we are drawing with.
     * @param length The length of the line we are drawing.
     * @param centerX The x coordinate of the start of the line.
     * @param centerY The y coordinate of the start of the line.
     * @param angle The direction in degrees of the line to be drawn. (0 = Right, 90 = Down, ect.)
     */
    public static void drawSpoke(Kiosk sketch, float length,
            float centerX, float centerY, float angle) {
        sketch.fill(255, 255, 255);
        sketch.stroke(255, 255, 255);
        sketch.line(
            centerX + length * .125f * (float) Math.cos(Math.toRadians(angle)),
            centerY + length * .125f * (float) Math.sin(Math.toRadians(angle)),
            (float) Math.cos(Math.toRadians(angle)) * .5f * length + centerX,
            (float) Math.sin(Math.toRadians(angle)) * .5f * length + centerY
        );
    }

    private static void drawOuterCircle(Kiosk sketch, float centerX, float centerY, float smRad,
            float size, float deg, int color, String optionText) {
        // Create the line from the edge of the inner circle to the center of the outer circle
        drawSpoke(sketch, size, centerX, centerY, deg);

        // Draw the outer circle
        sketch.stroke(color);
        sketch.fill(color);
        var smX = centerX + (.5f * size - smRad) * (float) Math.cos(Math.toRadians(deg)) - smRad;
        var smY = centerY + (.5f * size - smRad) * (float) Math.sin(Math.toRadians(deg)) - smRad;
        sketch.ellipse(smX, smY, (float) smRad * 2, (float) smRad * 2);

        // Draw text on top of circle
        sketch.stroke(256, 256, 256);
        sketch.fill(256, 256, 256);

        // Figure out the optimal size of the text to fit in the circles
        boolean sizeFlag = true;
        float buffer = 1.f;
        float textSize = 0;
        while (sizeFlag) {
            sketch.textSize(buffer * smRad / (TextRatioEstimate * largestTextLine(optionText)));
            float width = sketch.textWidth(optionText);
            if (((width / smRad) < 1.30) && ((width / smRad) > 1.20)) {
                textSize = (buffer * smRad / (TextRatioEstimate * largestTextLine(optionText)));
                sizeFlag = false;
            } else {
                buffer += 0.05f;
            }
        }
        //Set the spacing between lines to fit nicely
        sketch.textLeading(textSize * 0.95f);
        Graphics.useGothic(sketch, (int)textSize, true);
        sketch.text(optionText, (float) (smX + smRad), (float) (smY + smRad), smRad * 2, smRad * 2);
    }

    private static int largestTextLine(String text) {
        var lineList = text.split("\n");
        int largestLineSize = Integer.MIN_VALUE;
        for (var line : lineList) {
            if (line.length() > largestLineSize) {
                largestLineSize = line.length();
            }
        }
        return largestLineSize;
    }

    private static int getColor(float weight, float maxValue, float minValue, Kiosk sketch) {
        var percentage = (weight - minValue) / (maxValue - minValue);
        var from = sketch.color(252, 177, 22);
        var to = sketch.color(57, 160, 91);
        return sketch.lerpColor(from, to, percentage);
    }
}
