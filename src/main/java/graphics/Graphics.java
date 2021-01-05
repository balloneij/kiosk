package graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import kiosk.Kiosk;
import processing.core.PConstants;
import processing.core.PFont;

public class Graphics {

    private static final String SANS_SERIF_PATH = "assets/Gothic.ttf";
    private static final String SANS_SERIF_BOLD_PATH = "assets/Gothic-Bold.ttf";
    private static final String SANS_SERIF_ITALICIZE_PATH = "assets/Gothic-Italicize.ttf";
    private static final String SANS_SERIF_BOLD_ITALICIZE_PATH = "assets/Gothic-Bold-Italicize.ttf";

    private static boolean fontsLoaded = false;
    private static PFont sansSerif = null;
    private static PFont sansSerifBold = null;
    private static PFont sansSerifItalicize = null;
    private static PFont sansSerifBoldItalicize = null;

    private Graphics() {

    }

    /**
     * Loads fonts so they are ready for use.
     */
    public static void loadFonts() {
        if (Graphics.fontsLoaded) {
            System.err.println("FontManager.loadFonts should only be called once");
        } else {

            File sansSerifFile = new File(SANS_SERIF_PATH);
            File sansSerifStrongFile = new File(SANS_SERIF_BOLD_PATH);
            File sansSerifItalicizeFile = new File(SANS_SERIF_ITALICIZE_PATH);
            File sansSerifBoldItalicizeFile = new File(SANS_SERIF_BOLD_ITALICIZE_PATH);

            try {
                Graphics.sansSerif = new PFont(
                        Font.createFont(Font.TRUETYPE_FONT, sansSerifFile), true);
                Graphics.sansSerifBold = new PFont(
                        Font.createFont(Font.TRUETYPE_FONT, sansSerifStrongFile), true);
                Graphics.sansSerifItalicize = new PFont(
                        Font.createFont(Font.TRUETYPE_FONT, sansSerifItalicizeFile), true);
                Graphics.sansSerifBoldItalicize = new PFont(
                        Font.createFont(Font.TRUETYPE_FONT, sansSerifBoldItalicizeFile), true);
            } catch (FontFormatException | IOException exception) {
                throw new IllegalStateException("Could not load fonts");
            }

            Graphics.fontsLoaded = true;
        }
    }

    /**
     * Use sanserif font.
     * @param sketch sketch to apply the font to
     * @param fontSize size of the font
     */
    public static void useSansSerif(Kiosk sketch, int fontSize) {
        sketch.textFont(sansSerif, fontSize);
    }

    /**
     * Use sanserif font bold.
     * @param sketch sketch to apply the font to
     * @param fontSize size of the font
     */
    public static void useSansSerifBold(Kiosk sketch, int fontSize) {
        sketch.textFont(sansSerifBold, fontSize);
    }

    /**
     * Use sanserif font and italicize.
     * @param sketch sketch to apply the font to
     * @param fontSize size of the font
     */
    public static void useSansSerifItalicize(Kiosk sketch, int fontSize) {
        sketch.textFont(sansSerifItalicize, fontSize);
    }

    /**
     * Use sanserif font and italicize.
     * @param sketch sketch to apply the font to
     * @param fontSize size of the font
     */
    public static void useSansSerifBoldItalicize(Kiosk sketch, int fontSize) {
        sketch.textFont(sansSerifBoldItalicize, fontSize);
    }

    /**
     * Draws a rounded rectangle.
     * @param sketch to draw to
     * @param x top left corner of the rectangle
     * @param y top left corner of the rectangle
     * @param w width of the rectangle
     * @param h height of the rectangle
     * @param r radius of the curve
     */
    public static void drawRoundedRectangle(Kiosk sketch, float x, float y,
                                            float w, float h, float r) {
        float d = r * 2;

        sketch.ellipseMode(PConstants.CORNER);
        sketch.rectMode(PConstants.CORNER);

        // Draw left side rounded corners and connect them
        sketch.ellipse(x, y, d, d);
        sketch.ellipse(x, y + h - d, d, d);
        sketch.rect(x, y + r, r, h - d);

        // Draw the center
        sketch.rect(x + r, y, w - d, h);

        // Draw the right side rounded corners and connect them
        sketch.ellipse(x + w - d, y, d, d);
        sketch.ellipse(x + w - d, y + h - d, d, d);
        sketch.rect(x + w - r, y + r, r, h - d);
    }

    /**
     * Draws a blue background with grey-blue circles
     * bubbling up from the bottom.
     * @param sketch to draw to
     */
    public static void drawBubbleBackground(Kiosk sketch) {
        final int width = sketch.width;
        final int height = sketch.height;

        float spacing = width / 50f;
        float radius = spacing / 2;

        sketch.ellipseMode(PConstants.CORNER);
        sketch.noStroke();

        Color color = Color.getInstance();
        sketch.background(color.dwBlue);
        sketch.fill(color.dwLightBlue);

        boolean stagger = false;
        float y = height - radius;
        while (radius > 1) {
            for (float x = stagger ? spacing / 2 : 0; x < width; x += spacing) {
                sketch.ellipse(x, y, radius, radius);
            }
            radius -= 0.25;
            y -= spacing;
            stagger = !stagger;
        }
    }
}
