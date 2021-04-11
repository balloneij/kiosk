package graphics;

import java.util.Random;
import kiosk.Kiosk;
import kiosk.models.ImageModel;
import kiosk.scenes.Image;

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
    private static final int SPEED_SLOW = 1;
    private static final int SPEED_FAST = 2;

    private static int width;
    private static int height;
    private static int boopDimens;

    private static Image lbFoot;
    private static Image lfFoot;
    private static Image rbFoot;
    private static Image rfFoot;
    private static Image shell;
    private static Image head;

    private static Image lbFoot_r;
    private static Image lfFoot_r;
    private static Image rbFoot_r;
    private static Image rfFoot_r;
    private static Image shell_r;
    private static Image head_r;

    private static boolean isMoving;
    private static boolean choseDirection;
    private static boolean choseLeft;
    private static boolean choseSlow;
    private static boolean choseScootAnimation;
    private static float currentX;
    private static float currentY;
    private static int timesNotMoved = 0;
    private static int lastMovementFrame;
    private static int firstMovementFrame;

    /**
     * Draws Boop the Turtle on the bottom of the screen.
     * Boop will walk back and forth and react to touches.
     * @param sketch to draw to
     */
    public static void movementLogic(Kiosk sketch) {
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
     * Draws boop, along with all of his leg animations depending
     * on frame count, speed, animation style chosen, etc.
     * @param sketch to draw to
     * @param amountToMove the amount that Boop should move across the screen each frame
     */
    public static void drawBoop(Kiosk sketch, int amountToMove) {
        if (choseDirection) {
            if (choseLeft) {
                if (choseScootAnimation) {
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
                        head.draw(sketch, currentX, currentY);
                    } else if (frameNumber == 2 || frameNumber == 3) {
                        //Right front foot and left back foot forwards a lot
                        lbFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX, currentY);
                        rbFoot.draw(sketch, currentX, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        shell.draw(sketch, currentX, currentY);
                        head.draw(sketch, currentX, currentY);
                    } else if (frameNumber == 4 || frameNumber == 5) {
                        //Right front foot and left back foot forwards a lot,
                        // body and head forwards a bit
                        lbFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX, currentY);
                        rbFoot.draw(sketch, currentX, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        shell.draw(sketch, currentX - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove, currentY);
                    } else if (frameNumber == 6 || frameNumber == 7) {
                        //Right front foot, left back foot, body and head forwards a lot
                        lbFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX, currentY);
                        rbFoot.draw(sketch, currentX, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        shell.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                    } else if (frameNumber == 8 || frameNumber == 9) {
                        //Right front foot, left back foot, body and head forwards a lot,
                        // left front foot and right back foot forwards a bit
                        lbFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX - amountToMove, currentY);
                        rbFoot.draw(sketch, currentX - amountToMove, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        shell.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                    } else if (frameNumber == 10 || frameNumber == 11) {
                        //Right front foot, left, back foot, body, head,
                        // left front foot and right back foot forwards a lot
                        lbFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        rbFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        shell.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove - amountToMove, currentY);
                    }
                    if (frameNumber == 11) {
                        currentX = currentX - amountToMove - amountToMove;
                    }
                } else {
                    //Boop is currently moving left...
                    //Animate Boop with the "TipToe" method...
                    int frameNumber = (sketch.frameCount - firstMovementFrame) % 16;
                    if (frameNumber == 0 || frameNumber == 1) {
                        //Right front foot up
                        lbFoot.draw(sketch, currentX - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX - amountToMove, currentY);
                        rbFoot.draw(sketch, currentX - amountToMove, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        shell.draw(sketch, currentX - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove, currentY);
                        currentX = currentX - amountToMove;
                    } else if (frameNumber == 2 || frameNumber == 3) {
                        //Right front foot and left back foot up
                        lbFoot.draw(sketch, currentX - amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        lfFoot.draw(sketch, currentX - amountToMove, currentY);
                        rbFoot.draw(sketch, currentX - amountToMove, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        shell.draw(sketch, currentX - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove, currentY);
                        currentX = currentX - amountToMove;
                    } else if (frameNumber == 4 || frameNumber == 5) {
                        //Left back foot up
                        lbFoot.draw(sketch, currentX - amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        lfFoot.draw(sketch, currentX - amountToMove, currentY);
                        rbFoot.draw(sketch, currentX - amountToMove, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove, currentY);
                        shell.draw(sketch, currentX - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove, currentY);
                        currentX = currentX - amountToMove;
                    } else if (frameNumber == 6 || frameNumber == 7
                            || frameNumber == 14 || frameNumber == 15) {
                        //All feet down
                        lbFoot.draw(sketch, currentX - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX - amountToMove, currentY);
                        rbFoot.draw(sketch, currentX - amountToMove, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove, currentY);
                        shell.draw(sketch, currentX - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove, currentY);
                        currentX = currentX - amountToMove;
                    } else if (frameNumber == 8 || frameNumber == 9) {
                        //Left front foot up
                        lbFoot.draw(sketch, currentX - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX - amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        rbFoot.draw(sketch, currentX - amountToMove, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove, currentY);
                        shell.draw(sketch, currentX - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove, currentY);
                        currentX = currentX - amountToMove;
                    } else if (frameNumber == 10 || frameNumber == 11) {
                        //Left front foot and right back foot up
                        lbFoot.draw(sketch, currentX - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX - amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        rbFoot.draw(sketch, currentX - amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        rfFoot.draw(sketch, currentX - amountToMove, currentY);
                        shell.draw(sketch, currentX - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove, currentY);
                        currentX = currentX - amountToMove;
                    } else if (frameNumber == 12 || frameNumber == 13) {
                        //Right back foot up
                        lbFoot.draw(sketch, currentX - amountToMove, currentY);
                        lfFoot.draw(sketch, currentX - amountToMove, currentY);
                        rbFoot.draw(sketch, currentX - amountToMove, currentY);
                        rfFoot.draw(sketch, currentX - amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        shell.draw(sketch, currentX - amountToMove, currentY);
                        head.draw(sketch, currentX - amountToMove, currentY);
                        currentX = currentX - amountToMove;
                    }
                }
            } else {
                if (choseScootAnimation) {
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
                        head_r.draw(sketch, currentX, currentY);
                    } else if (frameNumber == 2 || frameNumber == 3) {
                        //Right front foot and left back foot forwards a lot
                        lbFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX, currentY);
                        rbFoot_r.draw(sketch, currentX, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        shell_r.draw(sketch, currentX, currentY);
                        head_r.draw(sketch, currentX, currentY);
                    } else if (frameNumber == 4 || frameNumber == 5) {
                        //Right front foot and left back foot forwards a lot,
                        // body and head forwards a bit
                        lbFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX, currentY);
                        rbFoot_r.draw(sketch, currentX, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        shell_r.draw(sketch, currentX + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove, currentY);
                    } else if (frameNumber == 6 || frameNumber == 7) {
                        //Right front foot, left back foot, body and head forwards a lot
                        lbFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX, currentY);
                        rbFoot_r.draw(sketch, currentX, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        shell_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                    } else if (frameNumber == 8 || frameNumber == 9) {
                        //Right front foot, left back foot, body and head forwards a lot,
                        // left front foot and right back foot forwards a bit
                        lbFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        shell_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                    } else if (frameNumber == 10 || frameNumber == 11) {
                        //Right front foot, left, back foot, body, head,
                        // left front foot and right back foot forwards a lot
                        lbFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        rbFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        shell_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove + amountToMove, currentY);
                    }
                    if (frameNumber == 11) {
                        currentX = currentX + amountToMove + amountToMove;
                    }
                } else {
                    //Boop is currently moving right...
                    //Animate Boop using the "TipToe" method...
                    int frameNumber = (sketch.frameCount - firstMovementFrame) % 16;
                    if (frameNumber == 0 || frameNumber == 1) {
                        //Right front foot up
                        lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        shell_r.draw(sketch, currentX + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove, currentY);
                        currentX = currentX + amountToMove;
                    } else if (frameNumber == 2 || frameNumber == 3) {
                        //Right front foot and left back foot up
                        lbFoot_r.draw(sketch, currentX + amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        shell_r.draw(sketch, currentX + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove, currentY);
                        currentX = currentX + amountToMove;
                    } else if (frameNumber == 4 || frameNumber == 5) {
                        //Left back foot up
                        lbFoot_r.draw(sketch, currentX + amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        shell_r.draw(sketch, currentX + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove, currentY);
                        currentX = currentX + amountToMove;
                    } else if (frameNumber == 6 || frameNumber == 7
                            || frameNumber == 14 || frameNumber == 15) {
                        //All feet down
                        lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        shell_r.draw(sketch, currentX + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove, currentY);
                        currentX = currentX + amountToMove;
                    } else if (frameNumber == 8 || frameNumber == 9) {
                        //Left front foot up
                        lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX + amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        shell_r.draw(sketch, currentX + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove, currentY);
                        currentX = currentX + amountToMove;
                    } else if (frameNumber == 10 || frameNumber == 11) {
                        //Left front foot and right back foot up
                        lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX + amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        rbFoot_r.draw(sketch, currentX + amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        rfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        shell_r.draw(sketch, currentX + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove, currentY);
                        currentX = currentX + amountToMove;
                    } else if (frameNumber == 12 || frameNumber == 13) {
                        //Right back foot up
                        lbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        lfFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rbFoot_r.draw(sketch, currentX + amountToMove, currentY);
                        rfFoot_r.draw(sketch, currentX + amountToMove,
                                currentY - LIMB_MOVEMENT_RANGE * 2);
                        shell_r.draw(sketch, currentX + amountToMove, currentY);
                        head_r.draw(sketch, currentX + amountToMove, currentY);
                        currentX = currentX + amountToMove;
                    }
                }
            }
        } else if (choseLeft) {
            //Boop is currently stopped, facing left...
            lbFoot.draw(sketch, currentX, currentY);
            lfFoot.draw(sketch, currentX, currentY);
            rbFoot.draw(sketch, currentX, currentY);
            rfFoot.draw(sketch, currentX, currentY);
            shell.draw(sketch, currentX, currentY);
            head.draw(sketch, currentX, currentY);
        } else {
            //Boop is currently stopped, facing right...
            lbFoot_r.draw(sketch, currentX, currentY);
            lfFoot_r.draw(sketch, currentX, currentY);
            rbFoot_r.draw(sketch, currentX, currentY);
            rfFoot_r.draw(sketch, currentX, currentY);
            shell_r.draw(sketch, currentX, currentY);
            head_r.draw(sketch, currentX, currentY);
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

        currentX = width / 2f;
        currentY = height - boopDimens / 2f;
        lastMovementFrame = 0;
    }
}
