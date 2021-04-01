package editor;

import editor.sceneloaders.CareerPathwaySceneLoader;
import editor.sceneloaders.DetailsSceneLoader;
import editor.sceneloaders.PathwaySceneLoader;
import editor.sceneloaders.PromptSceneLoader;
import editor.sceneloaders.SpokeGraphPromptSceneLoader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kiosk.EventListener;
import kiosk.SceneGraph;
import kiosk.SceneModelException;
import kiosk.models.CareerDescriptionModel;
import kiosk.models.CareerModel;
import kiosk.models.CareerPathwaySceneModel;
import kiosk.models.DetailsSceneModel;
import kiosk.models.EmptySceneModel;
import kiosk.models.ErrorSceneModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.LoadedSurveyModel;
import kiosk.models.PathwaySceneModel;
import kiosk.models.PromptSceneModel;
import kiosk.models.SceneModel;
import kiosk.models.SpokeGraphPromptSceneModel;

public class Controller implements Initializable {

    public static SceneGraph sceneGraph;
    public static CareerModel[] careers;
    public static FilterGroupModel[] filters;

    private String previousId;
    private File surveyFile = null;
    private ArrayList<Boolean> expanded;

    @FXML
    AnchorPane rootPane;
    @FXML
    VBox toolbarBox;
    @FXML
    StackPane surveyPreviewPane;
    @FXML
    SplitPane splitPane;
    @FXML
    TreeView<SceneModel> sceneGraphTreeView;
    @FXML
    ComboBox<SceneModel> sceneTypeComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        // Add scene type options for user selection
        sceneTypeComboBox.setItems(FXCollections.observableArrayList(
                new PromptSceneModel(),
                SpokeGraphPromptSceneModel.create(),
                new PathwaySceneModel(),
                CareerPathwaySceneModel.create(),
                new DetailsSceneModel()
        ));

