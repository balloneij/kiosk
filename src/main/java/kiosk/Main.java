package kiosk;

public class Main {

    /**
     * Program entry point.
     * @param args TODO: Load the survey from provided path
     */
    public static void main(String[] args) {
        if (System.getProperty("java.version").length() < 3) {
            System.setProperty("java.version", System.getProperty("java.version") + ".0");
        }
        Kiosk kiosk = new Kiosk();
        kiosk.run();
    }
}
