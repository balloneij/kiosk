package kiosk;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import processing.core.PConstants;
import processing.core.PFont;

public class Graphics {

    private static final String SERIF_PATH = "assets/Acme-Regular.ttf";
    private static final String SANS_SERIF_PATH = "assets/OpenSans-Regular.ttf";
    private static final String STRONG_SANS_SERIF_PATH = "assets/OpenSans-SemiBold.ttf";

    private static boolean fontsLoaded = false;
    private static PFont serif = null;
    private static PFont sansSerif = null;
    private static PFont strongSansSerif = null;

    private Graphics() {

    }

    public static void loadFonts() {
        if (Graphics.fontsLoaded) {
            System.err.println("FontManager.loadFonts should only be called once");
        } else {

            File serifFile = new File(SERIF_PATH);
            File sansSerifFile = new File(SANS_SERIF_PATH);
            File strongSansSerifFile = new File(STRONG_SANS_SERIF_PATH);

            try {
                Graphics.serif = new PFont(Font.createFont(Font.TRUETYPE_FONT, serifFile), true);
                Graphics.sansSerif = new PFont(Font.createFont(Font.TRUETYPE_FONT, sansSerifFile), true);
                Graphics.strongSansSerif = new PFont(Font.createFont(Font.TRUETYPE_FONT, strongSansSerifFile), true);
            } catch (FontFormatException | IOException exception) {
                throw new IllegalStateException("Could not load fonts");
            }

            Graphics.fontsLoaded = true;
        }
    }

    public static void useSerif(Kiosk sketch) {
        useSerif(sketch, 48);
    }

    public static void useSerif(Kiosk sketch, int fontSize) {
        sketch.textFont(serif, fontSize);
    }

    public static void useSansSerif(Kiosk sketch) {
        useSansSerif(sketch, 48, false);
    }

    public static void useSansSerif(Kiosk sketch, int fontSize, boolean strong) {
        if (strong) {
            sketch.textFont(strongSansSerif, fontSize);
        } else {
            sketch.textFont(sansSerif, fontSize);
        }
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
    public static void drawRoundedRectangle(Kiosk sketch, float x, float y, float w, float h, float r) {
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
        sketch.background(44, 134, 194);
        sketch.fill(99, 144, 197);

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
