module MapSystem {
    // Gọi thư viện JavaFX
    requires javafx.controls;
    requires javafx.graphics;

    // Cấp quyền cho JavaFX khởi chạy package UI của ông
    exports UI;
}