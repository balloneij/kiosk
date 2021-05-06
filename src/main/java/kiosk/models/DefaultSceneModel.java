package kiosk.models;

import kiosk.scenes.DefaultScene;
import kiosk.scenes.Scene;

/**
 * Scene model for the DefaultScene that is displayed when no survey is loaded. It displays
 * instructions on how to load
 * a survey.
 */
public class DefaultSceneModel implements SceneModel {
    public String message = "Press F2 to open the file-chooser and select a survey file.\n"
            + "Afterwards, press F5 to refresh the view. \nThe program can also be started "
            + "from the command line with the command \n\"java -jar kiosk.jar <survey file>\"\n"
            + "where <survey file> is the path to the survey file.";
    public String id;
    public String name;

    /**
     * Creates a default scene.
     */
    public DefaultSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
        this.name = "Default Scene";
    }

    @Override
    public Scene createScene() {
        return new DefaultScene(this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public SceneModel deepCopy() {
        DefaultSceneModel newModel = new DefaultSceneModel();
        newModel.message = message;
        newModel.id = id;
        newModel.name = name;
        return newModel;
    }

    @Override
    public String[] getTargets() {
        return new String[0];
    }
}
