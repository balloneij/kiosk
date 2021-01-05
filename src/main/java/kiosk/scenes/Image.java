package kiosk.scenes;

import kiosk.models.ImageModel;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

public class Image {

    public final ImageModel model;
    private final PImage image;
    private float rotation = 0;

    private Image(ImageModel model, PImage image) {
        this.model = model;
        this.image = image;
    }

    /**
     * Draws the image to the sketch at (x, y). Will apply the
     * appropriate rotation if there is any.
     * @param sketch to draw to
     * @param x location
     * @param y location
     */
    public void draw(PApplet sketch, float x, float y) {
        if (this.rotation == 0) {
            // No need to rotate, simply draw
            sketch.image(this.image, x, y);
        } else {
            // Rotate around the center of the image
            sketch.translate(x + this.model.width / 2f, y + this.model.height / 2f);
            sketch.rotate(this.rotation);
            sketch.image(this.image, - this.model.width / 2f, -this.model.height / 2f);
        }
    }

    /**
     * Applies rotation to the image. The rotation is done
     * around the center of the image. Like a pinwheel.
     * @param radians to rotate
     */
    public void rotate(float radians) {
        this.rotation = (float) (radians % (2 * Math.PI));
    }

    /**
     * Loads an image from the ImageModel provided. If the image cannot
     * be loaded from the disk, a checkered image of the same dimensions
     * is returned.
     * Note that the image is resized to the dimensions specified in the
     * model.
     * @param sketch the image belongs to
     * @param model ImageModel containing path and image dimensions
     * @return the appropriately sized image
     */
    public static Image createImage(PApplet sketch, ImageModel model) {
        // Load image returns null if it fails
        PImage image = sketch.loadImage(model.path);

        if (image == null) {
            return Image.createCheckerImage(sketch, model);
        }

        image.resize(model.width, model.height);

        return new Image(model, image);
    }

    /**
     * Creates a checker image using the dimensions provided. The checkers are sized
     * such that the shortest side will always have eight squares.
     * @param sketch to the image belongs to
     * @param model with the height and dimensions of the desired image. The
     *              path is not necessary
     * @return the image created
     */
    public static Image createCheckerImage(PApplet sketch, ImageModel model) {
        PImage image = sketch.createImage(model.width, model.height, PConstants.RGB);

        // The size of the checker is 1/8 of either the height or width
        // of the image, whichever is greater.
        int checkerSize = model.width > model.height ? model.height / 8 : model.width / 8;

        for (int y = 0; y + checkerSize < model.height; y += checkerSize) {
            // Alternate colors between white/black
            int color1;
            int color2;

            if ((y / checkerSize) % 2 == 1) {
                color1 = sketch.color(255, 255, 255);
                color2 = sketch.color(0, 0, 0);
            } else {
                color1 = sketch.color(0, 0, 0);
                color2 = sketch.color(255, 255, 255);
            }

            for (int x = 0; x < model.width; x += checkerSize) {
                // Fill in the first square using color1
                for (int px = x; px < x + checkerSize && px < image.width; px++) {
                    for (int py = y; py < y + checkerSize && px < image.height; py++) {
                        image.pixels[px + py * image.width] = color1;
                    }
                }

                // Fill in the second square using color2
                x += checkerSize;
                for (int px = x; px < x + checkerSize && px < image.width; px++) {
                    for (int py = y; py < y + checkerSize && px < image.height; py++) {
                        image.pixels[px + py * image.width] = color2;
                    }
                }
            }
        }

        // Apply pixel changes
        image.updatePixels();

        return new Image(model, image);
    }
}
