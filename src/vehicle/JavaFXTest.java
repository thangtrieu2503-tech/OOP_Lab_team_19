package vehicle;

import behavior.DrivingStrategy;
import behavior.EmergencyDriver;
import behavior.NormalDriver;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JavaFXTest extends Application {

    @Override
    public void start(Stage stage) {
        VehicleSystem vehicleSystem = new VehicleSystem();
        Canvas canvas = new Canvas(1000, 700);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 🗺️ ĐỊNH NGHĨA CON ĐƯỜNG ĐI MỌI KIỂU (Ngang -> Chéo -> Dọc -> Ngang)
        List<MyPoint> freeFormRoad = new ArrayList<>();
        freeFormRoad.add(new MyPoint(50, 100));
        freeFormRoad.add(new MyPoint(350, 100));
        freeFormRoad.add(new MyPoint(650, 350));
        freeFormRoad.add(new MyPoint(650, 600));
        freeFormRoad.add(new MyPoint(950, 600));

        // Đặt đèn giao thông ở tọa độ X=650, Y=450
        double lightX = 650;
        double lightY = 450;
        final boolean[] isRedLight = {false};

        AnimationTimer timer = new AnimationTimer() {
            private long lastSpawnTime = 0;
            private long lastLightSwitchTime = 0;
            private Random random = new Random();

            @Override
            public void handle(long now) {
                if (now - lastLightSwitchTime > 4_000_000_000L) {
                    isRedLight[0] = !isRedLight[0];
                    lastLightSwitchTime = now;
                }

                if (now - lastSpawnTime > 2_500_000_000L) {
                    DrivingStrategy strategy = random.nextBoolean() ? new NormalDriver() : new CautiousDriver();
                    double randomMaxSpeed = 3.0 + (random.nextDouble() * 2.0);

                    Vehicle v;
                    int choice = random.nextInt(4);
                    // Lưu ý: Nếu ông chưa tạo đủ 6 class con Car, Motorbike, Bus, Ambulance...
                    // Thì tạm thời sửa hết đống chữ Car, Bus, Motorbike ở dưới này thành "new Vehicle" hoặc tạo file cho tụi nó nhé!
                    if (choice == 0) v = new Car(freeFormRoad, randomMaxSpeed, strategy);
                    else if (choice == 1) v = new Motorbike(freeFormRoad, randomMaxSpeed + 1.5, strategy);
                    else if (choice == 2) v = new Bus(freeFormRoad, randomMaxSpeed - 1.0, strategy);
                    else v = new Ambulance(freeFormRoad, randomMaxSpeed + 1.0, new EmergencyDriver());

                    vehicleSystem.spawnVehicle(v);
                    lastSpawnTime = now;
                }

                vehicleSystem.updateSystem(isRedLight[0], lightX, lightY);

                gc.clearRect(0, 0, 1000, 700);

                gc.setStroke(Color.LIGHTGRAY);
                gc.setLineWidth(4);
                for (int i = 0; i < freeFormRoad.size() - 1; i++) {
                    MyPoint p1 = freeFormRoad.get(i);
                    MyPoint p2 = freeFormRoad.get(i + 1);
                    gc.strokeLine(p1.x, p1.y, p2.x, p2.y);
                }

                gc.setFill(isRedLight[0] ? Color.RED : Color.GREEN);
                gc.fillOval(lightX - 10, lightY - 10, 20, 20);

                for (Vehicle v : vehicleSystem.getAllVehicles()) {
                    gc.save();
                    gc.translate(v.getX(), v.getY());
                    gc.rotate(v.getAngle());

                    if (v instanceof Ambulance) gc.setFill(Color.RED);
                    else if (v instanceof Bus) gc.setFill(Color.DARKBLUE);
                    else gc.setFill(Color.DARKGREEN);

                    gc.fillRect(-v.getWidth()/2, -v.getHeight()/2, v.getWidth(), v.getHeight());

                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(1);
                    gc.strokeRect(-v.getWidth()/2, -v.getHeight()/2, v.getWidth(), v.getHeight());

                    gc.restore();
                }
            }
        };

        timer.start();
        stage.setTitle("Mô phỏng đường tự do Đa hướng - Nhóm 19");
        stage.setScene(new Scene(new Pane(canvas), 1000, 700));
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}