package kiosk.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for storing a list of careers to filter by. Used to filter which careers appear in a
 * spoke graph.
 */
public class FilterGroupModel {
    private Set<String> careers;

    public FilterGroupModel(Set<String> careers) {
        this.setCareers(careers);
    }

    public FilterGroupModel(List<String> careers) {
        this.setCareers(careers);
    }

    public FilterGroupModel(String... careers) {
        this(Arrays.asList(careers));
    }

    public void setCareers(Set<String> careers) {
        this.careers = careers;
    }

    public void setCareers(List<String> careers) {
        this.setCareers(new HashSet<>(careers));
    }

    public Set<String> getCareers() {
        return careers;
    }
}
