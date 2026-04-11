package org.example.logic;

public class Cable {
    private final int fromId;
    private final int toId;

    public Cable(int fromId, int toId) {
        this.fromId = fromId;
        this.toId = toId;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }
}
