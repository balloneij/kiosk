package graphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import kiosk.Kiosk;
import kiosk.models.ImageModel;
import kiosk.scenes.Image;
import processing.core.PConstants;
import processing.core.PFont;

public class Graphics {

    private static final String GOTHIC_PATH = "assets/CenturyGothic.ttf";
    private static final String GOTHIC_BOLD_PATH = "assets/CenturyGothicBold.ttf";
    private static final String SANS_SERIF_PATH = "assets/Gothic.ttf";
    private static final String SANS_SERIF_BOLD_PATH = "assets/Gothic-Bold.ttf";
    private static final String SANS_SERIF_ITALICIZE_PATH = "assets/Gothic-Italicize.ttf";
    private static final String SANS_SERIF_BOLD_ITALICIZE_PATH = "assets/Gothic-Bold-Italicize.ttf";

    private static boolean fontsLoaded = false;
    private static PFont gothic = null;
    private static PFont gothicBold = null;
    private static PFont sansSerif = null;
    private static PFont sansSerifBold = null;
    private static PFont sansSerifItalicize = null;
    private static PFont sansSerifBoldItalicize = null;
    private static float bubbleOffset = 0;

    private Graphics() {

    }

    /**
     * Loads fonts so they are ready for use.
     */
    public static void loadFonts() {
        if (Graphics.fontsLoaded) {
            System.err.println("FontManager.loadFonts should only be called once");
        } else {
            File gothicFile = new File(GOTHIC_PATH);
            File gothicBoldFile = new File(GOTHIC_BOLD_PATH);
            File sansSerifFile = new File(SANS_SERIF_PATH);
            File sansSerifStrongFile = new File(SANS_SERIF_BOLD_PATH);
            File sansSerifItalicizeFile = new File(SANS_SERIF_ITALICIZE_PATH);
            File sansSerifBoldItalicizeFile = new File(SANS_SERIF_BOLD_ITALICIZE_PATH);

            try {
                Graphics.gothic = new PFont(
                        Font.createFont(Font.TRUETYPE_FONT, gothicFile), true);
                Graphics.gothicBold = new PFont(
                        Font.createFont(Font.TRUETYPE_FONT, gothicBoldFile), true);
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

    public static void useGothic(Kiosk sketch) {
        useGothic(sketch, 48, false);
    }

    public static void useGothic(Kiosk sketch, int fontSize) {
        useGothic(sketch, fontSize, false);
    }

    /**
     * Use Gothic font.
     * @param sketch sketch to apply the font to
     * @param fontSize specify the font size
     * @param bold specify if bold text is desired
     */
    public static void useGothic(Kiosk sketch, int fontSize, boolean bold) {
        if (bold) {
            sketch.textFont(gothicBold, fontSize);
        } else {
            sketch.textFont(gothic, fontSize);
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
        sketch.rectMode(PConstants.CENTER);

        // Draw the rounded rectangle
        sketch.rect(x, y, w, h, r);
    }

    /**
     * Draws a blue background with grey-blue circles
     * bubbling up from the bottom.
     * @param sketch to draw to
     */
    public static void drawBubbleBackground(Kiosk sketch) {
        final int width = sketch.getSettings().screenW;
        final int height = sketch.getSettings().screenH;

        float spacing = width / 50f;
        float radius = spacing / 2.05f;
        float radiusChipping = 0.30f * (width / 1280f);

        sketch.ellipseMode(PConstants.CORNER);
        sketch.noStroke();

        Color color = Color.getInstance();
        sketch.background(color.dwBlue);
        sketch.fill(color.dwLightBlue);

        boolean stagger = false;
        float y = height - radius;
        float iterationNumber = ((radius - 1) / radiusChipping);
        for (int i = 0; i < iterationNumber; i++) {
            for (float x = stagger ? spacing / 2 - bubbleOffset : -bubbleOffset;
                 x < width; x += spacing) {
                if (y > 0) {
                    sketch.ellipse(x, y, radius, radius);
                }
            }
            radius -= radiusChipping;
            y -= spacing;
            stagger = !stagger;
        }
        bubbleOffset = bubbleOffset + 0.125f;
    }
}
