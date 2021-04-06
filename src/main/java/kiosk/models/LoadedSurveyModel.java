package kiosk.models;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import kiosk.Riasec;

public class LoadedSurveyModel implements Serializable {

    public String rootSceneId;
    public SceneModel[] scenes;
    public CareerModel[] careers;
    public FilterGroupModel[] filters;

    /**
     * Creates a survey with a single, error scene.
     * This constructor should never be used directly. It is meant to
     * make programmer errors easy to find because the XML parser will
     * use the default constructor.
     */
    public LoadedSurveyModel() {
        // Left blank for the XML Encoder
    }

    /**
     * Factory method for a default model.
     * @return model instance
     */
    public static LoadedSurveyModel create() {
        LoadedSurveyModel model = new LoadedSurveyModel();
        EmptySceneModel sceneModel = new EmptySceneModel();
        sceneModel.message = "Default, empty loaded survey model";
        model.scenes = new SceneModel[] {
            sceneModel
        };
        model.rootSceneId = sceneModel.getId();
        model.careers = new CareerModel[0];
        model.filters = new FilterGroupModel[0];
        return model;
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

    private static List<CareerModel> loadCareers(File csvFile) {
        // Read careers from the csv file
        ArrayList<CareerModel> careersList = new ArrayList<>();
        if (csvFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");

                    // Get values from columns
                    CareerModel career = new CareerModel();
                    career.riasecCategory = Riasec.valueOf(values[0]);
                    career.field = values[1];
                    career.category = values[2];
                    career.name = values[3];
                    career.description = values[4];

                    careersList.add(career);
                }
            } catch (IOException e) {
                e.printStackTrace();
                careersList = new ArrayList<>();
            }
        }

