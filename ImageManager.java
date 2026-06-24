package UI; // Thay bằng package của ông

import javafx.scene.image.Image;
import java.io.File;

public class ImageManager {
    public static Image mapImg;
    public static Image carImg;
    public static Image motorbikeImg;
    public static Image busImg;
    public static Image ambulanceImg;
    public static Image firetruckImg;

    static {
        try {
            // Dùng đường dẫn File trực tiếp giống hệt vụ làm Âm thanh cho ăn chắc
            carImg = new Image(new File("src/main/resources/images/car.png").toURI().toString());
            motorbikeImg = new Image(new File("src/main/resources/images/motorbike.png").toURI().toString());
            busImg = new Image(new File("src/main/resources/images/bus.png").toURI().toString());
            ambulanceImg = new Image(new File("src/main/resources/images/ambulance.png").toURI().toString());
            firetruckImg = new Image(new File("src/main/resources/images/firetruck.png").toURI().toString());
            System.out.println("✅ Đã nạp toàn bộ ảnh Render thành công!");
        } catch (Exception e) {
            System.err.println("❌ Lỗi load ảnh: " + e.getMessage());
        }
    }

    // Hàm lấy ảnh dựa theo loại xe
    public static Image getVehicleImage(String type) {
        switch (type.toUpperCase()) {
            case "MOTORBIKE": return motorbikeImg;
            case "BUS": return busImg;
            case "AMBULANCE": return ambulanceImg;
            case "FIRE TRUCK": return firetruckImg;
            default: return carImg; // Car là mặc định
        }
    }
}