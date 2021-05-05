package graphics;

import kiosk.Kiosk;
import kiosk.Riasec;
import kiosk.SceneGraph;
import kiosk.models.CareerModel;
import kiosk.models.CreditsSceneModel;
import kiosk.models.FilterGroupModel;

public class SceneAnimationHelper {

    public enum Clicked {
        BACK, HOME, NEXT, MSOE, NONE
    }

    /**
     * The logic to determine the animation for all scene types except for
     * SpokeGraphPromptScenes and CareerPathwayScenes,
     * as they involve spoke interpolation.
     * @param sketch to draw to
     * @param clicked an enum stating what type of click just occurred
     * @param sceneToGoTo the scene to travel to, if "Next" was pressed
     * @param riasecToGoTo the riasec type to travel to, if "Next" was pressed
     * @param filterToGoTo the filter to use while travelling, if "Next" was pressed
     * @param totalTimeOpening the time currently spent animating the scene in
     * @param totalTimeEnding the time currently spent animating the scene out
     * @param sceneAnimationMilliseconds the duration of scene animations
     * @param screenW the screen width
     * @param screenH the screen height
     * @return an array of ints, where [0] is the offsetX and [1] is the offsetY.
     */
    public static int[] sceneAnimationLogic(Kiosk sketch,
                                           Clicked clicked,
                                           String sceneToGoTo, Riasec riasecToGoTo,
                                            FilterGroupModel filterToGoTo,
                                           float totalTimeOpening, float totalTimeEnding,
                                            int sceneAnimationMilliseconds,
                                           int screenW, int screenH) {
        int offsetX;
        int offsetY;

        if (sketch.isEditor) {
            if (clicked.equals(Clicked.NEXT)) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            } else if (clicked.equals(Clicked.BACK)) {
                sketch.getSceneGraph().popScene();
            } else if (clicked.equals(Clicked.HOME)) {
                sketch.getSceneGraph().reset();
            } else if (clicked.equals(Clicked.MSOE)) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        }

