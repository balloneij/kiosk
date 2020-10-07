package kiosk.models;

public final class ImageModel {

    public final String path;
    public final int width;
    public final int height;

    /**
     * Creates the model representation of an image.
     * @param path the image can be found at
     * @param width the width the image should be drawn at
     * @param height the height the image should be resized to
     */
    public ImageModel(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }
}
