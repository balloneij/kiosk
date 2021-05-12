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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import kiosk.Riasec;

public class LoadedSurveyModel implements Serializable {

    public String rootSceneId;
    public SceneModel[] scenes;

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
                    String[] values = line.split("\t");

                    // Get values from columns
                    CareerModel career = new CareerModel();
                    career.riasecCategory = Riasec.valueOf(values[2]);
                    career.field = values[1];
                    career.category = values[0];
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
        HashMap<String, FilterGroupModel> fieldFilters = new HashMap<>();
        HashMap<String, FilterGroupModel> categoryFilters = new HashMap<>();

        // Load careers from CSV
        CareerModelLoader loader =
                new CareerModelLoader(new File(CareerModelLoader.DEFAULT_CAREERS_CSV_PATH));

        CareerModel[] careers = loader.load();

        LinkedList<String> fields = new LinkedList<>();
        LinkedList<String> categories = new LinkedList<>();

        // Find the unique fields and categories
        {
            HashSet<String> fieldsSet = new HashSet<>();
            HashSet<String> categoriesSet = new HashSet<>();

            for (CareerModel career : careers) {
                if (!fieldsSet.contains(career.field)) {
                    fields.add(career.field);
                    fieldsSet.add(career.field);
                }
                if (!categoriesSet.contains(career.category)) {
                    categories.add(career.category);
                    categoriesSet.add(career.category);
                }
            }
        }

        // Create filters based off of the careers
        for (CareerModel career : careers) {
            FilterGroupModel fieldFilter;
            if (fieldFilters.containsKey(career.field)) {
                fieldFilter = fieldFilters.get(career.field);
                fieldFilter.category = career.category;
                fieldFilter.field = career.field;
            } else {
                fieldFilter = FilterGroupModel.create();
                fieldFilters.put(career.field, fieldFilter);
            }
            fieldFilter.careerNames.add(career.name);

            FilterGroupModel categoryFilter;
            if (categoryFilters.containsKey(career.category)) {
                categoryFilter = categoryFilters.get(career.category);
                categoryFilter.category = career.category;
                categoryFilter.field = "All";
            } else {
                categoryFilter = FilterGroupModel.create();
                categoryFilters.put(career.category, categoryFilter);
            }
            categoryFilter.careerNames.add(career.name);
        }

        // Create scenes
        LinkedList<SceneModel> scenes = new LinkedList<>();

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
        categoryScene.id = "categoryScene";
        categoryScene.name = "Category Scene";
        categoryScene.title = "Wonderful!";
        categoryScene.prompt = "How will you build the future?\n"
                + "What challenges do you want to take on?";
        categoryScene.actionPhrase = "Choose one of the Icons. Explore different paths.\n"
                + "You can always go back and begin again";
        categoryScene.answers = new ButtonModel[categories.size()];
        int j = 0;
        for (String category : categories) {
            ButtonModel button = new ButtonModel();
            button.text = category;
            button.target = "fieldPicker" + category;
            button.isCircle = true;
            button.rgb = new int[] { 152, 33, 107 };
            button.filter = categoryFilters.get(category);
            categoryScene.answers[j] = button;
            j++;
        }
        scenes.push(categoryScene);

        // Field picker for each category
        List<CareerModel> careersList = Arrays.asList(careers);
        for (String category : categories) {
            scenes.push(createFieldPicker(fieldFilters, careersList, category));
        }

        // Construct a 6 question survey. One question for each category of RIASEC
        final int questionCount = 6;
        for (int i = 0; i < questionCount; i++) {
            SpokeGraphPromptSceneModel scene = SpokeGraphPromptSceneModel.create();
            scene.id = "fieldPrompt" + i;
            scene.name = "Prompt " + i;
            scene.headerTitle = "Let's talk about your field!";
            scene.headerBody = "Select the answer that best applies to you";
            scene.careerCenterText = "Fields";
            scene.promptText = "Put your question here!";

            // Alternate between the categories buttons provided
            if (i % 2 == 0) {
                scene.answers = new ButtonModel[] {
                    new ButtonModel("Artistic answer",
                            "fieldPrompt" + (i + 1), Riasec.Artistic),
                    new ButtonModel("Realistic answer",
                            "fieldPrompt" + (i + 1), Riasec.Realistic),
                    new ButtonModel("Conventional answer",
                            "fieldPrompt" + (i + 1), Riasec.Conventional)
                };
            } else {
                scene.answers = new ButtonModel[] {
                    new ButtonModel("Enterprising answer",
                            "fieldPrompt" + (i + 1), Riasec.Enterprising),
                    new ButtonModel("Social answer",
                            "fieldPrompt" + (i + 1), Riasec.Social),
                    new ButtonModel("Investigative answer",
                            "fieldPrompt" + (i + 1), Riasec.Investigative)
                };
            }

            scenes.push(scene);

            CareerPathwaySceneModel resultScene = CareerPathwaySceneModel.create();
            resultScene.id = "fieldPrompt" + questionCount;
            resultScene.name = "Result";
            resultScene.headerTitle = "These are the career results";
            resultScene.headerBody = "Click each one to find more information";
            scenes.push(resultScene);
        }

        LoadedSurveyModel survey = LoadedSurveyModel.create();
        survey.rootSceneId = titleScene.id;
        survey.scenes = scenes.toArray(new SceneModel[0]);

        return survey;
    }

    private static SceneModel createFieldPicker(HashMap<String, FilterGroupModel> fieldFilters,
                                                List<CareerModel> careers,
                                                String category) {
        final int maxFields = 8;

        List<CareerModel> categoryCareers = careers
                .stream()
                .filter(career -> career.category.equals(category))
                .collect(Collectors.toList());

        HashSet<String> uniqueFields = new HashSet<>();
        for (CareerModel career : categoryCareers) {
            uniqueFields.add(career.field);
        }

        PathwaySceneModel scene = new PathwaySceneModel();

        scene.id = "fieldPicker" + category;
        scene.name = "Field Picker " + category;

        String[] fieldsArray = uniqueFields.toArray(new String[0]);
        int fieldCount = Math.min(uniqueFields.size(), maxFields);
        scene.buttonModels = new ButtonModel[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
            ButtonModel button = new ButtonModel();
            button.isCircle = true;
            button.text = fieldsArray[i];
            button.target = "fieldPrompt0";
            button.filter = fieldFilters.get(fieldsArray[i]);
            scene.buttonModels[i] = button;
        }
        scene.centerText = "Pick one!";
        scene.headerTitle = "Choose a field you are interested in!";
        scene.headerBody = "You can always go back and change your answer.";

        return scene;
    }
}
