package kiosk;

/**
 * Event listener for input events from Processing.
 */
public interface EventListener<T> {
    /**
     * Method code to run on the input event.
     * @param arg event data
     */
    void invoke(T arg);
}
