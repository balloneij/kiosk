import kiosk.SceneGraph;
import kiosk.models.*;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SceneGraphTest {
    @Test
    void rootSceneIsSetOnInit() {
        // Arrange
        LoadedSurveyModel survey = new LoadedSurveyModel();
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        SceneModel scene1 = new PromptSceneModel();
        SceneModel scene2 = new PromptSceneModel();
        survey.scenes = new SceneModel[] { scene1, scene2 };
        survey.rootSceneId = scene1.getId();

        // Act
        SceneGraph sceneGraph = new SceneGraph(survey, careerModelLoader);

        // Assert
        assertEquals(scene1.getId(), sceneGraph.getRootSceneModel().getId());
    }

    @Test
    void rootSceneIsSetManually() {
        // Arrange
        LoadedSurveyModel survey = new LoadedSurveyModel();
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        SceneModel scene1 = new PromptSceneModel();
        SceneModel scene2 = new PromptSceneModel();
        survey.scenes = new SceneModel[] { scene1, scene2 };
        survey.rootSceneId = scene1.getId();
        SceneGraph sceneGraph = new SceneGraph(survey, careerModelLoader);

        // Act
        sceneGraph.setRootSceneModel(scene2);

        // Assert
        assertEquals(scene2.getId(), sceneGraph.getRootSceneModel().getId());
    }
}
