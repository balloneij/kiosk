package kiosk.models;

import java.util.Collections;
import java.util.List;

public class LoadedSurveyModel {

    public final List<SceneModel> scenes;

    public LoadedSurveyModel(List<SceneModel> initialScenes) {
        scenes = Collections.unmodifiableList(initialScenes);
    }

}
