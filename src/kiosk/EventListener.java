package kiosk;

/**
 * Event listener for input events from Processing.
 */
public interface EventListener {
    /**
     * Method code to run on the input event.
     * @param arg event data
     */
    void invoke(Object arg);
}
