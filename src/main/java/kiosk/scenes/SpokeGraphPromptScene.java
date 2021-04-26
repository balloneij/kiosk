package kiosk.scenes;

import graphics.Graphics;
import graphics.GraphicsUtil;
import graphics.SpokeGraph;
import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.UserScore;
import kiosk.models.ButtonModel;
import kiosk.models.CareerModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.SpokeGraphPromptSceneModel;
import processing.core.PConstants;


public class SpokeGraphPromptScene implements Scene {

    // Pull constants from the settings
    private static int screenW = Kiosk.getSettings().screenW;
    private static int screenH = Kiosk.getSettings().screenH;

    // Header
    private static float headerW = screenW * 3f / 4;
    private static float headerH = screenH / 6f;
    private static float headerX = (screenW - headerW) / 2;
    private static float headerY = screenH / 32f;
    private static float headerCenterX = headerX + (headerW / 2);
    private static float headerCenterY = headerY + (headerH / 2);
    private static int headerCurveRadius = 25;

    // Header title
    private static int headerTitleFontSize = screenW / 55;
    private static float headerTitleY = headerCenterY - headerTitleFontSize;

    // Header body
    private static int headerBodyFontSize = screenW / 60;
    private static float headerBodyY = headerCenterY + headerBodyFontSize;

    // Answers
    private static int answersPadding = screenW / 58;
    private static final int ANSWER_IMAGE_PADDING = 20;
    private static float answersSpokeThickness = 2;
    private static int answersMax = 4;

    private final SpokeGraphPromptSceneModel model;
    private ButtonControl[] answerButtons;
    private final ButtonControl promptButton;
    private SpokeGraph spokeGraph;
    private ButtonControl backButton;
    private ButtonControl homeButton;
    private ButtonControl supplementaryButton;

    //Animations
    private int startFrame = 0;

