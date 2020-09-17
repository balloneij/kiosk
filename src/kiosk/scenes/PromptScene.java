package kiosk.scenes;

import java.awt.Point;
import java.awt.Rectangle;
import kiosk.Kiosk;
import kiosk.SceneControl;
import processing.core.PConstants;
import processing.event.MouseEvent;


public class PromptScene implements Scene {

    private String question;
    private String[] answers;
    private Rectangle[] answerButtons;
    private boolean selectionMade;
    private boolean invertedColors;

    /**
     * Asks the user a question.
     * @param question question to be asked
     * @param answers selection for the user to choose from
     * @param invertedColors false for a black background
     */
    public PromptScene(String question, String[] answers, boolean invertedColors) {
        this.question = question;
        this.answers = answers;
        this.answerButtons = new Rectangle[this.answers.length];
        this.selectionMade = false;
        this.invertedColors = invertedColors;
    }

    @Override
    public void init(Kiosk app) {
        app.addMouseReleasedCallback(arg -> this.mouseClicked((MouseEvent) arg));

        // Add 2 to account for the left and right
        int spacing = app.width / (this.answers.length + 1);
        int x = spacing;
        int y = app.height * 3 / 4;
        for (int i = 0; i < this.answerButtons.length; i++) {
            this.answerButtons[i] = new Rectangle(x - 100, y - 25, 200, 50);
            x += spacing;
        }
    }

    @Override
    public void update(float dt, SceneControl sceneControl) {
        if (selectionMade) {
            sceneControl.popScene();
            sceneControl.pushScene(new PromptScene("Which do you like best?", new String[]{
                "Dogs",
                "Cats"
            }, !this.invertedColors));
            sceneControl.pushScene(new WaveSwipeTransition(this.invertedColors));
        }
    }

    @Override
    public void draw(Kiosk app) {
        app.rectMode(PConstants.CENTER);
        app.textAlign(PConstants.CENTER, PConstants.CENTER);

        app.background(this.invertedColors ? 255 : 0);
        app.fill(this.invertedColors ? 0 : 255);
        app.text(this.question, app.width / 2, app.height / 4);

        for (int i = 0; i < this.answers.length; i++) {
            String answer = this.answers[i];
            Rectangle answerButton = this.answerButtons[i];

            app.fill(this.invertedColors ? 0 : 255);
            app.stroke(this.invertedColors ? 0 : 255);
            app.rect(answerButton.x + 100, answerButton.y + 25,
                    answerButton.width, answerButton.height);
            app.fill(this.invertedColors ? 255 : 0);
            app.text(answer, answerButton.x + 100, answerButton.y + 25);
        }
    }

    private void mouseClicked(MouseEvent event) {
        Point point = new Point(event.getX(), event.getY());

        for (int i = 0; i < this.answerButtons.length; i++) {
            Rectangle rect = this.answerButtons[i];
            if (rect.contains(point)) {
                System.out.println("You've selected" + this.answers[i]);
                this.selectionMade = true;
            }
        }
    }
}
