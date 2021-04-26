package kiosk;

import editor.ChildIdentifiers;
import editor.Controller;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import kiosk.models.CareerDescriptionModel;
import kiosk.models.CareerModel;
import kiosk.models.ErrorSceneModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.SceneModel;
import kiosk.scenes.Scene;

public class SceneGraph {

    private final UserScore userScore;
    private UserScore previousUserScore;
    private SceneModel root;
    public final LinkedList<SceneModel> history;
    private final HashMap<String, SceneModel> sceneModels;
    private Scene currentScene;
    private LinkedList<EventListener<SceneModel>> sceneChangeCallbacks;
    private final Set<String> careerCategories = new HashSet<>();
    // K: category V: fields inside that category
    private final HashMap<String, Set<String>> careerFields = new HashMap<>();
    private final CareerModel[] allCareers;
    public String recentActivity = "NONE";

    /**
     * Creates a scene graph which holds the root scene model, and
     * history while being traversed.
     * @param survey model to load from
     */
    public SceneGraph(LoadedSurveyModel survey) {
        this.userScore = new UserScore(survey.careers);
        this.previousUserScore = new UserScore(survey.careers);
        this.history = new LinkedList<>();
        this.sceneModels = new HashMap<>();
        this.sceneChangeCallbacks = new LinkedList<>();
        this.loadSurvey(survey);

        // Store careers, categories, and fields
        this.allCareers = survey.careers;
        for (CareerModel career : survey.careers) {
            careerCategories.add(career.category);

            Set<String> fields;
            if (!careerFields.containsKey(career.category)) {
                fields = new HashSet<>();
                careerFields.put(career.category, fields);
            } else {
                fields = careerFields.get(career.category);
            }
            fields.add(career.field);
        }
    }

    /**
     * Load survey from a model. History and callbacks are cleared.
     * sceneChangeCallbacks are _not_ called (because they were
     * just cleared, dummy). Re-add callbacks and invoke SceneGraph.reset()
     * if you would like the callbacks to be invoked.
     * @param survey to load
     */
    public synchronized void loadSurvey(LoadedSurveyModel survey) {
        // Reset to a new, initial state
        this.history.clear();
        userScore.reset();
        previousUserScore.reset();
        this.sceneModels.clear();
        this.sceneChangeCallbacks.clear();

        // Register the new scene models
        for (SceneModel sceneModel : survey.scenes) {
            this.registerSceneModel(sceneModel);
        }

        setRootSceneModel(this.sceneModels.get(survey.rootSceneId));
        this.currentScene = this.root.deepCopy().createScene();
        this.history.push(this.root);
    }

    /**
     * Reconstruct the a survey model based off of the scenes
     * currently loaded in the SceneGraph.
     * @return a survey model representation of the scene graph
     */
    public synchronized LoadedSurveyModel exportSurvey() {
        String rootSceneId = this.root.getId();
        List<SceneModel> scenes = new ArrayList<>(this.sceneModels.values());
        return new LoadedSurveyModel(rootSceneId, scenes);
    }

    public synchronized void pushScene(SceneModel sceneModel) {
        this.pushScene(sceneModel, Riasec.None, null);
    }

    /**
     * Changes the current Scene. Constructs the new scene
     * from the model provided
     * @param sceneModel to create a scene from
     * @param category selected by the previous scene
     * @param nullOrFilter career filter to apply, or none
     */
    public synchronized void pushScene(SceneModel sceneModel,
                                       Riasec category,
                                       FilterGroupModel nullOrFilter) {
        // Update the user score from the category selected on the
        // previous scene
        previousUserScore.setRealistic(userScore.getCategoryScore(Riasec.Realistic));
        previousUserScore.setInvestigative(userScore.getCategoryScore(Riasec.Investigative));
        previousUserScore.setArtistic(userScore.getCategoryScore(Riasec.Artistic));
        previousUserScore.setSocial(userScore.getCategoryScore(Riasec.Social));
        previousUserScore.setEnterprising(userScore.getCategoryScore(Riasec.Enterprising));
        previousUserScore.setConventional(userScore.getCategoryScore(Riasec.Conventional));
        userScore.apply(category, nullOrFilter);

        // Add the new scene
        this.currentScene = sceneModel.deepCopy().createScene();
        this.history.push(sceneModel);
        this.onSceneChange(sceneModel);

        this.recentActivity = "PUSH";
    }

