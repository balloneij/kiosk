import kiosk.models.*;
import org.junit.jupiter.api.Test;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Important note!
 * When testing models, change at least one attribute. The
 * XML encoder/decoder takes shortcuts when you don't change
 * any of the default values.
 *
 * If reading or writing is an issue, make sure the default
 * constructor for the model is blank and there are no class
 * variables being defined at the class level
 *
 * i.e.
 *
 * Potentially bad:
 * public class Dog {
 *     public int age = 0;
 *     public String name;
 *
 *     public Dog() {
 *         name = "Default Name"
 *     }
 * }
 *
 * Gooder:
 * public class Dog {
 *     public int age;
 *     public String name;
 *
 *     public Dog() {
 *         // Left blank for XML Encoder
 *     }
 *
 *     public static Dog create() {
 *         Dog dog = new Dog();
 *         dog.age = 0;
 *         dog.name = "Default Name";
 *         return dog;
 *     }
 * }
 */
public class ModelTest {

    @Test
    public void loadedSurveyModel() {
        // Arrange
        LoadedSurveyModel model = LoadedSurveyModel.createSampleSurvey();
        ModelWriter<LoadedSurveyModel> writer = new ModelWriter<>(model);

        // Act
        LoadedSurveyModel modelRead = writer.test();

        // Assert
        assertEquals(model.careers.length, modelRead.careers.length);
        assertEquals(model.filters.length, modelRead.filters.length);
        assertEquals(model.rootSceneId, modelRead.rootSceneId);
    }

    @Test
    public void careerPathwaySceneModel() {
        // Arrange
        CareerPathwaySceneModel model = new CareerPathwaySceneModel();
        model.filter = new FilterGroupModel();
        model.filter.name = "Joe Mama";
        ModelWriter<CareerPathwaySceneModel> writer = new ModelWriter<>(model);

        // Act
        CareerPathwaySceneModel modelRead = writer.test();

        // Assert
        assertEquals(model.filter.name, modelRead.filter.name);
    }

    @Test
    public void filterGroupModel() {
        // Arrange
        FilterGroupModel model = new FilterGroupModel();
        model.name = "test test test";
        ModelWriter<FilterGroupModel> writer = new ModelWriter<>(model);

        // Act
        FilterGroupModel modelRead = writer.test();

        // Assert
        assertEquals(model.name, modelRead.name);
    }

    @Test
    public void careerModel() {
        // Arrange
        CareerModel model = new CareerModel();
        model.name = "test test test";
        ModelWriter<CareerModel> writer = new ModelWriter<>(model);

        // Act
        CareerModel modelRead = writer.test();

        // Assert
        assertEquals(model.name, modelRead.name);
    }

    private static class ModelWriter<T> {

        private final String modelName;
        private final T model;

        public ModelWriter(T model) {
            this.modelName = model.getClass().getName();
            this.model = model;
        }

        private T test() {
            File file = null;
            T model;
            try {
                file = writeXML(this.model);
                model = readXML(file);
            } finally {
                if (file != null) {
                    clean(file);
                }
            }
            return model;
        }

        private File writeXML(T model) {
            File file = null;
            XMLEncoder encoder = null;
            try {
                file = File.createTempFile("tmp" + UUID.randomUUID(), ".xml", new File("./"));
                encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
                encoder.writeObject(model);
            } catch (IOException e) {
                fail(modelName + " could not be written");
            } finally {
                if (encoder != null) {
                    encoder.close();
                }
            }
            return file;
        }

        private T readXML(File file) {
            T model = null;
            try (XMLDecoder decoder = new XMLDecoder(
                    new BufferedInputStream(new FileInputStream(file)))) {
                decoder.setExceptionListener(e -> {
                    e.printStackTrace();
                    fail(modelName + " could not be read");
                });
                Object surveyObject = decoder.readObject();
                model = (T) surveyObject;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                fail(modelName + " could not be read");
            }
            return model;
        }

        private void clean(File file) {
            if (file.exists()) {
                assertTrue(file.delete());
            }
        }
    }
}
