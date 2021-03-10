package kiosk.models;

import java.util.Arrays;
import java.util.List;

/**
 * Class for storing a list of careers to filter by. Used to filter which careers appear in a
 * spoke graph.
 */
public class FilterGroupModel {
    private List<String> careers;

    public FilterGroupModel(List<String> careers) {
        this.setCareers(careers);
    }

    public FilterGroupModel(String... careers) {
        this(Arrays.asList(careers));
    }

    public void setCareers(List<String> careers) {
        this.careers = careers;
    }

    public List<String> getCareers() {
        return careers;
    }
}
