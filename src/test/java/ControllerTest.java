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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ControllerTest {

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

    @Test
    /**
     * A -> B -> C -> A
     */
    void simpleTree() {
        // Setup
        List<SceneModel> sceneModels = new ArrayList<>();
        PromptSceneModel a = new PromptSceneModel();
        PromptSceneModel b = new PromptSceneModel();
        PromptSceneModel c = new PromptSceneModel();

        a.answers = new ButtonModel[] { new ButtonModel("To B", b.getId()) };
        b.answers = new ButtonModel[] { new ButtonModel("To C", c.getId()) };
        c.answers = new ButtonModel[] { new ButtonModel("To Root", a.getId()) };

        sceneModels.add(a);
        sceneModels.add(b);
        sceneModels.add(c);

        LoadedSurveyModel loadedSurveyModel = new LoadedSurveyModel(sceneModels);
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        Controller.sceneGraph = new SceneGraph(loadedSurveyModel, careerModelLoader);
        Controller controller = new Controller();

        // Execution
        TreeItem<SceneModel> hiddenRoot = controller.buildSceneGraphTreeView();

        // Assertion
        assertEquals(hiddenRoot.getChildren().size(), 1); // No orphans

        // Ensure that the first item is the root item
        TreeItem<SceneModel> firstChild = hiddenRoot.getChildren().get(0);
        assertEquals(firstChild.getValue().getId(), a.getId());

        // Ensure that the second child is b
        TreeItem<SceneModel> secondChild = firstChild.getChildren().get(0);
        assertEquals(secondChild.getValue().getId(), b.getId());

        // Ensure that the third child is c
        TreeItem<SceneModel> thirdChild = secondChild.getChildren().get(0);
        assertEquals(thirdChild.getValue().getId(), c.getId());

        // Ensure that the fourth child is back to the root
        TreeItem<SceneModel> fourthChild = thirdChild.getChildren().get(0);
        assertEquals(fourthChild.getValue().getId(), a.getId());

        // Ensure that the root, as a loop doesn't have any children
        assertEquals(fourthChild.getChildren().size(), 0);
    }

    @Test
    /**
     * The Tree: A -> {B,C}, B->C->D->E->C, C->D->E->C
     * Expect: B to have a full branch of children {C, E, D}
     * Expect: To detect a loop from E to C. So E has a C, but it terminates after C
     * Expect: Both branches B and C to have the expected behavior as Expectation 2
     */
    void subTreeLoop() {
        // Setup
        List<SceneModel> sceneModels = new ArrayList<>();
        PromptSceneModel a = new PromptSceneModel();
        PromptSceneModel b = new PromptSceneModel();
        PromptSceneModel c = new PromptSceneModel();
        PromptSceneModel d = new PromptSceneModel();
        PromptSceneModel e = new PromptSceneModel();

        a.answers = new ButtonModel[] { new ButtonModel("To B", b.getId()),
                                        new ButtonModel("To C", c.getId()) };
        b.answers = new ButtonModel[] { new ButtonModel("To C", c.getId()) };
        c.answers = new ButtonModel[] { new ButtonModel("To D", d.getId()) };
        d.answers = new ButtonModel[] { new ButtonModel("To E", e.getId()) };
        e.answers = new ButtonModel[] { new ButtonModel("To C", c.getId()) };
        sceneModels.add(a);
        sceneModels.add(b);
        sceneModels.add(c);
        sceneModels.add(d);
        sceneModels.add(e);

        LoadedSurveyModel loadedSurveyModel = new LoadedSurveyModel(sceneModels);
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        SceneGraph sceneGraph = new SceneGraph(loadedSurveyModel, careerModelLoader);
        Controller.sceneGraph = sceneGraph;
        Controller controller = new Controller();

        // Execution
        TreeItem<SceneModel> hiddenRoot = controller.buildSceneGraphTreeView();

        // Assertion
        assertEquals(1, hiddenRoot.getChildren().size());
        TreeItem<SceneModel> aTI = hiddenRoot.getChildren().get(0);

        assertEquals(2, aTI.getChildren().size());
        TreeItem<SceneModel> bTI = aTI.getChildren().get(0);
        assertEquals(1, bTI.getChildren().size());
        assertEquals(b.getId(), bTI.getValue().getId());

        // Check B all the way through
        TreeItem<SceneModel> cTI = bTI.getChildren().get(0);
        assertEquals(1, cTI.getChildren().size());
        assertEquals(c.getId(), cTI.getValue().getId());

        TreeItem<SceneModel> dTI = cTI.getChildren().get(0);
        assertEquals(1, dTI.getChildren().size());
        assertEquals(d.getId(), dTI.getValue().getId());

        TreeItem<SceneModel> eTI = dTI.getChildren().get(0);
        assertEquals(1, eTI.getChildren().size());
        assertEquals(e.getId(), eTI.getValue().getId());

        cTI = eTI.getChildren().get(0);
        assertEquals(0, cTI.getChildren().size());
        assertEquals(c.getId(), cTI.getValue().getId());

    }

    @Test
    /**
     * root->B->C->root, orphan
     */
    void simpleTreeWithOrphan() {
        // Setup
        List<SceneModel> sceneModels = new ArrayList<>();
        PromptSceneModel rootPSModel = new PromptSceneModel();
        PromptSceneModel b = new PromptSceneModel();
        PromptSceneModel c = new PromptSceneModel();
        PromptSceneModel orphan = new PromptSceneModel();

        rootPSModel.answers = new ButtonModel[] { new ButtonModel("To B", b.getId()) };
        b.answers = new ButtonModel[] { new ButtonModel("To C", c.getId()) };
        c.answers = new ButtonModel[] { new ButtonModel("To Root", rootPSModel.getId()) };

        sceneModels.add(rootPSModel);
        sceneModels.add(b);
        sceneModels.add(c);
        sceneModels.add(orphan);

        LoadedSurveyModel loadedSurveyModel = new LoadedSurveyModel(sceneModels);
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        SceneGraph sceneGraph = new SceneGraph(loadedSurveyModel, careerModelLoader);
        Controller.sceneGraph = sceneGraph;
        Controller controller = new Controller();

        // Execution
        TreeItem<SceneModel> hiddenRoot = controller.buildSceneGraphTreeView();

        // Assertion
        assertEquals(hiddenRoot.getChildren().size(), 2); // One orphans

        // Ensure that the first item is the root item
        TreeItem<SceneModel> firstChild = hiddenRoot.getChildren().get(0);
        assertEquals(firstChild.getValue().getId(), rootPSModel.getId());

        // Ensure that the second child is b
        TreeItem<SceneModel> secondChild = firstChild.getChildren().get(0);
        assertEquals(secondChild.getValue().getId(), b.getId());

        // Ensure that the third child is c
        TreeItem<SceneModel> thirdChild = secondChild.getChildren().get(0);
        assertEquals(thirdChild.getValue().getId(), c.getId());

        // Ensure that the fourth child is back to the root
        TreeItem<SceneModel> fourthChild = thirdChild.getChildren().get(0);
        assertEquals(fourthChild.getValue().getId(), rootPSModel.getId());

        // Ensure that the root, as a loop doesn't have any children
        assertEquals(fourthChild.getChildren().size(), 0);

        // Ensure that there is an orphan
        TreeItem<SceneModel> orphanChild = hiddenRoot.getChildren().get(1);
        assertEquals(orphanChild.getValue().getId(), orphan.getId());
        // Make sure the orphan has no children
        assertEquals(orphanChild.getChildren().size(), 0);
    }

    @Test
    /*
     * A -> B, C -> {D, E}
     */
    void orphanWithChildren() {
        // Setup
        List<SceneModel> sceneModels = new ArrayList<>();
        PromptSceneModel a = new PromptSceneModel();
        PromptSceneModel b = new PromptSceneModel();
        PromptSceneModel d = new PromptSceneModel();
        PromptSceneModel e = new PromptSceneModel();
        PromptSceneModel c = new PromptSceneModel();
        // Instantiate in this order so that the elements don't come out nicely to form the orphan subtree

        a.answers = new ButtonModel[] { new ButtonModel("To B", b.getId()) };
        c.answers = new ButtonModel[] { new ButtonModel("To D", d.getId()),
                                        new ButtonModel("To E", e.getId()) };
        sceneModels.add(a);
        sceneModels.add(e);
        sceneModels.add(d);
        sceneModels.add(c);
        sceneModels.add(b);

        LoadedSurveyModel loadedSurveyModel = new LoadedSurveyModel(sceneModels);
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        SceneGraph sceneGraph = new SceneGraph(loadedSurveyModel, careerModelLoader);
        Controller.sceneGraph = sceneGraph;
        Controller controller = new Controller();

        // Execution
        TreeItem<SceneModel> hiddenRoot = controller.buildSceneGraphTreeView();

        // Assertion
        assertEquals(2, hiddenRoot.getChildren().size());
        TreeItem<SceneModel> aTI = hiddenRoot.getChildren().get(0);

        assertEquals(1, aTI.getChildren().size());
        assertEquals(a.getId(), aTI.getValue().getId());
        assertEquals(b.getId(), aTI.getChildren().get(0).getValue().getId());

        TreeItem<SceneModel> cTI = hiddenRoot.getChildren().get(1);
        assertEquals(c.getId(), cTI.getValue().getId());
        assertEquals(2, cTI.getChildren().size());

        TreeItem<SceneModel> dTI = cTI.getChildren().get(0);
        assertEquals(d.getId(), dTI.getValue().getId());
        assertEquals(0, dTI.getChildren().size());

        TreeItem<SceneModel> eTI = cTI.getChildren().get(1);
        assertEquals(e.getId(), eTI.getValue().getId());
        assertEquals(0, eTI.getChildren().size());
    }

    @Test
    /*
     * A->{B, A}
     */
    void selfReferentialLoop() {
        // Setup
        List<SceneModel> sceneModels = new ArrayList<>();
        PromptSceneModel a = new PromptSceneModel();
        PromptSceneModel b = new PromptSceneModel();
        a.answers = new ButtonModel[] { new ButtonModel("To b", b.getId()), new ButtonModel("To a", a.getId())};

        sceneModels.add(a);
        sceneModels.add(b);

        LoadedSurveyModel loadedSurveyModel = new LoadedSurveyModel(sceneModels);
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        Controller.sceneGraph = new SceneGraph(loadedSurveyModel, careerModelLoader);
        Controller controller = new Controller();

        // Execution
        TreeItem<SceneModel> hiddenRoot = controller.buildSceneGraphTreeView();

        // Assertion
        assertEquals(1, hiddenRoot.getChildren().size());

        TreeItem<SceneModel> aTI = hiddenRoot.getChildren().get(0);
        assertEquals(2, aTI.getChildren().size());
        assertEquals(a.getId(), aTI.getValue().getId());

        assertEquals(b.getId(), aTI.getChildren().get(0).getValue().getId());
        assertEquals(a.getId(), aTI.getChildren().get(1).getValue().getId());
    }

    @Test
    void addNewScene() {
        // Setup
        List<SceneModel> sceneModels = new ArrayList<>();
        PromptSceneModel a = new PromptSceneModel();
        PromptSceneModel b = new PromptSceneModel();
        a.answers = new ButtonModel[] { new ButtonModel("To b", b.getId())};

        sceneModels.add(a);
        sceneModels.add(b);

        LoadedSurveyModel loadedSurveyModel = new LoadedSurveyModel(sceneModels);
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        Controller.sceneGraph = new SceneGraph(loadedSurveyModel, careerModelLoader);
        Controller controller = new Controller();

        // Execution
        TreeItem<SceneModel> hiddenRoot = controller.buildSceneGraphTreeView();
        SceneModel c = new PromptSceneModel();
        controller.addNewScene(hiddenRoot, c);

        // Assertion
        assertEquals(2, hiddenRoot.getChildren().size());
        assertEquals(a.getId(), hiddenRoot.getChildren().get(0).getValue().getId());
        assertEquals(c.getId(), hiddenRoot.getChildren().get(1).getValue().getId());
    }

    @Test
    /**
     * A, B->C->D
     */
    void nonOrphansNestProperly() {
        // Setup
        List<SceneModel> sceneModels = new ArrayList<>();
        PromptSceneModel a = new PromptSceneModel();
        PromptSceneModel b = new PromptSceneModel();
        PromptSceneModel c = new PromptSceneModel();
        PromptSceneModel d = new PromptSceneModel();

        b.answers = new ButtonModel[] { new ButtonModel("To C", c.getId()) };
        c.answers = new ButtonModel[] { new ButtonModel("To D", d.getId()) };

        sceneModels.add(a);
        sceneModels.add(b);
        sceneModels.add(c);
        sceneModels.add(d);

        LoadedSurveyModel loadedSurveyModel = new LoadedSurveyModel(sceneModels);
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        Controller.sceneGraph = new SceneGraph(loadedSurveyModel, careerModelLoader);
        Controller controller = new Controller();

        // Execution
        TreeItem<SceneModel> hiddenRoot = controller.buildSceneGraphTreeView();

        // Assertion
        assertEquals(2, hiddenRoot.getChildren().size());
    }

    @Test
    /**
     * A->Deleted
     */
    void simulateSceneDeletion() {
        // Setup
        List<SceneModel> sceneModels = new ArrayList<>();
        PromptSceneModel a = new PromptSceneModel();
        a.answers = new ButtonModel[] {new ButtonModel("To some deleted scene", "Some deleted scene ID") };
        sceneModels.add(a);

        LoadedSurveyModel loadedSurveyModel = new LoadedSurveyModel(sceneModels);
        CareerModelLoader careerModelLoader = new CareerModelLoader(new File("non_existent.xxx"));
        Controller.sceneGraph = new SceneGraph(loadedSurveyModel, careerModelLoader);
        Controller controller = new Controller();

        // Execution
        TreeItem<SceneModel> hiddenRoot = controller.buildSceneGraphTreeView();
        // Of that call doesn't throw a null pointer exception we are good!

        // Assertion
        assertEquals(1, hiddenRoot.getChildren().size());

        TreeItem<SceneModel> aTI = hiddenRoot.getChildren().get(0);
        assertEquals(1, aTI.getChildren().size());

        TreeItem<SceneModel> errorTI = aTI.getChildren().get(0);
        assertEquals(EmptySceneModel.class, errorTI.getValue().getClass());
    }
}
