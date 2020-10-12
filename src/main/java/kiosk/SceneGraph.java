package kiosk;

import java.util.LinkedList;
import kiosk.models.SceneModel;
import kiosk.scenes.EmptyScene;
import kiosk.scenes.Scene;

public class SceneGraph {

    private final SceneModel root;
    private final LinkedList<SceneModel> history;
    private Scene currentScene;

    /**
     * Creates a scene graph which holds the root scene model, and
     * history while being traversed.
     * @param root of the scene graph
     */
    public SceneGraph(SceneModel root) {
        this.root = root;
        this.currentScene = this.root.createScene();
        this.history = new LinkedList<>();

        this.history.push(this.root);
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

    public Scene getCurrentScene() {
        return currentScene;
    }
}
