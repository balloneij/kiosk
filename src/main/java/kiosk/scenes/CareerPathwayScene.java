package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SceneAnimationHelper;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.UserScore;
import kiosk.models.ButtonModel;
import kiosk.models.CareerModel;
import kiosk.models.CareerPathwaySceneModel;
import kiosk.models.CreditsSceneModel;
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
    private boolean isRoot = false;

    //Animations
    private int sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
    private boolean clickedBack = false;
    private boolean clickedHome = false;
    private boolean clickedNext = false;
    private boolean clickedMsoe = false;
    private CareerModel desiredCareer;
    private float totalTimeOpening = 0;
    private float totalTimeEnding = 0;
    private float dt = 0;

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

        sceneAnimationMilliseconds = Kiosk.getSettings().sceneAnimationMilliseconds;
        totalTimeOpening = 0;
        totalTimeEnding = 0;

        // Attach user input hooks
        for (ButtonControl careerOption : this.spokeGraph.getButtonControls()) {
            sketch.hookControl(careerOption);
        }

        this.isRoot = sketch.getRootSceneModel().getId().equals(this.model.getId());
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        this.dt = dt;

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

        if ((totalTimeOpening < sceneAnimationMilliseconds) && sceneAnimationMilliseconds != 0) {
            totalTimeOpening += dt * 1000;
        }
        if ((clickedBack || clickedHome || clickedMsoe || clickedNext)
                && sceneAnimationMilliseconds != 0) {
            totalTimeEnding += dt * 1000;
        }

        int[] returnVals = SceneAnimationHelper.sceneAnimationLogicCareerPathwayScene(sketch,
                clickedNext, clickedBack, clickedHome, clickedMsoe,
                totalTimeOpening, totalTimeEnding, sceneAnimationMilliseconds,
                screenW, screenH, desiredCareer);

        switch (returnVals[3]) {
            case 1:
                drawThisFrame(sketch, returnVals[0], returnVals[1]);
                break;
            case 2:
                drawThisFrameCenteredSpoke(sketch, returnVals[0]);
                break;
            case 3:
                drawThisFrameReversedSpoke(sketch, returnVals[0], returnVals[2]);
                break;
            default:
                break;
        }
    }

    private void drawThisFrame(Kiosk sketch, int offsetX, int offsetY) {
        GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, offsetX, offsetY);
        this.spokeGraph.draw(sketch, offsetX, offsetY);

        if (isRoot) {
            supplementaryButton.draw(sketch, offsetX, 0);
        } else {
            if ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.PUSH))
                    || ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.POP))
                    && clickedBack) || clickedHome) {
                homeButton.draw(sketch, offsetX, offsetY);
                backButton.draw(sketch, offsetX, offsetY);
            } else if (clickedMsoe || sketch.getSceneGraph().recentActivity
                    .equals(SceneGraph.RecentActivity.POP)) {
                homeButton.draw(sketch, offsetX, offsetY);
                backButton.draw(sketch);
            } else {
                homeButton.draw(sketch);
                backButton.draw(sketch);
            }
        }
    }

    private void drawThisFrameCenteredSpoke(Kiosk sketch, int offsetX) {
        GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, offsetX, 0);

        float size = screenH - GraphicsUtil.headerY - GraphicsUtil.headerH;
        // Grab careers from the Kiosk and userScore from the SceneGraph
        UserScore userScore = sketch.getUserScore(); // Reference to user's RIASEC scores
        UserScore previousUserScore = sketch.getPreviousUserScore();
        this.careers = userScore.getCareers();

        // Create spokes for each of the careers (weighted based on user's RIASEC scores)
        ButtonModel[] careerButtons = new ButtonModel[careers.length];
        double[] careerWeights = new double[careers.length];

        for (int i = 0; i < careers.length; i++) {
            CareerModel career = careers[i];
            ButtonModel button = new ButtonModel(career.name, "");
            button.isCircle = true;
            careerButtons[i] = button;
            careerWeights[i] = previousUserScore.getCategoryScore(career.riasecCategory)
                    + (((userScore.getCategoryScore(career.riasecCategory)
                    - previousUserScore.getCategoryScore(career.riasecCategory))
                    * ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds)));
        }

        // Create spoke graph
        this.spokeGraph = new SpokeGraph(size,
                screenW / 2f - size / 2,
                GraphicsUtil.headerY + GraphicsUtil.headerH,
                model.centerText,
                careerButtons,
                careerWeights);
        this.spokeGraph.init(sketch);

        // Attach user input hooks
        for (ButtonControl careerOption : this.spokeGraph.getButtonControls()) {
            sketch.hookControl(careerOption);
        }

        this.spokeGraph.draw(sketch, 0, 0);

        if (isRoot) {
            supplementaryButton.draw(sketch, offsetX, 0);
        } else {
            if ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.PUSH))
                    || ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.POP))
                    && clickedBack) || clickedHome) {
                homeButton.draw(sketch, offsetX, 0);
                backButton.draw(sketch, offsetX, 0);
            } else if (clickedMsoe || sketch.getSceneGraph().recentActivity
                    .equals(SceneGraph.RecentActivity.POP)) {
                homeButton.draw(sketch, offsetX, 0);
                backButton.draw(sketch);
            } else {
                homeButton.draw(sketch);
                backButton.draw(sketch);
            }
        }
    }

    private void drawThisFrameReversedSpoke(Kiosk sketch, int offsetX, int headerOffsetX) {
        GraphicsUtil.drawHeader(sketch, model.headerTitle,
                model.headerBody, headerOffsetX, 0);

        float size = screenH - GraphicsUtil.headerY - GraphicsUtil.headerH;
        // Grab careers from the Kiosk and userScore from the SceneGraph
        UserScore userScore = sketch.getUserScore(); // Reference to user's RIASEC scores
        UserScore previousUserScore = sketch.getPreviousUserScore();
        this.careers = userScore.getCareers();

        // Create spokes for each of the careers (weighted based on user's RIASEC scores)
        ButtonModel[] careerButtons = new ButtonModel[careers.length];
        double[] careerWeights = new double[careers.length];

        for (int i = 0; i < careers.length; i++) {
            CareerModel career = careers[i];
            ButtonModel button = new ButtonModel(career.name, "");
            button.isCircle = true;
            careerButtons[i] = button;
            careerWeights[i] = userScore.getCategoryScore(career.riasecCategory)
                    + (((previousUserScore.getCategoryScore(career.riasecCategory)
                    - userScore.getCategoryScore(career.riasecCategory))
                    * ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds)));
        }

        // Create spoke graph
        this.spokeGraph = new SpokeGraph(size,
                screenW / 2f - size / 2,
                GraphicsUtil.headerY + GraphicsUtil.headerH,
                model.centerText,
                careerButtons,
                careerWeights);
        this.spokeGraph.init(sketch);

        // Attach user input hooks
        for (ButtonControl careerOption : this.spokeGraph.getButtonControls()) {
            sketch.hookControl(careerOption);
        }

        this.spokeGraph.draw(sketch, offsetX, 0);

        if (isRoot) {
            supplementaryButton.draw(sketch, offsetX, 0);
        } else {
            if ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.PUSH))
                    || ((sketch.getSceneGraph().getHistorySize() == 2
                    && sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.POP))
                    && clickedBack) || clickedHome) {
                homeButton.draw(sketch, offsetX, 0);
                backButton.draw(sketch, offsetX, 0);
            } else if (clickedMsoe) {
                homeButton.draw(sketch, offsetX, 0);
                backButton.draw(sketch);
            } else {
                homeButton.draw(sketch);
                backButton.draw(sketch);
            }
        }
    }
}
