package graphics;

import kiosk.Kiosk;

public class GraphicsUtil {

    public static final float TEXT_RATIO_ESTIMATE = 1.7f;

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
            String centerText, String[] options) {
        var bigCircleDiameter = size / 4.f;
        var smallCircleDiameter = calculateSmallCircleDiameter(size, options.length, padding);
        sketch.fill(256, 0, 0);
        sketch.rect(x, y, size, size);
        sketch.fill(204, 102, 0);
        var centerX = x + size / 2.f;
        var centerY = y + size / 2.f;
        sketch.ellipse(centerX, centerY, bigCircleDiameter, bigCircleDiameter);
        sketch.fill(0);
        sketch.textSize(2 * bigCircleDiameter
                / (TEXT_RATIO_ESTIMATE * largestTextLine(centerText)));
        var textWidth = sketch.textWidth(centerText);
        sketch.text(centerText, centerX - (textWidth / 2), centerY);
        var degreeShift = 360.f / options.length;

        for (var i = 0; i < options.length; i++) {
            sketch.fill(204, 102, 0);
            var smallCircleCenterX = getOuterCircleX((float) Math.toRadians(i * degreeShift),
                    centerX, size, smallCircleDiameter);
            var smallCircleCenterY = getOuterCircleY((float) Math.toRadians(i * degreeShift),
                    centerY, size, smallCircleDiameter);
            sketch.ellipse(smallCircleCenterX, smallCircleCenterY, smallCircleDiameter,
                    smallCircleDiameter);

            var lineStartX = getLineStartX((float) Math.toRadians(i * degreeShift),
                    centerX, bigCircleDiameter);
            var lineStartY = getLineStartY((float) Math.toRadians(i * degreeShift),
                    centerY, bigCircleDiameter);
            var lineEndX = getLineEndX((float) Math.toRadians(i * degreeShift),
                    centerX, size, smallCircleDiameter);
            var lineEndY = getLineEndY((float) Math.toRadians(i * degreeShift),
                    centerY, size, smallCircleDiameter);

            sketch.line(lineStartX, lineStartY, lineEndX, lineEndY);
            sketch.fill(0);
            sketch.textSize(2 * smallCircleDiameter
                    / (TEXT_RATIO_ESTIMATE * largestTextLine(options[i])));
            textWidth = sketch.textWidth(options[i]);
            sketch.text(options[i], smallCircleCenterX - (textWidth / 2), smallCircleCenterY);
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

    private static float calculateSmallCircleDiameter(float size, int spokeCount, float padding) {
        if (spokeCount <= 5) {
            return size * .33f;
        }
        var boxRad = size / 2;
        var theta = Math.toRadians(180.f / (float) spokeCount);
        var numerator = boxRad * Math.sin(theta);
        var denominator = (1 + Math.sin(theta));
        var radius = numerator / denominator;
        return (float) radius * 2.f - padding;
    }

    private static float getOuterCircleX(float radians, float spokeGraphCenterX,
            float size, float smallCircleDiameter) {
        float delta = (float) Math.sin(radians) * (size * .5f - smallCircleDiameter * .5f);
        return spokeGraphCenterX + delta;
    }

    private static float getOuterCircleY(float radians, float spokeGraphCenterY,
            float size, float smallCircleDiameter) {
        float delta = (float) Math.cos(radians) * (size * .5f - smallCircleDiameter * .5f);
        return spokeGraphCenterY + delta;
    }

    private static float getLineStartX(float radians, float centerX, float bigCircleDiameter) {
        float delta = (float) Math.sin(radians) * bigCircleDiameter * .5f;
        return centerX + delta;
    }

    private static float getLineStartY(float radians, float centerY, float bigCircleDiameter) {
        float delta = (float) Math.cos(radians) * bigCircleDiameter * .5f;
        return centerY + delta;
    }

    private static float getLineEndX(float toRadians, float centerX,
            float size, float smallCircleDiameter) {
        float delta = (float) Math.sin(toRadians) * (size * .5f - smallCircleDiameter);
        return centerX + delta;
    }

    private static float getLineEndY(float toRadians, float centerY,
            float size, float smallCircleDiameter) {
        float delta = (float) Math.cos(toRadians) * (size * .5f - smallCircleDiameter);
        return centerY + delta;
    }
}
