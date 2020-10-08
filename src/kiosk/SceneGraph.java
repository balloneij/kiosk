package kiosk;

import java.util.HashMap;
import java.util.LinkedList;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.SceneModel;
import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;

public class SceneGraph {

    private final SceneModel root;
    private final LinkedList<SceneModel> history;
    private final HashMap<String, SceneModel> sceneModels;
    private Scene currentScene;

    /**
     * Creates a scene graph which holds the root scene model, and
     * history while being traversed.
     * @param root of the scene graph
     */
    public SceneGraph(LoadedSurveyModel root) {
        this.history = new LinkedList<>();
        this.sceneModels = new HashMap<>();
        this.root = root.scenes.get(0);
        this.currentScene = this.root.createScene();
        this.history.push(this.root);
        root.scenes.forEach(this::registerSceneModel);
    }

    /**
     * Changes the current Scene. Constructs the new scene
     * from the model provided
     * @param sceneModel to create a scene from
     */
    public void pushScene(SceneModel sceneModel) {
        this.currentScene = sceneModel.createScene();
        this.history.push(sceneModel);
    }

    /**
     * Changes the current Scene. Constructs the new scene
     * from the scene model id.
     * @param sceneModelId The id of the registered scene to push.
     */
    public void pushScene(String sceneModelId) {
        var containsModel = sceneModels.containsKey(sceneModelId);

        if (containsModel) {
            pushScene(sceneModels.get(sceneModelId));
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
            this.currentScene = new EmptyScene();
        } else {
            this.currentScene = next.createScene();
        }
    }

    /**
     * Reset the current Scene to the root and clear
     * the scene history.
     */
    public void reset() {
        this.currentScene = this.root.createScene();
        this.history.push(this.root);
    }

    /**
     * Registers the scene model by it's ID.
     * @param sceneModel Returns the scene model at the ID, if one exists.
     */
    public void registerSceneModel(SceneModel sceneModel) {
        if (sceneModel.getId().equals(history.peekFirst().getId())) {
            this.currentScene = sceneModel.createScene();
        }
        sceneModels.put(sceneModel.getId(), sceneModel);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
