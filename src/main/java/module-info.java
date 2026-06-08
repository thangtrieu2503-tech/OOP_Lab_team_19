module traffic {
    requires javafx.controls;
    requires javafx.media; // Ép hệ thống nạp thêm Module âm thanh đồ họa
    
    exports traffic.main;
    exports traffic.components;
    exports traffic.map;
    exports traffic.render;
    exports traffic.sounds;
}