        // Handler for changing the type of scene via the combo box
        sceneTypeComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    // Ignore when the combo box is reset, or the scene type already matches.
                    if (newValue != null
                            && !newValue.toString().equals(
                            sceneGraph.getCurrentSceneModel().toString())) {
                        String currentSceneId = sceneGraph.getCurrentSceneModel().getId();
                        String currentSceneName = sceneGraph.getCurrentSceneModel().getName();

                        // A deep copy is NECESSARY here. We are duplicating the scenes
                        // loaded into the scene type combobox.
                        SceneModel newModel = newValue.deepCopy();
                        newModel.setId(currentSceneId);
                        newModel.setName(currentSceneName);
                        sceneGraph.registerSceneModel(newModel);

                        rebuildToolbar(newModel);
                    }
                });

        // Add listener that changes the scene when the user clicks it in the treeview
        sceneGraphTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, treeItem, selected) -> {
                    if (selected != null) {
                        SceneModel scene = sceneGraph.getSceneById(selected.getValue().getId());
                        // Change scene graph to the scene selected
                        sceneGraph.pushScene(scene);
                    }
                });

        // An empty root is used, so hide it
        sceneGraphTreeView.setShowRoot(false);

        sceneGraphTreeView.setEditable(true);

        // This "overrides the TreeCell implementation and redefines the tree items as specified
        // in the TextFieldTreeCellImpl class."
        // https://docs.oracle.com/javafx/2/ui_controls/tree-view.htm Example 13-3
        sceneGraphTreeView.setCellFactory(p -> new SceneModelTreeCell(this));

        sceneGraph.addSceneChangeCallback(newSceneModel -> sceneTypeComboBox
                .getItems()
                .filtered(scene -> scene.toString().equals(newSceneModel.toString()))
                .stream()
                .findFirst()
                .ifPresent(sceneModel -> sceneTypeComboBox.setValue(sceneModel)));

        MenuItem newSceneMenuItem = new MenuItem("Create a New Scene");
        sceneGraphTreeView.setContextMenu(new ContextMenu(newSceneMenuItem));
        newSceneMenuItem.setOnAction(t -> {
            createNewScene();
        });

        SceneModelTreeCell.sceneGraph = sceneGraph;
    }

    /**
     * Rebuild the toolbar. Public because the toolbar can need to
     * be remade under various circumstances (not just when switching scenes.)
     * todo might be able to be replaced with just an "updateSceneName" method,
     * todo as that is (currently) the only other way of rebuilding besides
     * todo switching scenes
     */
    public void rebuildToolbar(SceneModel model) {
        // Clear the scene type selector if we changed scenes
        if (previousId != null && !previousId.equals(model.getId())) {
            sceneTypeComboBox.getSelectionModel().clearSelection();
        }

        previousId = model.getId();
        if (model instanceof PromptSceneModel) {
            PromptSceneLoader.loadScene(this, (PromptSceneModel) model, toolbarBox, sceneGraph);
        } else if (model instanceof SpokeGraphPromptSceneModel) {
            SpokeGraphPromptSceneLoader.loadScene(this,
                    (SpokeGraphPromptSceneModel) model, toolbarBox, sceneGraph, filters);
        } else if (model instanceof CareerPathwaySceneModel) {
            CareerPathwaySceneLoader.loadScene(this, (CareerPathwaySceneModel) model,
                toolbarBox, sceneGraph, filters);
        } else if (model instanceof PathwaySceneModel) {
            PathwaySceneLoader.loadScene(this, (PathwaySceneModel) model, toolbarBox, sceneGraph);
        } else if (model instanceof DetailsSceneModel) {
            DetailsSceneLoader.loadScene(this, (DetailsSceneModel) model, toolbarBox, sceneGraph);
        } else {
            toolbarBox.getChildren().clear();
        }
    }

    /**
     * Constructs a brand new hidden root for the tree view to use.
     * @return The new tree root.
     */
    public TreeItem<SceneModel> buildSceneGraphTreeView() {
        // TODO change to hash map that stores scenemodels' id's AND boolean
        expanded = new ArrayList<>();
        for (int i = 0; i < sceneGraph.getAllSceneModels().size(); i++) {
            if (sceneGraphTreeView.getTreeItem(i) != null) {
                expanded.add(sceneGraphTreeView.getTreeItem(i).isExpanded());
            } else {
                break;
            }
        }

        TreeItem<SceneModel> hiddenRoot = new TreeItem<>();
        hiddenRoot.setExpanded(true);

        Set<String> unvisitedScenes = new HashSet<>(sceneGraph.getAllIds());
        HashMap<String, Integer> depths = new HashMap<>();

        TreeItem<SceneModel> rootTreeItem = new TreeItem<>();
        rootTreeItem.setValue(sceneGraph.getRootSceneModel());
        hiddenRoot.getChildren().add(buildSubtree(rootTreeItem,
                sceneGraph.getRootSceneModel().getId(), unvisitedScenes, depths, 0));

        while (!unvisitedScenes.isEmpty()) {
            // Get the next potential orphan
            String nextOrphanId = unvisitedScenes.stream().findFirst().get();
            SceneModel nextOrphan = sceneGraph.getSceneById(nextOrphanId);

            // Skip the end screen
            if (nextOrphan.getClass().equals(CareerDescriptionModel.class)) {
                unvisitedScenes.remove(nextOrphanId);
                continue;
            }

            TreeItem<SceneModel> orphanTreeItem = new TreeItem<>(nextOrphan);
            hiddenRoot.getChildren().add(buildSubtree(orphanTreeItem, nextOrphanId,
                    unvisitedScenes, depths, 0));
        }

        // If we added any orhphans that turned out to later have parents, remove them here
        for (int i = hiddenRoot.getChildren().size() - 1; i >= 0; i--) {
            TreeItem<SceneModel> child = hiddenRoot.getChildren().get(i);
            if (depths.get(child.getValue().getId()) > 0) {
                hiddenRoot.getChildren().remove(i);
            }
        }
        return hiddenRoot;
    }

    private TreeItem<SceneModel> buildSubtree(TreeItem<SceneModel> root, String rootParentId,
          Set<String> unvisitedScenes, HashMap<String, Integer> depths, int depth) {
        SceneModel rootModel = root.getValue();
        rootModel.setName(rootModel
                .getName()
                .replaceAll(ChildIdentifiers.ORPHAN, ChildIdentifiers.CHILD));
        unvisitedScenes.remove(rootModel.getId());

        // If we have a key for this, set it to the highest value available
        if (depths.containsKey(rootModel.getId())) {
            depth = Math.max(depths.get(rootModel.getId()), depth);
        }
        // Set the key to it's highest computed value
        depths.put(rootModel.getId(), depth);

        for (String childId : rootModel.getTargets()) {
            if (!unvisitedScenes.contains(childId)) { // This scene has already been touched
                // Spoke graph prompt scenes return children with the null targetId
                if (childId.equals("null")) {
                    continue;
                } else if (sceneGraph.getSceneById(childId)
                        .getClass().equals(ErrorSceneModel.class)) {
                    root.getChildren().add(new TreeItem<>(new ErrorSceneModel()));
                    continue;
                }

                if (depths.get(childId) < depth
                        || childId.equals(rootModel.getId())) {
                    // This is how we determine if this is THE parent,
                    // or just a child that needs pruning
                    if (depths.get(childId) == 0 && !childId.equals(rootParentId)) {
                        depths.put(childId, depth + 1);
                    }
                    SceneModel childSceneModel = sceneGraph.getSceneById(childId);
                    childSceneModel.setName(childSceneModel.getName()
                            .replaceAll(ChildIdentifiers.ROOT, ChildIdentifiers.CHILD));
                    // Add the parent to the tree element
                    root.getChildren().add(new TreeItem<>(childSceneModel));
                    continue;
                } else if (depths.get(childId) < depth + 1) {
                    depths.put(childId, depth + 1);
                }
            } else {
                depths.put(childId, depth + 1);
            }
            SceneModel childSceneModel = sceneGraph.getSceneById(childId);
            childSceneModel.setName(childSceneModel.getName()
                    .replaceAll(ChildIdentifiers.ROOT, ChildIdentifiers.CHILD));
            TreeItem<SceneModel> child = new TreeItem<>(childSceneModel);
            root.getChildren()
                    .add(buildSubtree(child, rootParentId, unvisitedScenes, depths, depth + 1));
        }
        return root;
    }

    private void resetChildDepths(TreeItem<SceneModel> root, HashMap<String,
            Integer> depths, Set<String> unvisitedScenes) {
        depths.remove(root.getValue().getId());
        unvisitedScenes.add(root.getValue().getId());
        for (TreeItem<SceneModel> child : root.getChildren()) {
            resetChildDepths(child, depths, unvisitedScenes);
        }
    }

    /**
     * Rebuild the scene graph tree view at the depth
     * specified by TREE_VIEW_DEPTH.
     */
    public void rebuildSceneGraphTreeView() {
        TreeItem<SceneModel> hiddenRoot = buildSceneGraphTreeView();

        for (TreeItem<SceneModel> potentialOrphan : hiddenRoot.getChildren()) {
            if (!potentialOrphan.getValue().equals(sceneGraph.getRootSceneModel())) {
                SceneModel orphan = potentialOrphan.getValue();
                if (!orphan.getName().contains(ChildIdentifiers.ORPHAN)) {
                    orphan.setName(ChildIdentifiers.ORPHAN + orphan.getName());
                }
            }
        }
        this.sceneGraphTreeView.setRoot(hiddenRoot);

        for (int i = 0; i < expanded.size(); i++) {
            this.sceneGraphTreeView.getTreeItem(i).setExpanded(expanded.get(i));
        }
    }

    /**
     * Sets the specified SceneModel as the SceneGraph's
     * new Root Scene. This is called through the TreeCells'
     * Context Menus.
     *
     * @param newRoot the SceneModel that becomes the Root
     */
    @FXML
    public void setRootScene(SceneModel newRoot) {
        if (newRoot.getClass().equals(EmptySceneModel.class)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid Root Scene");
            alert.setContentText("An empty scene type cannot be set as the root scene");
            alert.showAndWait();
        } else {
            sceneGraph.setRootSceneModel(newRoot);
            rebuildSceneGraphTreeView();
        }
    }

    /**
     * Deletes a specified SceneModel from the SceneGraph.
     * This is called through the TreeCells' Context Menus.
     *
     * @param toDelete the SceneModel to be deleted
     */
    @FXML
    public void deleteScene(SceneModel toDelete) {
        try {
            sceneGraph.unregisterSceneModel(toDelete);
            rebuildSceneGraphTreeView();
        } catch (SceneModelException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Unable to Delete Scene");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void deleteCurrentScene() {
        deleteScene(sceneGraph.getCurrentSceneModel());
    }

    @FXML
    private void createNewScene() {
        EmptySceneModel model = new EmptySceneModel();
        model.message = "This scene is empty! Change the scene type on the left side";

        // Add to the scene graph
        addNewScene(sceneGraphTreeView.getRoot(), model);
    }

    public void addNewScene(TreeItem<SceneModel> hiddenRoot, SceneModel newScene) {
        sceneGraph.registerSceneModel(newScene);
        hiddenRoot.getChildren().add(new TreeItem<>(newScene));
    }

    @FXML
    private void loadSurvey() {
        // Ask user for a survey file
        File file = Editor.showFileOpener();

        // If they chose a file that exists, try to load it
        if (file != null && file.exists()) {
            LoadedSurveyModel survey;
            try {
                // Attempt to load from file
                survey = LoadedSurveyModel.readFromFile(file);
                this.surveyFile = file;
            } catch (Exception exception) {
                // Survey could not be created, so make an error survey
                String errorMsg = "Could not read from survey at '" + file.getPath()
                        + "'\nThe XML is probably deformed in some way."
                        + "\nRefer to the console for more specific details.";
                survey = new LoadedSurveyModel();
                survey.scenes = new SceneModel[]{new ErrorSceneModel(errorMsg)};

                exception.printStackTrace();
            }

            // Update the scene graph, reattach the editor callback, refresh the editor
            // Note: rebuildToolbar() is invoked on sceneGraph.reset(), but we must explicitly
            // rebuild the tree view.
            sceneGraph.loadSurvey(survey);
            sceneGraph.addSceneChangeCallback(new EditorSceneChangeCallback(this));
            sceneGraph.reset();
            rebuildSceneGraphTreeView();
        }
    }

    @FXML
    private void reloadSurvey() {
        sceneGraph.reset();
        rebuildSceneGraphTreeView();
    }

    @FXML
    private void saveSurveyAs() {
        // Prompt user for a file path to save to
        File file = Editor.showFileSaver();

        if (file != null) {
            // Add a .xml extension if it's missing one
            if (!file.getName().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }

            LoadedSurveyModel survey = createSurvey();
            try {
                survey.writeToFile(file);
                surveyFile = file;
            } catch (Exception exception) {
                // Push temporary scene describing error
                String errorMsg = "Could not save survey to '" + surveyFile.getPath()
                        + "\nRefer to the console for more specific details.";
                sceneGraph.pushScene(new ErrorSceneModel(errorMsg));

                exception.printStackTrace();
            }
        }
    }

    @FXML
    private void saveSurvey() {
        if (surveyFile == null) {
            this.saveSurveyAs();
        } else {
            try {
                createSurvey().writeToFile(surveyFile);
            } catch (Exception exception) {
                // Push temporary scene describing error
                String errorMsg = "Could not save survey to '" + surveyFile.getPath()
                        + "\nRefer to the console for more specific details.";
                sceneGraph.pushScene(new ErrorSceneModel(errorMsg));

                exception.printStackTrace();
            }
        }
    }

    /**
     * Saves a sample survey file in the user's working directory and shows them an alert
     * confirming it was saved.
     *
     * @param event The event coming from the MenuItem for triggering the sample save.
     */
    @FXML
    private void saveSampleSurvey(ActionEvent event) {
        File sampleFile = new File("sample_survey.xml");

        try {
            LoadedSurveyModel.createSampleSurvey().writeToFile(sampleFile);

            // Let the user know where it was saved
            Alert sampleSavedAlert = new Alert(Alert.AlertType.INFORMATION,
                    "Saved to " + sampleFile.getAbsolutePath());
            sampleSavedAlert.setHeaderText("Sample survey saved.");
            sampleSavedAlert.show();
        } catch (Exception exception) {
            // Push temporary scene describing error
            String errorMsg = "Could not save survey to '" + sampleFile.getAbsolutePath()
                    + "\nRefer to the console for more specific details.";
            sceneGraph.pushScene(new ErrorSceneModel(errorMsg));

            exception.printStackTrace();
        }
    }

    /**
     * Event method that pops up the survey settings editor window.
     *
     * @throws IOException Can occur if the popup window's FXML file is missing.
     */
    @FXML
    public void editSurveySettings() throws IOException {
        File surveySettingsFile = new File("src/main/java/editor/SurveySettings.fxml");
        FXMLLoader loader = new FXMLLoader(surveySettingsFile.toURI().toURL());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage popupWindow = new Stage();

        SurveySettingsController.root = popupWindow;
        popupWindow.setScene(scene);
        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setTitle("Survey Settings");

        popupWindow.showAndWait();
    }

    private LoadedSurveyModel createSurvey() {
        LoadedSurveyModel survey = sceneGraph.exportSurvey();
        survey.careers = careers;
        survey.filters = filters;
        return survey;
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
}
