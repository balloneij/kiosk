package kiosk;

import java.util.HashMap;
import java.util.LinkedList;
import kiosk.models.EmptySceneModel;
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
            pushScene(new EmptySceneModel());
        }
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
            var emptySceneModel = new EmptySceneModel();
            this.currentScene = emptySceneModel.createScene();
            this.onSceneChange(emptySceneModel);
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
        if (currentScene != null && sceneModel.getId().equals(currentScene.getId())) {
            this.currentScene = sceneModel.deepCopy().createScene();
        }
        sceneModels.put(sceneModel.getId(), sceneModel);
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
            sceneChangeCallback.invoke(nextScene.deepCopy());
        }
    }

    /**
     * Get the scene most recently pushed to the state.
     * @return The current scene.
     */
    public Scene getCurrentScene() {
        return currentScene;
    }
}
