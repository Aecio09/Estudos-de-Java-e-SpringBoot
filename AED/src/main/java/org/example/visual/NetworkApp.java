package org.example.visual;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.logic.Cable;
import org.example.logic.Device;
import org.example.logic.NetworkLogic;

public class NetworkApp extends Application {
    private static final double APP_WIDTH = 1280;
    private static final double APP_HEIGHT = 720;
    private static final double MAP_WIDTH = 1280;
    private static final double MAP_HEIGHT = 640;
    private static final double DEVICE_RADIUS = 26;

    private static final String ROOT_STYLE = "-fx-background-color: linear-gradient(to bottom right, #07111f, #0e2340);";
    private static final String PANEL_STYLE = "-fx-background-color: rgba(8, 18, 34, 0.88);"
            + "-fx-background-radius: 22;"
            + "-fx-border-color: rgba(120, 196, 255, 0.22);"
            + "-fx-border-radius: 22;"
            + "-fx-border-width: 1;";
    private static final String MAP_STYLE = "-fx-background-color: linear-gradient(to bottom, rgba(8, 16, 30, 0.95), rgba(11, 24, 44, 0.98));"
            + "-fx-background-radius: 22;"
            + "-fx-border-color: rgba(120, 196, 255, 0.18);"
            + "-fx-border-radius: 22;"
            + "-fx-border-width: 1;";
    private static final String PRIMARY_BUTTON_STYLE = "-fx-background-color: linear-gradient(to bottom, #2ec4ff, #1978c8);"
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 14; -fx-padding: 8 16 8 16;";
    private static final String SECONDARY_BUTTON_STYLE = "-fx-background-color: linear-gradient(to bottom, #4f7cff, #254ab3);"
            + "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 14; -fx-padding: 8 16 8 16;";
    private static final String DIALOG_STYLE = "-fx-background-color: linear-gradient(to bottom right, #0c1a2d, #102746);"
            + "-fx-background-radius: 18;"
            + "-fx-border-color: rgba(120, 196, 255, 0.22);"
            + "-fx-border-radius: 18;"
            + "-fx-border-width: 1;";
    private static final String INPUT_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.08);"
            + "-fx-text-fill: white;"
            + "-fx-prompt-text-fill: #a9bbd4;"
            + "-fx-background-radius: 10;"
            + "-fx-border-color: rgba(120, 196, 255, 0.25);"
            + "-fx-border-radius: 10;";
    private static final String CHIP_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.07);"
            + "-fx-background-radius: 16;"
            + "-fx-border-color: rgba(255, 255, 255, 0.08);"
            + "-fx-border-radius: 16;"
            + "-fx-padding: 8 14 8 14;";
    private static final String STATUS_STYLE = "-fx-background-color: rgba(8, 18, 34, 0.88);"
            + "-fx-background-radius: 18;"
            + "-fx-border-color: rgba(120, 196, 255, 0.18);"
            + "-fx-border-radius: 18;"
            + "-fx-border-width: 1;";

    private final NetworkLogic logic = new NetworkLogic();
    private final Circle[] packetCircles = new Circle[NetworkLogic.MAX_PACKETS];
    private final Animation[] packetAnimations = new Animation[NetworkLogic.MAX_PACKETS];

    private Pane mapPane;
    private Label statusLabel;
    private Label transmissionLabel;
    private Timeline transmissionTimeline;
    private int activeTransmissions;
    private int deliveredPackets;
    private int expectedPackets;
    private String currentDestinationName;
    private String currentSourceInterfaceName;
    private String currentDestinationInterfaceName;
    private Label devicesInfoLabel;
    private Label cablesInfoLabel;
    private Label queueInfoLabel;

    public static void launchApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle(ROOT_STYLE);
        root.setPadding(new Insets(16));
        root.setTop(createTopBar());
        root.setCenter(createMapPane());
        root.setBottom(createStatusBar());
        BorderPane.setMargin(root.getTop(), new Insets(0, 0, 14, 0));
        BorderPane.setMargin(root.getCenter(), new Insets(0, 0, 14, 0));

        renderMap();

        Scene scene = new Scene(root, APP_WIDTH, APP_HEIGHT);
        stage.setTitle("Simulador de Rede");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createTopBar() {
        Label title = new Label("Simulador de Rede");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Visão geral da topologia e dos cabos da rede");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 12));
        subtitle.setTextFill(Color.web("#b8c7dd"));

        VBox titleBox = new VBox(2, title, subtitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Button resetButton = new Button("Rede Padrão");
        resetButton.setOnAction(event -> {
            logic.resetDefaultNetwork();
            renderMap();
            setStatus("Rede padrão carregada.");
        });
        resetButton.setStyle(PRIMARY_BUTTON_STYLE);

        MenuButton optionsButton = new MenuButton("Opções");
        optionsButton.setStyle(SECONDARY_BUTTON_STYLE);

        MenuItem addDevice = new MenuItem("Adicionar dispositivo");
        addDevice.setOnAction(event -> showAddDeviceDialog());
        MenuItem connectCable = new MenuItem("Conectar cabos");
        connectCable.setOnAction(event -> showConnectCableDialog());
        MenuItem sendPackets = new MenuItem("Enviar pacotes");
        sendPackets.setOnAction(event -> showSendPacketsDialog());
        MenuItem saveNetwork = new MenuItem("Salvar rede");
        saveNetwork.setOnAction(event -> showSaveNetworkDialog());
        MenuItem loadNetwork = new MenuItem("Carregar rede");
        loadNetwork.setOnAction(event -> showLoadNetworkDialog());

        optionsButton.getItems().addAll(addDevice, connectCable, sendPackets, saveNetwork, loadNetwork);

        HBox actions = new HBox(10, resetButton, optionsButton);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerRow = new HBox(18, titleBox, spacer, actions);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(16, 18, 14, 18));
        headerRow.setStyle(PANEL_STYLE);

        devicesInfoLabel = createChipLabel("Dispositivos: 0");
        cablesInfoLabel = createChipLabel("Cabos: 0");
        queueInfoLabel = createChipLabel("Pacotes: 0");

        HBox overviewRow = new HBox(10, devicesInfoLabel, cablesInfoLabel, queueInfoLabel);
        overviewRow.setAlignment(Pos.CENTER_LEFT);
        overviewRow.setPadding(new Insets(10, 4, 0, 4));

        return new VBox(8, headerRow, overviewRow);
    }

    private Pane createMapPane() {
        mapPane = new Pane();
        mapPane.setPrefSize(MAP_WIDTH, MAP_HEIGHT);
        mapPane.setStyle(MAP_STYLE);
        return mapPane;
    }

    private HBox createStatusBar() {
        Circle statusDot = new Circle(5, Color.web("#0dd27a"));

        statusLabel = new Label("Pronto.");
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));

        transmissionLabel = new Label("✉ Enviando...");
        transmissionLabel.setTextFill(Color.web("#bafcd2"));
        transmissionLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        transmissionLabel.setVisible(false);
        transmissionLabel.setManaged(false);

        HBox status = new HBox(10, statusDot, statusLabel, transmissionLabel);
        status.setAlignment(Pos.CENTER_LEFT);
        status.setPadding(new Insets(12, 16, 12, 16));
        status.setStyle(STATUS_STYLE);
        return status;
    }

    private void showAddDeviceDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Adicionar dispositivo");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("telefone", "pc", "tablet", "notebook", "roteador", "servidor");
        type.setValue("telefone");
        styleComboBox(type);
        TextField name = new TextField("Dispositivo");
        name.setStyle(INPUT_STYLE);
        TextField ip = new TextField("192.168.1.30");
        ip.setStyle(INPUT_STYLE);
        TextField network = new TextField("192.168.1.0/24");
        network.setStyle(INPUT_STYLE);
        TextField iface = new TextField("eth0");
        iface.setStyle(INPUT_STYLE);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(6, 2, 2, 2));
        form.addRow(0, createDialogLabel("Tipo"), type);
        form.addRow(1, createDialogLabel("Nome"), name);
        form.addRow(2, createDialogLabel("IP"), ip);
        form.addRow(3, createDialogLabel("Rede"), network);
        form.addRow(4, createDialogLabel("Interface"), iface);
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                boolean added = logic.addUserDevice(
                        type.getValue(),
                        safeText(name.getText(), "Dispositivo"),
                        safeText(ip.getText(), "192.168.1.100"),
                        safeText(network.getText(), "192.168.1.0/24"),
                        safeText(iface.getText(), "eth0")
                );
                if (added) {
                    renderMap();
                    setStatus("Dispositivo cadastrado com sucesso.");
                } else {
                    setStatus("Nao foi possivel adicionar. Limite atingido.");
                }
            }
        });
    }

    private void showConnectCableDialog() {
        Device[] devices = logic.getDevicesArray();
        if (devices.length < 2) {
            setStatus("Precisa de pelo menos 2 dispositivos.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Conectar cabos");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        ComboBox<Device> from = new ComboBox<>();
        ComboBox<String> fromInterface = new ComboBox<>();
        ComboBox<Device> to = new ComboBox<>();
        ComboBox<String> toInterface = new ComboBox<>();
        from.getItems().setAll(devices);
        to.getItems().setAll(devices);
        from.setValue(devices[0]);
        to.setValue(devices[Math.min(1, devices.length - 1)]);
        styleComboBox(from);
        styleComboBox(fromInterface);
        styleComboBox(to);
        styleComboBox(toInterface);

        updateInterfaceCombo(fromInterface, from.getValue());
        updateInterfaceCombo(toInterface, to.getValue());
        from.setOnAction(event -> updateInterfaceCombo(fromInterface, from.getValue()));
        to.setOnAction(event -> updateInterfaceCombo(toInterface, to.getValue()));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(6, 2, 2, 2));
        form.addRow(0, createDialogLabel("De"), from);
        form.addRow(1, createDialogLabel("Interface saída"), fromInterface);
        form.addRow(2, createDialogLabel("Para"), to);
        form.addRow(3, createDialogLabel("Interface entrada"), toInterface);
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                Device a = from.getValue();
                Device b = to.getValue();
                Integer aIf = selectedInterfaceIndex(fromInterface);
                Integer bIf = selectedInterfaceIndex(toInterface);
                if (a == null || b == null || aIf == null || bIf == null) {
                    setStatus("Selecione dispositivos e interfaces.");
                    return;
                }
                if (!logic.hasAvailableInterface(a.getId()) || !logic.hasAvailableInterface(b.getId())) {
                    setStatus("Um dos dispositivos está sem interfaces livres para novo cabo.");
                    return;
                }
                if (logic.addCable(a.getId(), aIf, b.getId(), bIf)) {
                    renderMap();
                    setStatus("Cabo conectado: " + a.getName() + "[" + a.getInterfaceNameAt(aIf) + "] -> "
                            + b.getName() + "[" + b.getInterfaceNameAt(bIf) + "]");
                } else {
                    setStatus("Cabo inválido: verifique se interfaces já estão em uso.");
                }
            }
        });
    }

    private void showSendPacketsDialog() {
        Device[] devices = logic.getDevicesArray();
        if (devices.length < 2) {
            setStatus("Precisa de origem e destino.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Enviar pacotes");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        ComboBox<Device> source = new ComboBox<>();
        ComboBox<Device> destination = new ComboBox<>();
        Spinner<Integer> count = new Spinner<>(1, NetworkLogic.MAX_PACKETS, 8);
        count.setEditable(true);
        stylePacketSpinner(count);

        source.getItems().setAll(devices);
        destination.getItems().setAll(devices);
        source.setValue(firstUserDevice(devices) != null ? firstUserDevice(devices) : devices[0]);
        destination.setValue(firstServerDevice(devices) != null ? firstServerDevice(devices) : devices[Math.min(1, devices.length - 1)]);
        styleComboBox(source);
        styleComboBox(destination);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(6, 2, 2, 2));
        form.addRow(0, createDialogLabel("Origem"), source);
        form.addRow(1, createDialogLabel("Destino"), destination);
        form.addRow(2, createDialogLabel("Pacotes"), count);
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                Device src = source.getValue();
                Device dst = destination.getValue();
                if (src == null || dst == null) {
                    setStatus("Selecione origem e destino.");
                    return;
                }
                int[] route = logic.calculateRoute(src.getId(), dst.getId());
                if (route == null) {
                    String error = logic.getLastRouteErrorMessage();
                    setStatus((error == null || error.isBlank()) ? "Sem rota: conecte os cabos corretamente." : error);
                    return;
                }
                animatePackets(route, count.getValue());
                boolean sameNetwork = src.getNetwork().equals(dst.getNetwork());
                if (sameNetwork) {
                    setStatus("Pacotes enviados diretamente na mesma rede (" + (route.length - 1) + " salto(s)).");
                } else {
                    Device router = logic.findRouter();
                    if (router != null) {
                        String ifaceInOut = routerInterfaceFlowText(route, router);
                        setStatus("Pacotes enviados via roteador " + ifaceInOut + " com " + (route.length - 1) + " salto(s).");
                    } else {
                        setStatus("Pacotes enviados por roteamento com " + (route.length - 1) + " salto(s).");
                    }
                }
            }
        });
    }

    private void showSaveNetworkDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Salvar rede");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        TextField name = new TextField("rede_demo");
        name.setStyle(INPUT_STYLE);
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(6, 2, 2, 2));
        form.addRow(0, createDialogLabel("Nome"), name);
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (logic.saveNetwork(name.getText())) {
                    setStatus("Rede salva em arquivo.");
                } else {
                    setStatus("Falha ao salvar rede.");
                }
            }
        });
    }

    private void showLoadNetworkDialog() {
        String[] names = logic.listSavedNetworks();
        if (names.length == 0) {
            setStatus("Nenhuma rede salva encontrada.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Carregar rede");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialog(dialog);

        ComboBox<String> networks = new ComboBox<>();
        networks.getItems().setAll(names);
        networks.setValue(names[0]);
        styleComboBox(networks);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(6, 2, 2, 2));
        form.addRow(0, createDialogLabel("Rede"), networks);
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String selected = networks.getValue();
                if (selected == null || selected.isBlank()) {
                    setStatus("Selecione uma rede.");
                    return;
                }
                if (logic.loadNetwork(selected)) {
                    renderMap();
                    setStatus("Rede carregada.");
                } else {
                    setStatus("Falha ao carregar rede.");
                }
            }
        });
    }

    private void renderMap() {
        stopAllAnimations();
        mapPane.getChildren().clear();
        drawGrid();
        drawMapHeader();
        drawCables();
        drawDevices();
        updateOverviewStrip();
    }

    private void drawGrid() {
        for (int x = 0; x <= MAP_WIDTH; x += 40) {
            Line line = new Line(x, 0, x, MAP_HEIGHT);
            line.setStroke(Color.rgb(255, 255, 255, 0.08));
            mapPane.getChildren().add(line);
        }
        for (int y = 0; y <= MAP_HEIGHT; y += 40) {
            Line line = new Line(0, y, MAP_WIDTH, y);
            line.setStroke(Color.rgb(255, 255, 255, 0.08));
            mapPane.getChildren().add(line);
        }
    }

    private void drawMapHeader() {
        Text title = new Text("TOPOLOGIA DA REDE");
        title.setFill(Color.web("#f4f8ff"));
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setLayoutX(22);
        title.setLayoutY(30);

        Text subtitle = new Text("Nós, cabos e fila do roteador organizados em uma visão única");
        subtitle.setFill(Color.web("#a9bbd4"));
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 11));
        subtitle.setLayoutX(22);
        subtitle.setLayoutY(48);

        Line accent = new Line(22, 58, 220, 58);
        accent.setStroke(Color.web("#2ec4ff"));
        accent.setStrokeWidth(3);

        mapPane.getChildren().addAll(title, subtitle, accent);
    }

    private void drawCables() {
        Cable[] cables = logic.getCablesArray();
        for (Cable cable : cables) {
            Device from = logic.findDeviceById(cable.getFromId());
            Device to = logic.findDeviceById(cable.getToId());
            if (from == null || to == null) {
                continue;
            }
            Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
            line.setStroke(Color.rgb(255, 255, 255, 0.7));
            line.setStrokeWidth(3);
            mapPane.getChildren().add(line);
        }
    }

    private void drawQueueSlots() {
        Device router = logic.findRouter();
        if (router == null) {
            return;
        }

        Text label = new Text("Fila do roteador");
        label.setFill(Color.web("#d8e8ff"));
        label.setLayoutX(logic.queueX(0) - 10);
        label.setLayoutY(router.getY() - 25);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        mapPane.getChildren().add(label);

        for (int i = 0; i < NetworkLogic.QUEUE_SIZE; i++) {
            Circle slot = new Circle(logic.queueX(i), logic.queueY(i), 7);
            slot.setFill(Color.rgb(255, 255, 255, 0.14));
            slot.setStroke(Color.rgb(255, 255, 255, 0.55));
            mapPane.getChildren().add(slot);
        }
    }

    @SuppressWarnings("all")
    private void drawDevices() {
        Device[] devices = logic.getDevicesArray();
        for (Device device : devices) {
            if (device == null) {
                continue;
            }
            mapPane.getChildren().add(createDeviceNode(device));
        }
    }

    @SuppressWarnings("all")
    private Pane createDeviceNode(Device device) {
        if (device == null) {
            return new Pane();
        }

        double deviceX = device.getX();
        double deviceY = device.getY();
        String type = device.getType();
        String name = device.getName();
        String ip = device.getIp();
        String interfaceName = device.getInterfaceName();
        boolean fixed = device.isFixed();
        int deviceId = device.getId();

        Circle halo = new Circle(deviceX, deviceY, 34);
        halo.setFill(Color.rgb(255, 255, 255, 0.03));
        halo.setStroke(Color.rgb(255, 255, 255, 0.12));

        Circle base = new Circle(deviceX, deviceY, DEVICE_RADIUS);
        base.setFill(colorByType(type));
        base.setStroke(Color.WHITE);
        base.setStrokeWidth(2);
        base.setEffect(new Glow(0.45));

        Text icon = new Text(DeviceAsciiIcons.iconForType(type));
        icon.setFill(Color.WHITE);
        icon.setFont(Font.font("System", FontWeight.BOLD, 17));
        icon.setLayoutX(deviceX - (icon.getLayoutBounds().getWidth() / 2));
        icon.setLayoutY(deviceY + (icon.getLayoutBounds().getHeight() / 3));

        Text typeTag = new Text(displayType(type));
        typeTag.setFill(Color.web("#a9bbd4"));
        typeTag.setFont(Font.font("System", FontWeight.BOLD, 9));
        typeTag.setLayoutX(deviceX - 25);
        typeTag.setLayoutY(deviceY - 40);

        Text label = new Text(name + "\n" + ip + " • " + interfaceName + " ... (" + device.getInterfaceCount() + " ifs)");
        label.setFill(Color.web("#f5f7fb"));
        label.setFont(Font.font("System", FontWeight.NORMAL, 11));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setLayoutX(deviceX - 72);
        label.setLayoutY(deviceY + 44);
        label.setWrappingWidth(144);

        Pane group = new Pane(halo, base, icon, typeTag, label);
        group.setCursor(Cursor.OPEN_HAND);
        group.setPickOnBounds(false);

        final double[] dragStartScene = new double[2];
        final double[] dragStartDevice = new double[2];
        final double[] pendingPosition = new double[2];
        final boolean[] dragging = new boolean[]{false};
        group.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                showDeviceInfoDialog(device);
                event.consume();
                return;
            }
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            dragStartScene[0] = event.getSceneX();
            dragStartScene[1] = event.getSceneY();
            dragStartDevice[0] = device.getX();
            dragStartDevice[1] = device.getY();
            pendingPosition[0] = device.getX();
            pendingPosition[1] = device.getY();
            dragging[0] = false;
            group.setCursor(Cursor.CLOSED_HAND);
            group.toFront();
            event.consume();
        });
        group.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown()) {
                return;
            }
            double deltaX = event.getSceneX() - dragStartScene[0];
            double deltaY = event.getSceneY() - dragStartScene[1];
            double newX = dragStartDevice[0] + deltaX;
            double newY = dragStartDevice[1] + deltaY;
            double clampedX = clamp(newX, DEVICE_RADIUS + 10, MAP_WIDTH - DEVICE_RADIUS - 10);
            double clampedY = clamp(newY, DEVICE_RADIUS + 58, MAP_HEIGHT - DEVICE_RADIUS - 54);
            pendingPosition[0] = clampedX;
            pendingPosition[1] = clampedY;
            group.setTranslateX(clampedX - dragStartDevice[0]);
            group.setTranslateY(clampedY - dragStartDevice[1]);
            dragging[0] = true;
            event.consume();
        });
        group.setOnMouseReleased(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            group.setCursor(Cursor.OPEN_HAND);
            if (dragging[0]) {
                device.setPosition(pendingPosition[0], pendingPosition[1]);
                group.setTranslateX(0);
                group.setTranslateY(0);
                renderMap();
                setStatus("Dispositivo movido.");
            }
            event.consume();
        });

        if (!fixed) {
            Circle deleteBg = new Circle(deviceX + 18, deviceY - 19, 8, Color.web("#ff4d4d"));
            Text deleteText = new Text("x");
            deleteText.setFill(Color.WHITE);
            deleteText.setFont(Font.font("System", FontWeight.BOLD, 11));
            deleteText.setLayoutX(deviceX + 14.5);
            deleteText.setLayoutY(deviceY - 15.5);
            deleteBg.setOnMouseClicked(event -> {
                event.consume();
                removeDevice(deviceId);
            });
            deleteText.setOnMouseClicked(event -> {
                event.consume();
                removeDevice(deviceId);
            });
            group.getChildren().addAll(deleteBg, deleteText);
        }
        return group;
    }

    private void showDeviceInfoDialog(Device device) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Informações do dispositivo");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        styleDialog(dialog);

        GridPane info = new GridPane();
        info.setHgap(10);
        info.setVgap(8);
        info.setPadding(new Insets(6, 2, 2, 2));
        info.addRow(0, createDialogLabel("ID"), createDialogValue(String.valueOf(device.getId())));
        info.addRow(1, createDialogLabel("Tipo"), createDialogValue(displayType(device.getType())));
        info.addRow(2, createDialogLabel("Nome"), createDialogValue(device.getName()));
        info.addRow(3, createDialogLabel("IP"), createDialogValue(device.getIp()));
        info.addRow(4, createDialogLabel("Rede"), createDialogValue(device.getNetwork()));
        info.addRow(5, createDialogLabel("Posição"), createDialogValue((int) device.getX() + ", " + (int) device.getY()));

        String interfacesText = "";
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            if (i > 0) {
                interfacesText += " | ";
            }
            interfacesText += device.getInterfaceNameAt(i);
        }
        info.addRow(6, createDialogLabel("Interfaces"), createDialogValue(interfacesText));

        dialog.getDialogPane().setContent(info);
        dialog.showAndWait();
    }

    private double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private void removeDevice(int id) {
        if (logic.removeDevice(id)) {
            renderMap();
            setStatus("Dispositivo removido.");
        } else {
            setStatus("Nao foi possivel remover este dispositivo.");
        }
    }

    private void animatePackets(int[] route, int count) {
        stopAllAnimations();
        if (route == null || route.length < 2) {
            return;
        }

        Device[] routeDevices = new Device[route.length];
        for (int i = 0; i < route.length; i++) {
            routeDevices[i] = logic.findDeviceById(route[i]);
            if (routeDevices[i] == null) {
                return;
            }
        }

        int safeCount = Math.min(Math.max(1, count), NetworkLogic.MAX_PACKETS);
        activeTransmissions = safeCount;
        deliveredPackets = 0;
        expectedPackets = safeCount;
        currentDestinationName = routeDevices[routeDevices.length - 1].getName();
        int sourceInterfaceIndex = logic.getInterfaceIndexForLink(route[0], route[1]);
        int destinationInterfaceIndex = logic.getInterfaceIndexForLink(route[route.length - 1], route[route.length - 2]);
        currentSourceInterfaceName = interfaceDisplay(routeDevices[0], sourceInterfaceIndex);
        currentDestinationInterfaceName = interfaceDisplay(routeDevices[routeDevices.length - 1], destinationInterfaceIndex);
        startTransmissionIndicator();
        for (int i = 0; i < safeCount; i++) {
            Circle packet = getPacketCircle(i);
            packet.setCenterX(routeDevices[0].getX());
            packet.setCenterY(routeDevices[0].getY());
            packet.setVisible(true);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(packet.centerXProperty(), routeDevices[0].getX()),
                            new KeyValue(packet.centerYProperty(), routeDevices[0].getY()))
            );

            double currentTime = 0.0;
            for (int hop = 1; hop < routeDevices.length; hop++) {
                Device hopDevice = routeDevices[hop];
                currentTime += 1.2;
                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(currentTime),
                        new KeyValue(packet.centerXProperty(), hopDevice.getX()),
                        new KeyValue(packet.centerYProperty(), hopDevice.getY())));

                if ("roteador".equals(hopDevice.getType()) && hop < routeDevices.length - 1) {
                    double queueX = logic.queueX(i % NetworkLogic.QUEUE_SIZE);
                    double queueY = logic.queueY(i % NetworkLogic.QUEUE_SIZE);
                    currentTime += 0.28;
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(currentTime),
                            new KeyValue(packet.centerXProperty(), queueX),
                            new KeyValue(packet.centerYProperty(), queueY)));
                    currentTime += 0.55 + (i * 0.08);
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(currentTime),
                            new KeyValue(packet.centerXProperty(), queueX),
                            new KeyValue(packet.centerYProperty(), queueY)));
                    currentTime += 0.25;
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(currentTime),
                            new KeyValue(packet.centerXProperty(), hopDevice.getX()),
                            new KeyValue(packet.centerYProperty(), hopDevice.getY())));
                }
            }

            int index = i;
            timeline.setDelay(Duration.millis(i * 220.0));
            timeline.setOnFinished(event -> {
                if (packetAnimations[index] == timeline) {
                    packet.setVisible(false);
                }
                deliveredPackets++;
                setStatus("Envio: " + currentSourceInterfaceName + " -> " + currentDestinationInterfaceName
                        + " | Recebidos no destino " + currentDestinationName + ": "
                        + deliveredPackets + "/" + expectedPackets + " pacote(s).");
                activeTransmissions = Math.max(0, activeTransmissions - 1);
                if (activeTransmissions == 0) {
                    stopTransmissionIndicator();
                    setStatus("Transmissão concluída: " + currentSourceInterfaceName + " -> "
                            + currentDestinationInterfaceName + " | destino " + currentDestinationName
                            + " recebeu " + expectedPackets + "/" + expectedPackets + " pacote(s).");
                }
            });
            packetAnimations[i] = timeline;
            timeline.play();
        }
    }

    private Circle getPacketCircle(int index) {
        if (packetCircles[index] == null) {
            Circle packet = new Circle(6, Color.web("#39ff8b"));
            packet.setStroke(Color.WHITE);
            packet.setStrokeWidth(1.1);
            packet.setEffect(new Glow(0.8));
            packet.setVisible(false);
            packetCircles[index] = packet;
            mapPane.getChildren().add(packet);
        } else if (!mapPane.getChildren().contains(packetCircles[index])) {
            mapPane.getChildren().add(packetCircles[index]);
        }
        return packetCircles[index];
    }

    private void stopAllAnimations() {
        for (int i = 0; i < packetAnimations.length; i++) {
            Animation animation = packetAnimations[i];
            if (animation != null) {
                animation.stop();
                packetAnimations[i] = null;
            }
            Circle packet = packetCircles[i];
            if (packet != null) {
                packet.setVisible(false);
            }
        }
        activeTransmissions = 0;
        deliveredPackets = 0;
        expectedPackets = 0;
        currentDestinationName = null;
        currentSourceInterfaceName = null;
        currentDestinationInterfaceName = null;
        stopTransmissionIndicator();
    }

    private void startTransmissionIndicator() {
        transmissionLabel.setVisible(true);
        transmissionLabel.setManaged(true);
        if (transmissionTimeline != null) {
            transmissionTimeline.stop();
        }
        transmissionTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(transmissionLabel.opacityProperty(), 0.45),
                        new KeyValue(transmissionLabel.translateXProperty(), 0)),
                new KeyFrame(Duration.seconds(0.55),
                        new KeyValue(transmissionLabel.opacityProperty(), 1.0),
                        new KeyValue(transmissionLabel.translateXProperty(), 8)),
                new KeyFrame(Duration.seconds(1.1),
                        new KeyValue(transmissionLabel.opacityProperty(), 0.45),
                        new KeyValue(transmissionLabel.translateXProperty(), 0))
        );
        transmissionTimeline.setCycleCount(Animation.INDEFINITE);
        transmissionTimeline.play();
    }

    private void stopTransmissionIndicator() {
        if (transmissionTimeline != null) {
            transmissionTimeline.stop();
            transmissionTimeline = null;
        }
        transmissionLabel.setVisible(false);
        transmissionLabel.setManaged(false);
        transmissionLabel.setTranslateX(0);
        transmissionLabel.setOpacity(1);
    }

    private Device firstUserDevice(Device[] devices) {
        for (Device device : devices) {
            if (!device.isFixed()) {
                return device;
            }
        }
        return null;
    }

    private Device firstServerDevice(Device[] devices) {
        for (Device device : devices) {
            if ("servidor".equals(device.getType())) {
                return device;
            }
        }
        return null;
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    private void updateOverviewStrip() {
        if (devicesInfoLabel == null || cablesInfoLabel == null || queueInfoLabel == null) {
            return;
        }
        int deviceCount = logic.getDevicesArray().length;
        int cableCount = logic.getCablesArray().length;
        int activePackets = 0;
        for (Animation packetAnimation : packetAnimations) {
            if (packetAnimation != null) {
                activePackets++;
            }
        }
        devicesInfoLabel.setText("Dispositivos: " + deviceCount);
        cablesInfoLabel.setText("Cabos: " + cableCount);
        queueInfoLabel.setText("Pacotes: " + activePackets);
    }

    private Label createChipLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web("#dbe7f5"));
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        label.setStyle(CHIP_STYLE);
        return label;
    }

    private Label createDialogLabel(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web("#edf4ff"));
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        return label;
    }

    private Label createDialogValue(String text) {
        Label label = new Label(text);
        label.setTextFill(Color.web("#ffffff"));
        label.setFont(Font.font("System", FontWeight.NORMAL, 12));
        return label;
    }

    private void updateInterfaceCombo(ComboBox<String> comboBox, Device device) {
        comboBox.getItems().clear();
        if (device == null) {
            comboBox.setValue(null);
            return;
        }
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            if (logic.isInterfaceAvailable(device.getId(), i)) {
                comboBox.getItems().add(i + " - " + device.getInterfaceNameAt(i));
            }
        }
        if (comboBox.getItems().isEmpty()) {
            comboBox.getItems().add("-1 - sem interfaces livres");
            comboBox.setValue(comboBox.getItems().get(0));
        } else {
            comboBox.setValue(comboBox.getItems().get(0));
        }
    }

    private Integer selectedInterfaceIndex(ComboBox<String> comboBox) {
        String value = comboBox.getValue();
        if (value == null || value.isBlank()) {
            return null;
        }
        int separator = value.indexOf(" - ");
        String indexPart = separator >= 0 ? value.substring(0, separator) : value;
        try {
            int parsed = Integer.parseInt(indexPart.trim());
            return parsed >= 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String routerInterfaceFlowText(int[] route, Device router) {
        int routerPosition = -1;
        for (int i = 0; i < route.length; i++) {
            if (route[i] == router.getId()) {
                routerPosition = i;
                break;
            }
        }
        if (routerPosition <= 0 || routerPosition >= route.length - 1) {
            return "(interface " + router.getInterfaceNameAt(0) + ")";
        }

        int previousId = route[routerPosition - 1];
        int nextId = route[routerPosition + 1];
        int inIndex = logic.getInterfaceIndexForLink(router.getId(), previousId);
        int outIndex = logic.getInterfaceIndexForLink(router.getId(), nextId);
        if (inIndex < 0 || outIndex < 0) {
            return "(interfaces do roteador)";
        }
        return "(" + router.getInterfaceNameAt(inIndex) + " -> " + router.getInterfaceNameAt(outIndex) + ")";
    }

    private String interfaceDisplay(Device device, int interfaceIndex) {
        if (device == null || interfaceIndex < 0 || interfaceIndex >= device.getInterfaceCount()) {
            return "?";
        }
        return device.getName() + "[" + device.getInterfaceNameAt(interfaceIndex) + "]";
    }

    private <T> void styleComboBox(ComboBox<T> comboBox) {
        comboBox.setStyle(INPUT_STYLE + "-fx-mark-color: white;");
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.toString());
                }
                setTextFill(Color.WHITE);
                setStyle("-fx-background-color: transparent;");
            }
        });
        comboBox.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.toString());
                }
                setTextFill(Color.web("#0b1f36"));
            }
        });
    }

    private void stylePacketSpinner(Spinner<Integer> spinner) {
        spinner.setStyle(INPUT_STYLE + "-fx-pref-width: 118;");
        spinner.getEditor().setStyle(INPUT_STYLE + "-fx-font-size: 16px; -fx-font-weight: bold; -fx-alignment: center;");
    }

    private void styleDialog(Dialog<ButtonType> dialog) {
        dialog.getDialogPane().setStyle(DIALOG_STYLE);
        dialog.getDialogPane().setPrefWidth(440);
        dialog.getDialogPane().setPrefHeight(260);
        dialog.setOnShown(event -> {
            Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
            if (okButton instanceof Button button) {
                button.setStyle(PRIMARY_BUTTON_STYLE);
            }
            Node cancelButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            if (cancelButton instanceof Button button) {
                button.setStyle(SECONDARY_BUTTON_STYLE);
            }
        });
    }

    private String safeText(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private String displayType(String type) {
        return switch (type) {
            case "telefone" -> "Telefone";
            case "pc" -> "PC";
            case "tablet" -> "Tablet";
            case "notebook" -> "Notebook";
            case "roteador" -> "Roteador";
            case "servidor" -> "Servidor";
            default -> type;
        };
    }

    private Color colorByType(String type) {
        return switch (type) {
            case "telefone", "pc", "tablet", "notebook" -> Color.web("#2ec4ff");
            case "roteador" -> Color.web("#00d26a");
            case "servidor" -> Color.web("#4d72ff");
            default -> Color.web("#6d6d6d");
        };
    }

}
