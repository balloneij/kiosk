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
import kiosk.Riasec;

public class LoadedSurveyModel implements Serializable {

    public String rootSceneId;
    public SceneModel[] scenes;
    public CareerModel[] careers;

    // TODO there will need to be methods for adding new filters to this array once filter
    //  creation/editing is implemented. The current filters are examples/placeholders.
    public static FilterGroupModel[] filters = {
        // "All" filter functionality is handled in FilterGroupModel.getCareers()
        new FilterGroupModel("All"),
        new FilterGroupModel("Nature Careers", "Realistic", "Investigative", "Social"),
        new FilterGroupModel("Human Careers", "Realistic")
    };



    /**
     * Creates a survey with a single, error scene.
     * This constructor should never be used directly. It is meant to
     * make programmer errors easy to find because the XML parser will
     * use the default constructor.
     */
    public LoadedSurveyModel() {
        this.scenes = new SceneModel[]{
            new ErrorSceneModel("Default, empty loaded survey model")
        };
        this.rootSceneId = this.scenes[0].getId();
    }

    /**
     * Create a survey model from a list of sceneModels.
     * CAREFUL!! The root scene will be automatically set to the first item
     * in the list. If this is not desired, specify the rootSceneId in the
     * other constructor.
     * @param sceneModels to create a survey using. First sceneModel is set to the root
     */
    public LoadedSurveyModel(List<SceneModel> sceneModels) {
        if (sceneModels.isEmpty()) {
            throw new IllegalArgumentException("A LoadedSurveyModel must have at least one scene");
        }

        this.rootSceneId = sceneModels.get(0).getId();
        this.scenes = new SceneModel[sceneModels.size()];
        this.scenes = sceneModels.toArray(scenes);
    }

