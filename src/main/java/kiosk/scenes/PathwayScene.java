package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SceneAnimationHelper;
import graphics.SpokeGraph;
import java.util.ArrayList;
import java.util.List;
import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
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
    private int sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
    private SceneAnimationHelper.Clicked clicked;
    private String sceneToGoTo;
    private Riasec riasecToGoTo;
    private FilterGroupModel filterToGoTo;
    private float totalTimeOpening = 0;
    private float totalTimeEnding = 0;
    private float dt = 0;

    private List<int[]> originalButtonColors;

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

        this.originalButtonColors = new ArrayList<>();
        for (ButtonModel buttonModel : model.buttonModels) {
            this.originalButtonColors.add(buttonModel.rgb);
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
        this.isRoot = sketch.getRootSceneModel().getId().equals(this.model.getId());
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

        sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
        totalTimeOpening = 0;
        totalTimeEnding = 0;

        spokeGraph.init(sketch);

        clicked = SceneAnimationHelper.Clicked.NONE;

        spokeGraph.setButtonColors(this.originalButtonColors);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        this.dt = dt;

        for (ButtonControl button : this.spokeGraph.getButtonControls()) {
            if (button.wasClicked()) {
                clicked = SceneAnimationHelper.Clicked.NEXT;
                sceneToGoTo = button.getTarget();
                riasecToGoTo = button.getModel().category;
                filterToGoTo = button.getModel().filter;
                break;
            }
        }

        if (!isRoot) {
            if (this.homeButton.wasClicked()) {
                clicked = SceneAnimationHelper.Clicked.HOME;
            } else if (this.backButton.wasClicked()) {
                clicked = SceneAnimationHelper.Clicked.BACK;
            }
        } else if (this.supplementaryButton.wasClicked()) {
            clicked = SceneAnimationHelper.Clicked.MSOE;
        }
    }

    @Override
    public void draw(Kiosk sketch) {
        Graphics.useGothic(sketch, 48, true);
        // Text Properties
        sketch.textAlign(PConstants.CENTER, PConstants.TOP);
        sketch.fill(0);

        if ((totalTimeOpening < sceneAnimationMilliseconds) && sceneAnimationMilliseconds != 0) {
            totalTimeOpening += dt * 1000;
        }
        if (!clicked.equals(SceneAnimationHelper.Clicked.NONE)
                && sceneAnimationMilliseconds != 0) {
            totalTimeEnding += dt * 1000;
        }

        int[] returnVals = SceneAnimationHelper.sceneAnimationLogic(sketch,
                clicked,
                sceneToGoTo, riasecToGoTo, filterToGoTo,
                totalTimeOpening, totalTimeEnding, sceneAnimationMilliseconds,
                screenW, screenH);
        drawThisFrame(sketch, returnVals[0], returnVals[1]);
    }

    private void drawThisFrame(Kiosk sketch, int offsetX, int offsetY) {
        GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, offsetX, offsetY);
        this.spokeGraph.draw(sketch, offsetX, offsetY);

        if (isRoot) {
            supplementaryButton.draw(sketch, offsetX, offsetY);
        } else {
            if ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.PUSH))
                    || ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.POP))
                    && clicked.equals(SceneAnimationHelper.Clicked.BACK)
                    || clicked.equals(SceneAnimationHelper.Clicked.HOME))) {
                homeButton.draw(sketch, offsetX, offsetY);
                backButton.draw(sketch, offsetX, offsetY);
            } else if (clicked.equals(SceneAnimationHelper.Clicked.MSOE)) {
                homeButton.draw(sketch, offsetX, offsetY);
                backButton.draw(sketch);
            } else {
                homeButton.draw(sketch);
                backButton.draw(sketch);
            }
        }
    }
}
