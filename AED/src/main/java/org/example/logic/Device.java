package org.example.logic;

public class Device {
    private final int id;
    private final String type;
    private final String name;
    private final String ip;
    private final String network;
    private final String interfaceName;
    private double x;
    private double y;
    private final boolean fixed;

    public Device(int id, String type, String name, String ip, String network, String interfaceName, double x, double y, boolean fixed) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.ip = ip;
        this.network = network;
        this.interfaceName = interfaceName;
        this.x = x;
        this.y = y;
        this.fixed = fixed;
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
        return interfaceName;
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

    @Override
    public String toString() {
        return name + " (" + ip + ")";
    }
}
