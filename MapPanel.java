import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;

public class MapPanel extends JPanel {

    private final Graph graph;
    private final Map<String, Point> nodePositions;
    private static final int R = 24; // node radius

    // ── Warna ──
    private static final Color C_BG        = new Color(255, 255, 255);
    private static final Color C_GRID      = new Color(225, 228, 235);
    private static final Color C_EDGE      = new Color(175, 180, 190);
    private static final Color C_EDGE_EX   = new Color(225, 155, 45);
    private static final Color C_EDGE_PATH = new Color(45, 120, 200);
    private static final Color C_NODE      = new Color(235, 240, 248);
    private static final Color C_NODE_BDR  = new Color(115, 135, 165);
    private static final Color C_START     = new Color(65, 165, 105);
    private static final Color C_END       = new Color(215, 125, 45);
    private static final Color C_PATH      = new Color(85, 115, 205);
    private static final Color C_VISITED   = new Color(195, 200, 210);
    private static final Color C_TEXT      = new Color(40, 45, 55);
    private static final Color C_MUTED     = new Color(130, 140, 155);

    // ── Font (cache) ──
    private static final Font F_NODE  = new Font("SansSerif", Font.BOLD, 11);
    private static final Font F_WGT   = new Font("SansSerif", Font.BOLD, 11);
    private static final Font F_BADGE = new Font("SansSerif", Font.BOLD, 9);
    private static final Font F_INFO  = new Font("SansSerif", Font.BOLD, 12);

    // ── Animasi ──
    private enum Phase { IDLE, EXPLORE, REVEAL, DONE }
    private Phase phase = Phase.IDLE;

    private final Timer timer;
    private List<RouteOptimizer.LangkahAnimasi> steps;
    private List<String> path;
    private Runnable onDone;
    private int idx;
    private float clock;
    private final Set<String> seen  = new HashSet<>();
    private final Set<String> relax = new HashSet<>();
    private String cur;

    public MapPanel(Graph graph) {
        this.graph = graph;
        this.nodePositions = new HashMap<>();
        setBackground(C_BG);
        setOpaque(true);
        setDoubleBuffered(true);

        nodePositions.put("Restoran",    new Point(90, 170));
        nodePositions.put("Perumahan A", new Point(250, 75));
        nodePositions.put("Sekolah", new Point(250, 265));
        nodePositions.put("Perumahan C", new Point(430, 75));
        nodePositions.put("Perumahan D", new Point(430, 265));
        nodePositions.put("Perumahan E", new Point(610, 75));
        nodePositions.put("Rumah Sakit", new Point(610, 265));

        timer = new Timer(50, ev -> tick());
        timer.setCoalesce(true); // jangan numpuk event
    }

    // ── Public ──

    public void mulaiAnimasi(List<RouteOptimizer.LangkahAnimasi> s,
                              List<String> p, Map<String,String> pm, Runnable cb) {
        if (s == null || p == null) return;
        this.steps = s;
        this.path = p;
        this.onDone = cb;
        this.idx = 0;
        this.clock = 0f;
        this.seen.clear();
        this.relax.clear();
        this.cur = null;
        this.phase = Phase.EXPLORE;
        if (!timer.isRunning()) timer.start();
    }

    public void resetAnimasi() {
        timer.stop();
        phase = Phase.IDLE;
        seen.clear();
        relax.clear();
        cur = null;
        repaint();
    }

    public boolean isAnimating() {
        return phase == Phase.EXPLORE || phase == Phase.REVEAL;
    }

    // ── Tick ──

    private void tick() {
        if (phase == Phase.IDLE) return;
        clock += 0.085f;
        switch (phase) {
            case EXPLORE: tickExplore(); break;
            case REVEAL:  tickReveal();  break;
            case DONE:    tickDone();    break;
        }
        repaint();
    }

    private void tickExplore() {
        if (steps == null) { phase = Phase.IDLE; return; }
        if (idx < steps.size()) {
            RouteOptimizer.LangkahAnimasi s = steps.get(idx++);
            if (s == null) return;
            if ("VISIT".equals(s.tipe) || "INIT".equals(s.tipe)) {
                cur = s.nodeDikunjungi;
                seen.add(s.nodeDikunjungi);
            } else if ("RELAX".equals(s.tipe)) {
                relax.add(s.nodeDikunjungi + "->" + s.nodeDiperbarui);
            } else if ("FINISH".equals(s.tipe)) {
                cur = null; phase = Phase.REVEAL; idx = 0; clock = 0f;
            }
        } else {
            cur = null; phase = Phase.REVEAL; idx = 0; clock = 0f;
        }
    }

