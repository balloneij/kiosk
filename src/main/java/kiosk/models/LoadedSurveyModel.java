package kiosk.models;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoadedSurveyModel implements Serializable {

    public SceneModel[] scenes;

    public LoadedSurveyModel() {
        scenes = new SceneModel[]{};
    }

    public LoadedSurveyModel(List<SceneModel> initialScenes) {
        this.scenes = new SceneModel[initialScenes.size()];
        this.scenes = initialScenes.toArray(scenes);
    }

    /**
     * Writes the survey as XML to the specified file.
     * @param file to write the survey to
     * @return true if successful, false otherwise
     */
    public boolean writeToFile(File file) {
        try (XMLEncoder encoder = new XMLEncoder(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            encoder.writeObject(this);
            return true;
        } catch (FileNotFoundException exc) {
            System.err.println("Could not write survey to '" + file.getPath()
                    + "': " + exc.getMessage());
            return false;
        }
    }

    /**
     * Reads the survey as XML from the specified file.
     * @param file to read the survey from
     * @return a valid survey, always (i.e. never null)
     */
    public static LoadedSurveyModel readFromFile(File file) {
        try (XMLDecoder decoder = new XMLDecoder(
                new BufferedInputStream(new FileInputStream(file)))) {
            Object surveyObject = decoder.readObject();
            if (!(surveyObject instanceof LoadedSurveyModel)) {
                String errorMsg = "Successfully loaded the survey XML, but\n"
                        + "the root object is not of the type 'LoadedSurveyModel'";
                LoadedSurveyModel errorSurvey = new LoadedSurveyModel();
                errorSurvey.scenes = new SceneModel[]{ new ErrorSceneModel(errorMsg) };
                return errorSurvey;
            }
            return (LoadedSurveyModel) surveyObject;
        } catch (FileNotFoundException exc) {
            String errorMsg = "Could not read from survey at '" + file.getPath()
                    + "':\n" + exc.getMessage();
            LoadedSurveyModel errorSurvey = new LoadedSurveyModel();
            errorSurvey.scenes = new SceneModel[]{ new ErrorSceneModel(errorMsg) };
            return errorSurvey;
        } catch (Exception exc) {
            String errorMsg = "Could not read from survey at '" + file.getPath()
                    + "'\nThe XML is probably deformed in some way."
                    + "\nRefer to the console for more specific details.";
            LoadedSurveyModel errorSurvey = new LoadedSurveyModel();
            errorSurvey.scenes = new SceneModel[]{ new ErrorSceneModel(errorMsg) };
            return errorSurvey;
        }
    }

    /**
     * Creates a sample survey full of cats, dogs, coffee, yogurt, and caps.
     * @return a survey
     */
    public static LoadedSurveyModel createSampleSurvey() {

        PromptSceneModel titleScreen = new PromptSceneModel();
        titleScreen.id = "introscene0";
        titleScreen.title = "Eye grabbing scene";
        titleScreen.prompt = "Eventually this should be a scene that grabs\n"
                + "attention. Maybe a fun animation or something?";
        ButtonModel titleScreenButton = new ButtonModel();
        titleScreenButton.text = "Let's go";
        titleScreenButton.target = "introscene1";
        titleScreen.answers = new ButtonModel[]{ titleScreenButton };

        PromptSceneModel challengePrompt = new PromptSceneModel();
        challengePrompt.id = "introscene1";
        challengePrompt.title = "What Challenge Do you Want to Take On?";
        challengePrompt.prompt = "Everyone asks what you want to be when you grow up.\n"
                + "We're asking what challenges you want to take on.\n"
                + "What big problem do you want to help solve?";
        challengePrompt.actionPhrase = "Ready?";
        ButtonModel challengeYesButton = new ButtonModel();
        challengeYesButton.text = "Yes!";
        challengeYesButton.target = "introscene2";
        challengePrompt.answers = new ButtonModel[] { challengeYesButton };

        PromptSceneModel agePrompt = new PromptSceneModel();
        agePrompt.id = "introscene2";
        agePrompt.title = "Awesome!";
        agePrompt.prompt = "Are you in Grade School, Middle School, or\n"
                + "High School? Or are you an adult?\n ";
        ButtonModel gradeSchoolButton = new ButtonModel();
        gradeSchoolButton.text = "Grade\nSchool";
        gradeSchoolButton.target = "introscene3";
        gradeSchoolButton.isCircle = true;
        gradeSchoolButton.rgb = new int[] { 248, 153, 29 };
        ButtonModel middleSchoolButton = new ButtonModel();
        middleSchoolButton.text = "Middle\nSchool";
        middleSchoolButton.target = "introscene3";
        middleSchoolButton.isCircle = true;
        middleSchoolButton.rgb = new int[] { 244, 117, 33 };
        ButtonModel adultButton = new ButtonModel();
        adultButton.text = "Adult";
        adultButton.target = "introscene3";
        adultButton.isCircle = true;
        adultButton.rgb = new int[] { 244, 80, 50 };
        agePrompt.answers = new ButtonModel[] { gradeSchoolButton, middleSchoolButton, adultButton };

        PromptSceneModel pathPrompt = new PromptSceneModel();
        pathPrompt.id = "introscene3";
        pathPrompt.title = "Wonderful!";
        pathPrompt.prompt = "How will you build the future?\n"
                + "What challenges do you want to take on?";
        pathPrompt.actionPhrase = "Choose one of the Icons. Explore different paths.\n"
                + "You can always go back and begin again";
        ButtonModel humanButton = new ButtonModel();
        humanButton.text = "";
        humanButton.target = "";
        humanButton.isCircle = true;
        humanButton.rgb = new int[] { 152, 33, 107 };
        humanButton.image = new ImageModel("assets/human.png", 80, 80);
        ButtonModel natureButton = new ButtonModel();
        natureButton.text = "";
        natureButton.target = "";
        natureButton.isCircle = true;
        natureButton.rgb = new int[] { 51, 108, 103 };
        natureButton.image = new ImageModel("assets/nature.png", 80, 80);
        ButtonModel smartMachinesButton = new ButtonModel();
        smartMachinesButton.text = "";
        smartMachinesButton.target = "";
        smartMachinesButton.isCircle = true;
        smartMachinesButton.rgb = new int[] { 219, 98, 38 };
        smartMachinesButton.image = new ImageModel("assets/robot.png", 80, 80);
        ButtonModel spaceButton = new ButtonModel();
        spaceButton.text = "";
        spaceButton.target = "";
        spaceButton.isCircle = true;
        spaceButton.rgb = new int[] { 21, 97, 157 };
        spaceButton.image = new ImageModel("assets/space.png", 80, 80);
        pathPrompt.answers = new ButtonModel[] { humanButton, natureButton, smartMachinesButton, spaceButton };

        var initialScenes = new ArrayList<SceneModel>();
        initialScenes.add(titleScreen);
        initialScenes.add(challengePrompt);
        initialScenes.add(agePrompt);
        initialScenes.add(pathPrompt);

        return new LoadedSurveyModel(initialScenes);
    }
}