    /**
     * Creates a survey model from a list of sceneModels with a specific
     * root.
     * @param rootSceneId id of the root scene
     * @param sceneModels that make up a survey
     */
    public LoadedSurveyModel(String rootSceneId, List<SceneModel> sceneModels) {
        if (sceneModels.isEmpty()) {
            throw new IllegalArgumentException("A LoadedSurveyModel must have at least one scene");
        }
        // Protect the survey from a bad state. A survey _must_ have a root
        // that exists within the scene list. This allows us to safely assume
        // there is always a root, and it decreases the complexity of editor code.
        boolean rootFound = false;
        for (int i = 0; !rootFound && i < sceneModels.size(); i++) {
            if (sceneModels.get(i).getId().equals(rootSceneId)) {
                rootFound = true;
            }
        }
        if (!rootFound) {
            throw new IllegalArgumentException("A LoadedSurveyModel cannot exist without a root");
        }

        this.rootSceneId = rootSceneId;
        this.scenes = new SceneModel[sceneModels.size()];
        this.scenes = sceneModels.toArray(scenes);
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
            decoder.setExceptionListener(ex -> {
                throw new RuntimeException("Malformed XML Dataset");
            });
            Object surveyObject = decoder.readObject();
            if (!(surveyObject instanceof LoadedSurveyModel)) {
                String errorMsg = "Successfully loaded the survey XML, but\n"
                        + "the root object is not of the type 'LoadedSurveyModel'";
                ErrorSceneModel defaultScene = new ErrorSceneModel(errorMsg);
                ArrayList<SceneModel> defaultSceneList = new ArrayList<SceneModel>();
                defaultSceneList.add(defaultScene);
                LoadedSurveyModel errorSurvey = new LoadedSurveyModel(defaultSceneList);
                return errorSurvey;
            }
            return (LoadedSurveyModel) surveyObject;
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            String errorMsg = "Could not read from survey at '" + file.getPath()
                    + "':\n" + exc.getMessage()
                    + "\n\nPress F2 to open the file-chooser and select a survey file. Press F5 "
                    + "to refresh the view. "
                    + "\nThe program can also be started from the command line with the command "
                    + "\n\"java -jar kiosk.jar <survey file>\""
                    + "\nwhere <survey file> is the path to the survey file.";
            ErrorSceneModel defaultScene = new ErrorSceneModel(errorMsg);
            ArrayList<SceneModel> defaultSceneList = new ArrayList<SceneModel>();
            defaultSceneList.add(defaultScene);
            LoadedSurveyModel errorSurvey = new LoadedSurveyModel(defaultSceneList);
            return errorSurvey;
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = "Could not read from survey at '" + file.getPath()
                    + "'\nThe XML is probably deformed in some way."
                    + "\nRefer to the console for more specific details.";
            ErrorSceneModel defaultScene = new ErrorSceneModel(errorMsg);
            ArrayList<SceneModel> defaultSceneList = new ArrayList<SceneModel>();
            defaultSceneList.add(defaultScene);
            LoadedSurveyModel errorSurvey = new LoadedSurveyModel(defaultSceneList);
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
        agePrompt.answers = new ButtonModel[] {
            gradeSchoolButton,
            middleSchoolButton,
            adultButton
        };

        PromptSceneModel pathPrompt = new PromptSceneModel();
        pathPrompt.id = "introscene3";
        pathPrompt.title = "Wonderful!";
        pathPrompt.prompt = "How will you build the future?\n"
                + "What challenges do you want to take on?";
        pathPrompt.actionPhrase = "Choose one of the Icons. Explore different paths.\n"
                + "You can always go back and begin again";
        ButtonModel humanButton = new ButtonModel();
        humanButton.text = "";
        humanButton.target = "pathway";
        humanButton.isCircle = true;
        humanButton.rgb = new int[] { 152, 33, 107 };
        humanButton.image = new ImageModel("assets/human.png", 80, 80);
        ButtonModel natureButton = new ButtonModel();
        natureButton.text = "";
        natureButton.target = "pathway";
        natureButton.isCircle = true;
        natureButton.rgb = new int[] { 51, 108, 103 };
        natureButton.image = new ImageModel("assets/nature.png", 80, 80);
        ButtonModel smartMachinesButton = new ButtonModel();
        smartMachinesButton.text = "";
        smartMachinesButton.target = "pathway";
        smartMachinesButton.isCircle = true;
        smartMachinesButton.rgb = new int[] { 219, 98, 38 };
        smartMachinesButton.image = new ImageModel("assets/robot.png", 80, 80);
        ButtonModel spaceButton = new ButtonModel();
        spaceButton.text = "";
        spaceButton.target = "pathway";
        spaceButton.isCircle = true;
        spaceButton.rgb = new int[] { 21, 97, 157 };
        spaceButton.image = new ImageModel("assets/space.png", 80, 80);
        pathPrompt.answers = new ButtonModel[] {
            humanButton,
            natureButton,
            smartMachinesButton,
            spaceButton
        };

        PathwaySceneModel pathway = new PathwaySceneModel();
        pathway.buttonModels = new ButtonModel[8];
        for (int i = 0; i < pathway.buttonModels.length; i++) {
            ButtonModel buttonModel = new ButtonModel();
            buttonModel.isCircle = true;
            buttonModel.text = "Choice " + i;
            buttonModel.target = "spoke";
            pathway.buttonModels[i] = buttonModel;
        }
        pathway.centerText = "Pick 1!";
        pathway.headerBody = "There are many options";
        pathway.headerTitle = "Choose one please";
        pathway.id = "pathway";

        SpokeGraphPromptSceneModel spoke = new SpokeGraphPromptSceneModel();
        spoke.id = "spoke";
        spoke.headerTitle = "Answer some questions";
        spoke.headerBody = "Click the answer the best applies to you";
        spoke.promptText = "What's your favorite subject?";

        ButtonModel answer1 = new ButtonModel();
        answer1.text = "I like math";
        ButtonModel answer2 = new ButtonModel();
        answer2.text = "I like math";
        ButtonModel answer3 = new ButtonModel();
        answer3.text = "I like math";
        spoke.answers = new ButtonModel[] { answer1, answer2, answer3 };

        spoke.answers = new ButtonModel[8];
        for (int i = 0; i < spoke.answers.length; i++) {
            ButtonModel buttonModel = new ButtonModel();
            buttonModel.text = "Career " + i;
            spoke.answers[i] = buttonModel;
        }

        ArrayList<SceneModel> initialScenes = new ArrayList<SceneModel>();
        initialScenes.add(titleScreen);
        initialScenes.add(challengePrompt);
        initialScenes.add(agePrompt);
        initialScenes.add(pathPrompt);
        initialScenes.add(pathway);
        initialScenes.add(spoke);

        LoadedSurveyModel survey = new LoadedSurveyModel(titleScreen.id, initialScenes);

        survey.careers = new CareerModel[]{
            new CareerModel("Realistic", Riasec.Realistic, "field", "category"),
            new CareerModel("Investigative", Riasec.Investigative, "field", "category"),
            new CareerModel("Artistic", Riasec.Artistic, "field", "category"),
            new CareerModel("Social", Riasec.Social, "field", "category"),
            new CareerModel("Enterprising", Riasec.Enterprising, "field", "category"),
            new CareerModel("Conventional", Riasec.Conventional, "field", "category"),
        };

        return survey;
    }
}
