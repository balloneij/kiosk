package kiosk.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for storing a set of careers to filter by. Used by certain classes to filter which
 * careers appear in a spoke graph. FilterGroupModels named "All" will return all the available
 * careers LoadedSurveyModel.
 */
public class FilterGroupModel {
    public Set<String> careerNames;

    public FilterGroupModel() {
        // Left blank for the XML Encoder
    }

    /**
     * Factory method.
     * @return a default FilterGroupModel
     */
    public static FilterGroupModel create() {
        FilterGroupModel model = new FilterGroupModel();
        model.careerNames = new HashSet<>();
        return model;
    }

    /**
     * Create a deep copy and return it.
     * @return new filter
     */
    public FilterGroupModel deepCopy() {
        FilterGroupModel newModel = FilterGroupModel.create();
        newModel.careerNames.addAll(this.careerNames);
        return newModel;
    }
}
