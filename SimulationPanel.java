package traffic.components;

import engine.Camera;
import engine.SimVehicle;
import engine.SimulationEngine;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import map.CityMap;
import map.Intersection;
import map.MapEditorMode;
import traffic.render.*;

public class SimulationPanel extends JPanel {

    // ── Dependencies ─────────────────────────────────────────────────────────
    private final SimulationEngine engine;
    private VehicleRenderer renderer = new BasicRenderer();
    private boolean graphicsMode = false;

    // ── Camera / draw state ───────────────────────────────────────────────────
    private int    mapDrawX = 60;
    private int    mapDrawY = 20;
    private int    mapDrawW = 1100;
    private int    mapDrawH = 750;
    private double displayScale = 1.0;

    // ── Assets ────────────────────────────────────────────────────────────────
    private BufferedImage mapBackground;

    // ── FPS tracking (5.4.6) ──────────────────────────────────────────────────
    private long lastFrameNano = System.nanoTime();
    private int  fps           = 0;

    // ── Developer mode: hiện/ẩn thông tin kỹ thuật (5.3.7) ──────────────────
    private boolean devMode = false;

    // ── StatusBar (tách khỏi paintComponent) (5.4.6) ─────────────────────────
    private final StatusBar statusBar;

    // ── Constructor ───────────────────────────────────────────────────────────
    public SimulationPanel(SimulationEngine engine) {
        this.engine    = engine;
        this.statusBar = new StatusBar();

        setLayout(new BorderLayout());
        setBackground(new Color(35, 38, 42));
        setPreferredSize(new Dimension(1200, 800));

        // Canvas: khu vực vẽ chính
        SimCanvas canvas = new SimCanvas();
        add(canvas, BorderLayout.CENTER);

        // StatusBar cố định ở dưới cùng
        add(statusBar, BorderLayout.SOUTH);

        loadMapBackground();
        setupMouse(canvas);
    }

