package graphics;

import java.util.Random;
import kiosk.Kiosk;
import kiosk.models.ImageModel;
import kiosk.scenes.Image;
import kiosk.scenes.Scene;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class Boop {

    private final float screenBoundaryFraction = 6f;
    private final int randomBounds = 100;
    private final int randomGoLeftChance = 50;
    private final int randomSwapSpeedChance = 50;
    private final int randomSwapAnimationChance = 50;
    private final int randomRoscoeAnimationChance = 33;
    private final double choiceFrequencyInSeconds = 3.5f; //TODO
    private final float stoppingBreakInSeconds = 1.5f; //TODO
    private final int movementPercentage = 75;
    private final int limbMovementRange = 1;
    private final float speedSlow = 0.5f;
    private final float speedFast = 0.75f;
    private final float speedZoom = 2.5f;
    private final int minimumShellFrames = 100;
    private final double minimumShellSeconds = 1.5f; //TODO
    private final float minimumHappySeconds = 4.5f; //TODO
    private final int minimumBlinkFrames = 30;
    private final float minimumBlinkSeconds = 0.5f; //TODO
    private final int framesBetweenBlinks = 200;
    private final float secondsBetweenBlinks = 3.5f; //TODO

    private int width;
    private int boopDimens;
    private float minX;
    private float maxX;

    private Image lbFoot;
    private Image lfFoot;
    private Image rbFoot;
    private Image rfFoot;
    private Image leftHideLeg;
    private Image rightHideLeg;
    private Image shell;
    private Image shellHide;
    private Image head;
    private Image headPeek;
    private Image headPeek1;
    private Image headPeek2;
    private Image headHappy;
    private Image headHappyBlink;
    private Image headBlink;
    private Image headLook;
    private Image headLookBlink;
    private Image headRoscoe;

    private Image lbFootR;
    private Image lfFootR;
    private Image rbFootR;
    private Image rfFootR;
    private Image leftHideLegR;
    private Image rightHideLegR;
    private Image shellR;
    private Image shellHideR;
    private Image headR;
    private Image headPeekR;
    private Image headPeek1R;
    private Image headPeek2R;
    private Image headHappyR;
    private Image headHappyBlinkR;
    private Image headBlinkR;
    private Image headLookR;
    private Image headLookBlinkR;
    private Image headRoscoeR;

    private boolean choseLeft;
    private boolean choseShake;
    private boolean choseRoscoeAnimation;

    private enum BoopState {
        HAPPY_LEFT, HAPPY_RIGHT,
        SCOOT_LEFT, SCOOT_RIGHT,
        TIPTOE_LEFT, TIPTOE_RIGHT,
        IN_SHELL_LEFT, IN_SHELL_RIGHT,
        STATIC_LEFT, STATIC_RIGHT
    }

    private BoopState boopState;

    private float currentX;
    private float currentY;
    private int timesNotMoved = 0;
    private float totalMovementTime;
    private float totalStoppedTime;
    private int firstMovementFrame;
    private float firstMovementTime; //TODO
    private int additionalShellFrames;
    private float additionalShellSeconds; //TODO
    private int lastClickedFrame;
    private float lastClickedTime; //TODO
    private float totalHappyTime = -1;
    private float additionalHappySeconds;
    private boolean shouldZoomAway;
    private boolean startedHappyAnimation = false;
    private final Random rand = new Random();

    public Boop() {
        this.boopState = BoopState.STATIC_LEFT;
    }

    /**
     * Calculate if Boop should turn around, start/stop moving,
     * and figure out if he's happy (arrival to CareerDescriptionScene).
     * Boop will never walk off screen.
     * @param sketch to draw to
     * @param currentScene the scene the user is currently viewing
     * @param dt the number of seconds that have passed since the last draw
     */
    public void movementLogic(Kiosk sketch, Scene currentScene, float dt) {
        minX = width / screenBoundaryFraction;
        maxX = width * ((screenBoundaryFraction - 1) / screenBoundaryFraction);
        if (currentScene.getClass().toString().contains("CareerDescriptionScene")
                && !startedHappyAnimation) {
            totalHappyTime = 0;
            additionalHappySeconds = rand.nextFloat();
            if (choseLeft) {
                boopState = BoopState.HAPPY_LEFT;
            } else {
                boopState = BoopState.HAPPY_RIGHT;
            }
        }
        if (!currentScene.getClass().toString().contains("CareerDescriptionScene")) {
            startedHappyAnimation = false;
        }
        if (currentScene.getClass().toString().contains("SpokeGraphPromptScene")) {
            minX = width / 15f * 6;
            maxX = width / 15f * 7.5f;
        }
        if (currentScene.getClass().toString().contains("PathwayScene")
                || currentScene.getClass().toString().contains("CareerPathwayScene")) {
            minX = width / 15f * 9;
            maxX = width * ((screenBoundaryFraction - 1) / screenBoundaryFraction);
        }
        boolean choseScootAnimation;
        if (currentX >= width * (screenBoundaryFraction - 1 / screenBoundaryFraction)
                || currentX >= maxX) {
            //Boop is too close to the right edge of the screen!
            //Somebody tell him to stop!
            //oh god he can't hear us he has airpods in oh god
            //Oh the hu-manatee!
            choseLeft = true;
            boopState = BoopState.SCOOT_LEFT;
            shouldZoomAway = true;
        } else if (currentX <= width / screenBoundaryFraction || currentX <= minX) {
            //Boop is too close to the left edge of the screen!
            //Somebody tell him to stop!
            //oh god he can't hear us he has airpods in oh god
            //Oh the hu-manatee!
            choseLeft = false;
            boopState = BoopState.SCOOT_RIGHT;
            shouldZoomAway = true;
        } else {
            shouldZoomAway = false;
        }
        if ((totalMovementTime >= choiceFrequencyInSeconds
                || totalStoppedTime >= choiceFrequencyInSeconds)
                && !boopState.equals(BoopState.IN_SHELL_LEFT)
                && !boopState.equals(BoopState.IN_SHELL_RIGHT)) {
            if (!boopState.equals(BoopState.HAPPY_RIGHT)
                    && !boopState.equals(BoopState.HAPPY_LEFT)) {
                if (boopState.equals(BoopState.SCOOT_LEFT)
                        || boopState.equals(BoopState.SCOOT_RIGHT)
                        || boopState.equals(BoopState.TIPTOE_LEFT)
                        || boopState.equals(BoopState.TIPTOE_RIGHT)) {
                    int randInt = rand.nextInt(randomBounds);
                    if (randInt >= movementPercentage
                            && totalMovementTime >= choiceFrequencyInSeconds) {
                        //If Boop randomly decides to stop...
                        if (choseLeft) {
                            boopState = BoopState.STATIC_LEFT;
                        } else {
                            boopState = BoopState.STATIC_RIGHT;
                        }
                        totalMovementTime = 0;
                        totalStoppedTime = 0;
                    } else {
                        //If Boop keeps moving...
                        totalMovementTime += dt;
                    }
                } else {
                    if (totalStoppedTime >= stoppingBreakInSeconds) {
                        //Boop must stay put for at least
                        // stoppingBreakInSeconds seconds after stopping.
                        int randInt = rand.nextInt(randomBounds) + timesNotMoved;
                        if (randInt >= movementPercentage) {
                            //If Boop randomly decides to start moving...
                            //Randomly decide if he should start moving left or right.
                            firstMovementFrame = sketch.frameCount;
                            timesNotMoved = 0;

                            randInt = rand.nextInt(randomBounds);
                            choseLeft = randInt >= randomGoLeftChance;
                            randInt = rand.nextInt(randomBounds);
                            choseScootAnimation = randInt >= randomSwapAnimationChance;
                            if (choseLeft) {
                                if (choseScootAnimation) {
                                    boopState = BoopState.SCOOT_LEFT;
                                } else {
                                    boopState = BoopState.TIPTOE_LEFT;
                                }
                            } else {
                                if (choseScootAnimation) {
                                    boopState = BoopState.SCOOT_RIGHT;
                                } else {
                                    boopState = BoopState.TIPTOE_RIGHT;
                                }
                            }
                            totalMovementTime = 0;
                            totalStoppedTime = 0;
                        } else {
                            //If Boop randomly decides to stay where he is...
                            //Make it more likely that he will move next time.
                            timesNotMoved++;
                            totalStoppedTime += dt;
                        }
                    }
                }
            }
        } else if (boopState.equals(BoopState.SCOOT_LEFT)
                || boopState.equals(BoopState.SCOOT_RIGHT)
                || boopState.equals(BoopState.TIPTOE_LEFT)
                || boopState.equals(BoopState.TIPTOE_RIGHT)) {
            totalMovementTime += dt;
            totalStoppedTime = 0;
        } else {
            totalStoppedTime += dt;
            totalMovementTime = 0;
        }
        drawThisFrame(sketch, dt);
    }

    /**
     * Draws the current frame of Boop's animation.
     * @param sketch to draw to
     * @param dt the number of seconds that have passed since the last draw
     */
    private void drawThisFrame(Kiosk sketch, float dt) {
        if (boopState.equals(BoopState.SCOOT_LEFT)
                || boopState.equals(BoopState.SCOOT_RIGHT)
                || boopState.equals(BoopState.TIPTOE_LEFT)
                || boopState.equals(BoopState.TIPTOE_RIGHT)) {
            //Boop is moving, draw him somewhere else depending on his speed.
            if (shouldZoomAway) {
                drawBoop(sketch, speedZoom, dt);
            } else if (rand.nextInt(randomBounds) >= randomSwapSpeedChance) {
                drawBoop(sketch, speedSlow, dt);
            } else {
                drawBoop(sketch, speedFast, dt);
            }
        } else {
            //Boop isn't moving right or left, draw him where he currently is.
            drawBoop(sketch, 0, dt);
        }
    }

    /**
     * Draws Boop, along with all of his leg animations depending
     * on frame count, speed, animation style chosen, etc.
     * @param sketch to draw to
     * @param amountToMove the amount that Boop should move across the screen each frame
     * @param dt the number of seconds that have passed since the last draw
     */
    private void drawBoop(Kiosk sketch, float amountToMove, float dt) {
        sketch.imageMode(PConstants.CENTER);
        int frameCheck = sketch.frameCount % framesBetweenBlinks;
        switch (boopState) {
            case SCOOT_LEFT:
                if (frameCheck <= minimumBlinkFrames) {
                    scootAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot,
                            shell, headLookBlink, 0 - amountToMove);
                } else {
                    scootAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot,
                            shell, headLook, 0 - amountToMove);
                }
                break;
            case SCOOT_RIGHT:
                if (frameCheck <= minimumBlinkFrames) {
                    scootAnimation(sketch, lbFootR, lfFootR, rbFootR, rfFootR,
                            shellR, headLookBlinkR, amountToMove);
                } else {
                    scootAnimation(sketch, lbFootR, lfFootR, rbFootR, rfFootR,
                            shellR, headLookR, amountToMove);
                }
                break;
            case TIPTOE_LEFT:
                if (frameCheck <= minimumBlinkFrames) {
                    tiptoeAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot,
                            shell, headLookBlink, 0 - amountToMove);
                } else {
                    tiptoeAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot,
                            shell, headLook, 0 - amountToMove);
                }
                break;
            case TIPTOE_RIGHT:
                if (frameCheck <= minimumBlinkFrames) {
                    tiptoeAnimation(sketch, lbFootR, lfFootR, rbFootR, rfFootR,
                            shellR, headLookBlinkR, amountToMove);
                } else {
                    tiptoeAnimation(sketch, lbFootR, lfFootR, rbFootR, rfFootR,
                            shellR, headLookR, amountToMove);
                }
                break;
            case STATIC_LEFT:
                if (frameCheck <= minimumBlinkFrames) {
                    staticAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot, shell, headBlink);
                } else {
                    staticAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot, shell, head);
                }
                break;
            case STATIC_RIGHT:
                if (frameCheck <= minimumBlinkFrames) {
                    staticAnimation(sketch, lbFootR, lfFootR,
                            rbFootR, rfFootR, shellR, headBlinkR);
                } else {
                    staticAnimation(sketch, lbFootR, lfFootR,
                            rbFootR, rfFootR, shellR, headR);
                }
                break;
            case HAPPY_LEFT:
                if (frameCheck <= minimumBlinkFrames) {
                    staticAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot, shell, headHappyBlink);
                } else {
                    staticAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot, shell, headHappy);
                }
                startedHappyAnimation = true;
                totalHappyTime += dt;
                if (totalHappyTime >= minimumHappySeconds + additionalHappySeconds) {
                    //Boop is currently experiencing depression...
                    //He's been happy for more than enough frames, put him out of his misery..
                    boopState = BoopState.STATIC_LEFT;
                }
                break;
            case HAPPY_RIGHT:
                if (frameCheck <= minimumBlinkFrames) {
                    staticAnimation(sketch, lbFootR, lfFootR, rbFootR, rfFootR,
                            shellR, headHappyBlinkR);
                } else {
                    staticAnimation(sketch, lbFootR, lfFootR, rbFootR, rfFootR,
                            shellR, headHappyR);
                }
                startedHappyAnimation = true;
                totalHappyTime += dt;
                if (totalHappyTime >= minimumHappySeconds + additionalHappySeconds) {
                    //Boop is currently experiencing depression...
                    //He's been happy for more than enough frames, put him out of his misery..
                    boopState = BoopState.STATIC_RIGHT;
                }
                break;
            case IN_SHELL_LEFT:
                if (choseRoscoeAnimation) {
                    staticAnimation(sketch, lbFoot, lfFoot, rbFoot, rfFoot, shell, headRoscoe);
                } else if (choseShake) {
                    inShellAnimation2(sketch, shellHide, shellHideR);
                } else {
                    inShellAnimation(sketch, shellHide, headPeek,
                            headPeek1, headPeek2, leftHideLeg, rightHideLeg);
                }
                if (sketch.frameCount >= lastClickedFrame
                        + minimumShellFrames + additionalShellFrames) {
                    //Boop is exiting his shell...
                    boopState = BoopState.STATIC_LEFT;
                }
                break;
            case IN_SHELL_RIGHT:
                if (choseRoscoeAnimation) {
                    staticAnimation(sketch, lbFootR, lfFootR, rbFootR,
                            rfFootR, shellR, headRoscoeR);
                } else if (choseShake) {
                    inShellAnimation2(sketch, shellHideR, shellHide);
                } else {
                    inShellAnimation(sketch, shellHideR, headPeekR,
                            headPeek1R, headPeek2R, leftHideLegR, rightHideLegR);
                }
                if (sketch.frameCount >= lastClickedFrame
                        + minimumShellFrames + additionalShellFrames) {
                    //Boop is exiting his shell...
                    boopState = BoopState.STATIC_RIGHT;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + boopState);
        }
    }

    /**
     *  Draws Boop amidst his scooting animation.
     * @param sketch to draw to
     * @param lbFoot the left back foot to be used
     * @param lfFoot the left front foot to be used
     * @param rbFoot the right back foot to be used
     * @param rfFoot the right front foot to be used
     * @param shell the shell to be used
     * @param head the head to be used
     * @param amountToMove the amount Boop should move every frame
     */
    private void scootAnimation(Kiosk sketch, Image lbFoot, Image lfFoot, Image rbFoot,
                                Image rfFoot, Image shell, Image head, float amountToMove) {
        int frameNumber = (sketch.frameCount - firstMovementFrame) % 12;
        if (totalMovementTime >= 0) { //TODO FIX ANIMATIONS
//        if (frameNumber == 0 || frameNumber == 1) {
            //Right front foot and left back foot forwards a bit
            lbFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange, currentY);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange, currentY);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 2 || frameNumber == 3) {
            //Right front foot and left back foot forwards a lot
            lbFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 4 || frameNumber == 5) {
            //Right front foot and left back foot forwards a lot,
            // body and head forwards a bit
            lbFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            shell.draw(sketch, currentX + amountToMove
                    + limbMovementRange, currentY);
            head.draw(sketch, currentX + amountToMove
                    + limbMovementRange, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 6 || frameNumber == 7) {
            //Right front foot, left back foot, body and head forwards a lot
            lbFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            shell.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            head.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 8 || frameNumber == 9) {
            //Right front foot, left back foot, body and head forwards a lot,
            // left front foot and right back foot forwards a bit
            lbFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            lfFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange, currentY);
            rbFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange, currentY);
            rfFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            shell.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            head.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 10 || frameNumber == 11) {
            //Right front foot, left, back foot, body, head,
            // left front foot and right back foot forwards a lot
            lbFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            lfFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            rbFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            rfFoot.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            shell.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            head.draw(sketch, currentX + amountToMove
                    + limbMovementRange + limbMovementRange, currentY);
            currentX = currentX + amountToMove;
        }
        if (frameNumber == 11) {
            currentX = currentX + limbMovementRange + limbMovementRange;
        }
    }

    /**
     * Draws Boop amidst his Tiptoe animation.
     * @param sketch to draw to
     * @param lbFoot the left back foot to be used
     * @param lfFoot the left front foot to be used
     * @param rbFoot the right back foot to be used
     * @param rfFoot the right front foot to be used
     * @param shell the shell to be used
     * @param head the head to be used
     * @param amountToMove the amount Boop should move every frame
     */
    private void tiptoeAnimation(Kiosk sketch, Image lbFoot, Image lfFoot, Image rbFoot,
                                 Image rfFoot, Image shell, Image head, float amountToMove) {
        int frameNumber = (sketch.frameCount - firstMovementFrame) % 32;
        if (totalMovementTime >= 0) { //TODO FIX ANIMATIONS
//        if (frameNumber == 0 || frameNumber == 1
//                || frameNumber == 2 || frameNumber == 3) {
            //Right front foot up
            lbFoot.draw(sketch, currentX + amountToMove, currentY);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove,
                    currentY - limbMovementRange * 2);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 4 || frameNumber == 5
                || frameNumber == 6 || frameNumber == 7) {
            //Right front foot and left back foot up
            lbFoot.draw(sketch, currentX + amountToMove,
                    currentY - limbMovementRange * 2);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove,
                    currentY - limbMovementRange * 2);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 8 || frameNumber == 9
                || frameNumber == 10 || frameNumber == 11) {
            //Left back foot up
            lbFoot.draw(sketch, currentX + amountToMove,
                    currentY - limbMovementRange * 2);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove, currentY);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 12 || frameNumber == 13
                || frameNumber == 14 || frameNumber == 15
                || frameNumber == 28 || frameNumber == 29
                || frameNumber == 30 || frameNumber == 31) {
            //All feet down
            lbFoot.draw(sketch, currentX + amountToMove, currentY);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove, currentY);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 16 || frameNumber == 17
                || frameNumber == 18 || frameNumber == 19) {
            //Left front foot up
            lbFoot.draw(sketch, currentX + amountToMove, currentY);
            lfFoot.draw(sketch, currentX + amountToMove,
                    currentY - limbMovementRange * 2);
            rbFoot.draw(sketch, currentX + amountToMove, currentY);
            rfFoot.draw(sketch, currentX + amountToMove, currentY);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 20 || frameNumber == 21
                || frameNumber == 22 || frameNumber == 23) {
            //Left front foot and right back foot up
            lbFoot.draw(sketch, currentX + amountToMove, currentY);
            lfFoot.draw(sketch, currentX + amountToMove,
                    currentY - limbMovementRange * 2);
            rbFoot.draw(sketch, currentX + amountToMove,
                    currentY - limbMovementRange * 2);
            rfFoot.draw(sketch, currentX + amountToMove, currentY);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        } else if (frameNumber == 24 || frameNumber == 25
                || frameNumber == 26 || frameNumber == 27) {
            //Right back foot up
            lbFoot.draw(sketch, currentX + amountToMove, currentY);
            lfFoot.draw(sketch, currentX + amountToMove, currentY);
            rbFoot.draw(sketch, currentX + amountToMove,
                    currentY - limbMovementRange * 2);
            rfFoot.draw(sketch, currentX + amountToMove, currentY);
            shell.draw(sketch, currentX + amountToMove, currentY);
            head.draw(sketch, currentX + amountToMove, currentY);
            currentX = currentX + amountToMove;
        }
    }

    /**
     * Draws Boop amidst his non-moving animation.
     * @param sketch to draw to
     * @param lbFoot the left back foot to be used
     * @param lfFoot the left front foot to be used
     * @param rbFoot the right back foot to be used
     * @param rfFoot the right front foot to be used
     * @param shell the shell to be used
     * @param head the head to be used
     */
    private void staticAnimation(Kiosk sketch, Image lbFoot, Image lfFoot, Image rbFoot,
                                 Image rfFoot, Image shell, Image head) {
        lbFoot.draw(sketch, currentX, currentY);
        lfFoot.draw(sketch, currentX, currentY);
        rbFoot.draw(sketch, currentX, currentY);
        rfFoot.draw(sketch, currentX, currentY);
        shell.draw(sketch, currentX, currentY);
        head.draw(sketch, currentX, currentY);
    }

    /**
     * Draws Boop amidst his hiding-in-shell animation where he looks around at the end.
     * @param sketch to draw to
     * @param shellEmpty the empty shell to be used
     * @param shellPeek  Boop's head popping out to use
     * @param shellPeek1 Boop's head popping out,
     *                   looking to one side to use
     * @param shellPeek2 Boop's head popping out,
     *                   looking to the other side to use
     * @param leftLeg    Boop's left leg to draw
     * @param rightLeg   Boop's right leg to draw
     */
    private void inShellAnimation(Kiosk sketch, Image shellEmpty,
                                  Image shellPeek, Image shellPeek1, Image shellPeek2,
                                  Image leftLeg, Image rightLeg) {
        int frameNumber = (sketch.frameCount - lastClickedFrame)
                % (minimumShellFrames + additionalShellFrames);
        if (totalStoppedTime >= 0) { //TODO FIX ANIMATIONS
//        if (frameNumber == 0) {
            leftLeg.draw(sketch, currentX, currentY + boopDimens / 10f);
            rightLeg.draw(sketch, currentX, currentY + boopDimens / 10f);
            shellEmpty.draw(sketch, currentX, currentY + boopDimens / 10f);
            shellPeek2.draw(sketch, currentX, currentY + boopDimens / 10f);
        } else if (frameNumber <= ((minimumShellFrames + additionalShellFrames) / 3f)) {
            shellEmpty.draw(sketch, currentX, currentY + boopDimens / 10f);
        } else if (frameNumber <= ((minimumShellFrames + additionalShellFrames) / 2f)) {
            shellEmpty.draw(sketch, currentX, currentY + boopDimens / 10f);
            shellPeek.draw(sketch, currentX, currentY + boopDimens / 10f);
        } else if (frameNumber <= ((minimumShellFrames + additionalShellFrames) / 3f * 2)) {
            leftLeg.draw(sketch, currentX, currentY + boopDimens / 10f);
            rightLeg.draw(sketch, currentX, currentY + boopDimens / 10f);
            shellEmpty.draw(sketch, currentX, currentY + boopDimens / 10f);
            shellPeek1.draw(sketch, currentX, currentY + boopDimens / 10f);
        } else {
            leftLeg.draw(sketch, currentX, currentY + boopDimens / 10f);
            rightLeg.draw(sketch, currentX, currentY + boopDimens / 10f);
            shellEmpty.draw(sketch, currentX, currentY + boopDimens / 10f);
            shellPeek2.draw(sketch, currentX, currentY + boopDimens / 10f);
        }
    }

    /**
     * Draws Boop amidst his hiding-in-shell animation where he shakes at the end.
     * @param sketch to draw to
     * @param shellEmpty the empty shell to be used
     * @param shellEmpty2 the empty shell facing the opposite way to be used
     */
    private void inShellAnimation2(Kiosk sketch, Image shellEmpty,
                                  Image shellEmpty2) {
        int frameNumber = (sketch.frameCount - lastClickedFrame)
                % (minimumShellFrames + additionalShellFrames);
        if (totalStoppedTime >= 0) { //TODO FIX ANIMATIONS
//        if (frameNumber == 0) {
            shellEmpty.draw(sketch, currentX, currentY + boopDimens / 10f);
        } else if (frameNumber <= ((minimumShellFrames + additionalShellFrames) / 2f)) {
            shellEmpty.draw(sketch, currentX, currentY + boopDimens / 10f);
        } else if (frameNumber > ((minimumShellFrames + additionalShellFrames) / 2f)
                && (frameNumber % 10 == 0 || frameNumber % 10 == 1 || frameNumber % 10 == 2
                || frameNumber % 10 == 3 || frameNumber % 10 == 4)) {
            shellEmpty2.draw(sketch, currentX, currentY + boopDimens / 10f);
        } else {
            shellEmpty.draw(sketch, currentX, currentY + boopDimens / 10f);
        }
    }


    /**
     * Compares Boop's location to the tap's location.
     * @param event the mouse tap just registered
     */
    public void checkTap(Kiosk sketch, MouseEvent event) {
        if (currentX >= event.getX() - boopDimens / 2f
                && currentX <= event.getX() + boopDimens / 2f) {
            if (currentY >= event.getY() - boopDimens / 2f
                    && currentY <= event.getY() + boopDimens / 2f) {
                lastClickedFrame = sketch.frameCount;
                additionalShellFrames = rand.nextInt(10);
                additionalShellSeconds = rand.nextFloat();
                choseRoscoeAnimation = false;
                if (sketch.getSceneGraph().getCurrentSceneModel().getName().contains("Credits")) {
                    if (rand.nextInt(randomBounds) < randomRoscoeAnimationChance) {
                        choseRoscoeAnimation = true;
                    }
                }
                if (!choseRoscoeAnimation) {
                    if (rand.nextInt(randomBounds) > randomSwapAnimationChance) {
                        choseShake = true;
                    } else {
                        choseShake = false;
                    }
                }
                if (choseLeft) {
                    boopState = BoopState.IN_SHELL_LEFT;
                } else {
                    boopState = BoopState.IN_SHELL_RIGHT;
                }
            }
        }
    }

    /**
     * Loads all images in so they aren't loaded on every drawn frame.
     * @param sketch to draw to
     */
    public void loadVariables(Kiosk sketch) {
        width = Kiosk.getSettings().screenW;
        int height = Kiosk.getSettings().screenH;
        boopDimens = height / 8;

        lbFoot = Image.createImage(sketch,
                new ImageModel("assets/boop/Left_Back_Foot.png", boopDimens, boopDimens));
        lfFoot = Image.createImage(sketch,
                new ImageModel("assets/boop/Left_Front_Foot.png", boopDimens, boopDimens));
        rbFoot = Image.createImage(sketch,
                new ImageModel("assets/boop/Right_Back_Foot.png", boopDimens, boopDimens));
        rfFoot = Image.createImage(sketch,
                new ImageModel("assets/boop/Right_Front_Foot.png", boopDimens, boopDimens));
        leftHideLeg = Image.createImage(sketch,
                new ImageModel("assets/boop/Left_Leg.png", boopDimens, boopDimens));
        rightHideLeg = Image.createImage(sketch,
                new ImageModel("assets/boop/Right_Leg.png", boopDimens, boopDimens));
        shell = Image.createImage(sketch,
                new ImageModel("assets/boop/Shell.png", boopDimens, boopDimens));
        shellHide = Image.createImage(sketch,
                new ImageModel("assets/boop/Shell_Hide.png", boopDimens, boopDimens));
        head = Image.createImage(sketch,
                new ImageModel("assets/boop/Head.png", boopDimens, boopDimens));
        headPeek = Image.createImage(sketch,
                new ImageModel("assets/boop/Peek.png", boopDimens, boopDimens));
        headPeek1 = Image.createImage(sketch,
                new ImageModel("assets/boop/Peek_Left.png", boopDimens, boopDimens));
        headPeek2 = Image.createImage(sketch,
                new ImageModel("assets/boop/Peek_Right.png", boopDimens, boopDimens));
        headHappy = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Happy.png", boopDimens, boopDimens));
        headHappyBlink = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Happy_Blink.png", boopDimens, boopDimens));
        headBlink = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Blink.png", boopDimens, boopDimens));
        headLook = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Look.png", boopDimens, boopDimens));
        headLookBlink = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Look_Blink.png", boopDimens, boopDimens));
        headRoscoe = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Roscoe.png", boopDimens, boopDimens));

        lbFootR = Image.createImage(sketch,
                new ImageModel("assets/boop/Left_Back_Foot_r.png", boopDimens, boopDimens));
        lfFootR = Image.createImage(sketch,
                new ImageModel("assets/boop/Left_Front_Foot_r.png", boopDimens, boopDimens));
        rbFootR = Image.createImage(sketch,
                new ImageModel("assets/boop/Right_Back_Foot_r.png", boopDimens, boopDimens));
        rfFootR = Image.createImage(sketch,
                new ImageModel("assets/boop/Right_Front_Foot_r.png", boopDimens, boopDimens));
        leftHideLegR = Image.createImage(sketch,
                new ImageModel("assets/boop/Left_Leg_r.png", boopDimens, boopDimens));
        rightHideLegR = Image.createImage(sketch,
                new ImageModel("assets/boop/Right_Leg_r.png", boopDimens, boopDimens));
        shellR = Image.createImage(sketch,
                new ImageModel("assets/boop/Shell_r.png", boopDimens, boopDimens));
        shellHideR = Image.createImage(sketch,
                new ImageModel("assets/boop/Shell_Hide_r.png", boopDimens, boopDimens));
        headR = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_r.png", boopDimens, boopDimens));
        headPeekR = Image.createImage(sketch,
                new ImageModel("assets/boop/Peek_r.png", boopDimens, boopDimens));
        headPeek1R = Image.createImage(sketch,
                new ImageModel("assets/boop/Peek_Left_r.png", boopDimens, boopDimens));
        headPeek2R = Image.createImage(sketch,
                new ImageModel("assets/boop/Peek_Right_r.png", boopDimens, boopDimens));
        headHappyR = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Happy_r.png", boopDimens, boopDimens));
        headHappyBlinkR = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Happy_Blink_r.png", boopDimens, boopDimens));
        headBlinkR = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Blink_r.png", boopDimens, boopDimens));
        headLookR = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Look_r.png", boopDimens, boopDimens));
        headLookBlinkR = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Look_Blink_r.png", boopDimens, boopDimens));
        headRoscoeR = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Roscoe_r.png", boopDimens, boopDimens));

        currentX = width / 2f;
        currentY = height - boopDimens / 2f;
    }
}
