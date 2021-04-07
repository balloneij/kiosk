package kiosk;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Settings {

    public static final String DEFAULT_SAVE_PATH = "settings.xml";

    public int timeoutMillis;
    public int sceneAnimationFrames;
    public int buttonAnimationFrames;
    public int buttonAnimationLengthFrames;
    public double buttonAnimationIntensity;
    public int screenW;
    public int screenH;
    public boolean fullScreenDesired;

    /**
     * Default constructor.
     */
    public Settings() {
        this(true);
    }

    /**
     * Default Constructor. This is used whenever
     * there is an error in the settings' xml
     * file. The reason that "weird" values are
     * used is because if these default values are
     * what are actually used, they will not appear
     * to be written out in the xml file.
     * @param fullScreenDesired if this kiosk should be made fullscreen or not
     */
    public Settings(boolean fullScreenDesired) {
        timeoutMillis = 60000;
        sceneAnimationFrames = 0;
        buttonAnimationFrames = 80;
        buttonAnimationLengthFrames = 20;
        buttonAnimationIntensity = buttonAnimationFrames * buttonAnimationLengthFrames / 2.0;
        this.fullScreenDesired = fullScreenDesired;
        if (this.fullScreenDesired) {
            try {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                screenW = (int) screenSize.getWidth();
                screenH = (int) screenSize.getHeight();
            } catch (HeadlessException e) {
                screenW = 1280;
                screenH = 720;
            }
        } else {
            screenW = 1280;
            screenH = 720;
        }
    }

    /**
     * Writes settings to XML. Meant to be used in the editor
     * @return true if successful
     */
    public boolean writeSettings() {
        try (XMLEncoder encoder = new XMLEncoder(
                new BufferedOutputStream(new FileOutputStream(new File(DEFAULT_SAVE_PATH))))) {
            encoder.writeObject(this);
            return true;
        } catch (FileNotFoundException exc) {
            return false;
        }
    }

    /**
     * Default constructor.
     * @return a valid settings object (i.e. never null)
     */
    public static Settings readSettings() {
        return readSettings(false);
    }

    /**
     * Reads the settings as XML from a
     * hardcoded location.
     * @param fullScreenDesired if this kiosk should be made fullscreen or not
     * @return a valid settings object (i.e. never null)
     */
    public static Settings readSettings(boolean fullScreenDesired) {
        try (XMLDecoder decoder = new XMLDecoder(
                new BufferedInputStream(new FileInputStream(new File(DEFAULT_SAVE_PATH))))) {
            return (Settings) decoder.readObject();
        } catch (FileNotFoundException | ClassCastException exc) {
            return new Settings(fullScreenDesired);
        }
    }
}
