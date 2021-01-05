package kiosk;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import kiosk.models.ErrorSceneModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.SceneModel;
import kiosk.scenes.Scene;

public class SceneGraph {

    private final SceneModel root;
    private final LinkedList<SceneModel> history;
    private final HashMap<String, SceneModel> sceneModels;
    private Scene currentScene;
    private final LinkedList<EventListener<SceneModel>> sceneChangeCallbacks;

    /**
     * Creates a scene graph which holds the root scene model, and
     * history while being traversed.
     * @param root of the scene graph
     */
    public SceneGraph(LoadedSurveyModel root) {
        this.history = new LinkedList<>();
        this.sceneModels = new HashMap<>();
        this.root = root.scenes[0];
        this.currentScene = this.root.deepCopy().createScene();
        this.history.push(this.root);
        this.sceneChangeCallbacks = new LinkedList<>();
        for (SceneModel sceneModel : root.scenes) {
            this.registerSceneModel(sceneModel);
        }
    }

    /**
     * Changes the current Scene. Constructs the new scene
     * from the model provided
     * @param sceneModel to create a scene from
     */
    public void pushScene(SceneModel sceneModel) {
        this.currentScene = sceneModel.deepCopy().createScene();
        this.history.push(sceneModel);
        this.onSceneChange(sceneModel);
    }

    /**
     * Changes the current Scene. Constructs the new scene
     * from the scene model id.
     * @param sceneModelId The id of the registered scene to push.
     */
    public void pushScene(String sceneModelId) {
        var containsModel = sceneModels.containsKey(sceneModelId);

        if (containsModel) {
            var nextSceneModel = sceneModels.get(sceneModelId);
            pushScene(nextSceneModel);
        } else {
            pushScene(new ErrorSceneModel(
                    "Scene of the id '" + sceneModelId + "' does not exist (yet)"));
        }
    }

    public boolean containsScene(String sceneId) {
        return this.sceneModels.containsKey(sceneId);
    }

    /**
     * Removes the current Scene. Creates a new scene based
     * on the last SceneModel.
     */
    public void popScene() {
        // Remove the current scene from history
        this.history.pop();

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
    public void reset() {
        this.currentScene = this.root.deepCopy().createScene();
        this.history.clear();
        this.history.push(this.root);
        this.onSceneChange(this.root);
    }

    /**
     * Registers the scene model by it's ID.
     * @param sceneModel Returns the scene model at the ID, if one exists.
     */
    public void registerSceneModel(SceneModel sceneModel) {
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
     */
    public void unregisterSceneModel(SceneModel sceneModel) {
        SceneModel currentScene = getCurrentSceneModel();

        // If we are removing the current active scene, pop it before removing
        if (currentScene != null && sceneModel.getId().equals(currentScene.getId())) {
            popScene();
        }

        sceneModels.remove(sceneModel.getId());
    }

    /**
     * Change the id of an existing model in the scene graph.
     * @param currentId the current id of the model in the graph
     * @param newId the new id to assign to it
     */
    public void reassignSceneModel(String currentId, String newId) {
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
    public void addSceneChangeCallback(EventListener<SceneModel> callBack) {
        sceneChangeCallbacks.add(callBack);
    }

    private void onSceneChange(SceneModel nextScene) {
        for (EventListener<SceneModel> sceneChangeCallback : sceneChangeCallbacks) {
            sceneChangeCallback.invoke(nextScene);
        }
    }

    /**
     * Get the scene most recently pushed to the state.
     * @return The current scene.
     */
    public Scene getCurrentScene() {
        return currentScene;
    }

    public SceneModel getCurrentSceneModel() {
        return this.history.peek();
    }

    /**
     * Get a scene by its id.
     * @param id of the scene model
     * @return Scene model associated with the idea or an error scene model.
     */
    public SceneModel getSceneById(String id) {
        SceneModel sceneModel = this.sceneModels.get(id);

        if (sceneModel == null) {
            return new ErrorSceneModel("Scene '" + id + "' does not exist");
        }
        return sceneModel;
    }

    public Set<String> getAllIds() {
        return this.sceneModels.keySet();
    }

    /**
     * Get the root scene's model.
     * @return The root sceneModel
     */
    public SceneModel getRootSceneModel() {
        return this.root;
    }

    /**
     * Returns the set of the scene Ids currently in the SceneGraph.
     * @return The set of the scene Ids currently in the SceneGraph.
     */
    public Set<String> getSceneIds() {
        return sceneModels.keySet();
    }

    public Collection<SceneModel> getAllSceneModels() {
        return sceneModels.values();
    }
}