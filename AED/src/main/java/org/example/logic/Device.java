package org.example.logic;

public class Device {
    public static final int INTERFACE_COUNT = 5;

    private final int id;
    private final String type;
    private final String name;
    private final String ip;
    private final String network;
    private final String[] interfaces = new String[INTERFACE_COUNT];
    private double x;
    private double y;
    private final boolean fixed;

    public Device(int id, String type, String name, String ip, String network, String interfacePrefix, double x, double y, boolean fixed) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.ip = ip;
        this.network = network;
        this.x = x;
        this.y = y;
        this.fixed = fixed;
        initializeInterfaces(interfacePrefix);
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getNetwork() {
        return network;
    }

    public String getInterfaceName() {
        return interfaces[0];
    }

    public int getInterfaceCount() {
        return interfaces.length;
    }

    public String getInterfaceNameAt(int index) {
        if (index < 0 || index >= interfaces.length) {
            return "";
        }
        return interfaces[index];
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isFixed() {
        return fixed;
    }

    private void initializeInterfaces(String interfacePrefix) {
        String safePrefix = (interfacePrefix == null || interfacePrefix.isBlank()) ? "if" : interfacePrefix.trim();
        while (safePrefix.length() > 1 && Character.isDigit(safePrefix.charAt(safePrefix.length() - 1))) {
            safePrefix = safePrefix.substring(0, safePrefix.length() - 1);
        }
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = safePrefix + i;
        }
    }

    @Override
    public String toString() {
        return name + " (" + ip + ")";
    }
}
