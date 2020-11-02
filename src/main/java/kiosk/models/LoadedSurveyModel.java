package kiosk.models;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
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
        titleScreen.title = "Velcome to my lair";
        titleScreen.prompt = "@,.,@\nI vant to suck\nyour blood";
        ButtonModel titleScreenButton = new ButtonModel();
        titleScreenButton.text = "Okay bud";
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
        ButtonModel middleSchoolButton = new ButtonModel();
        middleSchoolButton.text = "Middle\nSchool";
        middleSchoolButton.target = "introscene3";
        middleSchoolButton.isCircle = true;
        ButtonModel adultButton = new ButtonModel();
        adultButton.text = "Adult";
        adultButton.target = "introscene3";
        adultButton.isCircle = true;
        agePrompt.answers = new ButtonModel[] { gradeSchoolButton, middleSchoolButton, adultButton };

        var initialScenes = new ArrayList<SceneModel>();
        initialScenes.add(titleScreen);
        initialScenes.add(challengePrompt);
        initialScenes.add(agePrompt);

        return new LoadedSurveyModel(initialScenes);
    }
}
