package editor;

import editor.sceneloaders.PromptSceneLoader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import kiosk.EventListener;
import kiosk.SceneGraph;
import kiosk.models.EmptySceneModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.SceneModel;
import processing.javafx.PSurfaceFX;


public class Controller implements Initializable {

    /**
     * Depth of the tree view will display. Limiting
     * the depth is necessary for recursive scenes
     */
    public static final int TREE_VIEW_DEPTH = 25;

    public static PSurfaceFX surface;
    public static SceneGraph sceneGraph;
    protected static Stage stage;

    private String previousId;

    @FXML
    AnchorPane rootPane;
    @FXML
    VBox toolbarBox;
    @FXML
    StackPane surveyPreviewPane;
    @FXML
    SplitPane splitPane;
    @FXML
    TreeView<String> sceneGraphTreeView;
    @FXML
    ComboBox<SceneModel> sceneTypeComboBox;
    @FXML
    Button deleteCurrentSceneButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Canvas canvas = (Canvas) surface.getNative();
        surface.fx.context = canvas.getGraphicsContext2D();
        surveyPreviewPane.getChildren().add(canvas);
        canvas.widthProperty().bind(surveyPreviewPane.widthProperty());
        canvas.heightProperty().bind(surveyPreviewPane.heightProperty());

        sceneGraph.addSceneChangeCallback(new EditorSceneChangeCallback(this));
        previousId = null;

        for (Node node : splitPane.lookupAll(".split-pane-divider")) {
            node.setVisible(true);
        }

        // Calculate the divider location for the split pane based off of the width
        // of the preview window and the width of the editor toolbar
        splitPane.setDividerPosition(0,
                (double) Editor.TOOLBAR_WIDTH / (Editor.TOOLBAR_WIDTH + Editor.PREVIEW_WIDTH));

        // The split pane will respect max widths, so by assigning these, the divider
        // cannot be moved
        // TODO: There is a better way of doing this. Using CSS-like JavaFX styling
        // you can hide the cursor so the divider cannot be moved. I could not get that to work.
        // - Isaac
        toolbarBox.maxWidthProperty().setValue(Editor.TOOLBAR_WIDTH);
        toolbarBox.minWidthProperty().setValue(Editor.TOOLBAR_WIDTH);
        surveyPreviewPane.maxWidthProperty().setValue(Editor.PREVIEW_WIDTH);
        surveyPreviewPane.minWidthProperty().setValue(Editor.PREVIEW_WIDTH);

        // Populate the toolbar
        rebuildToolbar(sceneGraph.getRootSceneModel());

        // Populate the tree view
        rebuildSceneGraphTreeView();

        // Add scene type options for user seletion
        sceneTypeComboBox.setItems(FXCollections.observableArrayList(
                new PromptSceneModel()
        ));

