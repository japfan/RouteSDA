/**
 * ============================================================
 * MainFrame.java - Dashboard Visual Sistem Optimasi Pengiriman
 * ============================================================
 * Menghubungkan visualisasi data input dari pengguna langsung ke dalam 
 * struktur data Graf (Modul 1) dan PriorityQueue Min-Heap (Modul 2).
 * * Modul   : Modul Antarmuka & Integrasi (GUI & Main)
 * Anggota : Anggota 3
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrame extends JFrame {
    private Graph graph;
    private OrderManager orderManager;

    // Komponen visual GUI Swing
    private JTextArea txtAreaPeta;
    private JTextArea txtAreaAntrean;
    private JTextArea txtAreaRute;
    private JTextField txtOrderId, txtCustomerName, txtRestaurant, txtDestination, txtDeadline;

    public MainFrame() {
        // 1. Instansiasi Data Core Proyek & Jalankan Seeder
        graph = new Graph();
        orderManager = new OrderManager();
        MapSeeder.seedMap(graph, orderManager); 

        // 2. Properti Jendela Utama (Window)
        setTitle("Sistem Optimasi Rute Pengiriman Makanan - Dashboard Logistik");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(10, 10));

        // 3. Membangun Komponen Tampilan
        initComponents();
        
        // 4. Sinkronisasi Data Awal ke Layar Visual
        updatePetaDisplay();
        updateAntreanDisplay();
    }

    private void initComponents() {
        // ==================== PANEL KIRI (INPUT FORM & ANTREAN) ====================
        JPanel panelKiri = new JPanel(new BorderLayout(5, 5));
        panelKiri.setPreferredSize(new Dimension(380, 0));
        
        JPanel panelInput = new JPanel(new GridLayout(6, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Pesanan Baru"));
        
        panelInput.add(new JLabel(" Order ID:"));
        txtOrderId = new JTextField(); panelInput.add(txtOrderId);
        
        panelInput.add(new JLabel(" Nama Pelanggan:"));
        txtCustomerName = new JTextField(); panelInput.add(txtCustomerName);

        panelInput.add(new JLabel(" Node Restoran (Asal):"));
        txtRestaurant = new JTextField("Restoran");
        panelInput.add(txtRestaurant);
        
        panelInput.add(new JLabel(" Node Tujuan (Rumah):"));
        txtDestination = new JTextField(); panelInput.add(txtDestination);
        
        panelInput.add(new JLabel(" Deadline (Menit):"));
        txtDeadline = new JTextField(); panelInput.add(txtDeadline);
        
        JButton btnTambah = new JButton("Tambah Pesanan");
        panelInput.add(new JLabel("")); 
        panelInput.add(btnTambah);
        panelKiri.add(panelInput, BorderLayout.NORTH);

        txtAreaAntrean = new ReadOnlyTextArea("");
        JScrollPane scrollAntrean = new JScrollPane(txtAreaAntrean);
        scrollAntrean.setBorder(BorderFactory.createTitledBorder("Daftar Antrean (Priority Min-Heap By Deadline)"));
        panelKiri.add(scrollAntrean, BorderLayout.CENTER);
        
        add(panelKiri, BorderLayout.WEST);

        // ==================== PANEL KANAN (MAP & LOGISTIK RUTE) ====================
        JPanel panelKanan = new JPanel(new GridLayout(2, 1, 5, 5));
        
        txtAreaPeta = new ReadOnlyTextArea("");
        JScrollPane scrollPeta = new JScrollPane(txtAreaPeta);
        scrollPeta.setBorder(BorderFactory.createTitledBorder("Informasi Jaringan Jalan (Adjacency List Peta)"));
        panelKanan.add(scrollPeta);

        JPanel panelProsesRute = new JPanel(new BorderLayout(5, 5));
        JButton btnProses = new JButton("Proses & Cari Rute Tercepat untuk Pesanan Teratas");
        btnProses.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnProses.setBackground(new Color(46, 204, 113));
        btnProses.setForeground(Color.WHITE);
        
        txtAreaRute = new ReadOnlyTextArea("Belum ada rute yang diproses.");
        JScrollPane scrollRute = new JScrollPane(txtAreaRute);
        scrollRute.setBorder(BorderFactory.createTitledBorder("Console Navigasi / Hasil Optimasi Rute Dijkstra"));
        
        panelProsesRute.add(btnProses, BorderLayout.NORTH);
        panelProsesRute.add(scrollRute, BorderLayout.CENTER);
        panelKanan.add(panelProsesRute);

        add(panelKanan, BorderLayout.CENTER);

        // ==================== AKSI TOMBOL (INTEGRASI FITUR) ====================
        
        btnTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String id = txtOrderId.getText().trim();
                    String name = txtCustomerName.getText().trim();
                    String rest = txtRestaurant.getText().trim();
                    String dest = txtDestination.getText().trim();
                    String deadlineText = txtDeadline.getText().trim();

                    if (id.isEmpty() || name.isEmpty() || rest.isEmpty() || dest.isEmpty() || deadlineText.isEmpty()) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Semua kolom input wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int deadline = Integer.parseInt(deadlineText);
                    DeliveryOrder order = new DeliveryOrder(id, name, rest, dest, deadline);
                    
                    // Memanggil validasi penuh milik Anggota 2
                    boolean sukses = orderManager.addOrder(order, graph);

                    if (sukses) {
                        updateAntreanDisplay();
                        txtOrderId.setText("");
                        txtCustomerName.setText("");
                        txtDestination.setText("");
                        txtDeadline.setText("");
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "Gagal menambahkan pesanan! Pastikan Node Asal & Tujuan terdaftar di peta.", "Kesalahan Validasi Peta", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Input Deadline harus berupa angka bulat!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnProses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeliveryOrder topOrder = orderManager.getTopOrder();
                
                if (topOrder != null) {
                    // Sinkronisasi Terintegrasi penuh ke modul logistik kurir
                    List<String> ruteList = orderManager.cariRuteTopOrder(graph);
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("========================================\n");
                    sb.append("   HASIL NAVIGASI KURIR (DIJKSTRA)\n");
                    sb.append("========================================\n");
                    sb.append("Order ID    : ").append(topOrder.getOrderId()).append("\n");
                    sb.append("Pelanggan   : ").append(topOrder.getCustomerName()).append("\n");
                    sb.append("Titik Ambil : ").append(topOrder.getRestaurantNode()).append("\n");
                    sb.append("Titik Antar : ").append(topOrder.getDestinationNode()).append("\n");
                    sb.append("----------------------------------------\n");
                    sb.append("RUTE TERCEPAT:\n");
                    
                    if(ruteList.isEmpty()) {
                        sb.append("Tidak ditemukan rute/jalur penghubung di dalam peta!\n");
                    } else {
                        for (int i = 0; i < ruteList.size(); i++) {
                            sb.append(ruteList.get(i));
                            if (i < ruteList.size() - 1) sb.append(" -> ");
                        }
                        sb.append("\n");
                    }
                    sb.append("========================================\n");
                    
                    txtAreaRute.setText(sb.toString());
                    
                    // Selesai diproses kurir, pesanan dihapus dari Heap
                    orderManager.removeOrder();
                    updateAntreanDisplay();
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Antrean pengiriman kosong!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    /**
     * Membaca representasi graf internal dan mencetaknya menjadi struktur Adjacency List pada GUI.
     * * ANALISIS KOMPLEKSITAS WAKTU:
     * O(V + E) di mana V adalah jumlah Node dan E adalah jumlah Sisi/Jalan (Edge).
     * Fungsi ini mengiterasi setiap node yang ada di graf (O(V)) lalu mengambil seluruh tetangga
     * dari node tersebut (O(E)) untuk dirender dalam bentuk teks ke dalam komponen visual Swing JTextArea.
     */
    private void updatePetaDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("             DAFTAR JALAN PETA PERUMAHAN          \n");
        sb.append("==================================================\n");
        for (String node : graph.getAllNodes()) {
            sb.append(node).append(" -> [");
            List<Edge> edges = graph.getNeighbors(node);
            for (int i = 0; i < edges.size(); i++) {
                sb.append(edges.get(i).getDestination())
                  .append(" (bobot: ")
                  .append(edges.get(i).getWeight())
                  .append(")");
                if (i < edges.size() - 1) sb.append(", ");
            }
            sb.append("]\n");
        }
        txtAreaPeta.setText(sb.toString());
    }

    /**
     * Mengambil snapshot antrean berprioritas dan memperbarui daftar tampilan teks di layar GUI.
     * * ANALISIS KOMPLEKSITAS WAKTU:
     * O(N log N) di mana N adalah total jumlah pesanan yang aktif di dalam antrean.
     * Untuk menampilkan data secara berurutan di layar tanpa merusak struktur asli Min-Heap dari 
     * PriorityQueue, fungsi memanggil orderManager.sortOrdersByDeadline() yang melakukan duplikasi 
     * array dan melakukan pengurutan ulang menggunakan Timsort (O(N log N)), kemudian mencetak string 
     * sebanyak O(N) ke GUI.
     */
    private void updateAntreanDisplay() {
        if (orderManager.isEmpty()) {
            txtAreaAntrean.setText("Antrean kosong. Silakan input pesanan baru.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Total pesanan aktif: %d\n", orderManager.getSize()));
        sb.append("----------------------------------------------------------------------\n");
        
        List<DeliveryOrder> snapshot = orderManager.sortOrdersByDeadline();
        for (int i = 0; i < snapshot.size(); i++) {
            sb.append(String.format("%d. %s\n", (i + 1), snapshot.get(i).toString()));
        }
        txtAreaAntrean.setText(sb.toString());
    }
}

// Komponen Tambahan untuk Mengunci Text Area agar tidak bisa dimodifikasi manual oleh user
class ReadOnlyTextArea extends JTextArea {
    public ReadOnlyTextArea(String text) {
        super(text);
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setFont(new Font("Monospaced", Font.PLAIN, 12));
    }
}