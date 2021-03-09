package kiosk;

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
    public int animationFrames;
    public int screenW;
    public int screenH;

    /**
     * Default Constructor. This is used whenever
     * there is an error in the settings' xml
     * file. The reason that "weird" values are
     * used is because if these default values are
     * what are actually used, they will not appear
     * to be written out in the xml file.
     */
    public Settings() {
        timeoutMillis = 60000;
        animationFrames = 150;
        screenW = 1280;
        screenH = 720;
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
     * Reads the settings as XML from a
     * hardcoded location.
     * @return a valid settings object (i.e. never null)
     */
    public static Settings readSettings() {
        try (XMLDecoder decoder = new XMLDecoder(
                new BufferedInputStream(new FileInputStream(new File(DEFAULT_SAVE_PATH))))) {
            return (Settings) decoder.readObject();
        } catch (FileNotFoundException | ClassCastException exc) {
            return new Settings();
        }
    }
}
