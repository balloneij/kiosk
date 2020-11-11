package kiosk.models;

import kiosk.scenes.Scene;
import kiosk.scenes.SpokeGraphScene;

public class SpokeGraphSceneModel implements SceneModel {

    public final String id;
    public final float xpos;
    public final float ypos;
    public final float size;
    public final float padding;
    public final String centerText;
    public final String[] options;

    /**
     * Creates a new spoke graph Scene Model.
     * @param id The unique id of the scene.
     * @param x The upper left-hand x position of the Scene.
     * @param y The upper left-hand y position of the Scene.
     * @param size The scene is drawn in a square, this is the size of all sides.
     * @param padding The extra spacing between circles.
     * @param centerText The text in the center circle.
     * @param options The list of text appearing on the outer circles.
     */
    public SpokeGraphSceneModel(String id, float x, float y, float size, float padding,
            String centerText, String[] options) {
        this.id = id;
        this.xpos = x;
        this.ypos = y;
        this.size = size;
        this.padding = padding;
        this.centerText = centerText;
        this.options = options;
    }

    @Override
    public Scene createScene() {
        return new SpokeGraphScene(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SceneModel deepCopy() {
        return new SpokeGraphSceneModel(this.id, this.xpos, this.ypos,
                this.size, this.padding, this.centerText, this.options);
    }
}