        double offsetToUseX = screenW
                * (1 - ((totalTimeEnding) * 1.0
                / sceneAnimationMilliseconds + 1));
        if ((clicked.equals(Clicked.NEXT) || clicked.equals(Clicked.MSOE)) && !sketch.isEditor) {
            offsetX = (int) offsetToUseX;
            offsetY = 0;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                if (clicked.equals(Clicked.NEXT)) {
                    sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
                } else {
                    sketch.getSceneGraph().pushScene(new CreditsSceneModel());
                }
            }
        } else if (clicked.equals(Clicked.BACK) && !sketch.isEditor) {
            offsetX = (int) (0 - offsetToUseX);
            offsetY = 0;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clicked.equals(Clicked.HOME) && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.RESET)
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH + screenH
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
        } else {
            double offsetToUseOpeningX = screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1));
            if (sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.POP)
                    && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
                offsetX = (int) (0 - screenW - offsetToUseOpeningX);
                offsetY = 0;
            } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
                offsetX = (int) ((screenW + offsetToUseOpeningX));
                offsetY = 0;
            } else { //If it's already a second-or-two old, draw the scene normally
                offsetX = 0;
                offsetY = 0;
            }
        }

        return new int[] {offsetX, offsetY};
    }

    /**
     * The logic to determine the animation for CareerPathwayScenes,
     * involves interpolation.
     * @param sketch to draw to
     * @param clicked an enum stating what type of click just occurred
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
                                                              Clicked clicked,
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
            if (clicked.equals(Clicked.NEXT)) {
                sketch.getSceneGraph().pushEndScene(desiredCareer);
            } else if (clicked.equals(Clicked.BACK)) {
                sketch.getSceneGraph().popScene();
            } else if (clicked.equals(Clicked.HOME)) {
                sketch.getSceneGraph().reset();
            } else if (clicked.equals(Clicked.MSOE)) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        }

        double offsetToUseX = screenW
                * (1 - ((totalTimeEnding) * 1.0
                / sceneAnimationMilliseconds + 1));
        if ((clicked.equals(Clicked.NEXT) || clicked.equals(Clicked.MSOE)) && !sketch.isEditor) {
            offsetX = (int) offsetToUseX;
            offsetY = 0;
            headerOffsetX = 0;
            typeOfAnimation = 1;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                if (clicked.equals(Clicked.NEXT)) {
                    sketch.getSceneGraph().pushEndScene(desiredCareer);
                } else {
                    sketch.getSceneGraph().pushScene(new CreditsSceneModel());
                }
            }
        } else if (clicked.equals(Clicked.BACK) && !sketch.isEditor
                && sketch.getSceneGraph().getFromHistory(1)
                .toString().contains("Spoke Graph Prompt")) {
            final double availableHeight = (screenH - (screenH / 32f) - (screenH / 6f));
            final double size = Math.min(screenW, availableHeight);
            offsetX = (int) (((screenW - size) / 2)
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            headerOffsetX = (int) (0 - offsetToUseX);
            typeOfAnimation = 3;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clicked.equals(Clicked.BACK) && !sketch.isEditor) {
            offsetX = (int) (0 - offsetToUseX);
            offsetY = 0;
            headerOffsetX = 0;
            typeOfAnimation = 1;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clicked.equals(Clicked.HOME) && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            headerOffsetX = 0;
            typeOfAnimation = 1;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.RESET)
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH + screenH
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            headerOffsetX = 0;
            typeOfAnimation = 1;
        } else {
            double offsetToUseOpeningX = screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1));
            if (sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.POP)
                    && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
                offsetX = (int) (0 - screenW - offsetToUseOpeningX);
                offsetY = 0;
                headerOffsetX = 0;
                typeOfAnimation = 1;
            } else if (sceneAnimationMilliseconds > totalTimeOpening
                    && !sketch.isEditor && sketch.getSceneGraph().getFromHistory(1)
                    .toString().contains("Spoke Graph Prompt")) {
                offsetX = (int) (screenW + offsetToUseOpeningX);
                offsetY = 0;
                headerOffsetX = 0;
                typeOfAnimation = 2;
            } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
                offsetX = (int) (screenW + offsetToUseOpeningX);
                offsetY = 0;
                headerOffsetX = 0;
                typeOfAnimation = 1;
            } else {
                offsetX = 0;
                offsetY = 0;
                headerOffsetX = 0;
                typeOfAnimation = 1;
            }
        }

        return new int[] {offsetX, offsetY, headerOffsetX, typeOfAnimation};
    }

    /**
     * The logic to determine the animation for SpokeGraphPromptScenes,
     * involves interpolation.
     * @param sketch to draw to
     * @param clicked an enum stating what type of click just occurred
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
                                                                 Clicked clicked,
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
            if (clicked.equals(Clicked.NEXT)) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            } else if (clicked.equals(Clicked.BACK)) {
                sketch.getSceneGraph().popScene();
            } else if (clicked.equals(Clicked.HOME)) {
                sketch.getSceneGraph().reset();
            }
        }

        double offsetToUseX = screenW
                * (1 - ((totalTimeEnding) * 1.0
                / sceneAnimationMilliseconds + 1));
        if ((clicked.equals(Clicked.MSOE)) && !sketch.isEditor) {
            offsetX = (int) offsetToUseX;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 1;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().pushScene(new CreditsSceneModel());
            }
        } else if ((clicked.equals(Clicked.NEXT)) && !sketch.isEditor
                && sketch.getSceneGraph().getSceneById(sceneToGoTo)
                .toString().contains("Career Pathway")) {
            final double availableHeight = (screenH - headerY - headerH);
            final double size = Math.min(screenW, availableHeight);
            offsetX = (int) (0 - ((screenW - size) / 2)
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            offsetY = 0;
            otherOffsetX = (int) offsetToUseX;
            typeOfAnimation = 3;
            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
            }
        } else if ((clicked.equals(Clicked.NEXT)) && !sketch.isEditor
                && !sketch.getSceneGraph().getSceneById(sceneToGoTo)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = 0;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 1;

            sketch.getSceneGraph().pushScene(sceneToGoTo, riasecToGoTo, filterToGoTo);
        } else if ((clicked.equals(Clicked.NEXT)) && !sketch.isEditor
                && sketch.getSceneGraph().getSceneById(sceneToGoTo)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = 0;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 5;
        } else if (clicked.equals(Clicked.BACK) && !sketch.isEditor
                && !sketch.getSceneGraph().getFromHistory(1)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = (int) (0 - offsetToUseX);
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 2;

            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().popScene();
            }
        } else if (clicked.equals(Clicked.BACK) && !sketch.isEditor
                && sketch.getSceneGraph().getFromHistory(1)
                .toString().contains("Spoke Graph Prompt")) {
            offsetX = 0;
            offsetY = 0;
            otherOffsetX = 0;
            typeOfAnimation = 4;
        } else if (clicked.equals(Clicked.HOME) && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH
                    * (1 - ((totalTimeEnding) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            otherOffsetX = 0;
            typeOfAnimation = 1;

            if (sceneAnimationMilliseconds <= totalTimeEnding) {
                sketch.getSceneGraph().reset();
            }
        } else if (sketch.getSceneGraph().recentActivity.equals(SceneGraph.RecentActivity.RESET)
                && sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor) {
            offsetX = 0;
            offsetY = (int) (screenH + screenH
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1)));
            otherOffsetX = 0;
            typeOfAnimation = 1;
        } else {
            double offsetToUseOpeningPartialX = screenW
                    * (1 - ((totalTimeOpening) * 1.0
                    / sceneAnimationMilliseconds + 1));
            double offsetToUseOpeningX = 0 - screenW - offsetToUseOpeningPartialX;
            if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor
                    && sketch.getSceneGraph().recentScene
                    .equals(SceneGraph.RecentScene.CAREER_PATHWAY)
                    && sketch.getSceneGraph().recentActivity
                    .equals(SceneGraph.RecentActivity.POP)) {
                offsetX = 0;
                offsetY = 0;
                otherOffsetX = (int) offsetToUseOpeningX;
                typeOfAnimation = 3;
            } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor
                    && !sketch.getSceneGraph().recentScene
                    .equals(SceneGraph.RecentScene.SPOKE_GRAPH_PROMPT)
                    && sketch.getSceneGraph().recentActivity
                    .equals(SceneGraph.RecentActivity.POP)) {
                offsetX = (int) offsetToUseOpeningX;
                offsetY = 0;
                otherOffsetX = 0;
                typeOfAnimation = 2;
            } else if (sceneAnimationMilliseconds > totalTimeOpening && !sketch.isEditor
                    && !sketch.getSceneGraph().recentScene
                    .equals(SceneGraph.RecentScene.SPOKE_GRAPH_PROMPT)) {
                offsetX = (int) (screenW + offsetToUseOpeningPartialX);
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
        }

        return new int[] {offsetX, offsetY, otherOffsetX, typeOfAnimation};
    }
}
