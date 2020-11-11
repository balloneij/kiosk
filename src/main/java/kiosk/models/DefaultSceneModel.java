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
            + "from the command line with the command \n\"java -jar kiosk.jar <survey file>\"\nwhere "
            + "<survey file> is the path to the survey file.";
    public String id;

    public DefaultSceneModel() {
        this.id = IdGenerator.getInstance().getNextId();
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
    public SceneModel deepCopy() {
        return new DefaultSceneModel();
    }
}
