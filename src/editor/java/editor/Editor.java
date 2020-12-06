package editor;

import javafx.application.Application;
import kiosk.Kiosk;
import kiosk.Settings;
import processing.core.PSurface;
import processing.javafx.PSurfaceFX;

public class Editor extends Kiosk {

    /**
     * Aspect ratio of the preview window.
     */
    public static final double PREVIEW_ASPECT_RATIO = 16.0 / 9;

    /**
     * Width of the preview window. Height is calculated based off of
     * the preview window
     */
    public static final int PREVIEW_WIDTH = 1280;

    /**
     * Width of the side toolbar.
     */
    public static final int TOOLBAR_WIDTH = 320;

    /**
     * Instantiates the editor and starts the sketch.
     */
    public Editor(String surveyPath, Settings settings) {
        super(surveyPath, settings);
    }

    @Override
    public void settings() {
        size(settings.screenW, settings.screenH, FX2D);
    }

    @Override
    protected PSurface initSurface() {
        g = createPrimaryGraphics();
        PSurface genericSurface = g.createSurface();
        PSurfaceFX fxSurface = (PSurfaceFX) genericSurface;

        fxSurface.sketch = this;
        App.surface = fxSurface;
        Controller.surface = fxSurface;
        Controller.sceneGraph = this.sceneGraph;

        new Thread(() -> Application.launch(App.class)).start();

        while (fxSurface.stage == null) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Error sleeping");
            }
        }

        this.surface = fxSurface;
        return surface;
    }

    /**
     * Starts the editor.
     * @param args unused
     */
    public static void main(String[] args) {
        Settings settings = new Settings();

        settings.screenW = Editor.PREVIEW_WIDTH;
        settings.screenH = (int) (Editor.PREVIEW_WIDTH / Editor.PREVIEW_ASPECT_RATIO);

        Editor editor = new Editor("survey.xml", settings);
        editor.runSketch();
    }
}
