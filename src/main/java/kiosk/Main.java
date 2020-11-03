package kiosk;

import java.util.Arrays;

public class Main {

    /**
     * Program entry point.
     * @param args The path to a survey file to be loaded.
     */
    public static void main(String[] args) {
        if (System.getProperty("java.version").length() < 3) {
            System.setProperty("java.version", System.getProperty("java.version") + ".0");
        }

        Kiosk kiosk;

        if(args.length > 0){
            if(args.length > 1){
                System.out.println("Multiple survey files specified, the first will be used.");
            }
            System.out.println("Starting kiosk with " + args[0]);
            kiosk = new Kiosk(args[0]);
        } else {
            System.out.println("No survey file specified. A survey file can be specified " +
                    "by including its filepath after the command to start the kiosk.");
            kiosk = new Kiosk("");
        }

        kiosk.run();
    }
}
