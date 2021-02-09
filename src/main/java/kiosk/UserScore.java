package kiosk;

import java.util.LinkedList;
import javafx.util.Pair;

public class UserScore {

    private int realistic = 0;
    private int investigative = 0;
    private int artistic = 0;
    private int social = 0;
    private int enterprising = 0;
    private int conventional = 0;

    private final LinkedList<Pair<UserScoreOperation, Riasec>> history = new LinkedList<>();

    /**
     * Add a point from the specified category.
     * @param category to add to
     */
    public void add(Riasec category) {
        this.add(category, true);
    }

    /**
     * Add a point from the specified category. Specify
     * whether it will affect history or not.
     * @param category to add to
     * @param addToHistory true if it should be undo-able
     */
    public void add(Riasec category, boolean addToHistory) {
        switch (category) {
            case Realistic -> this.realistic++;
            case Investigative -> this.investigative++;
            case Artistic -> this.artistic++;
            case Social -> this.social++;
            case Enterprising -> this.enterprising++;
            case Conventional -> this.conventional++;
            case None -> { }
            default -> throw new IllegalStateException("Unexpected value: " + category);
        }
        if (addToHistory) {
            this.history.push(new Pair<>(UserScoreOperation.Add, category));
        }
    }

    /**
     * Remove a point from the specified category.
     * @param category to remove from
     */
    public void subtract(Riasec category) {
        this.subtract(category, true);
    }

    /**
     * Remove a point from the specified category. Specify
     * whether it will affect history or not.
     * @param category to remove from
     * @param addToHistory true if it should be undo-able
     */
    public void subtract(Riasec category, boolean addToHistory) {
        switch (category) {
            case Realistic -> this.realistic--;
            case Investigative -> this.investigative--;
            case Artistic -> this.artistic--;
            case Social -> this.social--;
            case Enterprising -> this.enterprising--;
            case Conventional -> this.conventional--;
            case None -> { }
            default -> throw new IllegalStateException("Unexpected value: " + category);
        }
        if (addToHistory) {
            this.history.push(new Pair<>(UserScoreOperation.Subtract, category));
        }
    }

    /**
     * Undo the previous operation.
     */
    public void undo() {
        if (this.history.isEmpty()) {
            System.err.println("There are no more UserScore operations to undo!");
            return;
        }

        Pair<UserScoreOperation, Riasec> lastOperationPair = this.history.pop();
        UserScoreOperation operation = lastOperationPair.getKey();
        Riasec category = lastOperationPair.getValue();

        // Do the opposite of the previous operation in order
        // to revert back to the previous state
        if (operation == UserScoreOperation.Add) {
            this.subtract(category, false);
        } else {
            this.add(category, false);
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
    }

    public int getRealistic() {
        return realistic;
    }

    public int getInvestigative() {
        return investigative;
    }

    public int getArtistic() {
        return artistic;
    }

    public int getSocial() {
        return social;
    }

    public int getEnterprising() {
        return enterprising;
    }

    public int getConventional() {
        return conventional;
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

    private enum UserScoreOperation {
        Add,
        Subtract
    }
}