package graphics;

import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.models.CareerModel;
import kiosk.models.CreditsSceneModel;
import kiosk.models.FilterGroupModel;

public class SceneAnimationHelper {

    /**
     * The logic to determine the animation for all scene types except for
     * SpokeGraphPromptScenes and CareerPathwayScenes,
     * as they involve spoke interpolation.
     * @param sketch to draw to
     * @param clickedNext if the "Next" button was just clicked
     * @param clickedBack if the "Back" button was just clicked
     * @param clickedHome if the "Home" button was just clicked
     * @param clickedMsoe if the "MSOE" button was just clicked
     * @param sceneToGoTo the scene to travel to, if "Next" was pressed
     * @param riasecToGoTo the riasec type to travel to, if "Next" was pressed
     * @param filterToGoTo the filter to use while travelling, if "Next" was pressed
     * @param totalTimeOpening the time currently spent animating the scene in
     * @param totalTimeEnding the time currently spent animating the scene out
     * @param sceneAnimationMilliseconds the duration of scene animations
     * @param dt the time spent between the last draw update
     * @param screenW the screen width
     * @param screenH the screen height
     * @return an array of ints, where [0] is the offsetX and [1] is the offsetY.
     */
    public static int[] sceneAnimationLogic(Kiosk sketch,
                                           boolean clickedNext, boolean clickedBack,
                                            boolean clickedHome, boolean clickedMsoe,
                                           String sceneToGoTo, Riasec riasecToGoTo,
                                            FilterGroupModel filterToGoTo,
                                           float totalTimeOpening, float totalTimeEnding,
                                            int sceneAnimationMilliseconds, float dt,
                                           int screenW, int screenH) {
        int offsetX;
        int offsetY;

        if (sketch.isEditor) {
            if (clickedNext) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            } else if (clickedBack) {
                sketch.getSceneGraph().popScene();
            } else if (clickedHome) {
                sketch.getSceneGraph().reset();
            } else if (clickedMsoe) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        }

