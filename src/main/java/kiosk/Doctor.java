package kiosk;


/**
 * Doctor tells the user what is
 * wrong with their setup. Issues _must_ be fixable by the
 * user, otherwise, just put it out to System.err
 */
public class Doctor {

    private boolean hasIssues = false;
    private final StringBuilder summary;

    /**
     * Create a doctor for keeping track of issues.
     */
    public Doctor() {
        this.summary = new StringBuilder();
        this.summary.append("There are some potential issues with your careers.csv file.\n"
                + "You can fix them and restart the kiosk, "
                + "or simply press 'Restart' to continue\n\n");
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
