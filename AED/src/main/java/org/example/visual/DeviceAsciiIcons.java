package org.example.visual;

public final class DeviceAsciiIcons {
    private DeviceAsciiIcons() {
    }

    public static String iconForType(String type) {
        String normalizedType = type == null ? "" : type.trim().toLowerCase();
        return switch (normalizedType) {
            case "telefone" -> "\u260e";
            case "pc" -> "\ud83d\udda5";
            case "tablet" -> "\ud83d\udcf1";
            case "notebook" -> "\ud83d\udcbb";
            case "roteador", "router" -> "\u21c4";
            case "servidor" -> "\ud83d\uddc4";
            default -> "\u25a3";
        };
    }
}