    private void tickReveal() {
        if (path == null) { phase = Phase.DONE; return; }
        int total = Math.max(1, path.size() - 1);
        idx = Math.min(total, (int)(clock * 0.7f) + 1);
        if (clock > total + 1.8f) {
            phase = Phase.DONE; idx = 0; clock = 0f;
        }
    }

    private void tickDone() {
        idx++;
        if (idx > 35) {
            timer.stop();
            phase = Phase.IDLE;
            if (onDone != null) { Runnable r = onDone; onDone = null; r.run(); }
        }
    }

    // ── Paint ──

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            paintGrid(g2, w, h);
            paintEdges(g2);
            paintNodes(g2);
            paintStatus(g2, w);
        } finally {
            g2.dispose();
        }
    }

    // ── Grid ──

    private void paintGrid(Graphics2D g2, int w, int h) {
        g2.setColor(C_GRID);
        int step = 30;
        // Gambar titik saja, bukan fillOval per titik — jauh lebih cepat
        for (int x = step; x < w; x += step) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = step; y < h; y += step) {
            g2.drawLine(0, y, w, y);
        }
    }

    // ── Edges ──

    private void paintEdges(Graphics2D g2) {
        Set<String> drawn = new HashSet<>();
        Set<String> nodes = graph.getAllNodes();
        if (nodes == null) return;

        for (String src : nodes) {
            Point ps = nodePositions.get(src);
            if (ps == null) continue;
            List<Edge> edges = graph.getNeighbors(src);
            if (edges == null) continue;

            for (Edge e : edges) {
                String dst = e.getDestination();
                String key = src.compareTo(dst) < 0 ? src + "|" + dst : dst + "|" + src;
                if (!drawn.add(key)) continue;
                Point pd = nodePositions.get(dst);
                if (pd == null) continue;

                int wgt = e.getWeight();
                boolean onP  = onPath(src, dst);
                boolean ex   = relax.contains(src + "->" + dst) || relax.contains(dst + "->" + src);
                int rev      = (phase == Phase.REVEAL || phase == Phase.DONE) ? idx : 999;

                if (onP && pathIdx(src, dst) < rev) {
                    paintPathEdge(g2, ps, pd, wgt);
                } else if (ex && phase == Phase.EXPLORE) {
                    paintExploreEdge(g2, ps, pd, wgt);
                } else {
                    paintNormalEdge(g2, ps, pd, wgt);
                }
            }
        }
    }

    private void paintNormalEdge(Graphics2D g2, Point a, Point b, int w) {
        g2.setColor(C_EDGE);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(a.x, a.y, b.x, b.y);
        arrow(g2, a, b, C_EDGE);
        weightLabel(g2, a, b, w, C_MUTED);
    }

    private void paintExploreEdge(Graphics2D g2, Point a, Point b, int w) {
        g2.setColor(C_EDGE_EX);
        float[] dash = {7f, 4f};
        g2.setStroke(new BasicStroke(2.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, dash, clock * 4));
        g2.drawLine(a.x, a.y, b.x, b.y);
        arrow(g2, a, b, C_EDGE_EX);
        weightLabel(g2, a, b, w, C_EDGE_EX);
    }

    private void paintPathEdge(Graphics2D g2, Point a, Point b, int w) {
        g2.setColor(C_EDGE_PATH);
        g2.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(a.x, a.y, b.x, b.y);

        if (phase == Phase.REVEAL || phase == Phase.DONE) {
            g2.setColor(new Color(255, 255, 255, 110));
            float[] fd = {8f, 12f};
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0f, fd, -clock * 5));
            g2.drawLine(a.x, a.y, b.x, b.y);
        }
        arrow(g2, a, b, C_EDGE_PATH);
        weightLabel(g2, a, b, w, C_EDGE_PATH);
    }

    private void weightLabel(Graphics2D g2, Point a, Point b, int w, Color c) {
        int mx = (a.x + b.x) / 2;
        int my = (a.y + b.y) / 2;
        String t = Integer.toString(w);
        g2.setFont(F_WGT);
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(t);
        int th = fm.getAscent();
        g2.setColor(new Color(255, 255, 255, 195));
        g2.fillRoundRect(mx - tw / 2 - 4, my - th / 2 - 2, tw + 8, th + 4, 4, 4);
        g2.setColor(c);
        g2.drawString(t, mx - tw / 2, my + th / 4);
    }

    private void arrow(Graphics2D g2, Point a, Point b, Color c) {
        int mx = (a.x + b.x) / 2;
        int my = (a.y + b.y) / 2;
        double ang = Math.atan2(b.y - a.y, b.x - a.x);
        int s = 7;
        int x1 = (int)(mx - s * Math.cos(ang - Math.PI / 6));
        int y1 = (int)(my - s * Math.sin(ang - Math.PI / 6));
        int x2 = (int)(mx - s * Math.cos(ang + Math.PI / 6));
        int y2 = (int)(my - s * Math.sin(ang + Math.PI / 6));
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(mx, my, x1, y1);
        g2.drawLine(mx, my, x2, y2);
    }

    // ── Nodes ──

    private void paintNodes(Graphics2D g2) {
        for (Map.Entry<String, Point> e : nodePositions.entrySet()) {
            String name = e.getKey();
            Point p = e.getValue();

            boolean start   = "Restoran".equals(name);
            boolean isEnd   = path != null && !path.isEmpty() && name.equals(path.get(path.size() - 1));
            boolean onP     = path != null && path.contains(name);
            boolean vis     = seen.contains(name);
            boolean isCur   = name.equals(cur);
            boolean showPath = (phase == Phase.REVEAL || phase == Phase.DONE);

            Color fill, border;
            if (isCur) {
                fill   = new Color(255, 245, 220);
                border = C_EDGE_EX;
            } else if (start) {
                fill   = C_START;
                border = C_START.darker();
            } else if (isEnd && showPath) {
                fill   = C_END;
                border = C_END.darker();
            } else if (onP && showPath) {
                fill   = C_PATH;
                border = C_PATH.darker();
            } else if (vis) {
                fill   = C_VISITED;
                border = C_NODE_BDR;
            } else {
                fill   = C_NODE;
                border = C_NODE_BDR;
            }

            int x = p.x - R, y = p.y - R, d = R * 2;

            g2.setColor(fill);
            g2.fillOval(x, y, d, d);

            g2.setColor(border);
            g2.setStroke(new BasicStroke(isCur ? 2.6f : 2.0f));
            g2.drawOval(x, y, d, d);

            // Pulsing ring
            if (isCur) {
                float pulse = 0.35f + 0.30f * (float)Math.abs(Math.sin(clock * 2.5));
                g2.setColor(new Color(225, 155, 45, (int)(pulse * 200)));
                g2.setStroke(new BasicStroke(3.2f));
                g2.drawOval(x - 4, y - 4, d + 8, d + 8);
            }

            // Teks
            g2.setFont(F_NODE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = p.x - fm.stringWidth(name) / 2;
            int ty = p.y + fm.getAscent() / 2 - 1;
            boolean whiteText = (start || (isEnd && showPath) || (onP && showPath));
            g2.setColor(whiteText ? Color.WHITE : C_TEXT);
            g2.drawString(name, tx, ty);

            // Badge
            if (start) badge(g2, p, "ASAL", C_START);
            else if (isEnd && showPath) badge(g2, p, "TUJUAN", C_END);
        }
    }

    private void badge(Graphics2D g2, Point p, String label, Color bg) {
        g2.setFont(F_BADGE);
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(label);
        int th = fm.getAscent();
        int bx = p.x - tw / 2 - 3;
        int by = p.y + R + 4;
        g2.setColor(bg);
        g2.fillRoundRect(bx, by, tw + 6, th + 3, 4, 4);
        g2.setColor(Color.WHITE);
        g2.drawString(label, bx + 3, by + th);
    }

    // ── Status ──

    private void paintStatus(Graphics2D g2, int w) {
        if (phase == Phase.IDLE) return;
        String s; Color c;
        switch (phase) {
            case EXPLORE: s = "Menjelajahi graph...";     c = C_EDGE_EX;   break;
            case REVEAL:  s = "Menampilkan rute terpendek"; c = C_EDGE_PATH; break;
            case DONE:    s = "Rute ditemukan";           c = C_START;     break;
            default:      return;
        }
        g2.setFont(F_INFO);
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(s), pad = 14;
        int bx = w - tw - pad * 2 - 10, by = 12;
        g2.setColor(new Color(255, 255, 255, 210));
        g2.fillRoundRect(bx, by, tw + pad * 2, 28, 6, 6);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawRoundRect(bx, by, tw + pad * 2, 28, 6, 6);
        g2.setColor(C_TEXT);
        g2.drawString(s, bx + pad, by + 19);
    }

    // ── Helpers ──

    private boolean onPath(String a, String b) {
        if (path == null) return false;
        for (int i = 0; i < path.size() - 1; i++) {
            String x = path.get(i), y = path.get(i + 1);
            if ((a.equals(x) && b.equals(y)) || (a.equals(y) && b.equals(x))) return true;
        }
        return false;
    }

    private int pathIdx(String a, String b) {
        if (path == null) return 999;
        for (int i = 0; i < path.size() - 1; i++) {
            String x = path.get(i), y = path.get(i + 1);
            if ((a.equals(x) && b.equals(y)) || (a.equals(y) && b.equals(x))) return i;
        }
        return 999;
    }
}
