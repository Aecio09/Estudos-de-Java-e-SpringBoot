package org.example.logic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class NetworkLogic {
    public static final int MAX_DEVICES = 20;
    public static final int MAX_CABLES = 40;
    public static final int MAX_PACKETS = 30;
    public static final int QUEUE_SIZE = 8;

    private static final Path STORAGE_DIR = Path.of("networks");

    private final Device[] devices = new Device[MAX_DEVICES];
    private final Cable[] cables = new Cable[MAX_CABLES];

    private int deviceCount;
    private int cableCount;
    private int nextDeviceId;
    private String lastRouteErrorMessage;

    public NetworkLogic() {
        deviceCount = 0;
        cableCount = 0;
        nextDeviceId = 1;
    }

    public void resetDefaultNetwork() {
        deviceCount = 0;
        cableCount = 0;
        nextDeviceId = 1;

        Device phone = addDeviceInternal("telefone", "Telefone 1", "192.168.1.10", "192.168.1.0/24", "wlan0", 120, 110, false);
        Device router = addDeviceInternal("roteador", "Roteador", "10.0.0.1", "10.0.0.0/24", "eth0", 470, 220, true);
        Device server = addDeviceInternal("servidor", "Servidor", "10.0.0.20", "10.0.0.0/24", "eth1", 800, 220, true);

        if (phone == null || router == null || server == null) {
            return;
        }

        addCable(phone.getId(), router.getId());
        addCable(router.getId(), server.getId());
    }

    public boolean addUserDevice(String type, String name, String ip, String network, String interfaceName) {
        if (deviceCount >= MAX_DEVICES) {
            return false;
        }
        int userCount = countUserDevices();
        double x = 120;
        double y = 110 + (userCount * 70);
        return addDeviceInternal(type, name, ip, network, interfaceName, x, y, false) != null;
    }

    public boolean removeDevice(int deviceId) {
        int index = indexOfDevice(deviceId);
        if (index < 0 || devices[index].isFixed()) {
            return false;
        }

        for (int i = index; i < deviceCount - 1; i++) {
            devices[i] = devices[i + 1];
        }
        devices[deviceCount - 1] = null;
        deviceCount--;

        removeCablesForDevice(deviceId);
        return true;
    }

    public boolean addCable(int fromId, int toId) {
        if (fromId == toId || cableCount >= MAX_CABLES) {
            return false;
        }
        if (indexOfDevice(fromId) < 0 || indexOfDevice(toId) < 0 || isCablePresent(fromId, toId)) {
            return false;
        }
        cables[cableCount] = new Cable(fromId, toId);
        cableCount++;
        return true;
    }

    public Device[] getDevicesArray() {
        Device[] copy = new Device[deviceCount];
        System.arraycopy(devices, 0, copy, 0, deviceCount);
        return copy;
    }

    public Cable[] getCablesArray() {
        Cable[] copy = new Cable[cableCount];
        System.arraycopy(cables, 0, copy, 0, cableCount);
        return copy;
    }

    public Device findDeviceById(int id) {
        int index = indexOfDevice(id);
        return index >= 0 ? devices[index] : null;
    }

    public int[] calculateRoute(int sourceId, int destinationId) {
        lastRouteErrorMessage = null;
        Device source = findDeviceById(sourceId);
        Device destination = findDeviceById(destinationId);
        if (source == null || destination == null || sourceId == destinationId) {
            lastRouteErrorMessage = "Origem e destino inválidos.";
            return null;
        }

        Device router = findRouter();
        boolean sameNetwork = source.getNetwork().equals(destination.getNetwork());

        if (sameNetwork) {
            int[] directPath = findPath(sourceId, destinationId, -1);
            if (directPath == null) {
                lastRouteErrorMessage = "Mesma rede, mas sem conexão por cabo entre os dispositivos.";
            }
            return directPath;
        }

        if (router == null) {
            lastRouteErrorMessage = "Dispositivos em redes diferentes: é necessário um roteador.";
            return null;
        }
        int routerId = router.getId();

        if (sourceId == routerId || destinationId == routerId) {
            int[] pathToRouter = findPath(sourceId, destinationId, -1);
            if (pathToRouter == null) {
                lastRouteErrorMessage = "Sem conexão por cabo com o roteador.";
            }
            return pathToRouter;
        }

        boolean sourceConnectedToRouter = isCablePresent(sourceId, routerId);
        boolean destinationConnectedToRouter = isCablePresent(destinationId, routerId);
        if (!sourceConnectedToRouter || !destinationConnectedToRouter) {
            lastRouteErrorMessage = "Dispositivos não estão na mesma rede. Conecte ambos ao roteador.";
            return null;
        }
        return new int[]{sourceId, routerId, destinationId};
    }

    public String getLastRouteErrorMessage() {
        return lastRouteErrorMessage;
    }

    public double queueX(int index) {
        Device router = findRouter();
        if (router == null) {
            return 0;
        }
        return router.getX() - 130 + (index * 20);
    }

    public double queueY(int index) {
        Device router = findRouter();
        return router == null ? 0 : router.getY() + (index * 0.0);
    }

    public Device findRouter() {
        for (int i = 0; i < deviceCount; i++) {
            if ("roteador".equals(devices[i].getType())) {
                return devices[i];
            }
        }
        return null;
    }

    public boolean saveNetwork(String name) {
        String safeName = sanitizeName(name);
        if (safeName.isEmpty()) {
            return false;
        }

        try {
            Files.createDirectories(STORAGE_DIR);
            Path file = STORAGE_DIR.resolve(safeName + ".net");
            StringBuilder data = new StringBuilder();
            data.append("DEVICES\n");
            for (int i = 0; i < deviceCount; i++) {
                Device d = devices[i];
                data.append(d.getId()).append(";")
                        .append(d.getType()).append(";")
                        .append(d.getName()).append(";")
                        .append(d.getIp()).append(";")
                        .append(d.getNetwork()).append(";")
                        .append(d.getInterfaceName()).append(";")
                        .append(d.getX()).append(";")
                        .append(d.getY()).append(";")
                        .append(d.isFixed()).append("\n");
            }
            data.append("CABLES\n");
            for (int i = 0; i < cableCount; i++) {
                Cable c = cables[i];
                data.append(c.getFromId()).append(";").append(c.getToId()).append("\n");
            }
            Files.writeString(file, data.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean loadNetwork(String name) {
        String safeName = sanitizeName(name);
        if (safeName.isEmpty()) {
            return false;
        }

        Path file = STORAGE_DIR.resolve(safeName + ".net");
        if (!Files.exists(file)) {
            return false;
        }

        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            String[] lines = content.split("\\R");

            deviceCount = 0;
            cableCount = 0;
            nextDeviceId = 1;

            boolean readingDevices = false;
            boolean readingCables = false;
            int maxId = 0;

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                if ("DEVICES".equals(line)) {
                    readingDevices = true;
                    readingCables = false;
                    continue;
                }
                if ("CABLES".equals(line)) {
                    readingDevices = false;
                    readingCables = true;
                    continue;
                }

                if (readingDevices) {
                    String[] parts = line.split(";");
                    if (parts.length != 9 || deviceCount >= MAX_DEVICES) {
                        continue;
                    }
                    int id = Integer.parseInt(parts[0]);
                    maxId = Math.max(maxId, id);
                    devices[deviceCount] = new Device(
                            id,
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4],
                            parts[5],
                            Double.parseDouble(parts[6]),
                            Double.parseDouble(parts[7]),
                            Boolean.parseBoolean(parts[8])
                    );
                    deviceCount++;
                } else if (readingCables) {
                    String[] parts = line.split(";");
                    if (parts.length != 2 || cableCount >= MAX_CABLES) {
                        continue;
                    }
                    int from = Integer.parseInt(parts[0]);
                    int to = Integer.parseInt(parts[1]);
                    if (indexOfDevice(from) >= 0 && indexOfDevice(to) >= 0 && !isCablePresent(from, to)) {
                        cables[cableCount] = new Cable(from, to);
                        cableCount++;
                    }
                }
            }
            nextDeviceId = maxId + 1;
            return deviceCount > 0;
        } catch (IOException | NumberFormatException e) {
            return false;
        }
    }

    public String[] listSavedNetworks() {
        if (!Files.exists(STORAGE_DIR)) {
            return new String[0];
        }
        try {
            String[] names;
            try (var stream = Files.list(STORAGE_DIR)) {
                names = stream
                        .filter(path -> path.getFileName().toString().endsWith(".net"))
                        .map(path -> path.getFileName().toString().replace(".net", ""))
                        .sorted()
                        .toArray(String[]::new);
            }
            return Arrays.copyOf(names, names.length);
        } catch (IOException e) {
            return new String[0];
        }
    }

    private Device addDeviceInternal(
            String type,
            String name,
            String ip,
            String network,
            String interfaceName,
            double x,
            double y,
            boolean fixed
    ) {
        if (deviceCount >= MAX_DEVICES) {
            return null;
        }
        Device device = new Device(nextDeviceId, type, name, ip, network, interfaceName, x, y, fixed);
        devices[deviceCount] = device;
        deviceCount++;
        nextDeviceId++;
        return device;
    }

    private int countUserDevices() {
        int count = 0;
        for (int i = 0; i < deviceCount; i++) {
            String type = devices[i].getType();
            if (!"roteador".equals(type) && !"servidor".equals(type)) {
                count++;
            }
        }
        return count;
    }

    private void repositionUserDevices() {
        int userIndex = 0;
        for (int i = 0; i < deviceCount; i++) {
            Device d = devices[i];
            if (!d.isFixed()) {
                d.setPosition(120, 110 + (userIndex * 70));
                userIndex++;
            }
        }
    }

    private void removeCablesForDevice(int deviceId) {
        int write = 0;
        for (int i = 0; i < cableCount; i++) {
            Cable cable = cables[i];
            if (cable.getFromId() == deviceId || cable.getToId() == deviceId) {
                continue;
            }
            cables[write] = cable;
            write++;
        }
        for (int i = write; i < cableCount; i++) {
            cables[i] = null;
        }
        cableCount = write;
    }

    private boolean isCablePresent(int a, int b) {
        for (int i = 0; i < cableCount; i++) {
            Cable cable = cables[i];
            boolean direct = cable.getFromId() == a && cable.getToId() == b;
            boolean reverse = cable.getFromId() == b && cable.getToId() == a;
            if (direct || reverse) {
                return true;
            }
        }
        return false;
    }

    private int[] findPath(int fromId, int toId, int blockedId) {
        if (fromId == toId) {
            return new int[]{fromId};
        }
        if (fromId == blockedId || toId == blockedId) {
            return null;
        }
        int startIndex = indexOfDevice(fromId);
        int endIndex = indexOfDevice(toId);
        if (startIndex < 0 || endIndex < 0) {
            return null;
        }

        boolean[] visited = new boolean[deviceCount];
        int[] queue = new int[deviceCount];
        int[] parentByIndex = new int[deviceCount];
        for (int i = 0; i < parentByIndex.length; i++) {
            parentByIndex[i] = -1;
        }
        int head = 0;
        int tail = 0;

        visited[startIndex] = true;
        queue[tail++] = fromId;

        while (head < tail) {
            int currentId = queue[head++];
            for (int i = 0; i < cableCount; i++) {
                Cable cable = cables[i];
                int nextId = -1;
                if (cable.getFromId() == currentId) {
                    nextId = cable.getToId();
                } else if (cable.getToId() == currentId) {
                    nextId = cable.getFromId();
                }

                if (nextId < 0) {
                    continue;
                }
                if (nextId == blockedId) {
                    continue;
                }

                int nextIndex = indexOfDevice(nextId);
                if (nextIndex >= 0 && !visited[nextIndex]) {
                    visited[nextIndex] = true;
                    parentByIndex[nextIndex] = currentId;
                    if (nextId == toId) {
                        return buildPath(fromId, toId, parentByIndex);
                    }
                    queue[tail++] = nextId;
                }
            }
        }
        return null;
    }

    private int[] buildPath(int fromId, int toId, int[] parentByIndex) {
        int[] reversed = new int[deviceCount];
        int count = 0;
        int currentId = toId;

        while (currentId != -1) {
            reversed[count++] = currentId;
            if (currentId == fromId) {
                break;
            }
            int currentIndex = indexOfDevice(currentId);
            if (currentIndex < 0) {
                return null;
            }
            currentId = parentByIndex[currentIndex];
        }

        if (count == 0 || reversed[count - 1] != fromId) {
            return null;
        }

        int[] path = new int[count];
        for (int i = 0; i < count; i++) {
            path[i] = reversed[count - 1 - i];
        }
        return path;
    }

    private int[] joinPaths(int[] firstPath, int[] secondPath) {
        if (firstPath.length == 0 || secondPath.length == 0) {
            return null;
        }
        int[] joined = new int[firstPath.length + secondPath.length - 1];
        System.arraycopy(firstPath, 0, joined, 0, firstPath.length);
        System.arraycopy(secondPath, 1, joined, firstPath.length, secondPath.length - 1);
        return joined;
    }

    private int indexOfDevice(int id) {
        for (int i = 0; i < deviceCount; i++) {
            if (devices[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private String sanitizeName(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
