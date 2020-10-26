package kiosk.models;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoadedSurveyModel implements Serializable {

    public SceneModel[] scenes;

    public LoadedSurveyModel() {
        scenes = new SceneModel[]{};
    }

    public LoadedSurveyModel(List<SceneModel> initialScenes) {
        this.scenes = new SceneModel[initialScenes.size()];
        this.scenes = initialScenes.toArray(scenes);
    }

    public static void main(String args[]) {
        var catsOrDogs = new PromptSceneModel("Which do you prefer?", new ButtonModel[]{
                new ButtonModel("Cats", "0"),
                new ButtonModel("Dogs", "0")
        }, false);

        var coffee = new PromptSceneModel("How do you like your coffee?", new ButtonModel[]{
                new ButtonModel("Black", "1"),
                new ButtonModel("Blacker", "1")
        }, true);

        var yogurt = new PromptSceneModel("Are you supposed to stir greek yogurt?",
                new ButtonModel[] {
                        new ButtonModel("No", "2")
                }, false);

        var caps = new PromptSceneModel("Caps! Caps for sale!", new ButtonModel[]{
                new ButtonModel("Fifty", "3"),
                new ButtonModel("cents", "3"),
                new ButtonModel("a", "3"),
                new ButtonModel("cap", "3"),
                new ButtonModel("!", "3")
        }, true);

        var transitionToCoffee = new WaveTransitionSceneModel(coffee.getId(), false, "0");
        var transitionToYogurt = new WaveTransitionSceneModel(yogurt.getId(), true, "1");
        var transitionToCaps = new WaveTransitionSceneModel(caps.getId(), false, "2");
        var transitionToCatsOrDogs = new WaveTransitionSceneModel(catsOrDogs.getId(), true, "3");

        var initialScenes = new ArrayList<SceneModel>();
        initialScenes.add(caps);
        initialScenes.add(transitionToYogurt);
        initialScenes.add(yogurt);
        initialScenes.add(transitionToCoffee);
        initialScenes.add(coffee);
        initialScenes.add(transitionToCatsOrDogs);
        initialScenes.add(catsOrDogs);
        initialScenes.add(transitionToCaps);

        var mySurvey = new LoadedSurveyModel(initialScenes);

        // Writing
        try {
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("test.xml")));
            encoder.writeObject(mySurvey);
            encoder.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Reading
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("test.xml")));
            var myObj = decoder.readObject();
            decoder.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
