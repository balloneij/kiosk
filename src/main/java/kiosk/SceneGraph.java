package kiosk;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import editor.SceneModelException;
import kiosk.models.ErrorSceneModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.SceneModel;
import kiosk.scenes.Scene;

public class SceneGraph {

    private final UserScore userScore = new UserScore();
    private SceneModel root;
    private final LinkedList<SceneModel> history;
    private final HashMap<String, SceneModel> sceneModels;
    private Scene currentScene;
    private LinkedList<EventListener<SceneModel>> sceneChangeCallbacks;

    /**
     * Creates a scene graph which holds the root scene model, and
     * history while being traversed.
     * @param survey model to load from
     */
    public SceneGraph(LoadedSurveyModel survey) {
        this.history = new LinkedList<>();
        this.sceneModels = new HashMap<>();
        this.sceneChangeCallbacks = new LinkedList<>();
        this.loadSurvey(survey);
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
        this.userScore.reset();
        this.sceneModels.clear();
        this.sceneChangeCallbacks.clear();

        // Register the new scene models
        for (SceneModel sceneModel : survey.scenes) {
            this.registerSceneModel(sceneModel);
        }

        this.root = this.sceneModels.get(survey.rootSceneId);
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
        this.pushScene(sceneModel, Riasec.None);
    }

    /**
     * Changes the current Scene. Constructs the new scene
     * from the model provided
     * @param sceneModel to create a scene from
     * @param category selected by the previous scene
     */
    public synchronized void pushScene(SceneModel sceneModel, Riasec category) {
        // Update the user score from the category selected on the
        // previous scene
        this.userScore.add(category);

        // Add the new scene
        this.currentScene = sceneModel.deepCopy().createScene();
        this.history.push(sceneModel);
        this.onSceneChange(sceneModel);
    }

    public synchronized void pushScene(String sceneModelId) {
        this.pushScene(sceneModelId, Riasec.None);
    }

    /**
     * Changes the current Scene. Constructs the new scene
     * from the scene model id.
     * @param sceneModelId The id of the registered scene to push.
     * @param category selected by the previous scene
     */
    public synchronized void pushScene(String sceneModelId, Riasec category) {
        var containsModel = sceneModels.containsKey(sceneModelId);

        if (containsModel) {
            var nextSceneModel = sceneModels.get(sceneModelId);
            pushScene(nextSceneModel, category);
        } else {
            pushScene(new ErrorSceneModel(
                    "Scene of the id '" + sceneModelId + "' does not exist (yet)"),
                    Riasec.None);
        }
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
        this.userScore.undo();

        // Set the next scene from the stack
        SceneModel next = this.history.peek();

        if (next == null) {
            var errorScene = new ErrorSceneModel("Popped too far from history");
            this.currentScene = errorScene.createScene();
            this.onSceneChange(errorScene);
        } else {
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
        this.userScore.reset();

        // Reset the root scene
        this.currentScene = this.root.deepCopy().createScene();
        this.history.clear();
        this.history.push(this.root);
        this.onSceneChange(this.root);
    }

    /**
     * Registers the scene model by it's ID.
     * @param sceneModel Returns the scene model at the ID, if one exists.
     */
    public synchronized void registerSceneModel(SceneModel sceneModel) {
        var currentScene = history.peekFirst();

        // If we are changing the current scene model, recreate the scene
        if (currentScene != null && sceneModel.getId().equals(currentScene.getId())) {
            this.currentScene = sceneModel.deepCopy().createScene();
            this.onSceneChange(sceneModel);
        }

        sceneModels.put(sceneModel.getId(), sceneModel);
    }

    /**
     * Unregister a scene model.
     * @param sceneModel to remove from the scene graph
     * @throws SceneModelException this is a check to ensure the
     * root is never deleted; shouldn't be possible because the option
     * is disabled in the ContextMenu
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

    public synchronized void setRootSceneModel(SceneModel newRoot) {
        this.root = newRoot;
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

    public UserScore getUserScore() {
        return this.userScore;
    }
}
