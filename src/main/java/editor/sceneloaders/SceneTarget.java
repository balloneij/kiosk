package editor.sceneloaders;

public class SceneTarget {
    private String sceneId;
    private String sceneName;


    public SceneTarget(String sceneId, String sceneName) {
        this.sceneId = sceneId;
        this.sceneName = sceneName;
    }

    // the rest of the getters and setters should never be used
    public String getSceneId() {
        return this.sceneId;
    }

    @Override
    public String toString() {
        return sceneName;
    }
}