import kiosk.SceneGraph;
import kiosk.models.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SceneGraphTest {
    @Test
    void rootSceneIsSetOnInit() {
        // Arrange
        LoadedSurveyModel survey = new LoadedSurveyModel();
        SceneModel scene1 = new PromptSceneModel();
        SceneModel scene2 = new PromptSceneModel();
        survey.scenes = new SceneModel[] { scene1, scene2 };
        survey.rootSceneId = scene1.getId();
        survey.careers = new CareerModel[0];

        // Act
        SceneGraph sceneGraph = new SceneGraph(survey);

        // Assert
        assertEquals(scene1.getId(), sceneGraph.getRootSceneModel().getId());
    }

    @Test
    void rootSceneIsSetManually() {
        // Arrange
        LoadedSurveyModel survey = new LoadedSurveyModel();
        SceneModel scene1 = new PromptSceneModel();
        SceneModel scene2 = new PromptSceneModel();
        survey.scenes = new SceneModel[] { scene1, scene2 };
        survey.rootSceneId = scene1.getId();
        survey.careers = new CareerModel[0];
        SceneGraph sceneGraph = new SceneGraph(survey);

        // Act
        sceneGraph.setRootSceneModel(scene2);

        // Assert
        assertEquals(scene2.getId(), sceneGraph.getRootSceneModel().getId());
    }
}
