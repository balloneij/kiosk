package editor;

import editor.sceneloaders.CareerDescriptionSceneLoader;
import editor.sceneloaders.CareerPathwaySceneLoader;
import editor.sceneloaders.DetailsSceneLoader;
import editor.sceneloaders.PathwaySceneLoader;
import editor.sceneloaders.PromptSceneLoader;
import editor.sceneloaders.SpokeGraphPromptSceneLoader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kiosk.EventListener;
import kiosk.SceneGraph;
import kiosk.SceneModelException;
import kiosk.models.*;

public class Controller implements Initializable {

    public static SceneGraph sceneGraph;
    public static CareerModel[] careers;

    private String previousId;
    private File surveyFile = null;

    public static boolean hasPendingChanges;

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

        File surveyFile = new File("survey.xml");
        if (surveyFile.exists()) {
            this.surveyFile = surveyFile;
        }

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

                        // A deep copy is NECESSARY here. We are duplicating the scenes
                        // loaded into the scene type combobox.
                        SceneModel newModel = newValue.deepCopy();
                        newModel.setId(sceneGraph.getCurrentSceneModel().getId());
                        newModel.setName(sceneGraph.getCurrentSceneModel().getName());
                        sceneGraph.registerSceneModel(newModel);

                        rebuildToolbar(newModel);
                        rebuildSceneGraphTreeView();
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

        sceneGraph.addSceneChangeCallback(this::populateSceneTypeChooser);

        MenuItem newSceneMenuItem = new MenuItem("Create a New Scene");
        sceneGraphTreeView.setContextMenu(new ContextMenu(newSceneMenuItem));
        newSceneMenuItem.setOnAction(t -> {
            createNewScene(true);
            rebuildSceneGraphTreeView();
            rebuildToolbar(sceneGraph.getCurrentSceneModel());
        });

