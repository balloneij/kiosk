package kiosk.models;

public final class ButtonModel {

    public String text;
    public String target;
    public boolean isCircle;
    public boolean isRound;

    public ButtonModel() {
        this.text = "null";
        this.target = "null";
        this.isCircle = false;
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
}
