package traffic.sounds;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static final Map<String, AudioClip> soundMap = new HashMap<>();

    public static void loadSounds() {
        System.out.println("[SoundManager] Đang tải âm thanh vào bộ nhớ (JavaFX Core)...");
        
        // 1. Các file âm thanh cơ bản
        loadAudioClip("HORN", "/audio/horn.wav");
        loadAudioClip("SIREN", "/audio/siren.wav");
        loadAudioClip("TURN_SIGNAL", "/audio/turn_signal.wav");

        // 2. Map âm thanh chi tiết từng loại xe của ông
        loadAudioClip("CAR_HORN", "/audio/car_horn.wav"); 
        loadAudioClip("MOTORBIKE_HORN", "/audio/horn.wav"); 
        loadAudioClip("BUS_HORN", "/audio/horn.wav");  
        loadAudioClip("AMBULANCE_SIREN", "/audio/ambulance.wav");
        loadAudioClip("FIRE_TRUCK_SIREN", "/audio/fire_truck.wav");

        System.out.println("[SoundManager] Hoàn tất nạp âm thanh!");
    }

    private static void loadAudioClip(String key, String resPath) {
        try {
            URL url = SoundManager.class.getResource(resPath);
            if (url != null) {
                AudioClip clip = new AudioClip(url.toExternalForm());
                soundMap.put(key, clip);
            } else {
                System.out.println("[Sound - Warning] Không thấy file: " + resPath);
            }
        } catch (Exception e) {
            // Bỏ qua log rác nếu thiếu vài file phụ
        }
    }

    public static void play(String key) {
        AudioClip clip = soundMap.get(key);
        if (clip != null) {
            clip.play(); // JavaFX tự động tua về đầu phát đè đa luồng cực mượt
        }
    }

    public static void loop(String key) {
        AudioClip clip = soundMap.get(key);
        if (clip != null) {
            clip.setCycleCount(AudioClip.INDEFINITE);
            clip.play();
        }
    }

    public static void stop(String key) {
        AudioClip clip = soundMap.get(key);
        if (clip != null) {
            clip.stop();
        }
    }
}