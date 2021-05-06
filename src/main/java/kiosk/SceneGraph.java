package kiosk;

import editor.ChildIdentifiers;
import editor.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import kiosk.models.*;
import kiosk.scenes.ErrorScene;
import kiosk.scenes.Scene;

public class SceneGraph {

    private UserScore userScore;
    private UserScore previousUserScore;
    private SceneModel root;
    private String rootId;
    private final LinkedList<SceneModel> history;
    private final HashMap<String, SceneModel> sceneModels;
    private Scene currentScene;
    private Scene previousScene;
    private LinkedList<EventListener<SceneModel>> sceneChangeCallbacks;
    private final Set<String> careerCategories = new HashSet<>();
    // K: category V: fields inside that category
    private final HashMap<String, Set<String>> careerFields = new HashMap<>();
    private CareerModel[] allCareers;

    public enum RecentActivity {
            RESET, POP, PUSH
    }

    public RecentActivity recentActivity = RecentActivity.RESET;

    public enum RecentScene {
        CAREER_DESCRIPTION, CAREER_PATHWAY,
        CREDITS, ERROR, DETAILS, PATHWAY,
        PROMPT, SPOKE_GRAPH_PROMPT, TIMEOUT
    }

    public RecentScene recentScene = RecentScene.PROMPT;

    /**
     * Creates a scene graph which holds the root scene model, and
     * history while being traversed.
     * @param survey model to load from
     */
    public SceneGraph(LoadedSurveyModel survey, CareerModelLoader careerModelLoader) {
        this.history = new LinkedList<>();
        this.sceneModels = new HashMap<>();
        this.sceneChangeCallbacks = new LinkedList<>();
        this.loadSurvey(survey, careerModelLoader);
    }

    /**
     * Load survey from a model. History and callbacks are cleared.
     * sceneChangeCallbacks are _not_ called (because they were
     * just cleared, dummy). Re-add callbacks and invoke SceneGraph.reset()
     * if you would like the callbacks to be invoked.
     * @param survey to load
     */
    public synchronized void loadSurvey(LoadedSurveyModel survey,
                                        CareerModelLoader careerModelLoader) {
        // Reset to a new, initial state
        this.history.clear();
        this.sceneModels.clear();
        this.sceneChangeCallbacks.clear();

        // Register the new scene models
        for (SceneModel sceneModel : survey.scenes) {
            this.registerSceneModel(sceneModel);
        }

        // Reset all careers
        careerCategories.clear();
        careerFields.clear();
        this.allCareers = careerModelLoader.load();
        for (CareerModel career : this.allCareers) {
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

        // Create a new user score
        this.userScore = new UserScore(allCareers);
        this.previousUserScore = new UserScore(allCareers);

        // Set the root and load it as the first scene
        setRootSceneModel(this.sceneModels.get(survey.rootSceneId));
        this.previousScene = this.currentScene;
        SceneModel root = this.getRootSceneModel();
        this.currentScene = root.deepCopy().createScene();
        this.history.push(root);
    }

    /**
     * Reconstruct the a survey model based off of the scenes
     * currently loaded in the SceneGraph.
     * @return a survey model representation of the scene graph
     */
    public synchronized LoadedSurveyModel exportSurvey() {
        List<SceneModel> scenes = new ArrayList<>(this.sceneModels.values());
        return new LoadedSurveyModel(rootId, scenes);
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
        String lastScene = this.history.peek().toString();
        if (lastScene.contains("Career Description")) {
            this.recentScene = RecentScene.CAREER_DESCRIPTION;
        } else if (lastScene.contains("Career Pathway")) {
            this.recentScene = RecentScene.CAREER_PATHWAY;
        } else if (lastScene.contains("Credits")) {
            this.recentScene = RecentScene.CREDITS;
        } else if (lastScene.contains("Details")) {
            this.recentScene = RecentScene.DETAILS;
        } else if (lastScene.contains("Pathway")) {
            this.recentScene = RecentScene.PATHWAY;
        } else if (lastScene.contains("Spoke Graph Prompt")) {
            this.recentScene = RecentScene.SPOKE_GRAPH_PROMPT;
        } else if (lastScene.contains("Prompt")) {
            this.recentScene = RecentScene.PROMPT;
        }
        this.recentActivity = RecentActivity.PUSH;

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
        this.previousScene = this.currentScene;
        this.currentScene = sceneModel.deepCopy().createScene();
        this.history.push(sceneModel);
        this.onSceneChange(sceneModel);
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
        // this is handled whether the scene exists or not
        SceneModel nextSceneModel = getSceneById(sceneModelId);

        if (!containsScene(sceneModelId)) {
            category = Riasec.None;
            nullOrFilter = null;
        }

        pushScene(nextSceneModel, category, nullOrFilter);
    }

    /**
     * Pushes the end scene.
     */
    public void pushEndScene(CareerModel career) {
        CareerDescriptionModel description = new CareerDescriptionModel();
        description.careerModel = career;
        pushScene(description);
    }

    public synchronized boolean containsScene(String sceneId) {
        return this.sceneModels.containsKey(sceneId);
    }

    /**
     * Removes the current Scene. Creates a new scene based
     * on the last SceneModel.
     */
    public synchronized void popScene() {

        String lastScene = this.history.peek().toString();
        if (lastScene.contains("Career Description")) {
            this.recentScene = RecentScene.CAREER_DESCRIPTION;
        } else if (lastScene.contains("Career Pathway")) {
            this.recentScene = RecentScene.CAREER_PATHWAY;
        } else if (lastScene.contains("Credits")) {
            this.recentScene = RecentScene.CREDITS;
        } else if (lastScene.contains("Details")) {
            this.recentScene = RecentScene.DETAILS;
        } else if (lastScene.contains("Pathway")) {
            this.recentScene = RecentScene.PATHWAY;
        } else if (lastScene.contains("Spoke Graph Prompt")) {
            this.recentScene = RecentScene.SPOKE_GRAPH_PROMPT;
        } else if (lastScene.contains("Prompt")) {
            this.recentScene = RecentScene.PROMPT;
        }
        this.recentActivity = RecentActivity.POP;


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
            this.previousScene = this.currentScene;
            this.currentScene = errorScene.createScene();
            this.onSceneChange(errorScene);
        } else {
            this.previousScene = this.currentScene;
            this.currentScene = next.deepCopy().createScene();
            this.onSceneChange(next);
        }
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
        SceneModel root = getRootSceneModel();
        this.previousScene = this.currentScene;
        this.currentScene = root.deepCopy().createScene();
        this.history.clear();
        this.history.push(root);
        this.onSceneChange(root);
        this.recentActivity = RecentActivity.RESET;
    }

