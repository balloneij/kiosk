package kiosk.models;

import com.sun.xml.internal.fastinfoset.algorithm.UUIDEncodingAlgorithm;

import java.util.UUID;

public class IdGenerator {

    private static final IdGenerator instance = new IdGenerator();

    public static IdGenerator getInstance() {
        return instance;
    }

    public IdGenerator() {}

    public String getNextId() {
        return "Generated" + UUID.randomUUID();
    }
}
