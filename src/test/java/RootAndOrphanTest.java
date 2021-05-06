import editor.ChildIdentifiers;
import editor.Controller;
import editor.Editor;
import javafx.scene.control.TreeItem;
import kiosk.SceneGraph;
import kiosk.Settings;
import kiosk.models.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RootAndOrphanTest {

    static Settings oldSettings;

    @BeforeAll
    static void initialize() {
        oldSettings = Settings.readSettings();
        Editor.applySettings(new Settings());
    }

    @AfterAll
    static void restoreSettings() {
        oldSettings.writeSettings();
    }

    Controller createController(SceneModel ...scenes) {
        LoadedSurveyModel surveyModel = new LoadedSurveyModel(Arrays.asList(scenes));
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        Controller.sceneGraph = new SceneGraph(surveyModel, careerModelLoader);
        return new Controller();
    }

    private TreeItem<SceneModel> getRoot(SceneModel ...sceneModels) {
        return createController(sceneModels).buildSceneGraphTreeView();
    }

    private List<TreeItem<SceneModel>> assertChildCountAndGet(int expected, TreeItem<SceneModel> item) {
        assertEquals(expected, item.getChildren().size());
        return item.getChildren();
    }

    private void assertStartsWithSymbol(TreeItem<SceneModel> item, String identifier) {
        assertTrue(item.getValue().getName().startsWith(identifier));
    }

    @Test
    void singleItemIsRoot() {
        TreeItem<SceneModel> hiddenRoot = getRoot(new PromptSceneModel());
        List<TreeItem<SceneModel>> rootSceneModel = assertChildCountAndGet(1, hiddenRoot);
        assertStartsWithSymbol(rootSceneModel.get(0), ChildIdentifiers.ROOT);
    }

    @Test
    void savedRootHasOneSymbol() {
        SceneModel root = new PromptSceneModel();
        root.setName(ChildIdentifiers.ROOT + "Rest Of Name");
        TreeItem<SceneModel> hiddenRoot = getRoot(root);
        List<TreeItem<SceneModel>> rootSceneModel = assertChildCountAndGet(1, hiddenRoot);
        assertStartsWithSymbol(rootSceneModel.get(0), ChildIdentifiers.ROOT);
        assertTrue(rootSceneModel.get(0).getValue().getName().charAt(1) != ChildIdentifiers.ROOT.charAt(0));
    }
}
