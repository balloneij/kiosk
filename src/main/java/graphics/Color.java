package graphics;

import processing.core.PApplet;
import processing.core.PConstants;

public class Color {

    // Colors as defined by Discovery World Jody herself
    public static final int[] DW_WHITE_RGB = new int[] { 255, 255, 255 };
    public static final int[] DW_BLACK_RGB = new int[] { 59, 58, 57 };
    public static final int[] DW_BLUE_RGB = new int[] { 50, 123, 174 };
    public static final int[] DW_LIGHT_BLUE_RGB = new int[] { 95, 134, 178 };
    public static final int[] DW_DARK_BLUE_RGB = new int[] { 31, 88, 138 };
    public static final int[] DW_MAROON_RGB = new int[] { 138, 37, 93 };
    public static final int[] DW_TEAL_RGB = new int[] { 44, 160, 97 };
    public static final int[] DW_ORANGE_RGB = new int[] { 204, 94, 45 };
    public static final int[] DW_LIGHT_ORANGE_RGB = new int[] { 232, 128, 46 };
    public static final int[] DW_RED_RGB = new int[] { 183, 48, 52 };
    public static final int[] DW_GREEN_RGB = new int[] { 105, 183, 77 };

    public final int dwWhite;
    public final int dwBlack;
    public final int dwBlue;
    public final int dwLightBlue;
    public final int dwDarkBlue;
    public final int dwMaroon;
    public final int dwTeal;
    public final int dwOrange;
    public final int dwLightOrange;
    public final int dwRed;
    public final int dwGreen;

    private static PApplet sketch;
    private static Color instance;

    private Color() {
        sketch.colorMode(PConstants.RGB);

        dwWhite = sketch.color(DW_WHITE_RGB[0], DW_WHITE_RGB[1], DW_WHITE_RGB[2]);
        dwBlack = sketch.color(DW_BLACK_RGB[0], DW_BLACK_RGB[1], DW_BLACK_RGB[2]);
        dwBlue = sketch.color(DW_BLUE_RGB[0], DW_BLUE_RGB[1], DW_BLUE_RGB[2]);
        dwLightBlue = sketch.color(
                DW_LIGHT_BLUE_RGB[0], DW_LIGHT_BLUE_RGB[1], DW_LIGHT_BLUE_RGB[2]);
        dwDarkBlue = sketch.color(DW_DARK_BLUE_RGB[0], DW_DARK_BLUE_RGB[1], DW_DARK_BLUE_RGB[2]);
        dwMaroon = sketch.color(DW_MAROON_RGB[0], DW_MAROON_RGB[1], DW_MAROON_RGB[2]);
        dwTeal = sketch.color(DW_TEAL_RGB[0], DW_TEAL_RGB[1], DW_TEAL_RGB[2]);
        dwOrange = sketch.color(DW_ORANGE_RGB[0], DW_ORANGE_RGB[1], DW_ORANGE_RGB[2]);
        dwLightOrange = sketch.color(
                DW_LIGHT_ORANGE_RGB[0], DW_LIGHT_ORANGE_RGB[1], DW_LIGHT_ORANGE_RGB[2]);
        dwRed = sketch.color(DW_RED_RGB[0], DW_RED_RGB[1], DW_RED_RGB[2]);
        dwGreen = sketch.color(DW_GREEN_RGB[0], DW_GREEN_RGB[1], DW_GREEN_RGB[2]);
    }

    /**
     * Gets instance of the color, it's the singleton.
     * It relies on sketch
     * @return Color instance
     */
    public static Color getInstance() {
        if (Color.sketch == null) {
            throw new IllegalStateException("Color needs a sketch in order to interpolate colors. "
                    + "Use .setSketch()");
        }
        if (Color.instance == null) {
            Color.instance = new Color();
        }
        return Color.instance;
    }

    public static void setSketch(PApplet sketch) {
        Color.sketch = sketch;
    }

    /**
     * Converts an array of RGB values to an int representing
     * the color.
     * @param color to convert into an int
     * @return rgb int
     */
    public static int toRgb(int[] color) {
        int rgb = color[0];
        rgb = (rgb << 8) + color[1];
        rgb = (rgb << 8) + color[2];
        return rgb;
    }
}
