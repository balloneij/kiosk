package graphics;

import java.util.Arrays;

import kiosk.Graphics;
import kiosk.Kiosk;
import processing.core.PConstants;

public class GraphicsUtil {

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
        //sketch.textSize(18);
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
        sketch.textLeading(2 * diameter / (TextRatioEstimate * largestTextLine(text)) * 1.15f);
        Graphics.useGothic(sketch, (int)(2 * diameter / (TextRatioEstimate * largestTextLine(text))), true);
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
        sketch.fill(0, 0, 0);
        sketch.stroke(0, 0, 0);
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

    private static int getColor(float weight, float maxValue, float minValue, Kiosk sketch) {
        var percentage = (weight - minValue) / (maxValue - minValue);
        var from = sketch.color(252, 177, 22);
        var to = sketch.color(57, 160, 91);
        return sketch.lerpColor(from, to, percentage);
    }
}
