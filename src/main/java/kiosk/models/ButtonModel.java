package kiosk.models;

public final class ButtonModel {

    public String text;
    public String target;
    public boolean isCircle;
    public int[] rgb;

    public ButtonModel() {
        this.text = "null";
        this.target = "null";
        this.isCircle = false;
        this.rgb = new int[] { 112, 191, 76 };
    }

    /**
     * Constructs a button model.
     * @param text to display inside the button
     * @param target the scene this button links to
     */
    public ButtonModel(String text, String target) {
        this.text = text;
        this.target = target;
    }

    public ButtonModel deepCopy() {
        ButtonModel newButton = new ButtonModel();
        newButton.target = this.target;
        newButton.text = this.text;
        newButton.isCircle = this.isCircle;
        newButton.rgb = this.rgb.clone();
        return newButton;
    }
}
