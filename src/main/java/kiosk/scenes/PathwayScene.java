package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.CreditsSceneModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.PathwaySceneModel;
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
    private boolean isRoot = false;

    //Animations
    private int startFrame = 0;
    private int sceneAnimationFrames = Kiosk.getSettings().sceneAnimationFrames;
    private boolean clickedBack = false;
    private boolean clickedHome = false;
    private boolean clickedNext = false;
    private boolean clickedMsoe = false;
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
        if (!isRoot) {
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
        sceneAnimationFrames = Kiosk.getSettings().sceneAnimationFrames;

        spokeGraph.init(sketch);
        this.isRoot = sketch.getRootSceneModel().getId().equals(this.model.getId());
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

        if (!isRoot) {
            if (this.homeButton.wasClicked()) {
                clickedHome = true;
            } else if (this.backButton.wasClicked()) {
                clickedBack = true;
            }
        } else if (this.supplementaryButton.wasClicked()) {
            clickedMsoe = true;
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useGothic(sketch, 48, true);
        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.fill(0);

        if (sketch.isEditor) {
            if (clickedNext) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            } else if (clickedBack) {
                sketch.getSceneGraph().popScene();
            } else if (clickedHome) {
                sketch.getSceneGraph().reset();
            } else if (clickedMsoe) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        }

        if ((clickedNext || clickedMsoe) && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, (int) (screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))), 0);
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                if (clickedNext) {
                    sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
                } else if (clickedMsoe) {
                    sketch.getSceneGraph().pushScene(new CreditsSceneModel());
                }
            }
        } else if (clickedBack && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, (int) (0 - screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))), 0);
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedHome && !sketch.isEditor) {
            if (sketch.frameCount > startFrame + sceneAnimationFrames) {
                startFrame = sketch.frameCount;
            }
            drawThisFrame(sketch, 0, (int) (screenH
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))));
            if (startFrame + sceneAnimationFrames <= sketch.frameCount) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.contains("RESET")
                && sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch, 0, (int) (screenH + screenH
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))));
        } else if (sketch.getSceneGraph().recentActivity.contains("POP")
                && sketch.frameCount - startFrame <= sceneAnimationFrames && !sketch.isEditor) {
            drawThisFrame(sketch, (int) (0 - screenW - screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))), 0);
        } else if (sketch.frameCount - startFrame <= Kiosk.getSettings().sceneAnimationFrames
                && !sketch.isEditor) {
            drawThisFrame(sketch, (int) (screenW + screenW
                    * (1 - ((sketch.frameCount - startFrame)
                    * 1.0 / sceneAnimationFrames + 1))), 0);
        } else {
            drawThisFrame(sketch, 0, 0);
        }

        if (!isRoot) {
            this.backButton.draw(sketch);
            this.homeButton.draw(sketch);
        }
    }

    private void drawThisFrame(Kiosk sketch, int offsetX, int offsetY) {
        GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, offsetX, offsetY);
        this.spokeGraph.draw(sketch, offsetX, offsetY);

        if (isRoot) {
            supplementaryButton.draw(sketch, offsetX, offsetY);
        }
    }
}
