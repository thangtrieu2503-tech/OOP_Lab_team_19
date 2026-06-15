package traffic.components;

import engine.SimulationEngine;
import map.MapEditorMode;
import traffic.CountdownDisplayMode;
import traffic.sounds.SoundManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class ControlPanel extends JPanel {

    // ── Bảng màu chuẩn hóa ──────────────────────────────────────────────────
    private static final Color COLOR_PRIMARY = new Color(60, 120, 200);   // Xanh dương
    private static final Color COLOR_SUCCESS = new Color(80, 180, 100);   // Xanh lá
    private static final Color COLOR_DANGER  = new Color(210,  70,  70);  // Đỏ
    private static final Color COLOR_NEUTRAL = new Color(120, 130, 150);  // Xám
    private static final Color COLOR_WARNING = new Color(210, 140,  50);  // Cam

    private static final Color BG_PANEL  = new Color(240, 245, 250);
    private static final Color FG_TITLE  = new Color(30,  60, 120);
    private static final Color FG_LABEL  = new Color(40,  80, 140);
    private static final Color FG_SECTION= new Color(30,  60, 120);

    private static final Font FONT_BOLD    = new Font("Arial", Font.BOLD,  11);
    private static final Font FONT_SECTION = new Font("Arial", Font.BOLD,  12);
    private static final Font FONT_LABEL   = new Font("Arial", Font.PLAIN, 10);

    // ── Dependencies ─────────────────────────────────────────────────────────
    private final SimulationEngine engine;
    private final SimulationPanel  simPanel;

    // ── Constructor ───────────────────────────────────────────────────────────
    public ControlPanel(SimulationEngine engine, SimulationPanel simPanel) {
        this.engine   = engine;
        this.simPanel = simPanel;
        setBackground(BG_PANEL);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createRaisedBevelBorder(),
                "🚗 GIAO THÔNG MÔ PHỎNG",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13), FG_TITLE));
        setLayout(new BorderLayout());
        initComponents();
    }

    // ── Build layout ──────────────────────────────────────────────────────────
    private void initComponents() {
        // Nội dung thực nằm trong innerPanel (BoxLayout Y)
        JPanel innerPanel = new JPanel();
        innerPanel.setBackground(BG_PANEL);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        buildSimSection(innerPanel);
        buildTimelineSection(innerPanel);
        buildTrafficLightSection(innerPanel);
        buildDisplaySection(innerPanel);
        buildFocusSection(innerPanel);
        buildMapEditorSection(innerPanel);
        buildAudioSection(innerPanel);

        innerPanel.add(Box.createVerticalGlue());

        // Bọc trong JScrollPane để hỗ trợ màn hình nhỏ (5.4.3)
        JScrollPane scroll = new JScrollPane(innerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.setPreferredSize(new Dimension(320, 0));
        add(scroll, BorderLayout.CENTER);
    }

    // ── 🎮 MÔ PHỎNG ──────────────────────────────────────────────────────────
    private void buildSimSection(JPanel p) {
        addSection(p, "🎮 MÔ PHỎNG");

        addBtn(p, "+ SPAWN XE", COLOR_SUCCESS,
                e -> engine.spawnVehicle());

        // Toggle Spawn — cập nhật label theo trạng thái (5.3.2)
        JButton toggleSpawn = makeBtn(
                engine.isSpawnerEnabled() ? "⏸ TẮT SPAWN" : "▶ BẬT SPAWN",
                COLOR_PRIMARY);
        toggleSpawn.addActionListener(e -> {
            engine.setSpawnerEnabled(!engine.isSpawnerEnabled());
            toggleSpawn.setText(engine.isSpawnerEnabled() ? "⏸ TẮT SPAWN" : "▶ BẬT SPAWN");
        });
        addComponent(p, toggleSpawn);

        addSlider(p, "Lưu lượng", 1, 10, 3, v -> engine.setTrafficDensity(v));
        addSlider(p, "Spawn (s)",  1, 10, 2, v -> engine.setSpawnIntervalSec(v));
        gap(p, 10);
    }

    // ── ⏱️ TIMELINE ───────────────────────────────────────────────────────────
    private void buildTimelineSection(JPanel p) {
        addSection(p, "⏱️  TIMELINE");

        JButton pause  = makeBtn("⏸  PAUSE",  COLOR_DANGER);
        JButton resume = makeBtn("▶  RESUME", COLOR_SUCCESS);
        resume.setEnabled(false);

        pause.addActionListener(e -> {
            engine.setPaused(true);
            pause.setEnabled(false);
            resume.setEnabled(true);
        });
        resume.addActionListener(e -> {
            engine.setPaused(false);
            pause.setEnabled(true);
            resume.setEnabled(false);
        });

        addComponent(p, pause);
        addComponent(p, resume);
        gap(p, 10);
    }

    // ── 🚦 ĐÈN GIAO THÔNG ────────────────────────────────────────────────────
    private void buildTrafficLightSection(JPanel p) {
        addSection(p, "🚦 ĐÈN GIAO THÔNG");

        // RadioButton thay vì 2 JButton riêng (5.4.1)
        addRadioGroup(p,
                new String[]{"🟢 Tự động", "🖱️ Thủ công"},
                0,
                i -> engine.getTrafficController().setAutoMode(i == 0));

        addCombo(p, "Countdown",
                new String[]{"Mode 0: Ẩn", "Mode 1: Đầy đủ", "Mode 2: ≤10s"},
                2,
                i -> engine.setCountdownMode(CountdownDisplayMode.values()[i]));
        gap(p, 10);
    }

    // ── 🎨 HIỂN THỊ ───────────────────────────────────────────────────────────
    private void buildDisplaySection(JPanel p) {
        addSection(p, "🎨 HIỂN THỊ");

        // RadioButton cho Image / Rectangle (5.4.1)
        addRadioGroup(p,
                new String[]{"⬜ Rectangle", "🖼️  Image"},
                0,
                i -> simPanel.setGraphicsMode(i == 1));

        // Zoom slider thay cho 4 nút riêng (5.4.2)
        addSlider(p, "Zoom (%)", 30, 300, 100, v ->
                engine.getCamera().setScalePercent(v));

        // 1 nút Reset View duy nhất gộp Reset + Overview
        addBtn(p, "↔️  Reset View", COLOR_NEUTRAL, e -> {
            simPanel.resetZoom();
            simPanel.setOverview();
        });
        gap(p, 10);
    }

    // ── 📍 FOCUS (GIAO LỘ) ───────────────────────────────────────────────────
    private void buildFocusSection(JPanel p) {
        addSection(p, "📍 FOCUS (GIAO LỘ)");

        String[][] foci = {
            {"Ngã 3 — Trên Trái",  "J0_0"},
            {"Ngã 5 — Trên Phải",  "J3_0"},
            {"Ngã 4 — Dưới Trái",  "J0_1"},
            {"Ngã 3 — Dưới Phải",  "J3_1"},
        };
        for (String[] f : foci) {
            String label = f[0], id = f[1];
            addBtn(p, label, COLOR_PRIMARY, e -> simPanel.focusIntersection(id));
        }
        gap(p, 10);
    }

    // ── 🗺️ MAP EDITOR ────────────────────────────────────────────────────────
    private void buildMapEditorSection(JPanel p) {
        addSection(p, "🗺️  MAP EDITOR");

        addCombo(p, "Chế độ",
                new String[]{"Simulation", "Thêm Node", "Thêm Road", "Xóa Node", "Xóa Road"},
                0,
                i -> engine.setEditorMode(MapEditorMode.values()[i]));

        addSlider(p, "Số làn", 1, 6, 2, engine::setLaneCountForNewRoad);

        // Confirmation dialog trước khi xóa/khôi phục (5.4.4)
        addBtn(p, "🗑️  XÓA MAP", COLOR_DANGER, e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa toàn bộ bản đồ?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) engine.clearMap();
        });

        addBtn(p, "↩️  KHÔI PHỤC", COLOR_WARNING, e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Khôi phục bản đồ mặc định? Mọi thay đổi sẽ bị mất.",
                    "Xác nhận khôi phục", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) engine.restoreDefaultMap();
        });
        gap(p, 10);
    }

    // ── 🔊 ÂM THANH ───────────────────────────────────────────────────────────
    private void buildAudioSection(JPanel p) {
        addSection(p, "🔊 ÂM THANH");
        addBtn(p, "🔇 MUTE",   COLOR_DANGER,  e -> SoundManager.setMuted(true));
        addBtn(p, "🔉 UNMUTE", COLOR_SUCCESS, e -> SoundManager.setMuted(false));
        gap(p, 10);
    }

    // ── Helper: Section label ─────────────────────────────────────────────────
    private void addSection(JPanel p, String title) {
        JLabel lbl = new JLabel(title);
        lbl.setForeground(FG_SECTION);
        lbl.setFont(FONT_SECTION);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 4, 4, 4));
        p.add(lbl);
    }

    // ── Helper: Button (factory + add) ───────────────────────────────────────
    private void addBtn(JPanel p, String text, Color bg, Consumer<ActionEvent> action) {
        JButton btn = makeBtn(text, bg);
        if (action != null) btn.addActionListener(action::accept);
        addComponent(p, btn);
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(300, 32));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.setFocusPainted(false);
        return btn;
    }

    private void addComponent(JPanel p, JComponent c) {
        p.add(Box.createRigidArea(new Dimension(0, 2)));
        p.add(c);
    }

    // ── Helper: RadioButton group (thay thế toggle buttons) ──────────────────
    private void addRadioGroup(JPanel p, String[] labels, int defaultIndex, IntConsumer onChange) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setBackground(BG_PANEL);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(300, 30));

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            JRadioButton rb = new JRadioButton(labels[i]);
            rb.setFont(FONT_BOLD);
            rb.setBackground(BG_PANEL);
            rb.setForeground(FG_LABEL);
            rb.setSelected(i == defaultIndex);
            rb.addActionListener(e -> onChange.accept(idx));
            group.add(rb);
            row.add(rb);
        }

        p.add(Box.createRigidArea(new Dimension(0, 2)));
        p.add(row);
        p.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    // ── Helper: Slider ────────────────────────────────────────────────────────
    private void addSlider(JPanel p, String label, int min, int max, int val, IntConsumer onChange) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(FG_LABEL);
        lbl.setFont(FONT_LABEL);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JSlider slider = new JSlider(min, max, val);
        slider.setBackground(BG_PANEL);
        slider.setAlignmentX(LEFT_ALIGNMENT);
        slider.setMaximumSize(new Dimension(300, 22));
        slider.addChangeListener(e -> onChange.accept(slider.getValue()));

        p.add(Box.createRigidArea(new Dimension(0, 2)));
        p.add(lbl);
        p.add(slider);
        p.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    // ── Helper: ComboBox ──────────────────────────────────────────────────────
    private void addCombo(JPanel p, String label, String[] items, int selected, IntConsumer onChange) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(FG_LABEL);
        lbl.setFont(FONT_LABEL);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JComboBox<String> combo = new JComboBox<>(items);
        combo.setSelectedIndex(selected);
        combo.setAlignmentX(LEFT_ALIGNMENT);
        combo.setMaximumSize(new Dimension(300, 28));
        combo.addActionListener(e -> onChange.accept(combo.getSelectedIndex()));

        p.add(Box.createRigidArea(new Dimension(0, 2)));
        p.add(lbl);
        p.add(combo);
        p.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    // ── Helper: Vertical gap ─────────────────────────────────────────────────
    private void gap(JPanel p, int h) {
        p.add(Box.createRigidArea(new Dimension(0, h)));
    }
}