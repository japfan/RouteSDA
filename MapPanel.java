import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class MapPanel extends JPanel {
    private Graph graph;
    private Map<String, Point> nodePositions;
    private final int NODE_RADIUS = 25;

    public MapPanel(Graph graph) {
        this.graph = graph;
        this.nodePositions = new HashMap<>();
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Visualisasi Jaringan Jalan"));

        // Menata koordinat lingkaran visual agar muat dengan nama-nama Perumahan dari MapSeeder
        nodePositions.put("Restoran", new Point(100, 150));
        nodePositions.put("Perumahan A", new Point(280, 70));
        nodePositions.put("Perumahan B", new Point(280, 250));
        nodePositions.put("Perumahan C", new Point(480, 70));
        nodePositions.put("Perumahan D", new Point(480, 250));
        nodePositions.put("Perumahan E", new Point(680, 70));
        nodePositions.put("Perumahan F", new Point(680, 250));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. GAMBAR GARIS JALAN (EDGE)
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));

        // Menggunakan getAllNodes() asli untuk mengambil kumpulan simpul
        for (String source : graph.getAllNodes()) {
            Point pSrc = nodePositions.get(source);
            if (pSrc == null) continue;

            // Menggunakan getNeighbors() asli untuk mengambil relasi Edge
            List<Edge> edges = graph.getNeighbors(source);
            if (edges != null) {
                for (Edge edge : edges) {
                    // Mengakses data tujuan dan bobot melalui getter resmi (.getDestination() & .getWeight())
                    String dest = edge.getDestination(); 
                    int weight = edge.getWeight();
                    
                    Point pDest = nodePositions.get(dest);
                    
                    if (pDest != null) {
                        // Hubungkan garis antar node
                        g2d.drawLine(pSrc.x, pSrc.y, pDest.x, pDest.y);
                        
                        // Buat penanda arah panah di tengah jalan
                        drawArrowHead(g2d, pSrc, pDest);
                        
                        // Cetak angka bobot/meter jalan di atas garis
                        int midX = (pSrc.x + pDest.x) / 2;
                        int midY = (pSrc.y + pDest.y) / 2;
                        g2d.setColor(new Color(220, 20, 60)); 
                        g2d.setFont(new Font("Arial", Font.BOLD, 12));
                        g2d.drawString(String.valueOf(weight), midX + 6, midY - 4);
                        g2d.setColor(Color.BLACK);
                    }
                }
            }
        }

        // 2. GAMBAR LINGKARAN LOKASI (NODE)
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String nodeName = entry.getKey();
            Point p = entry.getValue();

            // Isi lingkaran (Warna Biru Cerah)
            g2d.setColor(new Color(135, 206, 250));
            g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            // Garis pinggir lingkaran
            g2d.setColor(Color.BLACK);
            g2d.drawOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            // Teks Nama Lokasi
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = p.x - fm.stringWidth(nodeName) / 2;
            int textY = p.y + fm.getAscent() / 2 - 2;
            g2d.drawString(nodeName, textX, textY);
        }
    }

    private void drawArrowHead(Graphics2D g2d, Point src, Point dest) {
        int arrowSize = 8;
        int midX = (src.x + dest.x) / 2;
        int midY = (src.y + dest.y) / 2;
        
        double dx = dest.x - src.x;
        double dy = dest.y - src.y;
        double angle = Math.atan2(dy, dx);
        
        int x1 = (int) (midX - arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = (int) (midY - arrowSize * Math.sin(angle - Math.PI / 6));
        int x2 = (int) (midX - arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = (int) (midY - arrowSize * Math.sin(angle + Math.PI / 6));
        
        g2d.drawLine(midX, midY, x1, y1);
        g2d.drawLine(midX, midY, x2, y2);
    }
}