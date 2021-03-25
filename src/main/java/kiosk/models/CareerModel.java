package kiosk.models;

import javafx.scene.text.Text;
import kiosk.Riasec;

/**
 * A model representing a job. Includes the job's RIASEC type, the field it's in, it's category,
 * and the name of the job.
 */
public class CareerModel {
    public String name;
    public String category; // Human, Nature, Space, Smart Machines
    public String field; // Computer engineering, biology, education, etc.
    public Riasec riasecCategory;
    public String description;

    public CareerModel() {
        this("Career", Riasec.Artistic, "field", "category", "description from .csv");
    }

    /**
     * Creates a CareerModel with the provided fields.
     * @param name The name of the career.
     * @param riasecCategory The RIASEC category of the career.
     * @param field The field the career is in (computer engineering, biology, education, etc.)
     * @param category The category the career is a part of (Human, Nature, Space, Smart Machines)
     * @param description The description of the career itself
     */
    public CareerModel(String name, Riasec riasecCategory, String field, String category, String description) {
        this.name = name;
        this.riasecCategory = riasecCategory;
        this.field = field;
        this.category = category;
        this.description = description;
    }

    public CareerModel deepCopy() {
        return new CareerModel(this.name, this.riasecCategory, this.field, this.category, this.description);
    }
}