    public synchronized void pushScene(String sceneModelId) {
        this.pushScene(sceneModelId, Riasec.None, null);
    }

    /**
     * Changes the current Scene. Constructs the new scene
     * from the scene model id.
     * @param sceneModelId The id of the registered scene to push.
     * @param category selected by the previous scene
     * @param nullOrFilter career filter to apply, or none
     */
    public synchronized void pushScene(String sceneModelId,
                                       Riasec category,
                                       FilterGroupModel nullOrFilter) {
        boolean containsModel = sceneModels.containsKey(sceneModelId);

        if (containsModel) {
            SceneModel nextSceneModel = sceneModels.get(sceneModelId);
            pushScene(nextSceneModel, category, nullOrFilter);
        } else {
            pushScene(new ErrorSceneModel(
                    "Scene of the id '" + sceneModelId + "' does not exist (yet)"),
                    Riasec.None, null);
        }
        this.recentActivity = "PUSH";
    }

    /**
     * Pushes the end scene.
     */
    public void pushEndScene(CareerModel career) {
        CareerDescriptionModel description = new CareerDescriptionModel();
        description.careerModel = career;
        pushScene(description);
        this.recentActivity = "PUSH";
    }

    public synchronized boolean containsScene(String sceneId) {
        return this.sceneModels.containsKey(sceneId);
    }

    /**
     * Removes the current Scene. Creates a new scene based
     * on the last SceneModel.
     */
    public synchronized void popScene() {
        // Remove the current scene from history
        this.history.pop();

        // Undo the last operation on the user score
        previousUserScore.setRealistic(userScore.getCategoryScore(Riasec.Realistic));
        previousUserScore.setInvestigative(userScore.getCategoryScore(Riasec.Investigative));
        previousUserScore.setArtistic(userScore.getCategoryScore(Riasec.Artistic));
        previousUserScore.setSocial(userScore.getCategoryScore(Riasec.Social));
        previousUserScore.setEnterprising(userScore.getCategoryScore(Riasec.Enterprising));
        previousUserScore.setConventional(userScore.getCategoryScore(Riasec.Conventional));
        userScore.undo();

        // Set the next scene from the stack
        SceneModel next = this.history.peek();

        if (next == null) {
            ErrorSceneModel errorScene = new ErrorSceneModel("Popped too far from history");
            this.currentScene = errorScene.createScene();
            this.onSceneChange(errorScene);
        } else {
            this.currentScene = next.deepCopy().createScene();
            this.onSceneChange(next);
        }

        this.recentActivity = "POP";
    }

    /**
     * Reset the current Scene to the root and clear
     * the scene history.
     */
    public synchronized void reset() {
        // Reset the user score
        previousUserScore.reset();
        userScore.reset();

        // Reset the root scene
        this.currentScene = this.root.deepCopy().createScene();
        this.history.clear();
        this.history.push(this.root);
        this.onSceneChange(this.root);

        this.recentActivity = "RESET";
    }

    /**
     * Registers the scene model by it's ID.
     * @param sceneModel Returns the scene model at the ID, if one exists.
     */
    public synchronized void registerSceneModel(SceneModel sceneModel) {
        SceneModel currentScene = history.peekFirst();

        // If we are changing the current scene model, recreate the scene
        if (currentScene != null && sceneModel.getId().equals(currentScene.getId())) {
            this.currentScene = sceneModel.deepCopy().createScene();
            this.onSceneChange(sceneModel);
        }

        sceneModels.put(sceneModel.getId(), sceneModel);
        Controller.setHasPendingChanges(true);
    }

    /**
     * Unregister a scene model.
     * @param sceneModel to remove from the scene graph
     * @throws SceneModelException this is a check to ensure the
     *     root is never deleted; shouldn't be possible because the option
     *     is disabled in the ContextMenu
     */
    public synchronized void unregisterSceneModel(SceneModel sceneModel)
            throws SceneModelException {
        // Can't remove the root scene
        if (sceneModel != this.root) {
            SceneModel currentScene = getCurrentSceneModel();

            // If we are removing the current active scene, pop it before removing
            if (currentScene != null && sceneModel.getId().equals(currentScene.getId())) {
                popScene();
            }

            sceneModels.remove(sceneModel.getId());
        } else {
            throw new SceneModelException("Cannot delete the root scene");
        }
    }

