package UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ControlPanel extends VBox {

    private Button btnNoCountdownLightMode;
    private Button btnResume;
    private Button btnPause;
    private Button btnMute; // THÊM KHAI BÁO NÚT MUTE Ở ĐÂY
    private Button btnZoomIn;
    private Button btnZoomOut;
    private Button btnSpawn;
    private Button btnRectangle;
    private Button btnImage;

    private Spinner<Integer> spinnerSpawnCount;
    private ComboBox<String> comboVehicleType;

    public ControlPanel() {
        // Cấu hình thanh Sidebar dọc rộng 190px, nền xám nhạt chuẩn chỉ
        this.setPrefWidth(190);
        this.setPadding(new Insets(20, 10, 20, 10));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #D3D3D3; -fx-border-width: 0 1 0 0;");
        this.setAlignment(Pos.TOP_CENTER);

        initComponents();
    }

    private void initComponents() {
        Font labelFont = new Font("Segoe UI", 11);
        double btnWidth = 160;
        double btnHeight = 28;

        // Khởi tạo nút Toggle cho đèn giao thông
        btnNoCountdownLightMode = createStyledButton("Toggle Countdown", btnWidth, btnHeight);

        // Khởi tạo các nút bấm phẳng bọc viền mảnh sạch sẽ
        btnResume = createStyledButton("Resume Simulation", btnWidth, btnHeight);
        btnPause = createStyledButton("Pause Simulation", btnWidth, btnHeight);
        btnMute = createStyledButton("Mute Sound: OFF", btnWidth, btnHeight); // KHỞI TẠO NÚT MUTE VÀ ĐẶT TÊN
        btnSpawn = createStyledButton("Spawn", btnWidth, btnHeight);
        btnRectangle = createStyledButton("Rectangle Mode", 160, btnHeight);
        btnImage = createStyledButton("Image Mode", 160, btnHeight);

        btnResume.setDisable(true); // Khóa nút Resume lúc bắt đầu

        // Bộ chọn số lượng đẻ xe (1 đến 20)
        spinnerSpawnCount = new Spinner<>(1, 20, 1);
        spinnerSpawnCount.setPrefWidth(55);
        spinnerSpawnCount.getEditor().setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 11;");

        HBox countPanel = new HBox(8);
        countPanel.setAlignment(Pos.CENTER);
        Label lblCount = new Label("Spawn:");
        lblCount.setFont(labelFont);
        countPanel.getChildren().addAll(lblCount, spinnerSpawnCount);

        // Menu xổ xuống chọn loại xe chạy (Nạp đủ các option cũ của ông)
        comboVehicleType = new ComboBox<>();
        comboVehicleType.getItems().addAll("All", "Car", "Motorbike", "Ambulance", "Fire Truck", "Bus");
        comboVehicleType.setValue("All");
        comboVehicleType.setPrefWidth(90);

        HBox typePanel = new HBox(14);
        typePanel.setAlignment(Pos.CENTER);
        Label lblType = new Label("Type:");
        lblType.setFont(labelFont);
        typePanel.getChildren().addAll(lblType, comboVehicleType);

        // Cặp nút đôi Zoom đặt ngang nhau
        btnZoomIn = createStyledButton("Zoom In", 78, btnHeight);
        btnZoomOut = createStyledButton("Zoom Out", 78, btnHeight);
        HBox zoomPanel = new HBox(4);
        zoomPanel.setAlignment(Pos.CENTER);
        zoomPanel.getChildren().addAll(btnZoomIn, btnZoomOut);

        // Sắp xếp bố cục theo thứ tự đệm khoảng trống y hệt file cũ
        this.getChildren().addAll(
                btnResume,
                btnPause,
                createSeparatorSpace(10),
                btnMute,
                createSeparatorSpace(10),
                btnNoCountdownLightMode,
                createSeparatorSpace(10),
                btnSpawn,
                countPanel,
                typePanel,
                createSeparatorSpace(10),
                btnRectangle,
                btnImage,
                createSeparatorSpace(10),
                zoomPanel
        );
    }

    private Button createStyledButton(String text, double w, double h) {
        Button btn = new Button(text);
        btn.setPrefSize(w, h);
        btn.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-radius: 2; -fx-background-radius: 2; -fx-font-family: 'Segoe UI'; -fx-font-size: 11;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #EAEAEA; -fx-border-color: #BBBBBB; -fx-border-radius: 2; -fx-background-radius: 2; -fx-font-family: 'Segoe UI'; -fx-font-size: 11;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-radius: 2; -fx-background-radius: 2; -fx-font-family: 'Segoe UI'; -fx-font-size: 11;"));
        return btn;
    }

    private VBox createSeparatorSpace(double height) {
        VBox spacer = new VBox();
        spacer.setPrefHeight(height);
        return spacer;
    }

    // --- CÁC HÀM GETTER KẾT NỐI SỰ KIỆN SANG MAINLAUNCHER ---
    public Button getBtnNoCountdownLightMode() { return btnNoCountdownLightMode; }
    public Button getBtnResume() { return btnResume; }
    public Button getBtnPause() { return btnPause; }
    public Button getBtnMute() { return btnMute; } // GETTER CHO NÚT MUTE ĐỂ LAUNCHER GỌI ĐƯỢC
    public Button getBtnZoomIn() { return btnZoomIn; }
    public Button getBtnZoomOut() { return btnZoomOut; }
    public Button getBtnSpawn() { return btnSpawn; }
    public Spinner<Integer> getSpinnerSpawnCount() { return spinnerSpawnCount; }
    public ComboBox<String> getComboVehicleType() { return comboVehicleType; }

    public Button getBtnRectangle() {
        return btnRectangle;
    }

    public Button getBtnImage() {
        return btnImage;
    }
}