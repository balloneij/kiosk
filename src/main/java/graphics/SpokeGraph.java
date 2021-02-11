package graphics;

import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import kiosk.scenes.ButtonControl;
import processing.core.PConstants;

import java.lang.reflect.Array;
import java.util.Arrays;

public class SpokeGraph {

    // The minimum button size is a percentage of the max button size
    private static final double MIN_BUTTON_RADIUS_RATIO = 0.8;

    private static final float SPOKE_THICKNESS = 2;
    private static final double STARTING_ANGLE = Math.PI / 2;

    private final float centerX;
    private final float centerY;

    private final double maxButtonRadius;
    private final double minButtonRadius;
    private final String centerText;

    private final float centerSquareSize;

    private final ButtonControl[] buttonControls;
    private final double[] weights;

    /**
     * Create a spoke graph.
     * @param size the square to fit the graph in
     * @param x top left corner
     * @param y top right corner
     * @param centerText text of the center wheel
     * @param buttons to create a spoke graph off of
     */
    public SpokeGraph(double size, double x, double y, String centerText, ButtonModel[] buttons) {
        this(size, x, y, centerText, buttons, new double[buttons.length]);
        Arrays.fill(weights, 0); // Set all weights to 0
    }

    /**
     * Create a spoke graph.
     * @param size the square to fit the graph in
     * @param x top left corner
     * @param y top right corner
     * @param centerText text of the center wheel
     * @param buttons to create a spoke graph off of
     */
    public SpokeGraph(double size, double x, double y, String centerText, ButtonModel[] buttons,
                      double[] weights) {
        this.centerText = centerText;
        this.centerX = (float) (x + size / 2);
        this.centerY = (float) (y + size / 2);
        this.weights = weights;

        double[] normalWeights = normalizeWeights(weights);

        // Worst case Spokegraph
        //      O
        //      |
        // O -- O -- O   8 maxButtonRadius across, and 8 maxButtonRadius tall
        //      |
        //      O
        // Diameter of two circle + two spoke lengths = 8 total radius'
        // Therefore, we divide our total available space by 8, and the max
        // size of the entire graph will be confined to that area
        this.maxButtonRadius = size / 8.0;
        this.minButtonRadius = maxButtonRadius * MIN_BUTTON_RADIUS_RATIO;

        double spokeLength = maxButtonRadius * 2;
        double angleDelta = (2 * Math.PI) / buttons.length;

        // The text has to fit inside the largest square possible inside the circle
        // so we're using the Pythagorean theorem to get the sides of the square, and
        // the diameter is the hypotenuse.
        // Additionally, we're using the diameter of the minimumButtonRadius in
        // order to keep all the font sizes consistent
        // Images must fit inside this circle too
        this.centerSquareSize = (float) Math.sqrt(Math.pow(minButtonRadius * 2, 2) / 2);

        this.buttonControls = new ButtonControl[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            final float radius = (float) lerp(minButtonRadius, maxButtonRadius, normalWeights[i]);
            final float diameter = radius * 2;
            final float buttonX = (float)
                    (centerX + Math.cos(STARTING_ANGLE + angleDelta * i) * (spokeLength + radius));
            final float buttonY = (float)
                    (centerY + Math.sin(STARTING_ANGLE + angleDelta * i) * (spokeLength + radius));

            // Create the ButtonControl
            ButtonModel button = buttons[i];
            button.isCircle = true;
            buttonControls[i] = new ButtonControl(button,
                    (int) (buttonX - radius), (int) (buttonY - radius),
                    (int) diameter, (int) diameter,
                    (int) radius);
        }
    }

    private double[] normalizeWeights(double[] weights) {
        // Find the min and max weights in the weights array
        double minWeight = Double.POSITIVE_INFINITY;
        double maxWeight = Double.NEGATIVE_INFINITY;
        for (double w : weights) {
            if (w < minWeight) {
                minWeight = w;
            }
            if (w > maxWeight) {
                maxWeight = w;
            }
        }


        double[] newWeights = new double[weights.length];
        if (minWeight != maxWeight) {
            for (int i = 0; i < weights.length; i++) {
                // Normalize the weights to be from 0-1
                newWeights[i] = (weights[i] - minWeight) / (maxWeight - minWeight);
            }
        } else {
            // If the min and max are the same, just use 0.5 for all
            Arrays.fill(newWeights, 0.5);
        }

        return newWeights;
    }

    /**
     * Draw the spoke graph.
     * @param sketch to draw to
     */
    public void draw(Kiosk sketch) {
        // Draw the buttons and spokes
        for (ButtonControl buttonControl : this.buttonControls) {
            sketch.stroke(255);
            sketch.strokeWeight(SPOKE_THICKNESS);
            sketch.line(centerX, centerY,
                    buttonControl.getCenterX(), buttonControl.getCenterY());

            buttonControl.draw(sketch);
        }

        // Set draw modes
        sketch.ellipseMode(PConstants.CENTER);
        sketch.textAlign(PConstants.CENTER, PConstants.CENTER);
        sketch.noStroke();

        // Draw the center circle
        sketch.fill(0);
        sketch.ellipse(centerX, centerY,
                (float) (maxButtonRadius + minButtonRadius),
                (float) (maxButtonRadius + minButtonRadius));
        sketch.fill(255);
        sketch.text(this.centerText,
                centerX, centerY,
                centerSquareSize, centerSquareSize);
    }

    private static double lerp(double a, double b, double amount) {
        return (1 - amount) * a + amount * b;
    }

    public ButtonControl[] getButtonControls() {
        return buttonControls;
    }

    /**
     * Visually disable buttons.
     * @param disabled true or false
     */
    public void setDisabled(boolean disabled) {
        if (disabled) {
            for (var button : buttonControls) {
                button.setDisabled(true);
            }
        } else {
            for (var button : buttonControls) {
                button.setDisabled(false);
            }
        }
    }
}
