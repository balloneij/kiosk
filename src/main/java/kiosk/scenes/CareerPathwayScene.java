package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.UserScore;
import kiosk.models.*;
import processing.core.PConstants;

import java.util.List;

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
        // Grab careers from the Kiosk and create buttons
        CareerModel[] careers = sketch.getAllCareers();
        ButtonModel[] buttons = new ButtonModel[careers.length];
        for (int i = 0; i < careers.length; i++) {
            String careerName = careers[i].name;
            ButtonModel button = new ButtonModel(careerName, careerName);
            button.isCircle = true;
            buttons[i] = button;
        }

        // Put career buttons into a spoke graph
        float size = SCREEN_H - GraphicsUtil.HEADER_Y - GraphicsUtil.HEADER_H;
        this.spokeGraph = new SpokeGraph(size,
                SCREEN_W / 2f - size / 2,
                GraphicsUtil.HEADER_Y + GraphicsUtil.HEADER_H,
                model.centerText,
                buttons);

        // Create home and back button
        this.homeButton = GraphicsUtil.initializeHomeButton();
        sketch.hookControl(this.homeButton);
        this.backButton = GraphicsUtil.initializeBackButton(sketch);
        sketch.hookControl(this.backButton);

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

        this.backButton.draw(sketch);
        this.homeButton.draw(sketch);
    }
}
