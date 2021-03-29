package kiosk.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for storing a set of careers to filter by. Used by certain classes to filter which
 * careers appear in a spoke graph. FilterGroupModels named "All" will return all the available
 * careers LoadedSurveyModel.
 */
public class FilterGroupModel {
    public String name;
    public Set<String> careerNames;

    public FilterGroupModel() {
        name = "none";
        careerNames = new HashSet<>();
    }

    public FilterGroupModel(String name, Set<String> careerNames) {
        this.name = name;
        this.setCareerNames(careerNames);
    }

    public FilterGroupModel(String name, List<String> careerNames) {
        this.name = name;
        this.setCareerNames(careerNames);
    }

    /**
     * Creates a FilterGroupModel with the provided name and career names.
     * @param name The name of the filter.
     * @param careerNames The names of the careers to include in the filter.
     */
    public FilterGroupModel(String name, String... careerNames) {
        this.name = name;
        this.setCareerNames(Arrays.asList(careerNames));
    }

    public void setCareerNames(Set<String> careerNames) {
        this.careerNames = careerNames;
    }

    public void setCareerNames(List<String> careers) {
        this.setCareerNames(new HashSet<>(careers));
    }

    public Set<String> getCareerNames() {
        return careerNames;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addCareer(String career) {
        this.careerNames.add(career);
    }

    /**
     * Returns a filtered version of the original List, containing only CareerModels whose names
     * are in the FilterGroupModel. FilterGroupModels named "All" will not apply any filtering.
     * @param original The List of CareerModels to filter.
     * @return Filtered version of the original List. FilterGroupModels named "All" will not
     *     apply any filtering.
     */
    public List<CareerModel> filter(List<CareerModel> original) {
        List<CareerModel> filtered = new ArrayList<>(original); // Copy the original

        // If filter is named "All", do not modify the copy
        // Else perform an intersection of the copy and the careerNames
        if (!name.equals("All")) {
            filtered = filtered.stream()
                .filter(careerModel -> careerNames.contains(careerModel.name))
                .collect(Collectors.toList());
        }

        return filtered;
    }

    /**
     * Returns a filtered version of the original array, containing only CareerModels whose names
     * are in the FilterGroupModel. FilterGroupModels named "All" will not apply any filtering.
     * @param original The array of CareerModels to filter.
     * @return Filtered version of the original array. FilterGroupModels named "All" will not
     *     apply any filtering.
     */
    public CareerModel[] filter(CareerModel[] original) {
        return filter(Arrays.asList(original)).toArray(new CareerModel[] {});
    }

    @Override
    public String toString() {
        return name;
    }
}
