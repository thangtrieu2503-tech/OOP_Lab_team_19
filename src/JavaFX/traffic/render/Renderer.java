package traffic.render;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Renderer {

    // Bộ nhớ RAM tạm thời (Cache) để lưu trữ các ảnh xe cộ dạng chuẩn JavaFX Image
    private static final Map<String, Image> spriteCache = new HashMap<>();

    public static void loadSprites() {
        System.out.println("[Renderer] Đang nạp tài nguyên ảnh bằng Stream chuẩn Maven...");
        
        // Cấu hình danh sách loại xe và tên file ảnh trong thư mục resources/images/
        String[] vehicleTypes = {"CAR", "BIKE", "MOTORBIKE", "AMBULANCE", "FIRE_TRUCK", "BUS", "TRUCK"};
        String[] fileNames = {"car.png", "bike.png", "motorbike.png", "ambulance.png", "firetruck.png", "bus.png", "truck.png"};

        for (int i = 0; i < vehicleTypes.length; i++) {
            String imagePath = "/images/" + fileNames[i]; // Dấu / đầu tiên đại diện cho thư mục src/main/resources
            
            try (InputStream is = Renderer.class.getResourceAsStream(imagePath)) {
                if (is != null) {
                    Image img = new Image(is);
                    spriteCache.put(vehicleTypes[i], img);
                    System.out.println("[Renderer] Nạp ảnh thành công: " + vehicleTypes[i] + " từ " + imagePath);
                } else {
                    System.out.println("[Renderer - WARNING] Không tìm thấy file tại: src/main/resources" + imagePath);
                }
            } catch (Exception e) {
                System.out.println("[Renderer - ERROR] Lỗi khi nạp ảnh xe " + vehicleTypes[i] + ": " + e.getMessage());
            }
        }
    }

    /**
     * Hàm lấy ảnh chuẩn JavaFX từ RAM ra để vẽ trên Canvas
     */
    public static Image getSprite(String type) {
        if (type == null) return null;
        return spriteCache.get(type.toUpperCase());
    }
}