        // Handler for changing the type of scene via the combo box
        sceneTypeComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, sceneModel, selectedModel) -> {
                    // Ignore when the combo box is reset
                    if (selectedModel != null) {
                        String currentSceneId = sceneGraph.getCurrentSceneModel().getId();

                        // A deep copy is NECESSARY here. We are duplicating the scenes
                        // loaded into the scene type combobox.
                        SceneModel newModel = selectedModel.deepCopy();
                        newModel.setId(currentSceneId);
                        sceneGraph.registerSceneModel(newModel);

                        rebuildToolbar(newModel);
                    }
                });

        // Add listener that changes the scene when the user clicks it in the treeview
        sceneGraphTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, treeItem, selected) -> {
                    if (selected != null) {
                        SceneModel scene = sceneGraph.getSceneById(selected.getValue());
                        // Change scene graph to the scene selected
                        sceneGraph.pushScene(scene);
                    }
                });

        // An empty root is used, so hide it
        sceneGraphTreeView.setShowRoot(false);
    }

    private void rebuildToolbar(SceneModel model) {
        // Clear the scene type selector if we changed scenes
        if (previousId != null && !previousId.equals(model.getId())) {
            sceneTypeComboBox.getSelectionModel().clearSelection();
        }

        // Grey out the delete button so we can't remove the root node
        deleteCurrentSceneButton.setDisable(
                model.getId().equals(sceneGraph.getRootSceneModel().getId()));

        previousId = model.getId();
        if (model instanceof PromptSceneModel) {
            PromptSceneLoader.loadScene(this, (PromptSceneModel) model, toolbarBox, sceneGraph);
        } else {
            toolbarBox.getChildren().clear();
        }
    }

    /**
     * Rebuild the scene graph tree view at the depth
     * specified by TREE_VIEW_DEPTH.
     */
    public void rebuildSceneGraphTreeView() {
        Set<String> goodChildren = new HashSet<>();

        // All treeviews must have a root. This root is hidden, so it's
        // impossible for the user to modify it. Under the hidden root
        // is where we add the survey and orphaned children
        TreeItem<String> hiddenRoot = new TreeItem<>("Hidden Root");

        // Create the survey subtree
        SceneModel rootScene = sceneGraph.getRootSceneModel();
        TreeItem<String> realRoot =
                rebuildSceneGraphTreeView(TREE_VIEW_DEPTH, rootScene, goodChildren);
        hiddenRoot.getChildren().add(realRoot);

        // Good children have parents
        Set<String> nonOrphanChildren = new HashSet<>();
        for (SceneModel scene : sceneGraph.getAllSceneModels()) {
            nonOrphanChildren.addAll(Arrays.asList(scene.getTargets().clone()));
        }
        Set<String> orphanChildren = new HashSet<>(sceneGraph.getAllIds());
        orphanChildren.removeAll(nonOrphanChildren);
        orphanChildren.remove(sceneGraph.getRootSceneModel().getId());

        // Create subtrees for the orphan children
        for (String childId : orphanChildren) {
            SceneModel childScene = sceneGraph.getSceneById(childId);

            if (childScene instanceof EmptySceneModel) {
                // Simply delete orphaned empty scene models
                sceneGraph.unregisterSceneModel(childScene);
            } else {
                // The results of this set aren't used because we already know the orphaned children
                Set<String> ignoredSet = new HashSet<>();

                TreeItem<String> childRoot =
                        rebuildSceneGraphTreeView(TREE_VIEW_DEPTH, childScene, ignoredSet);

                hiddenRoot.getChildren().add(childRoot);
            }
        }

        this.sceneGraphTreeView.setRoot(hiddenRoot);
    }

    private TreeItem<String> rebuildSceneGraphTreeView(int depth,
                                                       SceneModel model, Set<String> goodChildren) {
        TreeItem<String> node = new TreeItem<>(model.getId());
        String[] childrenIds = model.getTargets();

        if (depth > 0) {
            for (String id : childrenIds) {
                SceneModel scene;
                if (sceneGraph.containsScene(id)) {
                    // The child scene exists
                    scene = sceneGraph.getSceneById(id);
                } else {
                    // The child scene does not exist, put an empty scene in its
                    // place and register it with the scene graph
                    scene = new EmptySceneModel(id,
                            "This scene is empty! Change the scene type on the left side");
                    sceneGraph.registerSceneModel(scene);
                }

                // This child has parents
                goodChildren.add(id);

                TreeItem<String> childNode =
                        rebuildSceneGraphTreeView(depth - 1, scene, goodChildren);
                node.getChildren().add(childNode);
            }
        }

        return node;
    }

    private class EditorSceneChangeCallback implements EventListener<SceneModel> {
        private final Controller controller;

        public EditorSceneChangeCallback(Controller controller) {
            this.controller = controller;
        }

        @Override
        public void invoke(SceneModel arg) {
            if (!arg.getId().equals(previousId)) {
                controller.rebuildToolbar(arg);
            }
        }
    }

    @FXML
    private void deleteCurrentScene(ActionEvent event) {
        sceneGraph.unregisterSceneModel(sceneGraph.getCurrentSceneModel());
        rebuildSceneGraphTreeView();
    }

    @FXML
    private void createNewScene(ActionEvent event) {
        EmptySceneModel model = new EmptySceneModel();
        model.message = "This scene is empty! Change the scene type on the left side";

        // Add to the scene graph
        sceneGraph.registerSceneModel(model);

        // Add as to the tree view as an orphan child
        TreeItem<String> hiddenRoot = sceneGraphTreeView.getRoot();
        hiddenRoot.getChildren().add(new TreeItem<>(model.getId()));
    }
}