    /**
     * Creates a Spoke Graph Prompt Scene
     * It has a spoke graph of all the careers in the upper left corner
     * and a spoke graph in the bottom right for taking input.
     * @param model The model to pull data from.
     */
    public SpokeGraphPromptScene(SpokeGraphPromptSceneModel model) {
        this.model = model;

        // Pull constants from the settings
        screenW = Kiosk.getSettings().screenW;
        screenH = Kiosk.getSettings().screenH;

        // Header
        headerW = screenW * 3f / 4;
        headerH = screenH / 6f;
        headerX = (screenW - headerW) / 2;
        headerY = screenH / 32f;
        headerCenterX = headerX + (headerW / 2);
        headerCenterY = headerY + (headerH / 2);
        headerCurveRadius = 25;

        // Header title
        headerTitleFontSize = screenW / 55;
        headerTitleY = headerCenterY - headerTitleFontSize;

        // Header body
        headerBodyFontSize = screenW / 60;
        headerBodyY = headerCenterY + headerBodyFontSize;

        // Answers
        answersPadding = screenW / 58;
        answersSpokeThickness = 2;
        answersMax = 4;

        this.answerButtons = new ButtonControl[this.model.answers.length];

        int headerBottomY = (int) (headerY + headerH) + 40;
        int answerDiameter = (screenH - headerBottomY) / 3;
        int answerRadius = answerDiameter / 2;
        int halfHeight = (screenH - headerBottomY) / 2;

        int answersCenterX = screenW * 3 / 4;
        int answersCenterY = headerBottomY + halfHeight - 20;

        int answersCount = this.model.answers.length;
        this.answerButtons = new ButtonControl[Math.min(answersCount, answersMax)];

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

        for (int i = 0; i < answersCount; i++) {
            if (model.answers[i].image != null) {
                model.answers[i].image.width = answerDiameter - ANSWER_IMAGE_PADDING;
                model.answers[i].image.height = answerDiameter - ANSWER_IMAGE_PADDING;
            }
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
    }

    @Override
    public void init(Kiosk sketch) {

        // Define the size of the square that the spoke graph will fit in
        final double availableHeight = (screenH - headerY - headerH);
        final double size = Math.min(screenW, availableHeight);

        // Reference to current list of careers
        UserScore userScore = sketch.getUserScore(); // Reference to user's RIASEC scores
        CareerModel[] careers = userScore.getCareers();

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
        this.spokeGraph = new SpokeGraph(size, 0, headerY + headerH,
                this.model.careerCenterText, careerButtons, careerWeights);
        spokeGraph.setDisabled(true);
        spokeGraph.init(sketch);

        for (ButtonControl button : this.answerButtons) {
            button.init(sketch);
            sketch.hookControl(button);
        }

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

        this.promptButton.init(sketch);
    }

    @Override
    public void update(float dt, SceneGraph sceneGraph) {
        // Check for button clicks on the scene graph
        for (ButtonControl button : this.answerButtons) {
            if (button.wasClicked()) {
                String scene = button.getTarget();
                Riasec riasec = button.getModel().category;
                FilterGroupModel filter = button.getModel().filter;
                sceneGraph.pushScene(scene, riasec, filter);
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

        //If this scene is new, animate the spokes to gradually change
        if (sketch.frameCount - startFrame <= Kiosk.getSettings().sceneAnimationFrames) {
            GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, 0);

            // Calculate answer location constants
            float headerBottomY = headerY + headerH + 2 * answersPadding;
            int answersCenterX = (screenW * 3 / 4);
            float answersCenterY = headerBottomY + (screenH - headerBottomY) / 2 - answersPadding;

            // Draw answer buttons
            for (ButtonControl answer : answerButtons) {
                sketch.strokeWeight(answersSpokeThickness);
                sketch.stroke(255);
                sketch.line(answersCenterX, answersCenterY,
                        answer.getCenterX(), answer.getCenterY());
                answer.draw(sketch,  0);
            }

            // Draw the center prompt button
            this.promptButton.draw(sketch,  0);

            // Draw the career spoke graph
            // Define the size of the square that the spoke graph will fit in
            final double availableHeight = (screenH - headerY - headerH);
            final double size = Math.min(screenW, availableHeight);
            // Reference to current list of careers
            UserScore userScore = sketch.getUserScore(); // Reference to user's RIASEC scores
            UserScore previousUserScore = sketch.getPreviousUserScore();
            CareerModel[] careers = userScore.getCareers();

            // Create spokes for each of the careers (weighted based on user's RIASEC scores)
            ButtonModel[] careerButtons = new ButtonModel[careers.length];
            double[] careerWeights = new double[careers.length];

            for (int i = 0; i < careers.length; i++) {
                CareerModel career = careers[i];
                careerButtons[i] = new ButtonModel();
                careerButtons[i].text = career.name;
                careerWeights[i] = previousUserScore.getCategoryScore(career.riasecCategory)
                        + ((userScore.getCategoryScore(career.riasecCategory)
                        - previousUserScore.getCategoryScore(career.riasecCategory))
                        / (sketch.frameCount - startFrame * 1.0f));
            }

            // Create spoke graph
            SpokeGraph sg = new SpokeGraph(size, 0, headerY + headerH,
                    this.model.careerCenterText, careerButtons, careerWeights);
            sg.setDisabled(true);
            sg.init(sketch);
            sg.draw(sketch, 0);
        } else { //If it's already a second-or-two old, draw the scene normally
            GraphicsUtil.drawHeader(sketch, model.headerTitle, model.headerBody, 0);

            // Calculate answer location constants
            float headerBottomY = headerY + headerH + 2 * answersPadding;
            int answersCenterX = screenW * 3 / 4;
            float answersCenterY = headerBottomY + (screenH - headerBottomY) / 2 - answersPadding;

            // Draw answer buttons
            for (ButtonControl answer : answerButtons) {
                sketch.strokeWeight(answersSpokeThickness);
                sketch.stroke(255);
                sketch.line(answersCenterX, answersCenterY,
                        answer.getCenterX(), answer.getCenterY());
                answer.draw(sketch);
            }

            // Draw the center prompt button
            this.promptButton.draw(sketch);

            // Draw the career spoke graph
            spokeGraph.draw(sketch, 0);
        }

        if (!sketch.getRootSceneModel().getId().equals(this.model.getId())) {
            // Draw the back and home buttons
            this.backButton.draw(sketch);
            this.homeButton.draw(sketch);
        } else {
            supplementaryButton.draw(sketch);
        }
    }
}
