package graphics;

import java.util.Arrays;
import kiosk.Kiosk;
import kiosk.models.ButtonModel;
import kiosk.scenes.ButtonControl;
import processing.core.PConstants;

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
    private double[] weights;

    private final int[] rgbColor1 = new int[] { 0, 0, 255 };
    private final int[] rgbColor2 = new int[] { 255, 0, 0 };

    private boolean wasInit = false;
    private boolean initWarningPrinted = false;

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
            double weight = normalWeights[i];
            final float radius = (float) lerp(minButtonRadius, maxButtonRadius, weight);
            final float diameter = radius * 2;
            final double centerSquareSize = Math.sqrt(Math.pow(diameter, 2) / 2);
            final float buttonX = (float)
                    (centerX + Math.cos(STARTING_ANGLE + angleDelta * i) * (spokeLength + radius));
            final float buttonY = (float)
                    (centerY + Math.sin(STARTING_ANGLE + angleDelta * i) * (spokeLength + radius));

            // Create the ButtonControl
            ButtonModel button = buttons[i];
            button.isCircle = true;
            button.rgb = lerpColor(rgbColor1, rgbColor2, weight);
            if (button.image != null) {
                button.image.width = (int) centerSquareSize;
                button.image.height = (int) centerSquareSize;
            }

            buttonControls[i] = new ButtonControl(button,
                    (int) (buttonX - radius), (int) (buttonY - radius),
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
     * Initializes the SpokeGraph.
     * @param sketch Kiosk to initialize SpokeGraph for.
     */
    public void init(Kiosk sketch) {
        for (ButtonControl buttonControl : buttonControls) {
            buttonControl.init(sketch);
        }
        wasInit = true;
    }

    /**
     * Draw the spoke graph.
     * @param sketch to draw to
     */
    public void draw(Kiosk sketch) {
        checkInit(); // Prints a warning if the SpokeGraph wasn't initialized

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

        GraphicsUtil.textWithOutline(this.centerText,
                centerX, centerY,
                centerSquareSize, centerSquareSize, sketch);
    }

    private static double lerp(double a, double b, double amount) {
        return (1 - amount) * a + amount * b;
    }

    /**
     * Interpolate between two colors.
     * @param color1 the low, 0% color
     * @param color2 the high, 100% color
     * @param amount to interpolate between the two colors
     * @return the interpolated color
     */
    private static int[] lerpColor(int[] color1, int[] color2, double amount) {
        return new int[] {
            (int) lerp(color1[0], color2[0], amount),
            (int) lerp(color1[1], color2[1], amount),
            (int) lerp(color1[2], color2[2], amount)
        };
    }

    public ButtonControl[] getButtonControls() {
        return buttonControls;
    }

    /**
     * Updates the spoke weights with the provided values.
     * @param weights The new weights to apply to the spokes.
     */
    public void setWeights(double[] weights) {
        this.weights = weights;
        double[] normalWeights = normalizeWeights(weights);

        for (int i = 0; i < buttonControls.length; i++) {
            double weight = normalWeights[i];
            final float radius = (float) lerp(minButtonRadius, maxButtonRadius, weight);
            buttonControls[i].setWidth((int) radius * 2);
            buttonControls[i].setHeight((int) radius * 2);
            buttonControls[i].getModel().rgb = lerpColor(rgbColor1, rgbColor2, weight);
        }
    }

    /**
     * Visually disable buttons.
     * @param disabled true or false
     */
    public void setDisabled(boolean disabled) {
        if (disabled) {
            for (ButtonControl button : buttonControls) {
                button.setDisabled(true);
            }
        } else {
            for (ButtonControl button : buttonControls) {
                button.setDisabled(false);
            }
        }
    }

    /**
     * Prints a warning & stack trace if the SpokeGraph has not been initialized. Meant to be called
     * in the draw method.
     */
    private void checkInit() {
        // Print warning if SpokeGraph was not init and warning hasn't been printed
        if (!wasInit && !initWarningPrinted) {
            initWarningPrinted = true;
            throw new IllegalStateException("SpokeGraph was not init! Call SpokeGraph.init() in "
                + "the init method of the scene!");
        }
    }
}
