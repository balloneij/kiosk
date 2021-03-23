package kiosk.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for storing a set of careers to filter by. Used by certain classes to filter which
 * careers appear in a spoke graph. FilterGroupModels named "All" will return all the careers in
 * the LoadedSurveyModel.
 */
public class FilterGroupModel {
    // TODO can these be private?
    public String name;
    public Set<CareerModel> careers;

    public FilterGroupModel(String name, Set<CareerModel> careers) {
        this.name = name;
        this.setCareers(careers);
    }

    public FilterGroupModel(String name, List<CareerModel> careers) {
        this.name = name;
        this.setCareers(careers);
    }

    /**
     * Creates a FilterGroupModel with the provided name and career names.
     * @param name The name of the filter.
     * @param careerNames The names of the careers to include in the filter.
     */
    public FilterGroupModel(String name, String... careerNames) {
        this.name = name;

        // Filter the list of career models based on the provided names
        List<String> careerNameList = Arrays.asList(careerNames);
//        this.careers = Arrays.stream(LoadedSurveyModel.careers)
//            .filter(careerModel -> careerNameList.contains(careerModel.name))
//            .collect(Collectors.toSet());
    }

    public void setCareers(Set<CareerModel> careers) {
        this.careers = careers;
    }

    public void setCareers(List<CareerModel> careers) {
        this.setCareers(new HashSet<>(careers));
    }

    /**
     * Returns the Set of CareerModels associated with the filter. Note that if the filter is
     * named "All", this method will return ALL careers from the LoadedSurveyModel, even if they
     * are not in the Set associated with the filter.
     * @return The Set of CareerModels associated with the filter.
     */
    public Set<CareerModel> getCareers() {
        // TODO
//        if (name.equals("All")) {
//            // Return all careers in the survey if this is the "All" filter
//            return new HashSet<>(Arrays.asList(LoadedSurveyModel.careers));
//        } else {
//            return careers;
//        }
        return new HashSet<CareerModel>(Arrays.asList(new CareerModel(), new CareerModel()));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
