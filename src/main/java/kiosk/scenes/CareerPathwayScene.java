package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.UserScore;
import kiosk.models.*;
import processing.core.PConstants;

/**
 * A scene that displays a spoke graph containing buttons for each of the careers currently in
 * the list, weighted based on the career RIASEC type and the UserScore.
 */
public class CareerPathwayScene implements Scene {
    // Pull constants from the settings
    private static int screenW = Kiosk.getSettings().screenW;
    private static int screenH = Kiosk.getSettings().screenH;

    private final CareerPathwaySceneModel model;
    protected SpokeGraph spokeGraph;
    private ButtonControl backButton;
    private ButtonControl homeButton;
    private CareerModel[] careers;
    private ButtonModel[] buttons;
    private ButtonControl supplementaryButton;

    //Animations
    private int startFrame = 0;
    private boolean clickedBack = false;
    private boolean clickedHome = false;
    private boolean clickedNext = false;
    private CareerModel desiredCareer;

    /**
     * Create a pathway scene.
     * @param model to base the scene off of
     */
    public CareerPathwayScene(CareerPathwaySceneModel model) {
        this.model = model;
        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;
    }

    @Override
    public void init(Kiosk sketch) {
        // Grab careers from the Kiosk and userScore from the SceneGraph
        UserScore userScore = sketch.getUserScore(); // Reference to user's RIASEC scores
        this.careers = userScore.getCareers();

        // Create spokes for each of the careers (weighted based on user's RIASEC scores)
        ButtonModel[] careerButtons = new ButtonModel[careers.length];
        double[] careerWeights = new double[careers.length];

        for (int i = 0; i < careers.length; i++) {
            CareerModel career = careers[i];
            ButtonModel button = new ButtonModel(career.name, "");
            button.isCircle = true;
            careerButtons[i] = button;
            careerWeights[i] = userScore.getCategoryScore(career.riasecCategory);
        }

        // Put career buttons into a spoke graph
        float size = screenH - GraphicsUtil.headerY - GraphicsUtil.headerH;
        this.spokeGraph = new SpokeGraph(size,
                screenW / 2f - size / 2,
                GraphicsUtil.headerY + GraphicsUtil.headerH,
                model.centerText,
                careerButtons,
                careerWeights);
        this.spokeGraph.init(sketch);

        // Create home and back button
        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton = GraphicsUtil.initializeHomeButton(sketch);
            sketch.hookControl(this.homeButton);
            this.backButton = GraphicsUtil.initializeBackButton(sketch);
            sketch.hookControl(this.backButton);
        } else {
            this.supplementaryButton = GraphicsUtil.initializeMsoeButton(sketch);
            sketch.hookControl(this.supplementaryButton);
        }

        startFrame = sketch.frameCount;

        // Attach user input hooks
        for (ButtonControl careerOption : this.spokeGraph.getButtonControls()) {
            sketch.hookControl(careerOption);
        }
        sketch.hookControl(this.backButton);
        sketch.hookControl(this.homeButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        // Find which button was clicked in the spoke graph
        ButtonControl[] buttons = this.spokeGraph.getButtonControls();
        for (int i = 0; i < buttons.length; i++) {
            ButtonControl button = buttons[i];

            if (button.wasClicked()) {
                // Go to the end scene
                desiredCareer = careers[i];
                clickedNext = true;
                break;
            }
        }

        if (this.homeButton.wasClicked()) {
            clickedHome = true;
        } else if (this.backButton.wasClicked()) {
            clickedBack = true;
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useGothic(sketch, 48, true);
        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.fill(0);
        Graphics.drawBubbleBackground(sketch);

        if (sketch.isEditor) {
            if (clickedNext) {
                sketch.getSceneGraph().pushEndScene(desiredCareer);
            } else if (clickedBack) {
                sketch.getSceneGraph().popScene();
            } else if (clickedHome) {
                sketch.getSceneGraph().reset();
            }
        }

        if ((clickedNext) && !sketch.isEditor) {
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
                sketch.getSceneGraph().pushEndScene(desiredCareer);
            }
        } else if (clickedBack && !sketch.isEditor) {
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
        } else if (clickedHome && !sketch.isEditor) {
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
            this.homeButton.draw(sketch);
            this.backButton.draw(sketch);
        } else {
            supplementaryButton.draw(sketch);
        }
    }
}
