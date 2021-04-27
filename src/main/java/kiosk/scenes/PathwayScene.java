package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.PathwaySceneModel;
import kiosk.models.SceneModel;
import processing.core.PConstants;

public class PathwayScene implements Scene {

    // Pull constants from the settings
    private static int screenW = Kiosk.getSettings().screenW;
    private static int screenH = Kiosk.getSettings().screenH;

    private final PathwaySceneModel model;
    protected final SpokeGraph spokeGraph;
    private ButtonControl backButton;
    private ButtonControl homeButton;
    private ButtonControl supplementaryButton;

    //Animations
    private int startFrame = 0;
    private boolean clickedBack = false;
    private boolean clickedHome = false;
    private boolean clickedNext = false;
    private String sceneToGoTo;
    private Riasec riasecToGoTo;
    private FilterGroupModel filterToGoTo;

    /**
     * Create a pathway scene.
     * @param model to base the scene off of
     */
    public PathwayScene(PathwaySceneModel model) {
        this.model = model;
        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;
        for (ButtonModel careerModel : model.buttonModels) {
            careerModel.isCircle = true;
        }

        // Create the spoke graph
        float size = screenH - GraphicsUtil.headerY - GraphicsUtil.headerH;
        this.spokeGraph = new SpokeGraph(size,
                screenW / 2f - size / 2,
                GraphicsUtil.headerY + GraphicsUtil.headerH,
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
            sketch.hookControl(this.supplementaryButton);
        }

        for (ButtonControl careerOption : this.spokeGraph.getButtonControls()) {
            sketch.hookControl(careerOption);
        }

        startFrame = sketch.frameCount;

        spokeGraph.init(sketch);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.spokeGraph.getButtonControls()) {
            if (button.wasClicked()) {
                clickedNext = true;
                sceneToGoTo = button.getTarget();
                riasecToGoTo = button.getModel().category;
                filterToGoTo = button.getModel().filter;
                break;
            }
        }

        if (!sceneGraph.getRootSceneModel().getId().equals(this.model.getId())) {
            if (this.homeButton.wasClicked()) {
                clickedHome = true;
            } else if (this.backButton.wasClicked()) {
                clickedBack = true;
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

        if (clickedNext) {
            if (sketch.frameCount > startFrame + Kiosk.getSettings().sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody,
                    screenW * (1 - ((sketch.frameCount - startFrame)
                            * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)), 0);
            this.spokeGraph.draw(sketch, screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)), 0);
            if (startFrame + Kiosk.getSettings().sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            }
        } else if (clickedBack) {
            if (sketch.frameCount > startFrame + Kiosk.getSettings().sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody,
                    0 - screenW
                            * (1 - ((sketch.frameCount - startFrame)
                            * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)), 0);
            this.spokeGraph.draw(sketch, 0 - screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)), 0);
            if (startFrame + Kiosk.getSettings().sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedHome) {
            if (sketch.frameCount > startFrame + Kiosk.getSettings().sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody,
                    0,  screenH
                            * (1 - ((sketch.frameCount - startFrame)
                            * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)));
            this.spokeGraph.draw(sketch, 0, screenH
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)));
            if (startFrame + Kiosk.getSettings().sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.contains("RESET")) {
            if (sketch.frameCount - startFrame <= Kiosk.getSettings().sceneAnimationFrames && !sketch.isEditor) {
                GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody,
                        0,  screenH + screenH
                                * (1 - ((sketch.frameCount - startFrame)
                                * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)));
                this.spokeGraph.draw(sketch, 0, screenH + screenH
                        * (1 - ((sketch.frameCount - startFrame)
                        * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)));
            } else {
                GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, 0, 0);
                this.spokeGraph.draw(sketch, 0, 0);
            }
        } else if (sketch.getSceneGraph().recentActivity.contains("POP")) {
            if (sketch.frameCount - startFrame <= Kiosk.getSettings().sceneAnimationFrames && !sketch.isEditor) {
                GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody,
                        0 - screenW - screenW
                                * (1 - ((sketch.frameCount - startFrame)
                                * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)), 0);
                this.spokeGraph.draw(sketch, 0 - screenW - screenW
                        * (1 - ((sketch.frameCount - startFrame)
                        * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)), 0);
            } else {
                GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, 0, 0);
                this.spokeGraph.draw(sketch, 0, 0);
            }
        } else if (sketch.frameCount - startFrame <= Kiosk.getSettings().sceneAnimationFrames && !sketch.isEditor) {
            GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody,
                    screenW + screenW * (1 - ((sketch.frameCount - startFrame)
                            * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)), 0);
            this.spokeGraph.draw(sketch, screenW + screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / Kiosk.getSettings().sceneAnimationFrames + 1)), 0);
        } else {
            GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, 0, 0);
            this.spokeGraph.draw(sketch, 0, 0);
        }

        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            this.backButton.draw(sketch);
            this.homeButton.draw(sketch);
        } else {
            supplementaryButton.draw(sketch);
        }

    }
}
