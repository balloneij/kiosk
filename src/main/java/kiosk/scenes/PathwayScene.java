package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.PathwaySceneModel;
import processing.core.PConstants;

public class PathwayScene implements Scene {

    // Pull constants from the settings
    private static final int SCREEN_W = Kiosk.getSettings().screenW;
    private static final int SCREEN_H = Kiosk.getSettings().screenH;

    private final PathwaySceneModel model;
    protected final SpokeGraph spokeGraph;
    private ButtonControl backButton;
    private ButtonControl homeButton;
    private ButtonControl supplementaryButton;

    /**
     * Create a pathway scene.
     * @param model to base the scene off of
     */
    public PathwayScene(PathwaySceneModel model) {
        this.model = model;
        for (ButtonModel careerModel : model.buttonModels) {
            careerModel.isCircle = true;
        }

        // Create the spoke graph
        float size = SCREEN_H - GraphicsUtil.HEADER_Y - GraphicsUtil.HEADER_H;
        this.spokeGraph = new SpokeGraph(size,
                SCREEN_W / 2f - size / 2,
                GraphicsUtil.HEADER_Y + GraphicsUtil.HEADER_H,
                this.model.centerText,
                this.model.buttonModels);
    }

    @Override
    public void init(Kiosk sketch) {
        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton = GraphicsUtil.initializeHomeButton(sketch);
            sketch.hookControl(this.homeButton);
            this.backButton = GraphicsUtil.initializeBackButton(sketch);
            sketch.hookControl(this.backButton);
        } else {
            this.supplementaryButton = GraphicsUtil.initializeMsoeButton(sketch);
            this.supplementaryButton.init(sketch);
            sketch.hookControl(this.supplementaryButton);
        }

        for (ButtonControl careerOption : this.spokeGraph.getButtonControls()) {
            sketch.hookControl(careerOption);
        }

        spokeGraph.init(sketch);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.spokeGraph.getButtonControls()) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget(), button.getModel().category);
                break;
            }
        }

        if (!sceneGraph.getRootSceneModel().getId().equals(this.model.getId())) {
            if (this.homeButton.wasClicked()) {
                sceneGraph.reset();
            } else if (this.backButton.wasClicked()) {
                sceneGraph.popScene();
            }
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useGothic(sketch, 48, true);
        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.fill(0);
        Graphics.drawBubbleBackground(sketch);
        GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody);
        this.spokeGraph.draw(sketch);

        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            this.backButton.draw(sketch);
            this.homeButton.draw(sketch);
        } else {
            supplementaryButton.draw(sketch);
        }

    }
}
