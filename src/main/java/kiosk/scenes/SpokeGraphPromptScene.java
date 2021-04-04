package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.SceneGraph;
import kiosk.Settings;
import kiosk.UserScore;
import kiosk.models.ButtonModel;
import kiosk.models.CareerModel;
import kiosk.models.SpokeGraphPromptSceneModel;
import processing.core.PConstants;


public class SpokeGraphPromptScene implements Scene {

    // Pull constants from the settings
    private static final int SCREEN_W = Kiosk.getSettings().screenW;
    private static final int SCREEN_H = Kiosk.getSettings().screenH;

    // Header
    private static final float HEADER_W = SCREEN_W * 3f / 4;
    private static final float HEADER_H = SCREEN_H / 6f;
    private static final float HEADER_X = (SCREEN_W - HEADER_W) / 2;
    private static final float HEADER_Y = SCREEN_H / 32f;
    private static final float HEADER_CENTER_X = HEADER_X + (HEADER_W / 2);
    private static final float HEADER_CENTER_Y = HEADER_Y + (HEADER_H / 2);
    private static final int HEADER_CURVE_RADIUS = 25;

    // Header title
    private static final int HEADER_TITLE_FONT_SIZE = 24;
    private static final float HEADER_TITLE_Y = HEADER_CENTER_Y - HEADER_TITLE_FONT_SIZE;

    // Header body
    private static final int HEADER_BODY_FONT_SIZE = 16;
    private static final float HEADER_BODY_Y = HEADER_CENTER_Y + HEADER_BODY_FONT_SIZE;

    // Answers
    private static final int ANSWERS_PADDING = 20;
    private static final float ANSWERS_SPOKE_THICKNESS = 2;
    private static final int ANSWERS_MAX = 4;

    private final SpokeGraphPromptSceneModel model;
    private ButtonControl[] answerButtons;
    private final ButtonControl promptButton;
    private SpokeGraph spokeGraph;
    private ButtonControl backButton;
    private ButtonControl homeButton;
    private ButtonControl supplementaryButton;

    /**
     * Creates a Spoke Graph Prompt Scene
     * It has a spoke graph of all the careers in the upper left corner
     * and a spoke graph in the bottom right for taking input.
     * @param model The model to pull data from.
     */
    public SpokeGraphPromptScene(SpokeGraphPromptSceneModel model) {
        this.model = model;
        this.answerButtons = new ButtonControl[this.model.answers.length];

        int headerBottomY = (int) (HEADER_Y + HEADER_H) + 40;
        int answerDiameter = (SCREEN_H - headerBottomY) / 3;
        int answerRadius = answerDiameter / 2;
        int halfHeight = (SCREEN_H - headerBottomY) / 2;

        int answersCenterX = SCREEN_W * 3 / 4;
        int answersCenterY = headerBottomY + halfHeight - 20;

        int answersCount = this.model.answers.length;
        this.answerButtons = new ButtonControl[Math.min(answersCount, ANSWERS_MAX)];

        if (answersCount > 0) {
            this.answerButtons[0] = new ButtonControl(
                    model.answers[0],
                    answersCenterX - answerDiameter * 3 / 2,
                    answersCenterY + answerRadius,
                    answerRadius
            );
        }
        if (answersCount > 1) {
            this.answerButtons[1] = new ButtonControl(
                    model.answers[1],
                    answersCenterX + answerRadius,
                    answersCenterY - answerDiameter * 3 / 2,
                    answerRadius
            );
        }
        if (answersCount > 2) {
            this.answerButtons[2] = new ButtonControl(
                    model.answers[2],
                    answersCenterX + answerRadius,
                    answersCenterY + answerRadius,
                    answerRadius
            );
        }
        if (answersCount > 3) {
            this.answerButtons[3] = new ButtonControl(
                    model.answers[3],
                    answersCenterX - answerDiameter * 3 / 2,
                    answersCenterY - answerDiameter * 3 / 2,
                    answerRadius
            );
        }

        ButtonModel prompt = new ButtonModel();
        prompt.isCircle = true;
        prompt.rgb = new int[]{ 0, 0, 0 };
        prompt.text = this.model.promptText;
        this.promptButton = new ButtonControl(
                prompt,
                answersCenterX - answerRadius,
                answersCenterY - answerRadius,
                answerRadius
        );
        promptButton.setDisabled(true);

        this.backButton = ButtonControl.createBackButton();
        this.homeButton = ButtonControl.createHomeButton();
    }

