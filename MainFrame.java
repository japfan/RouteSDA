import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final Graph graph;
    private final OrderManager orderManager;

    private MapPanel mapPanel;
    private JTextArea txtAreaAntrean;
    private JTextArea txtAreaRute;
    private JTextField txtOrderId, txtCustomerName, txtDestination, txtDeadline;
    private JButton btnTambah, btnProses;
    private JLabel lblStatus;

    // ── Warna datar ──
    private static final Color WHITE      = new Color(255, 255, 255);
    private static final Color BG         = new Color(248, 249, 250);
    private static final Color BORDER     = new Color(210, 215, 225);
    private static final Color TEXT_DARK  = new Color(40, 45, 55);
    private static final Color TEXT_MUTED = new Color(130, 140, 155);
    private static final Color BLUE       = new Color(55, 125, 210);
    private static final Color BLUE_HOVER = new Color(70, 145, 230);
    private static final Color GREEN      = new Color(60, 160, 100);
    private static final Color GREEN_HOVER = new Color(80, 180, 120);
    private static final Color RED        = new Color(210, 70, 60);
    private static final Color INPUT_BG   = new Color(255, 255, 255);
    private static final Font  FONT_MONO  = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font  FONT_BTN   = new Font("SansSerif", Font.BOLD, 13);

    public MainFrame() {
        graph = new Graph();
        orderManager = new OrderManager();
        MapSeeder.seedMap(graph, orderManager);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        setTitle("RouteSDA — Optimasi Rute Pengiriman Makanan");
        setSize(1000, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        initComponent();
        updateAntreanView();
    }

    private void initComponent() {
        setLayout(new BorderLayout(0, 0));

        JPanel header = header();
        add(header, BorderLayout.NORTH);

        JPanel left = leftPanel();
        add(left, BorderLayout.WEST);

        JPanel center = centerPanel();
        add(center, BorderLayout.CENTER);

        JPanel footer = footer();
        add(footer, BorderLayout.SOUTH);

        wireButtons();
    }

    // ═══════════ HEADER ═══════════

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel title = new JLabel("RouteSDA");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(BLUE);
        p.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("Sistem Optimasi Rute Pengiriman Makanan");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        p.add(sub, BorderLayout.EAST);

        return p;
    }

    // ═══════════ LEFT PANEL ═══════════

    private JPanel leftPanel() {
        JPanel wrap = new JPanel(new BorderLayout(8, 8));
        wrap.setBackground(BG);
        wrap.setPreferredSize(new Dimension(330, 580));
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 6));

        wrap.add(formPanel(), BorderLayout.NORTH);
        wrap.add(antreanPanel(), BorderLayout.CENTER);

        return wrap;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        p.setMaximumSize(new Dimension(300, 255));

        JLabel head = new JLabel("Input Pesanan Baru");
        head.setFont(new Font("SansSerif", Font.BOLD, 13));
        head.setForeground(TEXT_DARK);
        head.setAlignmentX(LEFT_ALIGNMENT);
        p.add(head);
        p.add(Box.createVerticalStrut(8));

        p.add(lbl("Order ID"));
        txtOrderId = field("ORD-004");
        p.add(txtOrderId);
        p.add(Box.createVerticalStrut(6));

        p.add(lbl("Nama Pelanggan"));
        txtCustomerName = field("Dian");
        p.add(txtCustomerName);
        p.add(Box.createVerticalStrut(6));

        p.add(lbl("Tujuan (Node Peta)"));
        txtDestination = field("Perumahan F");
        p.add(txtDestination);
        p.add(Box.createVerticalStrut(6));

        p.add(lbl("Deadline (menit)"));
        txtDeadline = field("30");
        p.add(txtDeadline);
        p.add(Box.createVerticalStrut(10));

        btnTambah = button("Tambah Pesanan", GREEN, GREEN_HOVER);
        btnTambah.setAlignmentX(LEFT_ALIGNMENT);
        btnTambah.setMaximumSize(new Dimension(276, 34));
        p.add(btnTambah);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.add(p, BorderLayout.NORTH);
        return outer;
    }

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField field(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_DARK);
        tf.setCaretColor(TEXT_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        tf.setMaximumSize(new Dimension(276, 30));
        tf.setAlignmentX(LEFT_ALIGNMENT);
        return tf;
    }

    private JPanel antreanPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);

        JLabel head = new JLabel("Daftar Antrean Pesanan");
        head.setFont(new Font("SansSerif", Font.BOLD, 12));
        head.setForeground(TEXT_DARK);
        head.setBorder(BorderFactory.createEmptyBorder(4, 2, 6, 2));
        p.add(head, BorderLayout.NORTH);

        txtAreaAntrean = new JTextArea("Belum ada antrean pesanan.");
        txtAreaAntrean.setEditable(false);
        txtAreaAntrean.setLineWrap(true);
        txtAreaAntrean.setWrapStyleWord(true);
        txtAreaAntrean.setFont(FONT_MONO);
        txtAreaAntrean.setBackground(WHITE);
        txtAreaAntrean.setForeground(TEXT_DARK);
        txtAreaAntrean.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane sc = new JScrollPane(txtAreaAntrean);
        sc.setBackground(BG);
        sc.getViewport().setBackground(WHITE);
        sc.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sc.setPreferredSize(new Dimension(300, 260));
        p.add(sc, BorderLayout.CENTER);

        return p;
    }

    // ═══════════ CENTER PANEL ═══════════

    private JPanel centerPanel() {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBackground(BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 12));

        JPanel mapBox = new JPanel(new BorderLayout());
        mapBox.setBackground(WHITE);
        mapBox.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        mapPanel = new MapPanel(graph);
        mapBox.add(mapPanel, BorderLayout.CENTER);
        wrap.add(mapBox, BorderLayout.CENTER);

        JPanel bottom = resultPanel();
        wrap.add(bottom, BorderLayout.SOUTH);

        return wrap;
    }

    private JPanel resultPanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBackground(BG);

        btnProses = button("Cari Rute Tercepat", BLUE, BLUE_HOVER);
        btnProses.setPreferredSize(new Dimension(200, 34));
        top.add(btnProses, BorderLayout.WEST);

        lblStatus = new JLabel("Siap memproses antrean");
        lblStatus.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblStatus.setForeground(TEXT_MUTED);
        top.add(lblStatus, BorderLayout.CENTER);

        p.add(top, BorderLayout.NORTH);

        txtAreaRute = new JTextArea("Rute tercepat akan muncul di sini setelah tombol di atas diklik.");
        txtAreaRute.setEditable(false);
        txtAreaRute.setLineWrap(true);
        txtAreaRute.setWrapStyleWord(true);
        txtAreaRute.setFont(FONT_MONO);
        txtAreaRute.setBackground(WHITE);
        txtAreaRute.setForeground(TEXT_DARK);
        txtAreaRute.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane sc = new JScrollPane(txtAreaRute);
        sc.setBackground(BG);
        sc.getViewport().setBackground(WHITE);
        sc.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sc.setPreferredSize(new Dimension(620, 130));
        p.add(sc, BorderLayout.CENTER);

        return p;
    }

    // ═══════════ FOOTER ═══════════

    private JPanel footer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)));

        JLabel l = new JLabel("RouteSDA v1.0  ·  Algoritma Dijkstra  ·  Priority Queue Min-Heap");
        l.setFont(new Font("SansSerif", Font.PLAIN, 10));
        l.setForeground(TEXT_MUTED);
        p.add(l, BorderLayout.WEST);

        JLabel r = new JLabel("7 Node  ·  11 Edge  ·  Undirected Graph");
        r.setFont(new Font("SansSerif", Font.PLAIN, 10));
        r.setForeground(TEXT_MUTED);
        p.add(r, BorderLayout.EAST);

        return p;
    }

    // ═══════════ BUTTON ═══════════

    private JButton button(String text, Color bg, Color hover) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setOpaque(true);

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (b.isEnabled()) b.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (b.isEnabled()) b.setBackground(bg);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (b.isEnabled()) b.setBackground(bg.darker());
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (b.isEnabled()) b.setBackground(hover);
            }
        });

        return b;
    }

    // ═══════════ LOGIC ═══════════

    private void wireButtons() {
        btnTambah.addActionListener(e -> {
            try {
                String id = txtOrderId.getText().trim();
                String cust = txtCustomerName.getText().trim();
                String dest = txtDestination.getText().trim();
                String dl = txtDeadline.getText().trim();

                if (id.isEmpty() || cust.isEmpty() || dest.isEmpty() || dl.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Semua field harus diisi.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!graph.containsNode(dest)) {
                    JOptionPane.showMessageDialog(this, "Node tidak ditemukan di peta.\nTersedia: " + String.join(", ", graph.getAllNodes()), "Gagal", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int deadline = Integer.parseInt(dl);
                orderManager.addOrder(new DeliveryOrder(id, cust, "Restoran", dest, deadline), graph);
                updateAntreanView();
                mapPanel.resetAnimasi();

                txtOrderId.setText(""); txtCustomerName.setText("");
                txtDestination.setText(""); txtDeadline.setText("");

                lblStatus.setText("Pesanan [" + id + "] ditambahkan");
                lblStatus.setForeground(GREEN);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Deadline harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnProses.addActionListener(e -> {
            DeliveryOrder top = orderManager.getTopOrder();
            if (top == null) {
                JOptionPane.showMessageDialog(this, "Tidak ada antrean pesanan.", "Antrean Kosong", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String asal = top.getRestaurantNode();
            String tujuan = top.getDestinationNode();

            btnProses.setEnabled(false);
            btnTambah.setEnabled(false);
            lblStatus.setText("Menjalankan Dijkstra...");
            lblStatus.setForeground(BLUE);

            RouteOptimizer.HasilAnimasi hasil = RouteOptimizer.hitungDijkstraDenganAnimasi(graph, asal, tujuan);

            if (hasil.totalJarak < 0) {
                btnProses.setEnabled(true);
                btnTambah.setEnabled(true);
                lblStatus.setText("Tidak ada jalur dari " + asal + " ke " + tujuan);
                lblStatus.setForeground(RED);
                JOptionPane.showMessageDialog(this, "Tidak ada jalur.", "Rute Tidak Ditemukan", JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hasil.path.size(); i++) {
                sb.append(hasil.path.get(i));
                if (i < hasil.path.size() - 1) sb.append("  →  ");
            }

            txtAreaRute.setText(
                "HASIL OPTIMASI ALGORITMA DIJKSTRA\n" +
                "======================================\n\n" +
                "Detail Pesanan:\n" +
                "  Order ID    : " + top.getOrderId() + "\n" +
                "  Pelanggan   : " + top.getCustomerName() + "\n" +
                "  Dari        : " + asal + "\n" +
                "  Tujuan      : " + tujuan + "\n" +
                "  Deadline    : " + top.getDeadlineTime() + " menit\n\n" +
                "Rute Terpendek:\n" +
                "  " + sb.toString() + "\n\n" +
                "Total Jarak   : " + hasil.totalJarak + " meter\n" +
                "Status        : Berhasil dioptimasi\n"
            );

            final String orderId = top.getOrderId();

            mapPanel.mulaiAnimasi(hasil.langkahList, hasil.path, hasil.parentMap, () -> {
                SwingUtilities.invokeLater(() -> {
                    orderManager.removeOrder();
                    updateAntreanView();
                    btnProses.setEnabled(true);
                    btnTambah.setEnabled(true);
                    lblStatus.setText("Rute ditemukan! Jarak: " + hasil.totalJarak + " m  |  Pesanan " + orderId + " selesai");
                    lblStatus.setForeground(GREEN);
                });
            });

            lblStatus.setText("Memutar animasi eksplorasi...");
            lblStatus.setForeground(BLUE);
        });
    }

    private void updateAntreanView() {
        if (orderManager.isEmpty()) {
            txtAreaAntrean.setText("Belum ada antrean pesanan.\n\nSilakan tambahkan pesanan baru\nmelalui form di atas.");
        } else {
            java.util.List<DeliveryOrder> list = orderManager.sortOrdersByDeadline();
            StringBuilder sb = new StringBuilder();
            int n = 1;
            for (DeliveryOrder o : list) {
                sb.append(n++).append(". [").append(o.getOrderId()).append("] ")
                  .append(o.getCustomerName()).append("\n")
                  .append("   Tujuan: ").append(o.getDestinationNode())
                  .append("  |  Deadline: ").append(o.getDeadlineTime()).append(" mnt\n\n");
            }
            txtAreaAntrean.setText(sb.toString());
        }
    }
}
