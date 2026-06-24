package UI; // Thay bằng package thực tế của ông nếu khác

import java.io.File;
import javafx.scene.media.AudioClip;

public class SoundManager {
    private static AudioClip sirenClip;
    private static AudioClip hornClip; // Dành cho còi Car/Bus

    static {
        try {
            // 1. Nạp còi Cứu thương
            File sirenFile = new File("src/main/resources/sounds/ambulance_siren2.mp3");
            if (sirenFile.exists()) {
                sirenClip = new AudioClip(sirenFile.toURI().toString());
            }

            // 2. Nạp còi Car/Bus
            File hornFile = new File("src/main/resources/sounds/car_horn.mp3"); // Nhớ đổi tên file mp3 cho khớp
            if (hornFile.exists()) {
                hornClip = new AudioClip(hornFile.toURI().toString());
                System.out.println("Đã nạp file tiếng còi ô tô thành công!");
            }
        } catch (Exception e) {
            System.err.println("Lỗi đọc file âm thanh: " + e.getMessage());
        }
    }

    // ==========================================
    // CÒI CỨU THƯƠNG (Phát liên tục)
    // ==========================================
    public static void playSiren() {
        if (sirenClip != null && !sirenClip.isPlaying()) {
            sirenClip.setCycleCount(AudioClip.INDEFINITE);
            sirenClip.play();
        }
    }

    public static void stopSiren() {
        if (sirenClip != null && sirenClip.isPlaying()) {
            sirenClip.stop();
        }
    }

    // ==========================================
    // CÒI Ô TÔ THƯỜNG (Chỉ bóp 1 phát)
    // ==========================================
    public static void playCarHorn() {
        // Chỉ bóp thêm nếu còi trước đó đã kêu xong (tránh loạn âm)
        if (hornClip != null && !hornClip.isPlaying()) {
            hornClip.play(); // Không có setCycleCount vì chỉ bóp 1 lần
        }
    }
}