    @Override
    public void init(Kiosk sketch) {
        final int width = Settings.readSettings().screenW;
        final int height = Settings.readSettings().screenH;

        // Define the size of the square that the spoke graph will fit in
        final double availableHeight = (height - HEADER_Y - HEADER_H);
        final double size = Math.min(width, availableHeight);

        // Reference to current list of careers
        CareerModel[] careers = model.filter.filter(sketch.getAllCareers());
        UserScore userScore = SceneGraph.getUserScore(); // Reference to user's RIASEC scores

        // Create spokes for each of the careers (weighted based on user's RIASEC scores)
        ButtonModel[] careerButtons = new ButtonModel[careers.length];
        double[] careerWeights = new double[careers.length];

        for (int i = 0; i < careers.length; i++) {
            CareerModel career = careers[i];
            careerButtons[i] = new ButtonModel();
            careerButtons[i].text = career.name;
            careerWeights[i] = userScore.getCategoryScore(career.riasecCategory);
        }

        // Create spoke graph
        this.spokeGraph = new SpokeGraph(size, 0, HEADER_Y + HEADER_H,
                this.model.careerCenterText, careerButtons, careerWeights);
        spokeGraph.setDisabled(true);

        for (ButtonControl button : this.answerButtons) {
            button.init(sketch);
            sketch.hookControl(button);
        }

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
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        // Check for button clicks on the scene graph
        for (ButtonControl button : this.answerButtons) {
            if (button.wasClicked()) {
                sceneGraph.pushScene(button.getTarget(), button.getModel().category);
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
        Graphics.drawBubbleBackground(sketch);

        // Draw the white header box
        sketch.fill(255);
        sketch.stroke(255);
        Graphics.drawRoundedRectangle(sketch,
                HEADER_X + HEADER_W / 2, HEADER_Y + HEADER_H / 2,
                HEADER_W, HEADER_H, HEADER_CURVE_RADIUS);

        Graphics.useSansSerifBold(sketch, 48);

        // Draw the title text
        sketch.rectMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.fill(0);
        sketch.stroke(0);
        Graphics.useSansSerifBold(sketch, HEADER_TITLE_FONT_SIZE);
        sketch.text(this.model.headerTitle,
                HEADER_CENTER_X, HEADER_TITLE_Y,
                HEADER_W, HEADER_TITLE_FONT_SIZE * 2);

        // Draw the body text
        Graphics.useSansSerif(sketch, HEADER_BODY_FONT_SIZE);
        sketch.text(this.model.headerBody,
                HEADER_CENTER_X, HEADER_BODY_Y,
                HEADER_W, HEADER_BODY_FONT_SIZE * 2);

        // Calculate answer location constants
        float headerBottomY = HEADER_Y + HEADER_H + 2 * ANSWERS_PADDING;
        int answersCenterX = SCREEN_W * 3 / 4;
        float answersCenterY = headerBottomY + (SCREEN_H - headerBottomY) / 2 - ANSWERS_PADDING;

        // Draw answer buttons
        for (ButtonControl answer : answerButtons) {
            sketch.strokeWeight(ANSWERS_SPOKE_THICKNESS);
            sketch.stroke(255);
            sketch.line(answersCenterX, answersCenterY,
                    answer.getCenterX(), answer.getCenterY());
            answer.draw(sketch);
        }

        // Draw the center prompt button
        this.promptButton.draw(sketch);

        // Draw the career spoke graph
        this.spokeGraph.draw(sketch);

        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            // Draw the back and home buttons
            this.backButton.draw(sketch);
            this.homeButton.draw(sketch);
        } else {
            supplementaryButton.draw(sketch);
        }
    }
}