    /**
     * Change the id of an existing model in the scene graph.
     * @param currentId the current id of the model in the graph
     * @param newId the new id to assign to it
     */
    public synchronized void reassignSceneModel(String currentId, String newId) {
        // Don't reassign if the id didn't change
        if (currentId.equals(newId)) {
            return;
        }

        SceneModel sceneModel = getSceneById(currentId);
        sceneModels.remove(currentId);
        sceneModel.setId(newId);
        sceneModels.put(newId, sceneModel);

        if (this.getCurrentSceneModel().getId().equals(sceneModel.getId())) {
            this.currentScene = sceneModel.deepCopy().createScene();
            this.onSceneChange(sceneModel);
        }
    }

    /**
     * Pass in a callback which will be called with the current scene when the scene changes.
     * @param callBack The callback to be registered
     */
    public synchronized void addSceneChangeCallback(EventListener<SceneModel> callBack) {
        sceneChangeCallbacks.add(callBack);
    }

    private synchronized void onSceneChange(SceneModel nextScene) {
        for (EventListener<SceneModel> sceneChangeCallback : sceneChangeCallbacks) {
            sceneChangeCallback.invoke(nextScene);
        }
    }

    /**
     * Get the scene most recently pushed to the state.
     * @return The current scene.
     */
    public synchronized Scene getCurrentScene() {
        return currentScene;
    }

    public synchronized SceneModel getCurrentSceneModel() {
        return this.history.peek();
    }

    /**
     * Get a scene by its id.
     * @param id of the scene model
     * @return Scene model associated with the idea or an error scene model.
     */
    public synchronized SceneModel getSceneById(String id) {
        SceneModel sceneModel = this.sceneModels.get(id);

        if (sceneModel == null) {
            return new ErrorSceneModel("Scene '" + id + "' does not exist");
        }
        return sceneModel;
    }

    public synchronized Set<String> getAllIds() {
        return this.sceneModels.keySet();
    }

    /**
     * Get the root scene's model.
     * @return The root sceneModel
     */
    public synchronized SceneModel getRootSceneModel() {
        return this.root;
    }

    /**
     * Sets the new root for the scene graph. Also takes care of naming conventions for the editor
     * @param newRoot The scene which will become the launching point for the Kiosk.
     */
    public synchronized void setRootSceneModel(SceneModel newRoot) {
        this.root = newRoot;
        // Remove root from original child
        if (root != null) {
            this.root.setName(this.root.getName()
                    .replaceAll(ChildIdentifiers.ROOT, ChildIdentifiers.CHILD));
        }
        // Set new root and give em the special star
        this.root.setName(ChildIdentifiers.ROOT + this.root.getName());
    }

    /**
     * Returns the set of the scene Ids currently in the SceneGraph.
     * @return The set of the scene Ids currently in the SceneGraph.
     */
    public synchronized Set<String> getSceneIds() {
        return sceneModels.keySet();
    }

    public synchronized Collection<SceneModel> getAllSceneModels() {
        return sceneModels.values();
    }

    public UserScore getPreviousUserScore() {
        return previousUserScore;
    }

    public UserScore getUserScore() {
        return userScore;
    }

    /**
     * Check to see if there is a scene whose name matches the current scene.
     * @param sceneName The name of the model for which we are looking.
     * @return Whether or not 2 or more scenes have the same name.
     */
    public SceneModel getSceneModelByName(String sceneName) {
        return sceneModels
            .values().stream()
            .filter(sceneModel -> sceneModel.getName().equals(sceneName))
            .findFirst().orElse(null);
    }

    /**
     * Get career categories.
     * @return a set of unique categories
     */
    public Set<String> getCareerCategories() {
        return this.careerCategories;
    }

    /**
     * Get the career fields defined in the survey.
     * @param category the fields belong to
     * @return a set of unique fields
     */
    public Set<String> getCareerFields(String category) {
        if (this.careerFields.containsKey(category)) {
            return this.careerFields.get(category);
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Get the careers that belong to category and field.
     * @param category to filter by
     * @param field to filter by
     * @return a set of unique careers
     */
    public Set<String> findCareers(String category, String field) {
        boolean allFields = field.equals("All");

        if (!careerCategories.contains(category)) {
            return new HashSet<>();
        }
        if (!allFields && !careerFields.get(category).contains(field)) {
            return new HashSet<>();
        }

        HashSet<String> careers = new HashSet<>();
        for (CareerModel career : allCareers) {
            if (career.category.equals(category) && (allFields || career.field.equals(field))) {
                careers.add(career.name);
            }
        }
        return careers;
    }
}
