package kiosk.models;

import kiosk.Riasec;

public final class ButtonModel {

    public String text;
    public String target;
    public boolean isCircle;
    public boolean noButton = false;
    public int[] rgb = new int[] { 112, 191, 76 };
    // Optional. Null is a valid value
    public ImageModel image;
    public Riasec category = Riasec.None;

    private static int buttonCount = 1; // Un-named buttons will be numbered

    /**
     * Create a new button model with default values.
     * They will need to overwritten
     */
    public ButtonModel() {
        this("button " + buttonCount++, "null");
    }

    /**
     * Constructs a button model.
     * @param text to display inside the button
     * @param target the scene this button links to
     */
    public ButtonModel(String text, String target) {
        this.text = text;
        this.target = target;
        this.isCircle = false;
        this.image = null;
    }
    
    /**
     * Constructs a button model. Let's you set a RIASEC cateogry.
     * @param text to display inside the button
     * @param target the scene this button links to
     * @param category RIASEC category to add to the user score
     */
    public ButtonModel(String text, String target, Riasec category) {
        this.text = text;
        this.target = target;
        this.category = category;
        this.isCircle = false;
        this.image = null;
    }

    /**
     * Create a deep copy of the button model. There should
     * be zero shared references between the copy and the original.
     * It can modified as pleased.
     * @return copy of the current button model
     */
    public ButtonModel deepCopy() {
        ButtonModel newButton = new ButtonModel();
        newButton.target = this.target;
        newButton.text = this.text;
        newButton.isCircle = this.isCircle;
        newButton.rgb = this.rgb.clone();
        newButton.image = this.image == null ? null : this.image.deepCopy();
        newButton.category = this.category;
        return newButton;
    }
}
