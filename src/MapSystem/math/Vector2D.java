package MapSystem.math;

public class Vector2D {
    // Để public hoặc dùng getter/setter đều được, ở đây dùng thuộc tính private cho chuẩn OOP
    private double x;
    private double y;

    // Khởi tạo
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // ---------------------------------------------------------
    // 1. CÔNG CỤ RADAR: TÍNH KHOẢNG CÁCH (PYTAGO)
    // ---------------------------------------------------------
    // Dùng để xe đo khoảng cách đến đèn đỏ hoặc đến xe phía trước
    public double distanceTo(Vector2D other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // ---------------------------------------------------------
    // 2. CÔNG CỤ BẺ LÁI: TÌM VECTOR HƯỚNG MỚI
    // ---------------------------------------------------------
    // Trừ 2 tọa độ (Điểm Đích - Điểm Hiện Tại) để ra độ lệch x, y
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(this.x - other.x, this.y - other.y);
    }

    // ---------------------------------------------------------
    // 3. CÔNG CỤ ÉP KHUÔN: CHUẨN HÓA (NORMALIZE)
    // ---------------------------------------------------------
    // Cực kỳ quan trọng: Rút ngắn độ dài vector về đúng bằng 1
    // để không làm xe bị dịch chuyển tức thời đến đích.
    public Vector2D normalize() {
        double length = Math.sqrt(x * x + y * y);

        // Chống lỗi toán học (Chia cho 0) khi xe đã đứng chính xác tại tâm ngã tư
        if (length == 0) {
            return new Vector2D(0, 0);
        }

        return new Vector2D(x / length, y / length);
    }

    // Getters & Setters
    public double getX() { return x; }
    public double getY() { return y; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}