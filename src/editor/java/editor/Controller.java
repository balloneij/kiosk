package editor;

import editor.sceneloaders.DetailsSceneLoader;
import editor.sceneloaders.PromptSceneLoader;
import java.net.URL;
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
import kiosk.models.DetailsSceneModel;
import kiosk.models.EmptySceneModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.SceneModel;
import kiosk.scenes.DetailsScene;
import processing.javafx.PSurfaceFX;


public class Controller implements Initializable {

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
                new PromptSceneModel(),
                new DetailsSceneModel()
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
        } else if (model instanceof DetailsSceneModel) {
            DetailsSceneLoader.loadScene(this, (DetailsSceneModel) model, toolbarBox, sceneGraph);
        } else {
            toolbarBox.getChildren().clear();
        }
    }

    /**
     * Rebuild the scene graph tree view at the depth
     * specified by TREE_VIEW_DEPTH.
     */
    public void rebuildSceneGraphTreeView() {
        Set<String> nonOrphanChildren = new HashSet<>();

        // All treeviews must have a root. This root is hidden, so it's
        // impossible for the user to modify it. Under the hidden root
        // is where we add the survey and orphaned children
        TreeItem<String> hiddenRoot = new TreeItem<>("Hidden Root");

        // Create the survey subtree
        SceneModel rootScene = sceneGraph.getRootSceneModel();
        rebuildSceneGraphTreeView(hiddenRoot, rootScene, nonOrphanChildren);

        // Start with all the children, remove the children
        // who have parents, and you are left with orphaned children
        Set<String> orphanChildren = new HashSet<>(sceneGraph.getAllIds());
        orphanChildren.removeAll(nonOrphanChildren);
        orphanChildren.remove(rootScene.getId());

        // Create subtrees for the orphan children
        Set<String> orphansAccountedFor = new HashSet<>();
        Set<String> orphanRoots = new HashSet<>();
        for (String childId : orphanChildren) {
            if (!orphansAccountedFor.contains(childId)) {
                // Create new root node
                Set<String> newOrphansAccountedFor = new HashSet<>();
                SceneModel childScene = sceneGraph.getSceneById(childId);
                rebuildSceneGraphTreeView(hiddenRoot, childScene, newOrphansAccountedFor);

                // Add its children to the accounted for
                orphansAccountedFor.addAll(newOrphansAccountedFor);

                // Check if the new root subsumes previous roots by performing
                // an intersection on the orphan roots and new accounted for
                Set<String> subsumedIds = new HashSet<>(orphanRoots);
                subsumedIds.retainAll(newOrphansAccountedFor);
                for (String subsumedId : subsumedIds) {
                    // Remove from the tree view because its no longer needed
                    hiddenRoot.getChildren().removeIf(node -> node.getValue().equals(subsumedId));
                    orphanRoots.remove(subsumedId);
                }

                // A new root was made, so add it to the set
                orphanRoots.add(childId);
            }
        }

        this.sceneGraphTreeView.setRoot(hiddenRoot);
    }

    private void rebuildSceneGraphTreeView(TreeItem<String> parent,
                                           SceneModel model, Set<String> nonOrphanChildren) {
        // Add node to parent
        String modelId = model.getId();
        TreeItem<String> node = new TreeItem<>(modelId);
        parent.getChildren().add(node);

        // Walk through the new node's parents. If it's recursive,
        // return early instead of adding its children
        TreeItem<String> parentWalker = parent;
        while (parentWalker != null) {
            if (parentWalker.getValue().equals(modelId)) {
                return;
            }
            parentWalker = parentWalker.getParent();
        }

        // Check children
        String[] childrenIds = model.getTargets();

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
            nonOrphanChildren.add(id);

            // Add child to node
            rebuildSceneGraphTreeView(node, scene, nonOrphanChildren);
        }
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
