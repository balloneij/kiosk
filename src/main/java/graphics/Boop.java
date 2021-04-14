package graphics;

import java.util.Random;
import kiosk.Kiosk;
import kiosk.models.ImageModel;
import kiosk.scenes.Image;
import processing.event.MouseEvent;

public class Boop {

    private static final float SCREEN_BOUNDARY_FRACTION = 6f;
    private static final int RANDOM_BOUNDS = 100;
    private static final int RANDOM_GO_LEFT_CHANCE = 50;
    private static final int RANDOM_SWAP_SPEED_CHANCE = 50;
    private static final int RANDOM_SWAP_ANIMATION_CHANCE = 50;
    private static final int CHOICE_FREQUENCY_IN_FRAMES = 30;
    private static final int STOPPING_BREAK_IN_FRAMES = 25;
    private static final int MOVEMENT_PERCENTAGE = 75;
    private static final int LIMB_MOVEMENT_RANGE = 1;
    private static final float SPEED_SLOW = 0.5f;
    private static final float SPEED_FAST = 0.75f;
    private static final int MINIMUM_SHELL_FRAMES = 30;
    private static final int MINIMUM_HAPPY_FRAMES = 100;

    private static int width;
    private static int height;
    private static int boopDimens;

    private static Image lbFoot;
    private static Image lfFoot;
    private static Image rbFoot;
    private static Image rfFoot;
    private static Image shell;
    private static Image head;
    private static Image headHappy;
    private static Image headBlink;
    private static Image headLook;
    private static Image headLookBlink;

    private static Image lbFoot_r;
    private static Image lfFoot_r;
    private static Image rbFoot_r;
    private static Image rfFoot_r;
    private static Image shell_r;
    private static Image head_r;
    private static Image headHappy_r;
    private static Image headBlink_r;
    private static Image headLook_r;
    private static Image headLookBlink_r;

    private static boolean isMoving;
    private static boolean isTapped;
    private static boolean isHappy;
    private static boolean insideHappyState;
    private static boolean choseDirection;
    private static boolean choseLeft;
    private static boolean choseSlow;
    private static boolean choseScootAnimation;
    private static float currentX;
    private static float currentY;
    private static int timesNotMoved = 0;
    private static int lastMovementFrame;
    private static int firstMovementFrame;
    private static int additional_shell_frames;
    private static int lastClickedFrame;
    private static int firstHappyFrame = 0;
    private static int additional_happy_frames;

