package traffic.map;

public class IntersectionNode {
    private int id;
    private int gridX; // Vị trí cột (0, 1, 2, 3...)
    private int gridY; // Vị trí hàng (0, 1, 2, 3...)

    public IntersectionNode(int id, int gridX, int gridY) {
        this.id = id;
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGridX() { return gridX; }
    public void setGridX(int gridX) { this.gridX = gridX; }

    public int getGridY() { return gridY; }
    public void setGridY(int gridY) { this.gridY = gridY; }

    // Khoảng cách mỗi ngã tư là 320 pixel, tâm ngã tư cách lề 160 pixel
    public double getWorldX() {
        return 160.0 + (this.gridX * 320.0);
    }

    public double getWorldY() {
        return 160.0 + (this.gridY * 320.0);
    }
}