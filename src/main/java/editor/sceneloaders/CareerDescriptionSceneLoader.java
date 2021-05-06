package editor.sceneloaders;

import editor.Controller;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import kiosk.CareerModelLoader;
import kiosk.SceneGraph;
import kiosk.models.CareerDescriptionModel;

public class CareerDescriptionSceneLoader {

    /**
     * Creates an informative toolbar.
     * @param controller unused
     * @param model unused
     * @param toolbarBox to add the text to
     * @param graph unused
     */
    public static void loadScene(Controller controller,
                                 CareerDescriptionModel model, VBox toolbarBox, SceneGraph graph) {
        VBox vbox = new VBox();

        Text text = new Text("This scene can't be edited directly.\n"
                + "To change this scene, you must edit the careers.csv file.\n\n"
                + "The csv is a spreadsheet. Each row is a career,\n"
                + "and the columns are, in order:\n"
                + "Category, Field, Holland Code, Career Name,\n"
                + "Description, and final image\n\n"
                + "Categories are grouped by name. Fields are\n"
                + "also grouped by named\n\n"
                + "Holland codes must be either 'Realistic',\n"
                + "'Investigative', 'Artistic'\n"
                + "'Social', 'Enterprising', or 'Conventional'\n\n"
                + "Descriptions are simply lines of text\n\n"
                + "The final image must be the name of an\n"
                + "image inside the assets folder\n"
                + "(i.e. if I have an image 'janitor.jpg' inside the assets folder,\n"
                + "that's what is put in the column.)\n\n"
                + "If no final image is specified, the 'default.png' is used instead."
        );
        vbox.getChildren().add(text);

        Button button = new Button();
        button.setText("Open the csv");
        button.setOnAction(event -> {
            try {
                Desktop.getDesktop().open(new File(CareerModelLoader.DEFAULT_CAREERS_CSV_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        vbox.getChildren().add(button);

        // Clear the editor pane and re-populate with the new Nodes
        toolbarBox.getChildren().clear();
        toolbarBox.getChildren().add(vbox);
    }
}