    /**
     * Calculate if Boop should turn around, start/stop moving,
     * and figure out if he's happy (arrival to CareerDescriptionScene).
     * Boop will never walk off screen.
     * @param sketch to draw to
     */
    public static void movementLogic(Kiosk sketch, String currentSceneName) {
        if (currentSceneName.contains("CareerDescriptionScene")
                && !insideHappyState && firstHappyFrame != -1) {
            firstHappyFrame = sketch.frameCount;
            isHappy = true;
            Random rand = new Random();
            additional_happy_frames = rand.nextInt(30) + 15;
        }
        if (!currentSceneName.contains("CareerDescriptionScene")) {
            firstHappyFrame = 0;
        }
        if (currentX >= width * (SCREEN_BOUNDARY_FRACTION - 1 / SCREEN_BOUNDARY_FRACTION)) {
            //Boop is too close to the right edge of the screen!
            //Somebody tell him to stop!
            //oh god he can't hear us he has airpods in oh god
            //Oh the hu-manatee!
            isMoving = true;
            choseDirection = true;
            choseLeft = true;
            choseSlow = false;
            choseScootAnimation = true;
        } else if (currentX <= width / SCREEN_BOUNDARY_FRACTION) {
            //Boop is too close to the left edge of the screen!
            //Somebody tell him to stop!
            //oh god he can't hear us he has airpods in oh god
            //Oh the hu-manatee!
            isMoving = true;
            choseDirection = true;
            choseLeft = false;
            choseSlow = false;
            choseScootAnimation = true;
        }
        if (sketch.frameCount % CHOICE_FREQUENCY_IN_FRAMES == 0) {
            if (!isHappy) {
                if (!isTapped) {
                    if (isMoving) {
                        Random rand = new Random();
                        int randInt = rand.nextInt(RANDOM_BOUNDS);
                        if (randInt >= MOVEMENT_PERCENTAGE) {
                            //If Boop randomly decides to stop...
                            isMoving = false;
                            choseDirection = false;
                        } else {
                            //If Boop keeps moving...
                            isMoving = true;
                            choseDirection = true;
                            lastMovementFrame = sketch.frameCount;
                        }
                    } else {
                        if (lastMovementFrame < sketch.frameCount + STOPPING_BREAK_IN_FRAMES) {
                            //Boop must stay put for at least
                            // STOPPING_BREAK_IN_FRAMES frames after stopping.
                            Random rand = new Random();
                            int randInt = rand.nextInt(RANDOM_BOUNDS) + timesNotMoved;
                            if (randInt >= MOVEMENT_PERCENTAGE) {
                                //If Boop randomly decides to start moving...
                                //Randomly decide if he should start moving left or right.
                                isMoving = true;
                                choseDirection = true;
                                lastMovementFrame = sketch.frameCount;
                                firstMovementFrame = sketch.frameCount;
                                timesNotMoved = 0;

                                randInt = rand.nextInt(RANDOM_BOUNDS);
                                choseLeft = randInt >= RANDOM_GO_LEFT_CHANCE;
                                randInt = rand.nextInt(RANDOM_BOUNDS);
                                choseSlow = randInt >= RANDOM_SWAP_SPEED_CHANCE;
                                randInt = rand.nextInt(RANDOM_BOUNDS);
                                choseScootAnimation = randInt >= RANDOM_SWAP_ANIMATION_CHANCE;
                            } else {
                                //If Boop randomly decides to stay where he is...
                                //Make it more likely that he will move next time.
                                isMoving = false;
                                choseDirection = false;
                                timesNotMoved++;
                            }
                        }
                    }
                }
            }
        }
        drawThisFrame(sketch);
    }

    /**
     * Draws the current frame of Boop's animation.
     * @param sketch to draw to
     */
    public static void drawThisFrame(Kiosk sketch) {
        if (choseDirection) {
            //Boop is moving, draw him somewhere else depending on his speed.
            if (choseSlow) {
                drawBoop(sketch, SPEED_SLOW);
            } else {
                drawBoop(sketch, SPEED_FAST);
            }
        } else {
            //Boop isn't moving right or left, draw him where he currently is.
            drawBoop(sketch, 0);
        }
    }