        return careersList;
    }

    /**
     * Creates a sample survey full of cats, dogs, coffee, yogurt, and caps.
     * @return a survey
     */
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    public static LoadedSurveyModel createSampleSurvey() {
        HashMap<String, FilterGroupModel> filters = new HashMap<>();

        // Load careers from CSV
        List<CareerModel> careers = loadCareers(new File("sample_careers.csv"));

        // Create filters based off of the sample careers
        for (CareerModel career : careers) {
            FilterGroupModel filter;
            if (filters.containsKey(career.field)) {
                filter = filters.get(career.field);
            } else {
                filter = FilterGroupModel.create();
                filter.name = career.field;
                filters.put(career.field, filter);
            }
            filter.careerNames.add(career.name);
        }

        // Remove fields that only have one career
        LinkedList<String> toRemove = new LinkedList<>();
        for (String field : filters.keySet()) {
            if (filters.get(field).careerNames.size() <= 1) {
                toRemove.push(field);
            }
        }
        for (String field : toRemove) {
            filters.remove(field);
        }

        // Create scenes
        LinkedList<SceneModel> scenes = new LinkedList<>();

        // Eye catching scene
        PromptSceneModel eyeCatcher = new PromptSceneModel();
        scenes.push(eyeCatcher);
        eyeCatcher.name = "Eye Catcher";
        eyeCatcher.title = "Eye grabbing scene";
        eyeCatcher.prompt = "Eventually this should be a scene that grabs\n"
                + "attention. Maybe a fun animation or something?";
        ButtonModel eyeCatcherButton = new ButtonModel();
        eyeCatcherButton.text = "Let's go";
        eyeCatcherButton.target = "titleScene";
        eyeCatcher.answers = new ButtonModel[]{ eyeCatcherButton };

        // Title scene
        PromptSceneModel titleScene = new PromptSceneModel();
        scenes.push(titleScene);
        titleScene.id = "titleScene";
        titleScene.name = "Title Scene";
        titleScene.title = "What Challenge Do you Want to Take On?";
        titleScene.prompt = "Everyone asks what you want to be when you grow up.\n"
                + "We're asking what challenges you want to take on.\n"
                + "What big problem do you want to help solve?";
        titleScene.actionPhrase = "Ready?";
        ButtonModel titleSceneButton = new ButtonModel();
        titleSceneButton.text = "Yes!";
        titleSceneButton.target = "categoryScene";
        titleScene.answers = new ButtonModel[] { titleSceneButton };

        // Category scene
        PromptSceneModel categoryScene = new PromptSceneModel();
        scenes.push(categoryScene);
        categoryScene.id = "categoryScene";
        categoryScene.name = "Category Scene";
        categoryScene.title = "Wonderful!";
        categoryScene.prompt = "How will you build the future?\n"
                + "What challenges do you want to take on?";
        categoryScene.actionPhrase = "Choose one of the Icons. Explore different paths.\n"
                + "You can always go back and begin again";
        ButtonModel humanButton = new ButtonModel();
        humanButton.text = "";
        humanButton.target = "fieldPickerHuman";
        humanButton.isCircle = true;
        humanButton.rgb = new int[] { 152, 33, 107 };
        humanButton.image = new ImageModel("assets/Human.png", 80, 80);
        ButtonModel natureButton = new ButtonModel();
        natureButton.text = "";
        natureButton.target = "fieldPickerNature";
        natureButton.isCircle = true;
        natureButton.rgb = new int[] { 51, 108, 103 };
        natureButton.image = new ImageModel("assets/Nature.png", 80, 80);
        ButtonModel smartMachinesButton = new ButtonModel();
        smartMachinesButton.text = "";
        smartMachinesButton.target = "fieldPickerSmartMachine";
        smartMachinesButton.isCircle = true;
        smartMachinesButton.rgb = new int[] { 219, 98, 38 };
        smartMachinesButton.image = new ImageModel("assets/SmartMachines.png", 80, 80);
        ButtonModel spaceButton = new ButtonModel();
        spaceButton.text = "";
        spaceButton.target = "fieldPickerSpace";
        spaceButton.isCircle = true;
        spaceButton.rgb = new int[] { 21, 97, 157 };
        spaceButton.image = new ImageModel("assets/Space.png", 80, 80);
        categoryScene.answers = new ButtonModel[] {
            humanButton,
            natureButton,
            smartMachinesButton,
            spaceButton
        };

        // Field picker for each category
        scenes.push(createFieldPicker(careers, Category.Human));
        scenes.push(createFieldPicker(careers, Category.Nature));
        scenes.push(createFieldPicker(careers, Category.SmartMachine));
        scenes.push(createFieldPicker(careers, Category.Space));

        // Construct a 6 question survey. One question for each category of RIASEC
        for (String field : filters.keySet()) {
            for (int i = 0; i < 6; i++) {
                SpokeGraphPromptSceneModel scene = SpokeGraphPromptSceneModel.create();
                scene.id = "fieldPrompt" + i + field;
                scene.filter = filters.get(field);
                scene.name = field + " Prompt " + i;
                scene.headerTitle = "Let's talk about monke!";
                scene.headerBody = "Select the answer that best applies to you";
                scene.careerCenterText = "monke";
                scene.answers = new ButtonModel[] {
                    new ButtonModel("Bananas", "fieldPrompt" + (i + 1) + field, Riasec.Realistic),
                    new ButtonModel("Bananas", "fieldPrompt" + (i + 1) + field, Riasec.Artistic),
                    new ButtonModel("Bananas", "fieldPrompt" + (i + 1) + field, Riasec.Realistic)
                };
                scenes.push(scene);
            }
        }

        LoadedSurveyModel survey = LoadedSurveyModel.create();
        survey.rootSceneId = eyeCatcher.id;
        survey.scenes = scenes.toArray(new SceneModel[0]);
        survey.careers = careers.toArray(new CareerModel[0]);
        survey.filters = filters.values().toArray(new FilterGroupModel[0]);

        return survey;
    }

    private static SceneModel createFieldPicker(List<CareerModel> careers, Category category) {
        final int maxFields = 8;

        List<CareerModel> categoryCareers = careers
                .stream()
                .filter(career -> Category.valueOf(
                        career.category.replace(" ", "")).equals(category))
                .collect(Collectors.toList());

        HashSet<String> uniqueFields = new HashSet<>();
        for (CareerModel career : categoryCareers) {
            uniqueFields.add(career.field);
        }

        PathwaySceneModel scene = new PathwaySceneModel();

        scene.id = "fieldPicker" + category.name();
        scene.name = "Field Picker " + category.name();

        String[] fieldsArray = uniqueFields.toArray(new String[0]);
        int fieldCount = Math.min(uniqueFields.size(), maxFields);
        scene.buttonModels = new ButtonModel[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            ButtonModel button = new ButtonModel();
            button.isCircle = true;
            button.text = fieldsArray[i];
            button.target = "fieldPrompt0" + fieldsArray[i];
            scene.buttonModels[i] = button;
        }
        scene.centerText = "Pick one!";
        scene.headerTitle = "Choose a field you are interested in!";
        scene.headerBody = "You can always go back and change your answer.";

        return scene;
    }

    private enum Category {
        Human,
        Nature,
        SmartMachine,
        Space
    }
}
