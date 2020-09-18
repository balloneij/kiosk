package kiosk;

/**
 * Event callback for input events from Processing.
 */
public interface EventCallback {
    /**
     * Method code to run on the input event.
     * @param arg event data
     */
    void invoke(Object arg);
}
