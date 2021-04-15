package editor.sceneloaders;

import editor.ChildIdentifiers;
import editor.Controller;
import java.util.LinkedList;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kiosk.SceneGraph;
import kiosk.models.ButtonModel;
import kiosk.models.FilterGroupModel;
import kiosk.models.SceneModel;

public class SceneLoader {
    // The default padding to space the editing Nodes
    static final Insets PADDING = new Insets(0, 0, 10, 10);
    static boolean ShowingNameAlert = false;

    protected static Node getNameBox(Controller controller, SceneModel model, SceneGraph graph) {
        TextField nameField = new TextField(getEditableName(model));

        nameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !ShowingNameAlert
                    && !getEditableName(model).equals(nameField.getText())) {
                evaluateNameProperty(controller, model, graph, nameField);
            }
        });

        nameField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)
                    && !getEditableName(model).equals(nameField.getText())) {
                evaluateNameProperty(controller, model, graph, nameField);
            }
        });

        VBox vbox = new VBox(new Label("Name:"), nameField);
        vbox.setPadding(PADDING);
        return vbox;
    }

    /**
     * Create a filter editing box that applies changes to the parent model.
     * @param graph to apply changes to
     * @param parent that owns the button
     * @param button to create the filter off of
     * @return JavaFx node
     */
    public static Node getFilterBox(SceneGraph graph, SceneModel parent, ButtonModel button) {
        // Category selector
        LinkedList<String> categories = new LinkedList<>(graph.getCareerCategories());
        categories.addFirst("None");
        ComboBox<String> categoriesComboBox =
                new ComboBox<>(FXCollections.observableList(categories));

        // Field selector
        ComboBox<String> fieldsComboBox;

        // Populate the field selector and set defaults based off the current filter
        if (button.filter == null) {
            categoriesComboBox.setValue("None");
            fieldsComboBox = new ComboBox<>(FXCollections.emptyObservableList());
        } else {
            categoriesComboBox.setValue(button.filter.category);
            LinkedList<String> fields = new LinkedList<>();
            fields.addFirst("All");
            fields.addAll(graph.getCareerFields(button.filter.category));
            fieldsComboBox = new ComboBox<>(FXCollections.observableList(fields));
            fieldsComboBox.setValue(button.filter.field);
        }

        // Create event handler
        EventHandler<ActionEvent> eventHandler = event -> {
            String category = categoriesComboBox.getValue();
            String field = fieldsComboBox.getValue();

            // Update the combo boxes and values
            if (category.equals("None")) {
                fieldsComboBox.setItems(FXCollections.emptyObservableList());
            } else {
                LinkedList<String> fields = new LinkedList<>();
                fields.addFirst("All");
                Set<String> fieldSet = graph.getCareerFields(category);
                fields.addAll(fieldSet);
                fieldsComboBox.setItems(FXCollections.observableList(fields));
            }

            // Update the filter
            if (category.equals("None")) {
                // #nofilter
                button.filter = null;
            } else {
                button.filter = FilterGroupModel.create();

                if (field == null || field.equals("All")) {
                    // Category filter
                    button.filter.category = category;
                    button.filter.field = "All";
                    button.filter.careerNames = graph.findCareers(category, "All");
                } else {
                    // Field filter
                    button.filter.category = category;
                    button.filter.field = field;
                    button.filter.careerNames = graph.findCareers(category, field);
                }
            }

            // Re-register the model to update the scene
            graph.registerSceneModel(parent);
        };

        // Set event handlers
        categoriesComboBox.setOnAction(eventHandler);
        fieldsComboBox.setOnAction(eventHandler);

        return new VBox(
                new HBox(new Label("Category: "), categoriesComboBox),
                new HBox(new Label("Fields: "), fieldsComboBox)
        );
    }

    private static void evaluateNameProperty(Controller controller, SceneModel model,
            SceneGraph graph, TextField nameField) {
        String newValue = nameField.getText();
        String oldName = model.getName();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(String.format("There is already a scene with the name %s."
                + "\r\n Please try a different name.", newValue));

        if (graph.getSceneModelByName(newValue) == null) { // No matches
            model.setName(newValue);
            controller.rebuildSceneGraphTreeView();
        } else {
            if (!alert.isShowing()) {
                alert.showAndWait();
            }
            nameField.setText(oldName);
            nameField.positionCaret(oldName.length());
        }
    }

    private static String getEditableName(SceneModel model) {
        String name = model.getName();
        name = name.replaceAll(ChildIdentifiers.ORPHAN, ChildIdentifiers.CHILD);
        return name.replaceAll(ChildIdentifiers.ROOT, ChildIdentifiers.CHILD);
    }
}