        sceneGraphTreeView.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.DELETE) {
                if (sceneGraphTreeView.getSelectionModel().getSelectedItem() != null) {
                    SceneModel selectedModel =
                            sceneGraphTreeView.getSelectionModel().getSelectedItem().getValue();
                    if (selectedModel != null) {
                        // Operators could be distributed internally, but it reduces clarity
                        if (!(selectedModel == sceneGraph.getRootSceneModel()
                                || (selectedModel.getClass().equals(EmptySceneModel.class)
                                && !((EmptySceneModel) selectedModel).intent))) {
                            deleteScene(selectedModel);
                        } else {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Information");
                            alert.setHeaderText("Unable to Delete Scene");
                            alert.setContentText("The root scene, as well as empty scenes "
                                    + "pointed to by a button, cannot be deleted.");
                            alert.showAndWait();
                        }
                    }
                }
            }
        });

        SceneModelTreeCell.sceneGraph = sceneGraph;
        hasPendingChanges = false;
        if (sceneGraph.getRootSceneModel() instanceof ErrorSceneModel) {
            Editor.setTitle("No file loaded");
        } else {
            Editor.setTitle(surveyFile != null ? surveyFile.getName() : "No file loaded");
        }
        populateSceneTypeChooser(sceneGraph.getCurrentSceneModel());
    }

    /**
     * Rebuild the toolbar. Public because the toolbar can need to
     * be remade under various circumstances (not just when switching scenes.)
     */
    public void rebuildToolbar(SceneModel model) {
        // Clear the scene type selector if we changed scenes
        if (previousId != null && !previousId.equals(model.getId())) {
            sceneTypeComboBox.getSelectionModel().clearSelection();
        }

        previousId = model.getId();
        sceneTypeComboBox.setDisable(false);
        if (model instanceof PromptSceneModel) {
            PromptSceneLoader.loadScene(this, (PromptSceneModel) model, toolbarBox, sceneGraph);
        } else if (model instanceof SpokeGraphPromptSceneModel) {
            SpokeGraphPromptSceneLoader.loadScene(this,
                    (SpokeGraphPromptSceneModel) model, toolbarBox, sceneGraph);
        } else if (model instanceof CareerPathwaySceneModel) {
            CareerPathwaySceneLoader.loadScene(this, (CareerPathwaySceneModel) model,
                toolbarBox, sceneGraph);
        } else if (model instanceof PathwaySceneModel) {
            PathwaySceneLoader.loadScene(this, (PathwaySceneModel) model, toolbarBox, sceneGraph);
        } else if (model instanceof DetailsSceneModel) {
            DetailsSceneLoader.loadScene(this, (DetailsSceneModel) model, toolbarBox, sceneGraph);
        } else if (model instanceof CareerDescriptionModel) {
            sceneTypeComboBox.setDisable(true);
            CareerDescriptionSceneLoader.loadScene(
                    this, (CareerDescriptionModel) model, toolbarBox, sceneGraph);
        } else if (model instanceof EmptySceneModel) {
            toolbarBox.getChildren().clear();
        } else {
            toolbarBox.getChildren().clear();
            sceneTypeComboBox.setDisable(true);
        }
    }

    /**
     * Sets the flag to track if we have pending changs.
     * Also adds or removes the star from the window indicating pending changes.
     * @param hasPendingChanges True if we have unsaved changes, false otherwise.
     */
    public static void setHasPendingChanges(boolean hasPendingChanges) {
        Controller.hasPendingChanges = hasPendingChanges;
        if (Controller.hasPendingChanges) {
            Editor.setTitle("*" + Editor.getTitle().replaceAll("\\*", ""));
        } else {
            Editor.setTitle(Editor.getTitle().replaceAll("\\*", ""));
        }
    }

    /**
     * Constructs a brand new hidden root for the tree view to use.
     * @return The new tree root.
     */
    public TreeItem<SceneModel> buildSceneGraphTreeView() {

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

        // If we added any orphans that turned out to later have parents, remove them here
        // Iterate until i >= 1 to avoid removing the root
        int childrenCount = hiddenRoot.getChildren().size();
        for (int i = childrenCount - 1; i >= 1; i--) {
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

        // create a set of the children that need to be added yet (no duplicates)
        Set<String> remainingChildren = new HashSet<>();
        Collections.addAll(remainingChildren, rootModel.getTargets());

        for (String childId : rootModel.getTargets()) {
            if (!unvisitedScenes.contains(childId)) { // This scene has already been touched
                // Spoke graph prompt scenes return children with the null targetId
                if (childId.equals("null")) {
                    continue;
                } else if (sceneGraph.getSceneById(childId)
                        .getClass().equals(ErrorSceneModel.class)) {
                    // prevents adding an error scene to the treeView
                    EmptySceneModel replaceError = new EmptySceneModel(childId,
                            ((ErrorSceneModel) sceneGraph.getSceneById(childId)).errorMsg);
                    replaceError.intent = false;
                    sceneGraph.registerSceneModel(replaceError);
                    root.getChildren().add(new TreeItem<>(replaceError));
                    continue;
                }

                // needs to re-check in the case that a scene targeted by two scenes was deleted
                if (depths.containsKey(childId)) {
                    if (depths.get(childId) < depth
                            || childId.equals(rootModel.getId())) {
                        // This is how we determine if this is THE parent,
                        // or just a child that needs pruning
                        if (depths.get(childId) == 0 && !childId.equals(rootParentId)) {
                            depths.put(childId, depth + 1);
                        }
                        SceneModel childSceneModel = sceneGraph.getSceneById(childId);
                        // Add the parent to the tree element
                        root.getChildren().add(new TreeItem<>(childSceneModel));
                        continue;
                    } else if (depths.get(childId) < depth + 1) {
                        depths.put(childId, depth + 1);
                    }
                } else {
                    depths.put(childId, depth + 1);
                }
            }

            // check that the child being added is new
            if (remainingChildren.contains(childId)) {
                // if the child IS new, indicate that it no longer needs to be added
                remainingChildren.remove(childId);
                SceneModel childSceneModel = sceneGraph.getSceneById(childId);
                TreeItem<SceneModel> child = new TreeItem<>(childSceneModel);
                root.getChildren()
                        .add(buildSubtree(child, rootParentId, unvisitedScenes, depths, depth + 1));
            }
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
        // doesn't need to be a hashMap; every item in here is expanded
        // and we simply don't record the ones that are not expanded
        ArrayList<String> expandedItems = new ArrayList<>();

        // because the tree view is the way we want it here, it's fine to
        // loop through just the tree items - this is simpler, and/but
        // causes "hidden expanded items" to no be remembered on refresh
        if (sceneGraphTreeView.getRoot() != null) {
            for (int i = 0; i < sceneGraphTreeView.getExpandedItemCount(); i++) {
                TreeItem<SceneModel> treeItem = sceneGraphTreeView.getTreeItem(i);
                if (treeItem.isExpanded()) {
                    expandedItems.add(treeItem.getValue().getId());
                }
            }
        }

        TreeItem<SceneModel> hiddenRoot = buildSceneGraphTreeView();

        for (TreeItem<SceneModel> potentialOrphan : hiddenRoot.getChildren()) {
            if (!potentialOrphan.getValue().equals(sceneGraph.getRootSceneModel())) {
                SceneModel orphan = potentialOrphan.getValue();
                // check if orphan needs to find a new home (be removed)
                // this happens if it's an empty scene, and there was no intent of creation
                if (orphan.getClass().equals(EmptySceneModel.class)) {
                    EmptySceneModel emptyOrphan = (EmptySceneModel) orphan;
                    if (!emptyOrphan.intent) {
                        deleteScene(orphan);
                        potentialOrphan.getParent().getChildren().remove(potentialOrphan);
                        break;
                    }
                }
                // else keep and mark it
                if (!orphan.getName().contains(ChildIdentifiers.ORPHAN)) {
                    orphan.setName(ChildIdentifiers.ORPHAN + orphan.getName());
                }
            }
        }
        this.sceneGraphTreeView.setRoot(hiddenRoot);

        // null check for safety; should be initialized already (from above)
        if (sceneGraphTreeView.getRoot() != null) {
            int numToExpand = expandedItems.size();
            for (int i = 0; i < numToExpand; i++) {
                int j = 0;
                TreeItem<SceneModel> treeItem = sceneGraphTreeView.getTreeItem(j);
                while (treeItem != null) {
                    if (expandedItems.contains(treeItem.getValue().getId())) {
                        treeItem.setExpanded(true);
                        expandedItems.remove(treeItem.getValue().getId());
                        break;
                    }
                    treeItem = sceneGraphTreeView.getTreeItem(++j);
                }
            }
        }
        
        Controller.setHasPendingChanges(true);
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
            rebuildToolbar(sceneGraph.getCurrentSceneModel());
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

    /**
     * Creates a new scene.
     * @param intent Whether the user has intentionally created the scene, or
     *     whether the scene was created automatically via a new
     *     button
     * @return the newly-created EmptySceneModel
     * @apiNote  It's a good idea to call rebuildSceneGraphTreeView() and
     *     rebuildToolBar() soon after using this method.
     * @implNote   rebuildSceneGraphTreeView() cannot be called __in__ this
     *     method because the returned SceneModel needs to actually be returned
     *     before the tree view can be rebuilt; otherwise, if intent is false,
     *     the SceneModel will be deleted before it can even be returned.
     *     Similarly, rebuildToolbar() can't be called here because, on
     *     non-intentional empty scene creation, it rebuilds the toolbar
     *     for an empty scene instead of the current scene.
     */
    @FXML
    public EmptySceneModel createNewScene(boolean intent) {
        EmptySceneModel model = new EmptySceneModel();
        model.message = "This scene is empty! Change the scene type on the left side";
        model.intent = intent;

        // Add to the scene graph
        addNewScene(sceneGraphTreeView.getRoot(), model);
        return model;
    }

    /**
     * Adds a new scene to the hidden root.
     * @param hiddenRoot The root, invisible tree item.
     * @param newScene The scene we want to add to the survey.
     */
    public void addNewScene(TreeItem<SceneModel> hiddenRoot, SceneModel newScene) {
        sceneGraph.registerSceneModel(newScene);
        hiddenRoot.getChildren().add(new TreeItem<>(newScene));
        Controller.setHasPendingChanges(true);
    }

    @FXML
    private void loadSurvey() {
        if (Controller.hasPendingChanges) {
            Optional<ButtonType> result = UnsavedChangesAlert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == UnsavedChangesAlert.SAVE) {
                    saveSurveyAs();
                } else if (result.get() == UnsavedChangesAlert.CANCEL) {
                    return;
                }
            }
        }

        // Ask user for a survey file
        File file = Editor.showFileOpener();

        // If they chose a file that exists, try to load it
        if (file != null && file.exists()) {
            LoadedSurveyModel survey;
            try {
                // Attempt to load from file
                survey = LoadedSurveyModel.readFromFile(file);
                this.surveyFile = file;
                Editor.setTitle(file.getName());
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
            CareerModelLoader careerModelLoader =
                    new CareerModelLoader(new File(CareerModelLoader.DEFAULT_CAREERS_CSV_PATH));
            sceneGraph.loadSurvey(survey, careerModelLoader);
            sceneGraph.addSceneChangeCallback(new EditorSceneChangeCallback(this));
            sceneGraph.reset();

            // Display any issues
            if (careerModelLoader.hasIssues()) {
                DefaultSceneModel model = new DefaultSceneModel();
                model.message = careerModelLoader.getIssuesSummary();
                sceneGraph.pushScene(model);
            }

            rebuildSceneGraphTreeView();
            // If we load a file, the toolbar can hold old values.
            // Rebuild toolbar to clear them all out
            rebuildToolbar(sceneGraph.getCurrentSceneModel());
            sceneGraph.addSceneChangeCallback(this::populateSceneTypeChooser);
            populateSceneTypeChooser(sceneGraph.getCurrentSceneModel());
            Controller.hasPendingChanges = false;
            Editor.setTitle(surveyFile != null ? surveyFile.getName() : "No file loaded");
        }
    }

    @FXML
    private void reloadSurvey() {
        Optional<ButtonType> result = UnsavedChangesAlert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == UnsavedChangesAlert.SAVE) {
                saveSurvey();
            }

            // Reload from disk
            LoadedSurveyModel survey = LoadedSurveyModel.readFromFile(this.surveyFile);
            CareerModelLoader careerModelLoader =
                    new CareerModelLoader(new File(CareerModelLoader.DEFAULT_CAREERS_CSV_PATH));
            sceneGraph.loadSurvey(survey, careerModelLoader);
            sceneGraph.addSceneChangeCallback(new EditorSceneChangeCallback(this));
            sceneGraph.reset();

            // Display any issues
            if (careerModelLoader.hasIssues()) {
                DefaultSceneModel model = new DefaultSceneModel();
                model.message = careerModelLoader.getIssuesSummary();
                sceneGraph.pushScene(model);
            }

            rebuildSceneGraphTreeView();
            rebuildToolbar(sceneGraph.getCurrentSceneModel());
            sceneGraph.addSceneChangeCallback(this::populateSceneTypeChooser);
            populateSceneTypeChooser(sceneGraph.getCurrentSceneModel());
            Controller.hasPendingChanges = false;
            Editor.setTitle(surveyFile != null ? surveyFile.getName() : "No file loaded");
        }
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
                this.hasPendingChanges = false;
                Editor.setTitle(surveyFile != null ? surveyFile.getName() : "No file loaded");
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
                this.hasPendingChanges = false;
                Editor.setTitle(surveyFile != null ? surveyFile.getName() : "No file loaded");
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
        FXMLLoader loader;
        File editorFxml = new File("src/main/java/editor/SurveySettings.fxml");
        if (editorFxml.exists()) {
            loader = new FXMLLoader(editorFxml.toURI().toURL());
        } else {
            loader = new FXMLLoader(this.getClass().getResource("SurveySettings.fxml"));
        }
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
        return sceneGraph.exportSurvey();
    }

    private void populateSceneTypeChooser(SceneModel newSceneModel) {
        sceneTypeComboBox
                .getItems()
                .filtered(scene -> scene.toString().equals(newSceneModel.toString()))
                .stream()
                .findFirst()
                .ifPresent(sceneModel -> sceneTypeComboBox.setValue(sceneModel));
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
