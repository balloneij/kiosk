package kiosk;


/**
 * Doctor tells the user what is
 * wrong with their setup. Issues _must_ be fixable by the
 * user, otherwise, just put it out to System.err
 */
public class Doctor {

    private boolean hasIssues = false;
    private final StringBuilder summary;

    public Doctor() {
        this.summary = new StringBuilder();
    }

    /**
     * Diagnoses a problem and its solution. The user should
     * be able to fix the problem, else use System.err
     * @param problem found
     * @param solution to the problem
     */
    public void diagnose(String problem, String solution) {
        hasIssues = true;
        String message = "Problem:\n"
                + problem + "\n"
                + "Solution:\n"
                + solution + "\n\n";
        summary.append(message);
    }

    public String getSummary() {
        return summary.toString();
    }

    public boolean hasIssues() {
        return hasIssues;
    }
}
