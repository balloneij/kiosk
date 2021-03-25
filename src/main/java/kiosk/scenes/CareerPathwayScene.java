package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.UserScore;
import kiosk.models.ButtonModel;
import kiosk.models.CareerModel;
import kiosk.models.CareerPathwaySceneModel;
import processing.core.PConstants;

/**
 * A scene that displays a spoke graph containing buttons for each of the careers currently in
 * the list, weighted based on the career RIASEC type and the UserScore.
 */
public class CareerPathwayScene implements Scene {
    // Pull constants from the settings
    private static final int SCREEN_W = Kiosk.getSettings().screenW;
    private static final int SCREEN_H = Kiosk.getSettings().screenH;

    private final CareerPathwaySceneModel model;
    protected SpokeGraph spokeGraph;
    private ButtonControl backButton;
    private ButtonControl homeButton;
    private ButtonControl supplementaryButton;

    /**
     * Create a pathway scene.
     * @param model to base the scene off of
     */
    public CareerPathwayScene(CareerPathwaySceneModel model) {
        this.model = model;
        this.backButton = ButtonControl.createBackButton();
        this.homeButton = ButtonControl.createHomeButton();
    }

    @Override
    public void init(Kiosk sketch) {
        // Grab careers from the Kiosk and userScore from the SceneGraph
        CareerModel[] careers = model.filter.filter(sketch.getAllCareers());
        UserScore userScore = SceneGraph.getUserScore(); // Reference to user's RIASEC scores

        // Create spokes for each of the careers (weighted based on user's RIASEC scores)
        ButtonModel[] careerButtons = new ButtonModel[careers.length];
        double[] careerWeights = new double[careers.length];

        for (int i = 0; i < careers.length; i++) {
            CareerModel career = careers[i];
            ButtonModel button = new ButtonModel(career.name, career.name);
            button.isCircle = true;
            careerButtons[i] = button;
            careerWeights[i] = userScore.getCategoryScore(career.riasecCategory);
        }

        // Put career buttons into a spoke graph
        float size = SCREEN_H - GraphicsUtil.HEADER_Y - GraphicsUtil.HEADER_H;
        this.spokeGraph = new SpokeGraph(size,
                SCREEN_W / 2f - size / 2,
                GraphicsUtil.HEADER_Y + GraphicsUtil.HEADER_H,
                model.centerText,
                careerButtons,
                careerWeights);

        // Create home and back button
        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            this.homeButton = GraphicsUtil.initializeHomeButton();
            sketch.hookControl(this.homeButton);
            this.backButton = GraphicsUtil.initializeBackButton(sketch);
            sketch.hookControl(this.backButton);
        } else {
            this.supplementaryButton = GraphicsUtil.initializeMsoeButton(sketch);
            this.supplementaryButton.init(sketch);
            sketch.hookControl(this.supplementaryButton);
        }

        // Attach user input hooks
        for (ButtonControl careerOption : this.spokeGraph.getButtonControls()) {
            sketch.hookControl(careerOption);
        }
        sketch.hookControl(this.backButton);
        sketch.hookControl(this.homeButton);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        for (ButtonControl button : this.spokeGraph.getButtonControls()) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget(), button.getModel().category);
            }
        }

        if (this.homeButton.wasClicked()) {
            sceneGraph.reset();
        } else if (this.backButton.wasClicked()) {
            sceneGraph.popScene();
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
            this.homeButton.draw(sketch);
            this.backButton.draw(sketch);
        } else {
            supplementaryButton.draw(sketch);
        }
    }
}
