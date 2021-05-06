package kiosk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import kiosk.models.CareerModel;
import kiosk.models.FilterGroupModel;

public class UserScore {

    private int realistic = 0;
    private int investigative = 0;
    private int artistic = 0;
    private int social = 0;
    private int enterprising = 0;
    private int conventional = 0;

    private final HashMap<String, CareerModel> allCareers = new HashMap<>();
    private final HashMap<String, CareerModel> includedCareers = new HashMap<>();
    private final LinkedList<ScoreOperation> history = new LinkedList<>();

    /**
     * Create a new user score object.
     * @param careers that are available to the user
     */
    public UserScore(CareerModel[] careers) {
        if (careers != null) {
            for (CareerModel career : careers) {
                this.allCareers.put(career.name, career);
                this.includedCareers.put(career.name, career);
            }
        }
    }

    /**
     * Apply a category and filter to the user score. Intended to
     * be called every time the user makes a selection in the survey.
     * @param category score to add to
     * @param filterOrNull careers to filter out. Null if none
     */
    public void apply(Riasec category, FilterGroupModel filterOrNull) {
        // Apply RIASEC score
        this.add(category);

        // Apply filter
        if (filterOrNull != null) {
            // Filter careers and store the removed careers in a list
            // for undo history
            final LinkedList<String> removedCareers = new LinkedList<>();
            Set<String> includedKeySet = includedCareers.keySet();

            includedKeySet.removeIf(career -> {
                if (!filterOrNull.careerNames.contains(career)) {
                    removedCareers.add(career);
                    return true;
                }
                return false;
            });

            // Push to history
            this.history.push(new ScoreOperation(category, removedCareers));
        } else {
            // Push to history
            this.history.push(new ScoreOperation(category, null));
        }
    }

    /**
     * Undo the last apply. Intended to be called every time the user moves
     * backwards in the survey.
     */
    public void undo() {
        if (!this.history.isEmpty()) {
            ScoreOperation operation = this.history.pop();

            // Careers were removed in the previous operation, so add them back
            if (operation.careersRemoved != null) {
                for (String career : operation.careersRemoved) {
                    includedCareers.put(career, allCareers.get(career));
                }
            }

            // Undo RIASEC operation
            this.subtract(operation.category);
        }
    }

    /**
     * Add a point from the specified category. Specify
     * whether it will affect history or not.
     * @param category to add to
     */
    private void add(Riasec category) {
        switch (category) {
            case Realistic:
                this.realistic++;
                break;
            case Investigative:
                this.investigative++;
                break;
            case Artistic:
                this.artistic++;
                break;
            case Social:
                this.social++;
                break;
            case Enterprising:
                this.enterprising++;
                break;
            case Conventional:
                this.conventional++;
                break;
            case None:
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + category);
        }
    }

    /**
     * Remove a point from the specified category. Specify
     * whether it will affect history or not.
     * @param category to remove from
     */
    private void subtract(Riasec category) {
        switch (category) {
            case Realistic:
                this.realistic--;
                break;
            case Investigative:
                this.investigative--;
                break;
            case Artistic:
                this.artistic--;
                break;
            case Social:
                this.social--;
                break;
            case Enterprising:
                this.enterprising--;
                break;
            case Conventional:
                this.conventional--;
                break;
            case None:
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + category);
        }
    }

    /**
     * Reset user score to zero.
     */
    public void reset() {
        this.realistic = 0;
        this.investigative = 0;
        this.artistic = 0;
        this.social = 0;
        this.enterprising = 0;
        this.conventional = 0;

        this.history.clear();
        for (String career : this.allCareers.keySet()) {
            this.includedCareers.put(career, this.allCareers.get(career));
        }
    }

    public CareerModel[] getCareers() {
        return this.includedCareers.values().toArray(new CareerModel[0]);
    }

    public int getRealistic() {
        return realistic;
    }

    public void setRealistic(int value) {
        realistic = value;
    }

    public int getInvestigative() {
        return investigative;
    }

    public void setInvestigative(int value) {
        investigative = value;
    }

    public int getArtistic() {
        return artistic;
    }

    public void setArtistic(int value) {
        artistic = value;
    }

    public int getSocial() {
        return social;
    }

    public void setSocial(int value) {
        social = value;
    }

    public int getEnterprising() {
        return enterprising;
    }

    public void setEnterprising(int value) {
        enterprising = value;
    }

    public int getConventional() {
        return conventional;
    }

    public void setConventional(int value) {
        conventional = value;
    }

    @Override
    public String toString() {
        return "UserScore{"
                + "realistic=" + realistic
                + ", investigative=" + investigative
                + ", artistic=" + artistic
                + ", social=" + social
                + ", enterprising=" + enterprising
                + ", conventional=" + conventional
                + ", history=" + history
                + "}";
    }

    /**
     * Gets the score for the provided category.
     * @param category The Riasec category to get the user's score for
     * @return The user's score in the provided category.
     */
    public int getCategoryScore(Riasec category) {
        int score;
        switch (category) {
            case Realistic:
                score = this.realistic;
                break;
            case Investigative:
                score = this.investigative;
                break;
            case Artistic:
                score = this.artistic;
                break;
            case Social:
                score = this.social;
                break;
            case Enterprising:
                score = this.enterprising;
                break;
            case Conventional:
                score = this.conventional;
                break;
            default:
                score = 0;
                break;
        }

        return score;
    }

    private static class ScoreOperation {
        private final Riasec category;
        private final LinkedList<String> careersRemoved;

        private ScoreOperation(Riasec category, LinkedList<String> careersRemoved) {
            this.category = category;
            this.careersRemoved = careersRemoved;
        }
    }
}
