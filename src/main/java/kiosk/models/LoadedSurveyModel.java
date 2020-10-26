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
        var catsOrDogs = new PromptSceneModel("Which do you prefer?", new ButtonModel[]{
            new ButtonModel("Cats", "0"),
            new ButtonModel("Dogs", "0")
        }, false);

        var coffee = new PromptSceneModel("How do you like your coffee?", new ButtonModel[]{
            new ButtonModel("Black", "1"),
            new ButtonModel("Blacker", "1")
        }, true);

        var yogurt = new PromptSceneModel("Are you supposed to stir greek yogurt?",
            new ButtonModel[]{
                new ButtonModel("No", "2")
            }, false);

        var caps = new PromptSceneModel("Caps! Caps for sale!", new ButtonModel[]{
            new ButtonModel("Fifty", "3"),
            new ButtonModel("cents", "3"),
            new ButtonModel("a", "3"),
            new ButtonModel("cap", "3"),
            new ButtonModel("!", "3")
        }, true);

        var transitionToCoffee = new WaveTransitionSceneModel(coffee.getId(), false, "0");
        var transitionToYogurt = new WaveTransitionSceneModel(yogurt.getId(), true, "1");
        var transitionToCaps = new WaveTransitionSceneModel(caps.getId(), false, "2");
        var transitionToCatsOrDogs = new WaveTransitionSceneModel(catsOrDogs.getId(), true, "3");

        var initialScenes = new ArrayList<SceneModel>();
        initialScenes.add(caps);
        initialScenes.add(transitionToYogurt);
        initialScenes.add(yogurt);
        initialScenes.add(transitionToCoffee);
        initialScenes.add(coffee);
        initialScenes.add(transitionToCatsOrDogs);
        initialScenes.add(catsOrDogs);
        initialScenes.add(transitionToCaps);

        return new LoadedSurveyModel(initialScenes);
    }
}
