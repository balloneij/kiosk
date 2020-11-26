package editor;

import javafx.application.Application;
import kiosk.Kiosk;
import processing.core.PSurface;
import processing.javafx.PSurfaceFX;

public class Editor extends Kiosk {

    public Editor() {
        // TODO hardcoding for now, needs to be dynamic later
        super("survey.xml");
        runSketch();
    }

    @Override
    public void settings() {
        size(640, 360, FX2D);
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

    public static void main(String[] args) {
        new Editor();
    }
}
