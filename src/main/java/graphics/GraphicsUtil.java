package graphics;

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
        var bigCircleDiameter = size / 4.f;
        var centerX = x + size / 2.f;
        var centerY = y + size / 2.f;
        sketch.fill(0, 0, 0);
        sketch.stroke(0, 0, 0);
        sketch.ellipse(centerX - .5f * bigCircleDiameter, centerY - .5f * bigCircleDiameter, bigCircleDiameter, bigCircleDiameter);
        sketch.textSize(2 * bigCircleDiameter / (TextRatioEstimate * largestTextLine(centerText)));
        var textWidth = sketch.textWidth(centerText);
        sketch.stroke(256, 256, 256);
        sketch.fill(256, 256, 256);
        sketch.text(centerText, centerX + .5f * bigCircleDiameter - (textWidth / 2), centerY);

        sketch.fill(256);
        sketch.rect(x, y, size, size);

        var totalWeight = (float) Arrays.stream(weights).sum();

        float deg = 0.f;
        for (var i = 0; i < options.length; i++) {
            var text = options[i];
            var weight = weights[i];
            sketch.fill(0, 0, 0);

            var degOffSet = 180 * weight / totalWeight;
            deg += degOffSet;

            var smRad = (.5f * size * Math.sin(Math.toRadians(degOffSet)))
                    / (1 + Math.sin(Math.toRadians(degOffSet)));
            var maxRad = .125f * size;
            if (smRad > maxRad) {
                smRad = maxRad;
            }
            smRad -= padding;

            var smX = centerX + (.5f * size - smRad) * Math.cos(Math.toRadians(deg)) - smRad;
            var smY = centerY + (.5f * size - smRad) * Math.sin(Math.toRadians(deg)) - smRad;

            sketch.stroke(0, 0, 0);
            sketch.line(
                centerX + size * .125f * (float) Math.cos(Math.toRadians(deg)),
                centerY + size * .125f * (float) Math.sin(Math.toRadians(deg)),
                (float) Math.cos(Math.toRadians(deg)) * .5f * size + centerX,
                (float) Math.sin(Math.toRadians(deg)) * .5f * size + centerY
            );

            var colorSelection = colors != null ? colors[i] : getColor(weight, totalWeight, sketch);
            sketch.stroke(colorSelection);
            sketch.fill(colorSelection);

            sketch.ellipse((float) smX, (float) smY, (float) smRad * 2, (float) smRad * 2);

            sketch.stroke(256, 256, 256);
            sketch.fill(256, 256, 256);

            textWidth = sketch.textWidth(text);
            sketch.textSize(2.f * (float) smRad / (TextRatioEstimate * largestTextLine(text)));
            sketch.text(text, (float) smX - (textWidth / 2), (float) smY);
            deg += degOffSet;
        }
        sketch.textSize(18);
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