    /**
     * Registers the scene model by it's ID.
     * @param sceneModel Returns the scene model at the ID, if one exists.
     */
    public synchronized void registerSceneModel(SceneModel sceneModel) {
        SceneModel currentScene = history.peekFirst();

        // If we are changing the current scene model, recreate the scene
        // AND get the old scene out of history; it no longer exists and cannot be retrieved
        if (currentScene != null && sceneModel.getId().equals(currentScene.getId())) {
            this.previousScene = this.currentScene;
            this.currentScene = sceneModel.deepCopy().createScene();
            this.history.pop();
            this.history.push(sceneModel);
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
        if (!sceneModel.getId().equals(rootId)) {
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
            this.previousScene = this.currentScene;
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

    /**
     * Get the previous scene that was somewhat-recently pushed to the state.
     * @return The previous scene.
     */
    public synchronized Scene getPreviousScene() {
        return previousScene;
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
            sceneModel = new ErrorSceneModel("You might have deleted a scene that a button led to."
                    + " Because we can't have a button lead nowhere, this scene can't be deleted.");
            sceneModel.setId(id);
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
        return sceneModels.get(this.rootId);
    }

    /**
     * Sets the new root for the scene graph. Also takes care of naming conventions for the editor
     * @param newRoot The scene which will become the launching point for the Kiosk.
     */
    public synchronized void setRootSceneModel(SceneModel newRoot) {
        SceneModel previousRoot = this.getRootSceneModel();
        if (previousRoot != null) {
            previousRoot.setName(previousRoot.getName()
                    .replaceAll(ChildIdentifiers.ROOT, ChildIdentifiers.CHILD));
        }

        this.rootId = newRoot.getId();
        if (!newRoot.getName().startsWith(ChildIdentifiers.ROOT)) {
            newRoot.setName(ChildIdentifiers.ROOT + newRoot.getName());
        }
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

    /**
     * Gets a particular item from the history.
     * @param index the index to check
     * @return the scenemodel of that item in the history
     */
    public SceneModel getFromHistory(int index) {
        return history.get(index);
    }

    /**
     * Gets the history's size, used when checking root MSOE button placements.
     * @return the history's size
     */
    public int getHistorySize() {
        return history.size();
    }
}
