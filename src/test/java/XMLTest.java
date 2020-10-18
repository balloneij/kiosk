import java.io.File;
import kiosk.models.ButtonModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.SceneModel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class XMLTest {

    @Test
    void readAndWrite() {
        var buttons = new ButtonModel[]{
            new ButtonModel("Go to Prompt0", "Prompt0"),
            new ButtonModel("Go to Prompt1", "Prompt1")
        };
        var prompt0 = new PromptSceneModel("Prompt 0", buttons, false, "Prompt0");
        var prompt1 = new PromptSceneModel("Prompt 1", buttons, false, "Prompt1");

        var scenes = new SceneModel[]{ prompt0, prompt1 };
        var survey = new LoadedSurveyModel();
        survey.scenes = scenes;

        File file = new File("XMLTest-read-and-write.test.xml");

        survey.writeToFile(file);
        var loadedSurvey = LoadedSurveyModel.readFromFile(file);

        assertEquals(survey.scenes.length, loadedSurvey.scenes.length);

        for (int i = 0; i < survey.scenes.length; i++) {
            assertEquals(survey.scenes[i].getId(), loadedSurvey.scenes[i].getId());
            assertEquals(survey.scenes[i].getClass(), loadedSurvey.scenes[i].getClass());

            var expectedPrompt = (PromptSceneModel) survey.scenes[i];
            var prompt = (PromptSceneModel) loadedSurvey.scenes[i];

            for (int a = 0; a < expectedPrompt.answers.length; a++) {
                assertEquals(expectedPrompt.answers[a].target, prompt.answers[a].target);
                assertEquals(expectedPrompt.answers[a].text, prompt.answers[a].text);
            }
        }
    }
}
