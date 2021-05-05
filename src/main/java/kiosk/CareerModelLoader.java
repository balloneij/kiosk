package kiosk;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import kiosk.models.CareerModel;

public class CareerModelLoader {

    public static final String DEFAULT_IMAGE_PATH = "assets/default.png";
    private static final int COLUMN_COUNT = 6;

    private final File csvFile;
    private final Doctor doctor;
    private final HashMap<String, Integer> categories;
    private final HashMap<String, Integer> fields;

    /**
     * Loads careers, and validates it.l
     * @param csvFile to load from
     */
    public CareerModelLoader(File csvFile) {
        this.csvFile = csvFile;
        this.doctor = new Doctor();
        this.categories = new HashMap<>();
        this.fields = new HashMap<>();
    }

    public boolean hasIssues() {
        return this.doctor.hasIssues();
    }

    public String getIssuesSummary() {
        return this.doctor.getSummary();
    }

    /**
     * Loads the careers. Does not throw on IO exceptions. Will
     * return a valid careers array (even if it's a length of 0).
     * @return a list of careers loaded from disk
     */
    public CareerModel[] load() {
        LinkedList<CareerModel> careers = new LinkedList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            for (int rowNumber = 0; reader.peek() != null; rowNumber++) {
                String[] row = reader.readNext();

                if (validateRow(rowNumber, row)) {
                    // Create a careerModel and conform values
                    CareerModel career = new CareerModel();
                    career.category = conformCategory(rowNumber, row[0]);
                    career.field = conformField(rowNumber, row[1]);
                    career.riasecCategory = conformHollandCode(rowNumber, row[2]);
                    career.name = conformName(rowNumber, row[3]);
                    career.description = conformDescription(rowNumber, row[4]);
                    career.imagePath = conformImagePath(rowNumber, row[5]);

                    // Add to list
                    careers.add(career);
                }
            }

        } catch (IOException | CsvValidationException e) {
            // Do nothing
        }

        // Warn the user that there are no careers
        if (careers.size() == 0) {
            doctor.diagnose("The careers file is empty at " + csvFile.getPath(),
                    "Add careers. Check sample_careers.csv for inspiration");
        }

        // Review all fields and categories. If there is only
        // one career in a field or category, it could
        // be a typo.
        validateCategories();
        validateFields();

        // Convert to primitive array and return
        CareerModel[] careersResult = new CareerModel[careers.size()];
        for (int i = 0; !careers.isEmpty(); i++) {
            careersResult[i] = careers.pop();
        }
        return careersResult;
    }

    private boolean validateRow(int rowNumber, String[] row) {
        // Ignore empty lines
        if (row.length == 1 && row[0].equals("")) {
            return false;
        }

        // Incorrect number of columns
        if (row.length != COLUMN_COUNT) {
            String problem = "Row " + rowNumber + " of " + csvFile.getName()
                    + " should have " + COLUMN_COUNT + " columns.";
            String solution = "Make sure there is a column for "
                    + "category, field, holland code, name, description, and image path.";
            doctor.diagnose(problem, solution);
            return false;
        }

        // Row is gucci
        return true;
    }

    private String conformCategory(int rowNumber, String category) {
        if (category.isEmpty()) {
            String problem = "Row " + rowNumber + " of " + csvFile.getName()
                    + " should not have a blank category name.";
            String solution = "Add a category name";
            doctor.diagnose(problem, solution);

            return "CategoryRow" + rowNumber;
        }

        categories.merge(category, 1, Integer::sum);
        return category;
    }

    private String conformField(int rowNumber, String field) {
        if (field.isEmpty()) {
            String problem = "Row " + rowNumber + " of " + csvFile.getName()
                    + " should not have a blank field name.";
            String solution = "Add a field name";
            doctor.diagnose(problem, solution);

            return "FieldRow" + rowNumber;
        }

        fields.merge(field, 1, Integer::sum);
        return field;
    }

    private Riasec conformHollandCode(int rowNumber, String hollandCode) {
        Riasec value = Riasec.None;
        try {
            value = Riasec.valueOf(hollandCode);
        } catch (IllegalArgumentException e) {
            String problem = "Row " + rowNumber + " of " + csvFile.getName()
                    + " does not have a correct holland code.";
            String solution = "Specify a holland code of "
                    + Riasec.Realistic.name() + ", "
                    + Riasec.Investigative.name() + ", "
                    + Riasec.Artistic.name() + ", "
                    + Riasec.Social.name() + ", "
                    + Riasec.Enterprising.name() + ", or "
                    + Riasec.Conventional.name();
            doctor.diagnose(problem, solution);
        }

        return value;
    }

    private String conformName(int rowNumber, String name) {
        if (name.isEmpty()) {
            String problem = "Row " + rowNumber + " of " + csvFile.getName()
                    + " should not have a blank career name.";
            String solution = "Add a career name";
            doctor.diagnose(problem, solution);

            return "CareerNameRow" + rowNumber;
        }

        return name;
    }

    private String conformDescription(int rowNumber, String description) {
        if (description.isEmpty()) {
            String problem = "Row " + rowNumber + " of " + csvFile.getName()
                    + " should not have a blank career description.";
            String solution = "Add a career description";
            doctor.diagnose(problem, solution);

            return "Career Description Row" + rowNumber;
        }

        return description;
    }

    private String conformImagePath(int rowNumber, String imagePath) {
        // No image specified
        if (imagePath.isEmpty()) {
            return DEFAULT_IMAGE_PATH;
        }

        // Make sure the image exists
        File file = new File(imagePath);
        if (file.exists()) {
            return imagePath;
        } else {
            String problem = "Row " + rowNumber + " of " + csvFile.getName()
                    + " image not in assets folder";
            String solution = "Add an image to " + file.getAbsolutePath();
            doctor.diagnose(problem, solution);
            return DEFAULT_IMAGE_PATH;
        }
    }

    private void validateCategories() {
        for (String category : categories.keySet()) {
            if (categories.get(category) == 1) {
                String problem = "There is only one career in the category '"
                        + category + "'. Is this intentional?";
                String solution = "Grouping careers under the same category let you filter them";
                doctor.diagnose(problem, solution);
            }
        }
    }

    private void validateFields() {
        for (String field : fields.keySet()) {
            if (fields.get(field) == 1) {
                String problem = "There is only one career in the field '"
                        + field + "'. Is this intentional?";
                String solution = "Grouping careers under the same field let you filter them";
                doctor.diagnose(problem, solution);
            }
        }
    }
}