        if ((clickedNext || clickedMsoe) && !sketch.isEditor) {
            offsetX = (int) (screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                if (clickedNext) {
                    sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
                } else if (clickedMsoe) {
                    sketch.getSceneGraph().pushScene(new CreditsSceneModel());
                }
            }
        } else if (clickedBack && !sketch.isEditor) {
            offsetX = (int) (0 - screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedHome && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.contains("RESET")
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH + screenH
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
        } else if (sketch.getSceneGraph().recentActivity.contains("POP")
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = (int) (0 - screenW - screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
        } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = (int) ((screenW + screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1))));
            offsetY = 0;
        } else { //If it's already a second-or-two old, draw the scene normally
            offsetX = 0;
            offsetY = 0;
        }

        return new int[] {offsetX, offsetY};
    }

    /**
     * The logic to determine the animation for CareerPathwayScenes,
     * involves interpolation.
     * @param sketch to draw to
     * @param clickedNext if the "Next" button was just clicked
     * @param clickedBack if the "Back" button was just clicked
     * @param clickedHome if the "Home" button was just clicked
     * @param clickedMsoe if the "MSOE" button was just clicked
     * @param totalTimeOpening the time currently spent animating the scene in
     * @param totalTimeEnding the time currently spent animating the scene out
     * @param sceneAnimationMilliseconds the duration of scene animations
     * @param screenW the screen width
     * @param screenH the screen height
     * @param desiredCareer what career's scene should be viewed,
     *                      if the "Next" button was just clicked
     * @return an array of ints, where [0] is the offsetX, [1] is the offsetY,
     *         [2] is the headerOffsetX, [3] is the typeOfAnimation.
     */
    public static int[] sceneAnimationLogicCareerPathwayScene(Kiosk sketch,
                                                              boolean clickedNext,
                                                              boolean clickedBack,
                                                              boolean clickedHome,
                                                              boolean clickedMsoe,
                                                              float totalTimeOpening,
                                                              float totalTimeEnding,
                                                              int sceneAnimationMilliseconds,
                                                              int screenW, int screenH,
                                                              CareerModel desiredCareer) {
        int offsetX;
        int offsetY;
        int headerOffsetX;
        int typeOfAnimation;

        if (sketch.isEditor) {
            if (clickedNext) {
                sketch.getSceneGraph().pushEndScene(desiredCareer);
            } else if (clickedBack) {
                sketch.getSceneGraph().popScene();
            } else if (clickedHome) {
                sketch.getSceneGraph().reset();
            } else if (clickedMsoe) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        }

        if ((clickedNext || clickedMsoe) && !sketch.isEditor) {
            offsetX = (int) (screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            headerOffsetX = 0;
            typeOfAnimation = 1;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                if (clickedNext) {
                    sketch.getSceneGraph().pushEndScene(desiredCareer);
                } else if (clickedMsoe) {
                    sketch.getSceneGraph().pushScene(new CreditsSceneModel());
                }
            }
        } else if (clickedBack && !sketch.isEditor && sketch.getSceneGraph().history.get(1)
                .toString().contains("Spoke Graph Prompt")) {
            final double availableHeight = (screenH - (screenH / 32f) - (screenH / 6f));
            final double size = Math.min(screenW, availableHeight);
            offsetX = (int) (((screenW - size) / 2)
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            headerOffsetX = (int) (0 - screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            typeOfAnimation = 3;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedBack && !sketch.isEditor) {
            offsetX = (int) (0 - screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            headerOffsetX = 0;
            typeOfAnimation = 1;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedHome && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            headerOffsetX = 0;
            typeOfAnimation = 1;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.contains("RESET")
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH + screenH
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            headerOffsetX = 0;
            typeOfAnimation = 1;
        } else if (sketch.getSceneGraph().recentActivity.contains("POP")
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = (int) (0 - screenW - screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            headerOffsetX = 0;
            typeOfAnimation = 1;
        } else if (sceneAnimationMilliseconds > totalTimeOpening
                && !sketch.isEditor && sketch.getSceneGraph().history.get(1)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = (int) (screenW + screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            headerOffsetX = 0;
            typeOfAnimation = 2;
        } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = (int) (screenW + screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            headerOffsetX = 0;
            typeOfAnimation = 1;
        } else {
            offsetX = 0;
            offsetY = 0;
            headerOffsetX = 0;
            typeOfAnimation = 1;
        }

        return new int[] {offsetX, offsetY, headerOffsetX, typeOfAnimation};
    }

    /**
     * The logic to determine the animation for SpokeGraphPromptScenes,
     * involves interpolation.
     * @param sketch to draw to
     * @param clickedNext if the "Next" button was just clicked
     * @param clickedBack if the "Back" button was just clicked
     * @param clickedHome if the "Home" button was just clicked
     * @param clickedMsoe if the "MSOE" button was just clicked
     * @param sceneToGoTo the scene to travel to, if "Next" was pressed
     * @param riasecToGoTo the riasec type to travel to, if "Next" was pressed
     * @param filterToGoTo the filter to use while travelling, if "Next" was pressed
     * @param totalTimeOpening the time currently spent animating the scene in
     * @param totalTimeEnding the time currently spent animating the scene out
     * @param sceneAnimationMilliseconds the duration of scene animations
     * @param screenW the screen width
     * @param screenH the screen height
     * @param headerY the y-coordinate of the header
     * @param headerH the height of the header
     * @return an array of ints, where [0] is the offsetX, [1] is the offsetY,
     *         [2] is the otherOffsetX, [3] is the typeOfAnimation.
     */
    public static int[] sceneAnimationLogicSpokeGraphPromptScene(Kiosk sketch,
                                                                 boolean clickedNext,
                                                                 boolean clickedBack,
                                                                 boolean clickedHome,
                                                                 boolean clickedMsoe,
                                                                 String sceneToGoTo,
                                                                 Riasec riasecToGoTo,
                                                                 FilterGroupModel filterToGoTo,
                                                                 float totalTimeOpening,
                                                                 float totalTimeEnding,
                                                                 int sceneAnimationMilliseconds,
                                                                 int screenW,
                                                                 int screenH,
                                                                 float headerY,
                                                                 float headerH) {
        int offsetX;
        int offsetY;
        int otherOffsetX;
        int typeOfAnimation;

        if (sketch.isEditor) {
            if (clickedNext) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            } else if (clickedBack) {
                sketch.getSceneGraph().popScene();
            } else if (clickedHome) {
                sketch.getSceneGraph().reset();
            }
        }

        if ((clickedMsoe) && !sketch.isEditor) {
            offsetX = (int) (screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 1;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        } else if ((clickedNext) && !sketch.isEditor
                && sketch.getSceneGraph().getSceneById(sceneToGoTo)
                .toString().contains("Career Pathway")) {
            final double availableHeight = (screenH - headerY - headerH);
            final double size = Math.min(screenW, availableHeight);
            offsetX = (int) (0 - ((screenW - size) / 2)
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            otherOffsetX = (int) (screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            typeOfAnimation = 3;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            }
        } else if ((clickedNext) && !sketch.isEditor
                && !sketch.getSceneGraph().getSceneById(sceneToGoTo)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = 0;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 1;

            sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
        } else if ((clickedNext) && !sketch.isEditor
                && sketch.getSceneGraph().getSceneById(sceneToGoTo)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = 0;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 5;
        } else if (clickedBack && !sketch.isEditor
                && !sketch.getSceneGraph().history.get(1)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = (int) (0 - screenW
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 2;

            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clickedBack && !sketch.isEditor
                && sketch.getSceneGraph().history.get(1)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = 0;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 4;
        } else if (clickedHome && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            otherOffsetX = 0;
            typeOfAnimation = 1;

            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.contains("RESET")
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH + screenH
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            otherOffsetX = 0;
            typeOfAnimation = 1;
        } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor
                && sketch.getSceneGraph().recentActivity.contains("Career Pathway")
                && sketch.getSceneGraph().recentActivity.contains("POP")) {
            offsetX = 0;
            offsetY = (int) (screenW + screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            otherOffsetX = 0;
            typeOfAnimation = 3;
        } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor
                && !sketch.getSceneGraph().recentActivity.contains("Spoke Graph Prompt")
                && sketch.getSceneGraph().recentActivity.contains("POP")) {
            offsetX = (int) (0 - screenW - screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 2;
        } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor
                && !sketch.getSceneGraph().recentActivity.contains("Spoke Graph Prompt")) {
            offsetX = (int) (screenW + screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 2;
        } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = 0;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 2;
        } else { //If it's already a second-or-two old, draw the scene normally
            offsetX = 0;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 1;
        }

        return new int[] {offsetX, offsetY, otherOffsetX, typeOfAnimation};
    }
}