    /**
     * Draws Boop, along with all of his leg animations depending
     * on frame count, speed, animation style chosen, etc.
     * @param sketch to draw to
     * @param amountToMove the amount that Boop should move across the screen each frame
     */
    public static void drawBoop(Kiosk sketch, float amountToMove) {
        if (!isTapped) {
            if (!isHappy) {
                if (choseDirection) {
                    if (choseLeft) {
                        if (choseScootAnimation) {
                            //TODO INCLUDE BLINKING + LOOKING LEFT ANIMATION,
                            // IF JODI GIVES US THE FILES
                            //Boop is currently moving left...
                            //Animate Boop using the "Scoot" method...
                            int frameNumber = (sketch.frameCount - firstMovementFrame) % 12;
                            if (frameNumber == 0 || frameNumber == 1) {
                                //Right front foot and left back foot forwards a bit
                                lbFoot.draw(sketch, currentX - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX, currentY);
                                rbFoot.draw(sketch, currentX, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove, currentY);
                                shell.draw(sketch, currentX, currentY);
                                headLook.draw(sketch, currentX, currentY);
                            } else if (frameNumber == 2 || frameNumber == 3) {
                                //Right front foot and left back foot forwards a lot
                                lbFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX, currentY);
                                rbFoot.draw(sketch, currentX, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                shell.draw(sketch, currentX, currentY);
                                headLook.draw(sketch, currentX, currentY);
                            } else if (frameNumber == 4 || frameNumber == 5) {
                                //Right front foot and left back foot forwards a lot,
                                // body and head forwards a bit
                                lbFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX, currentY);
                                rbFoot.draw(sketch, currentX, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                shell.draw(sketch, currentX - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove, currentY);
                            } else if (frameNumber == 6 || frameNumber == 7) {
                                //Right front foot, left back foot, body and head forwards a lot
                                lbFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX, currentY);
                                rbFoot.draw(sketch, currentX, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                shell.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                Random rand = new Random();
                                if (rand.nextInt(6) == 0) {
                                    headLookBlink.draw(sketch, currentX - amountToMove
                                            - amountToMove, currentY);
                                } else {
                                    headLook.draw(sketch, currentX - amountToMove
                                            - amountToMove, currentY);
                                }
                            } else if (frameNumber == 8 || frameNumber == 9) {
                                //Right front foot, left back foot, body and head forwards a lot,
                                // left front foot and right back foot forwards a bit
                                lbFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX - amountToMove, currentY);
                                rbFoot.draw(sketch, currentX - amountToMove, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                shell.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                            } else if (frameNumber == 10 || frameNumber == 11) {
                                //Right front foot, left, back foot, body, head,
                                // left front foot and right back foot forwards a lot
                                lbFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                rbFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                shell.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove
                                        - amountToMove, currentY);
                            }
                            if (frameNumber == 11) {
                                currentX = currentX - amountToMove - amountToMove;
                            }
                        } else {
                            //TODO INCLUDE BLINKING + LOOKING LEFT ANIMATION,
                            // IF JODI GIVES US THE FILES
                            //Boop is currently moving left...
                            //Animate Boop with the "TipToe" method...
                            int frameNumber = (sketch.frameCount - firstMovementFrame) % 32;
                            if (frameNumber == 0 || frameNumber == 1
                                    || frameNumber == 2 || frameNumber == 3) {
                                //Right front foot up
                                lbFoot.draw(sketch, currentX - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX - amountToMove, currentY);
                                rbFoot.draw(sketch, currentX - amountToMove, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                shell.draw(sketch, currentX - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove, currentY);
                                currentX = currentX - amountToMove;
                            } else if (frameNumber == 4 || frameNumber == 5
                                    || frameNumber == 6 || frameNumber == 7) {
                                //Right front foot and left back foot up
                                lbFoot.draw(sketch, currentX - amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                lfFoot.draw(sketch, currentX - amountToMove, currentY);
                                rbFoot.draw(sketch, currentX - amountToMove, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                shell.draw(sketch, currentX - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove, currentY);
                                currentX = currentX - amountToMove;
                            } else if (frameNumber == 8 || frameNumber == 9
                                    || frameNumber == 10 || frameNumber == 11) {
                                //Left back foot up
                                lbFoot.draw(sketch, currentX - amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                lfFoot.draw(sketch, currentX - amountToMove, currentY);
                                rbFoot.draw(sketch, currentX - amountToMove, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove, currentY);
                                shell.draw(sketch, currentX - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove, currentY);
                                currentX = currentX - amountToMove;
                            } else if (frameNumber == 12 || frameNumber == 13
                                    || frameNumber == 14 || frameNumber == 15
                                    || frameNumber == 28 || frameNumber == 29
                                    || frameNumber == 30 || frameNumber == 31) {
                                //All feet down
                                lbFoot.draw(sketch, currentX - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX - amountToMove, currentY);
                                rbFoot.draw(sketch, currentX - amountToMove, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove, currentY);
                                shell.draw(sketch, currentX - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove, currentY);
                                currentX = currentX - amountToMove;
                            } else if (frameNumber == 16 || frameNumber == 17
                                    || frameNumber == 18 || frameNumber == 19) {
                                //Left front foot up
                                lbFoot.draw(sketch, currentX - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX - amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                rbFoot.draw(sketch, currentX - amountToMove, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove, currentY);
                                shell.draw(sketch, currentX - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove, currentY);
                                currentX = currentX - amountToMove;
                            } else if (frameNumber == 20 || frameNumber == 21
                                    || frameNumber == 22 || frameNumber == 23) {
                                //Left front foot and right back foot up
                                lbFoot.draw(sketch, currentX - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX - amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                rbFoot.draw(sketch, currentX - amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                rfFoot.draw(sketch, currentX - amountToMove, currentY);
                                shell.draw(sketch, currentX - amountToMove, currentY);
                                headLook.draw(sketch, currentX - amountToMove, currentY);
                                currentX = currentX - amountToMove;
                            } else if (frameNumber == 24 || frameNumber == 25
                                    || frameNumber == 26 || frameNumber == 27) {
                                //Right back foot up
                                lbFoot.draw(sketch, currentX - amountToMove, currentY);
                                lfFoot.draw(sketch, currentX - amountToMove, currentY);
                                rbFoot.draw(sketch, currentX - amountToMove, currentY);
                                rfFoot.draw(sketch, currentX - amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                shell.draw(sketch, currentX - amountToMove, currentY);
                                Random rand = new Random();
                                if (rand.nextInt(6) == 0) {
                                    headLookBlink.draw(sketch, currentX - amountToMove, currentY);
                                } else {
                                    headLook.draw(sketch, currentX - amountToMove, currentY);
                                }
                                currentX = currentX - amountToMove;
                            }
                        }
                    } else {
                        if (choseScootAnimation) {
                            //TODO INCLUDE BLINKING + LOOKING RIGHT ANIMATION,
                            // IF JODI GIVES US THE FILES
                            //Boop is currently moving right...
                            //Animate Boop using the "Scoot" method...
                            int frameNumber = (sketch.frameCount - firstMovementFrame) % 12;
                            if (frameNumber == 0 || frameNumber == 1) {
                                //Right front foot and left back foot forwards a bit
                                lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX, currentY);
                                rbFoot_r.draw(sketch, currentX, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                shell_r.draw(sketch, currentX, currentY);
                                headLook_r.draw(sketch, currentX, currentY);
                            } else if (frameNumber == 2 || frameNumber == 3) {
                                //Right front foot and left back foot forwards a lot
                                lbFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX, currentY);
                                rbFoot_r.draw(sketch, currentX, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                shell_r.draw(sketch, currentX, currentY);
                                headLook_r.draw(sketch, currentX, currentY);
                            } else if (frameNumber == 4 || frameNumber == 5) {
                                //Right front foot and left back foot forwards a lot,
                                // body and head forwards a bit
                                lbFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX, currentY);
                                rbFoot_r.draw(sketch, currentX, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                shell_r.draw(sketch, currentX + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove, currentY);
                            } else if (frameNumber == 6 || frameNumber == 7) {
                                //Right front foot, left back foot, body and head forwards a lot
                                lbFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX, currentY);
                                rbFoot_r.draw(sketch, currentX, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                shell_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                Random rand = new Random();
                                if (rand.nextInt(6) == 0) {
                                    headLookBlink_r.draw(sketch, currentX + amountToMove
                                            + amountToMove, currentY);
                                } else {
                                    headLook_r.draw(sketch, currentX + amountToMove
                                            + amountToMove, currentY);
                                }
                            } else if (frameNumber == 8 || frameNumber == 9) {
                                //Right front foot, left back foot, body and head forwards a lot,
                                // left front foot and right back foot forwards a bit
                                lbFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                shell_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                            } else if (frameNumber == 10 || frameNumber == 11) {
                                //Right front foot, left, back foot, body, head,
                                // left front foot and right back foot forwards a lot
                                lbFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                rbFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                shell_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove
                                        + amountToMove, currentY);
                            }
                            if (frameNumber == 11) {
                                currentX = currentX + amountToMove + amountToMove;
                            }
                        } else {
                            //TODO INCLUDE BLINKING + LOOKING RIGHT ANIMATION,
                            // IF JODI GIVES US THE FILES
                            //Boop is currently moving right...
                            //Animate Boop using the "TipToe" method...
                            int frameNumber = (sketch.frameCount - firstMovementFrame) % 32;
                            if (frameNumber == 0 || frameNumber == 1
                                    || frameNumber == 2 || frameNumber == 3) {
                                //Right front foot up
                                lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                shell_r.draw(sketch, currentX + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove, currentY);
                                currentX = currentX + amountToMove;
                            } else if (frameNumber == 4 || frameNumber == 5
                                    || frameNumber == 6 || frameNumber == 7) {
                                //Right front foot and left back foot up
                                lbFoot_r.draw(sketch, currentX + amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                shell_r.draw(sketch, currentX + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove, currentY);
                                currentX = currentX + amountToMove;
                            } else if (frameNumber == 8 || frameNumber == 9
                                    || frameNumber == 10 || frameNumber == 11) {
                                //Left back foot up
                                lbFoot_r.draw(sketch, currentX + amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                shell_r.draw(sketch, currentX + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove, currentY);
                                currentX = currentX + amountToMove;
                            } else if (frameNumber == 12 || frameNumber == 13
                                    || frameNumber == 14 || frameNumber == 15
                                    || frameNumber == 28 || frameNumber == 29
                                    || frameNumber == 30 || frameNumber == 31) {
                                //All feet down
                                lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                shell_r.draw(sketch, currentX + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove, currentY);
                                currentX = currentX + amountToMove;
                            } else if (frameNumber == 16 || frameNumber == 17
                                    || frameNumber == 18 || frameNumber == 19) {
                                //Left front foot up
                                lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX + amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                shell_r.draw(sketch, currentX + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove, currentY);
                                currentX = currentX + amountToMove;
                            } else if (frameNumber == 20 || frameNumber == 21
                                    || frameNumber == 22 || frameNumber == 23) {
                                //Left front foot and right back foot up
                                lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX + amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                rbFoot_r.draw(sketch, currentX + amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                shell_r.draw(sketch, currentX + amountToMove, currentY);
                                headLook_r.draw(sketch, currentX + amountToMove, currentY);
                                currentX = currentX + amountToMove;
                            } else if (frameNumber == 24 || frameNumber == 25
                                    || frameNumber == 26 || frameNumber == 27) {
                                //Right back foot up
                                lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                                rfFoot_r.draw(sketch, currentX + amountToMove,
                                        currentY - LIMB_MOVEMENT_RANGE * 2);
                                shell_r.draw(sketch, currentX + amountToMove, currentY);
                                Random rand = new Random();
                                if (rand.nextInt(6) == 0) {
                                    headLookBlink_r.draw(sketch, currentX + amountToMove, currentY);
                                } else {
                                    headLook_r.draw(sketch, currentX + amountToMove, currentY);
                                }
                                currentX = currentX + amountToMove;
                            }
                        }
                    }
                } else if (choseLeft) {
                    //TODO INCLUDE OFFICIAL BLINKING ANIMATION,
                    // IF JODI GIVES US THE FILES
                    //Boop is currently stopped, facing left...
                    lbFoot.draw(sketch, currentX, currentY);
                    lfFoot.draw(sketch, currentX, currentY);
                    rbFoot.draw(sketch, currentX, currentY);
                    rfFoot.draw(sketch, currentX, currentY);
                    shell.draw(sketch, currentX, currentY);
                    Random rand = new Random();
                    if (sketch.frameCount % 45 == 0 && rand.nextInt(3) == 0) {
                        //Boop blinked...
                        //Only occurs every 45 frames, 1/3 chance on those frames
                        headBlink.draw(sketch, currentX, currentY);
                    } else {
                        head.draw(sketch, currentX, currentY);
                    }
                } else {
                    //TODO INCLUDE OFFICIAL BLINKING ANIMATION,
                    // IF JODI GIVES US THE FILES
                    //Boop is currently stopped, facing right...
                    lbFoot_r.draw(sketch, currentX, currentY);
                    lfFoot_r.draw(sketch, currentX, currentY);
                    rbFoot_r.draw(sketch, currentX, currentY);
                    rfFoot_r.draw(sketch, currentX, currentY);
                    shell_r.draw(sketch, currentX, currentY);
                    Random rand = new Random();
                    if (sketch.frameCount % 45 == 0 && rand.nextInt(3) == 0) {
                        //Boop blinked...
                        //Only occurs every 45 frames, 1/3 chance on those frames
                        headBlink_r.draw(sketch, currentX, currentY);
                    } else {
                        head_r.draw(sketch, currentX, currentY);
                    }
                }
            } else {
                if (choseLeft) {
                    //TODO INCLUDE OFFICIAL HAPPY ANIMATION,
                    // IF JODI GIVES US THE FILES
                    //Boop is being happy for at least
                    // MIMIMUM_HAPPY_FRAMES + additional_happy_frames,
                    // previously was looking left...
                    lbFoot.draw(sketch, currentX, currentY);
                    lfFoot.draw(sketch, currentX, currentY);
                    rbFoot.draw(sketch, currentX, currentY);
                    rfFoot.draw(sketch, currentX, currentY);
                    shell.draw(sketch, currentX, currentY);
                    headHappy.draw(sketch, currentX, currentY);
                    insideHappyState = true;
                } else {
                    //TODO INCLUDE OFFICIAL HAPPY ANIMATION,
                    // IF JODI GIVES US THE FILES
                    //Boop is being happy for at least
                    // MIMIMUM_SHELL_FRAMES + additional_shell_frames,
                    // previously was looking right...
                    lbFoot_r.draw(sketch, currentX, currentY);
                    lfFoot_r.draw(sketch, currentX, currentY);
                    rbFoot_r.draw(sketch, currentX, currentY);
                    rfFoot_r.draw(sketch, currentX, currentY);
                    shell_r.draw(sketch, currentX, currentY);
                    headHappy_r.draw(sketch, currentX, currentY);
                    insideHappyState = true;
                }
                if (sketch.frameCount >= firstHappyFrame
                        + MINIMUM_HAPPY_FRAMES + additional_happy_frames) {
                    //Boop is currently experiencing depression...
                    //He's been happy for more than enough frames, put him out of his misery..
                    isHappy = false;
                    insideHappyState = false;
                    firstHappyFrame = -1;
                }
            }
        } else {
            //Boop has been tapped...
            if (choseLeft) {
                //TODO BOOP'S IN SHELL ANIMATION
                // FACING LEFT IF JODI GIVES US THE FILES
                //Boop is going into his shell for at least
                // MIMIMUM_SHELL_FRAMES + additional_shell_frames,
                // previously was looking left...
                int frameNumber = (sketch.frameCount - lastClickedFrame)
                        % (MINIMUM_SHELL_FRAMES + additional_shell_frames);
                if (frameNumber <= ((MINIMUM_SHELL_FRAMES + additional_shell_frames) / 2f)) {
                    shell.draw(sketch, currentX, currentY + boopDimens / 10f);
                } else {
                    if (sketch.frameCount % 5 == 0) {
                        //TODO BOOP'S LOOKING OUT OF SHELL ANIMATION
                        // IF JODI GIVES US THE FILES
                        shell_r.draw(sketch, currentX, currentY + boopDimens / 10f);
                    } else {
                        //TODO BOOP'S LOOKING OUT OF SHELL ANIMATION
                        // IF JODI GIVES US THE FILES
                        shell.draw(sketch, currentX, currentY + boopDimens / 10f);
                    }
                }
            } else {
                //TODO BOOP'S IN SHELL ANIMATION
                // FACING RIGHT IF JODI GIVES US THE FILES
                //Boop is going into his shell for at least
                // MIMIMUM_SHELL_FRAMES + additional_shell_frames,
                // previously was looking right...
                int frameNumber = (sketch.frameCount - lastClickedFrame)
                        % (MINIMUM_SHELL_FRAMES + additional_shell_frames);
                if (frameNumber <= ((MINIMUM_SHELL_FRAMES + additional_shell_frames) / 2f)) {
                    shell_r.draw(sketch, currentX, currentY + boopDimens / 10f);
                } else {
                    if (sketch.frameCount % 5 == 0) {
                        //TODO BOOP'S LOOKING OUT OF SHELL ANIMATION,
                        // IF JODI GIVES US THE FILES
                        shell.draw(sketch, currentX, currentY + boopDimens / 10f);
                    } else {
                        //TODO BOOP'S LOOKING OUT OF SHELL ANIMATION
                        // IF JODI GIVES US THE FILES
                        shell_r.draw(sketch, currentX, currentY + boopDimens / 10f);
                    }
                }
            }
            if (sketch.frameCount >= lastClickedFrame
                    + MINIMUM_SHELL_FRAMES + additional_shell_frames) {
                //Boop is exiting his shell...
                isTapped = false;
            }
        }
    }

    /**
     * Compares Boop's location to the tap's location.
     * @param event the mouse tap just registered
     */
    public static void checkTap(Kiosk sketch, MouseEvent event) {
        if (currentX >= event.getX() - boopDimens / 2f
                && currentX <= event.getX() + boopDimens / 2f) {
            if (currentY >= event.getY() - boopDimens / 2f
                    && currentY <= event.getY() + boopDimens / 2f) {
                isTapped = true;
                lastClickedFrame = sketch.frameCount;
                Random rand = new Random();
                additional_shell_frames = rand.nextInt(10);
            }
        }
    }

    /**
     * Loads all images in so they aren't loaded on every drawn frame.
     * @param sketch to draw to
     */
    public static void loadVariables(Kiosk sketch) {
        width = Kiosk.getSettings().screenW;
        height = Kiosk.getSettings().screenH;
        boopDimens = height / 8;

        lbFoot = Image.createImage(sketch,
                new ImageModel("assets/boop/LeftBackFoot.png", boopDimens, boopDimens));
        lfFoot = Image.createImage(sketch,
                new ImageModel("assets/boop/LeftFrontFoot.png", boopDimens, boopDimens));
        rbFoot = Image.createImage(sketch,
                new ImageModel("assets/boop/RightBackFoot.png", boopDimens, boopDimens));
        rfFoot = Image.createImage(sketch,
                new ImageModel("assets/boop/RightFrontFoot.png", boopDimens, boopDimens));
        shell = Image.createImage(sketch,
                new ImageModel("assets/boop/Shell.png", boopDimens, boopDimens));
        head = Image.createImage(sketch,
                new ImageModel("assets/boop/Head.png", boopDimens, boopDimens));
        headHappy = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Happy.png", boopDimens, boopDimens));
        headBlink = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Blink.png", boopDimens, boopDimens));
        headLook = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Look.png", boopDimens, boopDimens));
        headLookBlink = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Look_Blink.png", boopDimens, boopDimens));

        lbFoot_r = Image.createImage(sketch,
                new ImageModel("assets/boop/LeftBackFoot_r.png", boopDimens, boopDimens));
        lfFoot_r = Image.createImage(sketch,
                new ImageModel("assets/boop/LeftFrontFoot_r.png", boopDimens, boopDimens));
        rbFoot_r = Image.createImage(sketch,
                new ImageModel("assets/boop/RightBackFoot_r.png", boopDimens, boopDimens));
        rfFoot_r = Image.createImage(sketch,
                new ImageModel("assets/boop/RightFrontFoot_r.png", boopDimens, boopDimens));
        shell_r = Image.createImage(sketch,
                new ImageModel("assets/boop/Shell_r.png", boopDimens, boopDimens));
        head_r = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_r.png", boopDimens, boopDimens));
        headHappy_r = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Happy_r.png", boopDimens, boopDimens));
        headBlink_r = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Blink_r.png", boopDimens, boopDimens));
        headLook_r = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Look_r.png", boopDimens, boopDimens));
        headLookBlink_r = Image.createImage(sketch,
                new ImageModel("assets/boop/Head_Look_Blink_r.png", boopDimens, boopDimens));

        currentX = width / 2f;
        currentY = height - boopDimens / 2f;
        lastMovementFrame = 0;
    }
}
