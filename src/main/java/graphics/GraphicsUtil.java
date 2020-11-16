package graphics;

import java.nio.channels.SelectionKey;
import java.util.Arrays;
import kiosk.Kiosk;
import processing.core.PConstants;

public class GraphicsUtil {

    public static final float TextRatioEstimate = 1.2f; // 1.7

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
        drawInnerCircle(sketch, centerX, centerY, size / 4.f, centerText);

        float deg = 0.f;
        var totalWeight = (float) Arrays.stream(weights).sum();

        for (var i = 0; i < options.length; i++) {
            var degOffSet = 180 * weights[i] / totalWeight;
            var maxRad = .125f * size;
            var smRad = .5f * size * (float) Math.sin(Math.toRadians(degOffSet))
                / (1 + (float) Math.sin(Math.toRadians(degOffSet)));
            var colorSelection = colors != null
                    ? colors[i]
                    : getColor(weights[i], totalWeight, sketch);

            smRad = Math.min(smRad, maxRad) - padding; // Make sure circle is small enough to fit
            deg += degOffSet;
            drawOuterCircle(sketch, centerX, centerY, smRad, size, deg, colorSelection, options[i]);
            deg += degOffSet;
        }
        sketch.textSize(18);
    }

    private static void drawInnerCircle(Kiosk sketch, float centerX, float centerY,
            float bigCircleDiameter, String centerText) {
        sketch.fill(0, 0, 0);
        sketch.stroke(0, 0, 0);
        sketch.ellipse(centerX - .5f * bigCircleDiameter, centerY - .5f * bigCircleDiameter,
                bigCircleDiameter, bigCircleDiameter);
        sketch.textSize(2 * bigCircleDiameter / (TextRatioEstimate * largestTextLine(centerText)));
        sketch.stroke(256, 256, 256);
        sketch.fill(256, 256, 256);
        sketch.text(centerText, centerX, centerY);
    }

    private static void drawOuterCircle(Kiosk sketch, float centerX, float centerY, float smRad,
            float size, float deg, int color, String optionText) {
        // Create the line from the edge of the inner circle to the center of the outer circle
        sketch.fill(0, 0, 0);
        sketch.stroke(0, 0, 0);
        sketch.line(
            centerX + size * .125f * (float) Math.cos(Math.toRadians(deg)),
            centerY + size * .125f * (float) Math.sin(Math.toRadians(deg)),
            (float) Math.cos(Math.toRadians(deg)) * .5f * size + centerX,
            (float) Math.sin(Math.toRadians(deg)) * .5f * size + centerY
        );

        // Draw the outer circle
        sketch.stroke(color);
        sketch.fill(color);
        var smX = centerX + (.5f * size - smRad) * (float) Math.cos(Math.toRadians(deg)) - smRad;
        var smY = centerY + (.5f * size - smRad) * (float) Math.sin(Math.toRadians(deg)) - smRad;
        sketch.ellipse(smX, smY, (float) smRad * 2, (float) smRad * 2);

        // Draw text on top of circle
        sketch.stroke(256, 256, 256);
        sketch.fill(256, 256, 256);
        sketch.textSize(2.f * (float) smRad / (TextRatioEstimate * largestTextLine(optionText)));
        sketch.text(optionText, (float) (smX + smRad), (float) (smY + smRad));
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

    private static int getColor(float weight, float totalWeight, Kiosk sketch) {
        var percentage = weight / totalWeight;
        var from = sketch.color(212, 177, 0);
        var to = sketch.color(0, 79, 0);
        return sketch.lerpColor(from, to, percentage);
    }
}
