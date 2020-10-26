package kiosk.models;

public class IdGenerator {

    private static final IdGenerator instance = new IdGenerator();

    public static IdGenerator getInstance() {
        return instance;
    }

    private int currentId;

    public IdGenerator() {
        currentId = 0;
    }

    public String getNextId() {
        return "Generated" + currentId++;
    }

}
