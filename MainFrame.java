import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrame extends JFrame {
    private Graph graph;
    private OrderManager orderManager;

    // Komponen GUI
    private MapPanel mapPanel;
    private JTextArea txtAreaAntrean;
    private JTextArea txtAreaRute;
    private JTextField txtOrderId, txtCustomerName, txtDestination, txtDeadline;

    public MainFrame() {
        // 1. Inisialisasi Data & Modul asli dari Anggota 1 & 2
        graph = new Graph();
        orderManager = new OrderManager();
        
        // MapSeeder asli membutuhkan dua parameter (graph dan orderManager)
        MapSeeder.seedMap(graph, orderManager); 

        // 2. Pengaturan Dasar Window GUI
        setTitle("Aplikasi Optimasi Rute Pengiriman Makanan - RouteSDA");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 3. Bangun Komponen GUI
        initComponent();
        
        // Tampilkan antrean data awal dari seeder
        updateAntreanView();
    }

    private void initComponent() {
        // --- PANEL KIRI: Form Input & Antrean Pesanan ---
        JPanel panelKiri = new JPanel(new BorderLayout(5, 5));
        panelKiri.setPreferredSize(new Dimension(340, 700));
        
        // Form Input Pesanan Baru (Disesuaikan dengan DeliveryOrder asli)
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder("Input Pesanan Baru"));
        
        panelInput.add(new JLabel(" Order ID:"));
        txtOrderId = new JTextField(); 
        panelInput.add(txtOrderId);

        panelInput.add(new JLabel(" Nama Pelanggan:"));
        txtCustomerName = new JTextField(); 
        panelInput.add(txtCustomerName);
        
        panelInput.add(new JLabel(" Tujuan (Node Peta):"));
        txtDestination = new JTextField(); 
        panelInput.add(txtDestination);
        
        panelInput.add(new JLabel(" Deadline (Menit):"));
        txtDeadline = new JTextField(); 
        panelInput.add(txtDeadline);
        
        JButton btnTambah = new JButton("Tambah Pesanan");
        panelInput.add(new JLabel("")); // Spacer kosong
        panelInput.add(btnTambah);
        
        panelKiri.add(panelInput, BorderLayout.NORTH);

        // Tampilan Daftar Antrean
        txtAreaAntrean = new TextAreaCustom("Belum ada antrean pesanan.");
        JScrollPane scrollAntrean = new JScrollPane(txtAreaAntrean);
        scrollAntrean.setBorder(BorderFactory.createTitledBorder("Daftar Antrean Pesanan (Priority Queue)"));
        panelKiri.add(scrollAntrean, BorderLayout.CENTER);
        
        add(panelKiri, BorderLayout.WEST);

        // --- PANEL KANAN: Visualisasi Grafis & Navigasi ---
        JPanel panelKanan = new JPanel(new GridLayout(2, 1, 5, 5));
        
        // Memasang MapPanel visual lingkaran
        mapPanel = new MapPanel(graph);
        panelKanan.add(mapPanel);

        // Panel Hasil Kalkulasi Rute Dijkstra
        JPanel panelProsesRute = new JPanel(new BorderLayout(5, 5));
        JButton btnProses = new JButton("Cari Rute Tercepat untuk Pesanan Teratas");
        btnProses.setFont(new Font("Arial", Font.BOLD, 13));
        btnProses.setBackground(new Color(34, 139, 34));
        btnProses.setForeground(Color.WHITE);
        
        txtAreaRute = new TextAreaCustom("Rute tercepat akan muncul di sini setelah tombol di atas diklik.");
        JScrollPane scrollRute = new JScrollPane(txtAreaRute);
        scrollRute.setBorder(BorderFactory.createTitledBorder("Hasil Navigasi Jarak Terpendek (Dijkstra)"));
        
        panelProsesRute.add(btnProses, BorderLayout.NORTH);
        panelProsesRute.add(scrollRute, BorderLayout.CENTER);
        panelKanan.add(panelProsesRute);

        add(panelKanan, BorderLayout.CENTER);

        // --- 4. LOGIKA TOMBOL & INTEGRASI ---
        
        // Aksi Tombol "Tambah Pesanan"
        btnTambah.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String id = txtOrderId.getText().trim();
                    String customer = txtCustomerName.getText().trim();
                    String dest = txtDestination.getText().trim(); 
                    String deadlineStr = txtDeadline.getText().trim();

                    if (id.isEmpty() || customer.isEmpty() || dest.isEmpty() || deadlineStr.isEmpty()) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Semua field input harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Validasi keaslian node tujuan lewat Graph asli Anggota 1
                    if (!graph.containsNode(dest)) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Node tujuan tidak ditemukan di peta!", "Gagal", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int deadline = Integer.parseInt(deadlineStr);

                    // Membuat DeliveryOrder dengan 5 parameter asli (Mengasumsikan asal kurir dari "Restoran")
                    DeliveryOrder order = new DeliveryOrder(id, customer, "Restoran", dest, deadline);
                    
                    // Memasukkan ke OrderManager asli (membutuhkan objek order dan graph)
                    orderManager.addOrder(order, graph);

                    // Refresh teks antrean di GUI
                    updateAntreanView();
                    
                    // Reset field input
                    txtOrderId.setText("");
                    txtCustomerName.setText("");
                    txtDestination.setText("");
                    txtDeadline.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Input Deadline harus berupa angka bulat murni!", "Error Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Aksi Tombol "Cari Rute Tercepat"
        btnProses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Menggunakan getTopOrder() asli milik Anggota 2
                DeliveryOrder topOrder = orderManager.getTopOrder();
                
                if (topOrder != null) {
                    String asal = topOrder.getRestaurantNode(); 
                    String tujuan = topOrder.getDestinationNode();

                    // 1. Ambil List urutan langkah rute jalan (Dijkstra Path)
                    List<String> pathRute = RouteOptimizer.hitungDijkstra(graph, asal, tujuan);
                    
                    // 2. Ambil total angka jaraknya (Dijkstra Total Distance)
                    int totalJarak = RouteOptimizer.hitungTotalJarak(graph, asal, tujuan);
                    
                    // Gabungkan list path menjadi string estetik (contoh: Restoran -> Perumahan B -> Perumahan D)
                    StringBuilder ruteSby = new StringBuilder();
                    for (int i = 0; i < pathRute.size(); i++) {
                        ruteSby.append(pathRute.get(i));
                        if (i < pathRute.size() - 1) {
                            ruteSby.append(" -> ");
                        }
                    }

                    // Tampilkan hasil kalkulasi ke text area kanan bawah
                    txtAreaRute.setText("MENGANTAR PESANAN:\n" +
                                      "-----------------------------------------\n" +
                                      "Order ID         : " + topOrder.getOrderId() + "\n" +
                                      "Nama Pelanggan   : " + topOrder.getCustomerName() + "\n" +
                                      "Lokasi Asal      : " + asal + "\n" +
                                      "Lokasi Tujuan    : " + tujuan + "\n" +
                                      "Batas Deadline   : " + topOrder.getDeadlineTime() + " menit\n" +
                                      "-----------------------------------------\n" + 
                                      "HASIL OPTIMASI ALGORITMA DIJKSTRA:\n" +
                                      "Rute Tercepat    : " + ruteSby.toString() + "\n" +
                                      "Total Jarak      : " + totalJarak + " meter.\n" +
                                      "Status           : Berhasil diproses oleh kurir.");
                    
                    // Menggunakan removeOrder() asli milik Anggota 2 untuk menghapus pesanan setelah diproses
                    orderManager.removeOrder();
                    updateAntreanView();
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Tidak ada antrean pesanan makanan saat ini.", "Antrean Kosong", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private void updateAntreanView() {
        if (orderManager.isEmpty()) {
            txtAreaAntrean.setText("Belum ada antrean pesanan.");
        } else {
            // Memanfaatkan method bawaan sortOrdersByDeadline() asli dari Anggota 2
            java.util.List<DeliveryOrder> snapshot = orderManager.sortOrdersByDeadline();
            StringBuilder sb = new StringBuilder();
            int no = 1;
            for (DeliveryOrder o : snapshot) {
                sb.append(no++).append(". ")
                  .append("[").append(o.getOrderId()).append("] ")
                  .append(o.getCustomerName()).append("\n")
                  .append("   Tujuan: ").append(o.getDestinationNode())
                  .append(" | Deadline: ").append(o.getDeadlineTime()).append(" mnt\n\n");
            }
            txtAreaAntrean.setText(sb.toString());
        }
    }
}

// --- HELPER COMPONENT: Agar text area rapi & tidak bisa diketik manual oleh user ---
class TextAreaCustom extends JTextArea {
    public TextAreaCustom(String text) {
        super(text);
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        setBackground(new Color(245, 245, 245));
    }
}