package kiosk.models;

public class ButtonModel {

    public String text;
    public SceneModel target;

    /**
     * Constructs a button model.
     * @param text to display inside the button
     * @param target the scene this button links to
     */
    public ButtonModel(String text, SceneModel target) {
        this.text = text;
        this.target = target;
    }
}
