package graphics;

import java.util.Arrays;
import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import kiosk.scenes.ButtonControl;
import processing.core.PConstants;

public class SpokeUtil {

    public static final float TextRatioEstimate = 1.5f; // 1.7
    public static final float InnerOuterCircleRatio = 4.f;

    /**
     * Draws a new spoke graph. Draws a large circle in the middle with text and smaller circles
     * on the outside with the options in each one.
     * @param sketch The graphics context used to draw the spoke graph.
     * @param size The side length of the square the graph will fit into.
     * @param x The x location of the upper left-hand corner.
     * @param y The y location of the upper left-hand corner.
     * @param padding The gap space between each outer circle.
     * @param centerText The text that appears in the center circle.
     * @param answers The text that appears in each outer circle.
     */
    public static void spokeGraph(Kiosk sketch, float size, float x, float y, float padding,
          String centerText, ButtonControl[] answers) {
        var weights = new int[answers.length];
        Arrays.fill(weights, 1);
        spokeGraph(sketch, size, x, y, padding, centerText, answers, weights);
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
     * @param answers The text that appears in each outer circle.
     * @param weights The relative ratio and weight of each option.
     */
    public static void spokeGraph(Kiosk sketch, float size, float x, float y, float padding,
            String centerText, ButtonControl[] answers, int[] weights) {
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        drawInnerCircle(sketch, x, y, size / InnerOuterCircleRatio, centerText);

        float deg = 0.f;
        var totalWeight = (float) Arrays.stream(weights).sum();

        for (var i = 0; i < answers.length; i++) {
            var degOffSet = 180 * weights[i] / totalWeight;
            var maxRad = .125f * size;
            var smRad = .5f * size * (float) Math.sin(Math.toRadians(degOffSet))
                / (1 + (float) Math.sin(Math.toRadians(degOffSet)));

            smRad = Math.min(smRad, maxRad) - padding; // Make sure circle is small enough to fit
            deg += degOffSet;
            drawOuterCircle(sketch, x, y, smRad, size, deg, answers[i]);
            deg += degOffSet;
        }
        sketch.textSize(18);
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
        sketch.textSize(2 * diameter / (TextRatioEstimate * largestTextLine(text)));
        sketch.stroke(256, 256, 256);
        sketch.fill(256, 256, 256);
        sketch.textLeading(2 * diameter / (TextRatioEstimate * largestTextLine(text)) * 1.15f);
        sketch.text(text, centerX, centerY);
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
        sketch.fill(255);
        sketch.stroke(255);
        sketch.line(
            centerX + length * .125f * (float) Math.cos(Math.toRadians(angle)),
            centerY + length * .125f * (float) Math.sin(Math.toRadians(angle)),
            (float) Math.cos(Math.toRadians(angle)) * .5f * length + centerX,
            (float) Math.sin(Math.toRadians(angle)) * .5f * length + centerY
        );
    }

    private static void drawOuterCircle(Kiosk sketch, float centerX, float centerY, float smRad,
            float size, float deg, ButtonControl answer) {
        // Create the line from the edge of the inner circle to the center of the outer circle
        drawSpoke(sketch, size, centerX, centerY, deg);

        // Draw the outer circle
        var smX = centerX + (.5f * size - smRad) * (float) Math.cos(Math.toRadians(deg)) - smRad;
        var smY = centerY + (.5f * size - smRad) * (float) Math.sin(Math.toRadians(deg)) - smRad;

        answer.setLocation((int) smX, (int) smY);
        answer.setWidthAndHeight(2 * (int) smRad, 2 * (int) smRad);

        answer.draw(sketch);
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

    private static int[] getColor(float weight, float maxValue, float minValue, Kiosk sketch) {
        var percentage = (weight - minValue) / (maxValue - minValue);
        var from = sketch.color(252, 177, 22);
        var to = sketch.color(57, 160, 91);
        int color_single = sketch.lerpColor(from, to, percentage);
        int[] toReturn = new int[3];
        toReturn[0] = (color_single & 0xFF0000) >> 16;
        toReturn[1] = (color_single & 0x00FF00) >> 8;
        toReturn[2] = color_single & 0x0000FF;
        return toReturn;
    }
}