    // ── Mouse handling ────────────────────────────────────────────────────────
    private void setupMouse(JPanel canvas) {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                double wx = (e.getX() - mapDrawX) / displayScale;
                double wy = (e.getY() - mapDrawY) / displayScale;

                if (engine.getEditorMode() != MapEditorMode.SIMULATION) {
                    engine.handleMapClick(wx, wy);
                    canvas.repaint();
                    return;
                }
                if (!engine.getTrafficController().isAutoMode()) {
                    engine.toggleLightAt(e.getX(), e.getY(), mapDrawX, mapDrawY, displayScale);
                }
            }
        });

        canvas.addMouseWheelListener(e -> {
            if (e.getWheelRotation() < 0) zoomIn();
            else zoomOut();
        });
    }

    // ── Asset loading ─────────────────────────────────────────────────────────
    private void loadMapBackground() {
        try {
            File f = new File(traffic.tools.ResourcePaths.images() + "map.png");
            if (f.exists()) mapBackground = ImageIO.read(f);
        } catch (Exception ignored) {}
    }

    // ── Public API ────────────────────────────────────────────────────────────
    public void setGraphicsMode(boolean graphics) {
        graphicsMode = graphics;
        renderer = graphics ? new SpriteRenderer() : new BasicRenderer();
        repaint();
    }

    public void zoomIn()    { engine.getCamera().zoomIn();    repaint(); }
    public void zoomOut()   { engine.getCamera().zoomOut();   repaint(); }
    public void resetZoom() { engine.getCamera().resetZoom(); repaint(); }

    public void setDevMode(boolean dev) { this.devMode = dev; }

    public void focusIntersection(String id) {
        for (Intersection inter : engine.getCityMap().getGraph().getIntersections()) {
            if (inter.getId().equals(id)) {
                engine.getCamera().setFocusOnIntersection(inter);
                repaint();
                return;
            }
        }
    }

    public void setOverview() {
        engine.getCamera().setOverview();
        repaint();
    }

    // ── Inner: Canvas (khu vực vẽ) ───────────────────────────────────────────
    private class SimCanvas extends JPanel {

        SimCanvas() {
            setBackground(new Color(35, 38, 42));
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // FPS tính mỗi frame
            long now = System.nanoTime();
            long delta = now - lastFrameNano;
            if (delta > 0) fps = (int) (1_000_000_000L / delta);
            lastFrameNano = now;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Camera state
            Camera camera = engine.getCamera();
            displayScale = camera.getScale();
            mapDrawX = 40 + (int) camera.getScrollX();
            mapDrawY = 20 + (int) camera.getScrollY();
            mapDrawW = (int) (1100 * displayScale);
            mapDrawH = (int) (750  * displayScale);

            // Vẽ bản đồ
            CityMap cityMap = engine.getCityMap();
            if (graphicsMode && mapBackground != null) {
                g2d.drawImage(mapBackground, mapDrawX, mapDrawY, mapDrawW, mapDrawH, null);
            } else {
                renderer.drawMap(g2d, mapDrawX, mapDrawY, mapDrawW, mapDrawH, displayScale,
                        cityMap.getGraph().getLanes(), cityMap.getGraph().getIntersections());
            }

            // Vẽ đèn giao thông
            for (SimulationEngine.IntersectionLightInfo info : engine.getLightInfos()) {
                renderer.drawTrafficLight(g2d,
                        new TrafficLightRenderData(info.x, info.y, info.state, info.timer, info.horizontal),
                        mapDrawX, mapDrawY, displayScale);
            }

            // Vẽ phương tiện
            for (VehicleRenderData vd : buildVehicleRenderData()) {
                renderer.drawVehicle(g2d, vd, mapDrawX, mapDrawY, displayScale);
            }

            // HUD gọn (không còn thông tin kỹ thuật thừa)
            drawHud(g2d);
            drawEditorHint(g2d);

            // Cập nhật StatusBar (5.4.6)
            statusBar.update(
                    engine.getVehicleManager().getActiveCount(),
                    fps,
                    graphicsMode,
                    engine.getTrafficController().isAutoMode(),
                    engine.isSpawnerEnabled(),
                    engine.getCountdownMode().mode,
                    displayScale,
                    engine.getEditorMode()
            );
        }
    }

    // ── Vehicle render data builder ───────────────────────────────────────────
    private List<VehicleRenderData> buildVehicleRenderData() {
        List<VehicleRenderData> result = new ArrayList<>();
        for (SimVehicle sim : engine.getVehicleManager().getSimVehicles()) {
            if (!sim.isActive()) continue;
            vehicle.Vehicle v = sim.getVehicle();
            boolean inJunction = sim.isInJunction(engine.getCityMap().getGraph());
            double scaleFactor = engine.getCamera().getVehicleScaleFactor(inJunction);

            result.add(new VehicleRenderData(
                    v.getTypeName(), v.getDisplayLabel(),
                    v.getPositionX(), v.getPositionY(),
                    v.getWidth(), v.getLength(), v.getAngle(),
                    v.isEmergency(), v.isHonking(), v.isTurnSignalOn(),
                    scaleFactor
            ));
        }
        return result;
    }

    // ── HUD: chỉ hiển thị thông tin cần thiết (5.3.7) ────────────────────────
    private void drawHud(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(10, 10, 420, devMode ? 90 : 50, 10, 10);

        g2d.setColor(new Color(200, 220, 255));
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        g2d.drawString("🚗 Microscopic Traffic Simulation", 20, 30);

        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString(
            "Mode: " + (graphicsMode ? "🖼️ Image" : "⬜ Rectangle")
            + "  |  Control: " + (engine.getTrafficController().isAutoMode() ? "🟢 Auto" : "🖱️ Manual")
            + "  |  Spawn: "   + (engine.isSpawnerEnabled() ? "▶ ON" : "⏸ OFF"),
            20, 47);

        // Thông tin kỹ thuật: chỉ hiện khi bật devMode
        if (devMode) {
            g2d.setColor(new Color(180, 200, 255));
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2d.drawString("Kinematics: v=a·dt  |  Collision: OBB", 20, 66);
            g2d.drawString("Zoom: " + String.format("%.1f", displayScale)
                    + "x  |  FPS: " + fps, 20, 82);
        }
    }

    // ── Editor hint ───────────────────────────────────────────────────────────
    private void drawEditorHint(Graphics2D g2d) {
        if (engine.getEditorMode() == MapEditorMode.SIMULATION) return;
        g2d.setColor(new Color(255, 200, 0, 220));
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        // getHeight() từ SimCanvas thông qua anonymous paintComponent — dùng trực tiếp
        g2d.drawString("MAP EDITOR: " + engine.getEditorMode(), 20,
                ((JPanel) g2d.getClip() != null
                    ? (int)(g2d.getClipBounds() != null ? g2d.getClipBounds().getHeight() - 20 : 580)
                    : 580));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Inner class: StatusBar (5.4.6)
    // ═══════════════════════════════════════════════════════════════════════════
    static class StatusBar extends JPanel {

        private static final Color BG     = new Color(20, 25, 35);
        private static final Color FG     = new Color(180, 210, 255);
        private static final Font  FONT   = new Font("Arial", Font.PLAIN, 11);
        private static final Font  FONT_B = new Font("Arial", Font.BOLD,  11);

        private final JLabel lblVehicles;
        private final JLabel lblFps;
        private final JLabel lblMode;
        private final JLabel lblControl;
        private final JLabel lblSpawn;
        private final JLabel lblCountdown;
        private final JLabel lblZoom;
        private final JLabel lblEditor;

        StatusBar() {
            setBackground(BG);
            setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 80, 120)));
            setLayout(new FlowLayout(FlowLayout.LEFT, 14, 4));
            setPreferredSize(new Dimension(0, 26));

            lblVehicles  = addLabel("Vehicles: 0");
            addSep();
            lblFps       = addLabel("FPS: --");
            addSep();
            lblMode      = addLabel("⬜ Rect");
            addSep();
            lblControl   = addLabel("🟢 Auto");
            addSep();
            lblSpawn     = addLabel("▶ Spawn ON");
            addSep();
            lblCountdown = addLabel("Countdown: 2");
            addSep();
            lblZoom      = addLabel("Zoom: 1.0×");
            addSep();
            lblEditor    = addLabel("Sim");
        }

        private JLabel addLabel(String text) {
            JLabel l = new JLabel(text);
            l.setForeground(FG);
            l.setFont(FONT);
            add(l);
            return l;
        }

        private void addSep() {
            JLabel sep = new JLabel("│");
            sep.setForeground(new Color(70, 90, 130));
            sep.setFont(FONT);
            add(sep);
        }

        void update(int vehicles, int fps, boolean imageMode, boolean autoMode,
                    boolean spawnOn, int countdownMode, double scale, MapEditorMode editorMode) {
            lblVehicles .setText("Vehicles: " + vehicles);
            lblFps      .setText("FPS: " + fps);
            lblMode     .setText(imageMode ? "🖼️ Image" : "⬜ Rect");
            lblControl  .setText(autoMode  ? "🟢 Auto"  : "🖱️ Manual");
            lblSpawn    .setText(spawnOn    ? "▶ Spawn ON" : "⏸ Spawn OFF");
            lblCountdown.setText("Countdown: " + countdownMode);
            lblZoom     .setText(String.format("Zoom: %.1f×", scale));
            lblEditor   .setText(editorMode == MapEditorMode.SIMULATION
                    ? "Sim" : "✏️ " + editorMode);
        }
    }
}