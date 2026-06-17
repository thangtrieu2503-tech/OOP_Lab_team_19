package MapSystem.map;

import MapSystem.math.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class Road {
    private Intersection startNode;
    private Intersection endNode;
    private int laneCount;
    private List<Lane> lanes = new ArrayList<>();

    public Road(Intersection startNode, Intersection endNode, int laneCount) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.laneCount = laneCount;

        // Tự động tính toán và đẻ ra các làn xe ngay khi khởi tạo
        generateLanes();
    }

    private void generateLanes() {
        Vector2D dir = endNode.getPosition().subtract(startNode.getPosition()).normalize();

        // Xoay vector 90 độ để làm pháp tuyến (tịnh tiến sang ngang)
        Vector2D perpendicular = new Vector2D(-dir.getY(), dir.getX());

        double laneWidth = 25.0;
        double centerOffset = 5.0;

        for (int i = 0; i < laneCount; i++) {
            double totalOffset = centerOffset + (i * laneWidth) + (laneWidth / 2.0);

            Vector2D laneStart = new Vector2D(
                    startNode.getPosition().getX() + perpendicular.getX() * totalOffset,
                    startNode.getPosition().getY() + perpendicular.getY() * totalOffset
            );
            Vector2D laneEnd = new Vector2D(
                    endNode.getPosition().getX() + perpendicular.getX() * totalOffset,
                    endNode.getPosition().getY() + perpendicular.getY() * totalOffset
            );

            lanes.add(new Lane(laneStart, laneEnd));
        }
    }

    public Intersection getStartNode() { return startNode; }
    public Intersection getEndNode() { return endNode; }
    public List<Lane> getLanes() { return lanes; }

    // ==========================================
    // CLASS LỒNG: Quản lý điểm đầu/cuối của từng làn
    // ==========================================
    public class Lane {
        public Vector2D startPos;
        public Vector2D endPos;
        public Lane(Vector2D start, Vector2D end) {
            this.startPos = start;
            this.endPos = end;
        }
    }
}