package kiosk.models;

public final class ImageModel {

    public String path;
    public int width;
    public int height;

    /**
     * Creates an 16x16 ImageModel with an invalid path.
     */
    public ImageModel() {
        this.path = "null";
        this.width = 16;
        this.height = 16;
    }

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

    public ImageModel deepCopy() {
        return new ImageModel(path, width, height);
    }
}
