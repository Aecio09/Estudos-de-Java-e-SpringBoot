package org.example.logic;

public class Cable {
    private final int fromId;
    private final int fromInterfaceIndex;
    private final int toId;
    private final int toInterfaceIndex;

    public Cable(int fromId, int fromInterfaceIndex, int toId, int toInterfaceIndex) {
        this.fromId = fromId;
        this.fromInterfaceIndex = fromInterfaceIndex;
        this.toId = toId;
        this.toInterfaceIndex = toInterfaceIndex;
    }

    public int getFromId() {
        return fromId;
    }

    public int getFromInterfaceIndex() {
        return fromInterfaceIndex;
    }

    public int getToId() {
        return toId;
    }

    public int getToInterfaceIndex() {
        return toInterfaceIndex;
    }
}
