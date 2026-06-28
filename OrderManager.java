import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * ============================================================
 * OrderManager.java - Manajer Antrian Pesanan Pengiriman
 * ============================================================
 *
 * Mengelola seluruh daftar pesanan makanan menggunakan
 * PriorityQueue<DeliveryOrder> (MIN-HEAP by deadlineTime).
 *
 * Pesanan dengan deadline TERKECIL selalu berada di posisi atas
 * antrian — kurir selalu mengerjakan pesanan paling mendesak dulu.
 *
 * ============================================================
 * KETERKAITAN DENGAN MODUL LAIN:
 * ============================================================
 *
 * ► Graph.java (Anggota 1):
 * addOrder() menerima Graph untuk memvalidasi bahwa kedua node
 * (restaurantNode dan destinationNode) benar-benar ada di peta
 * sebelum pesanan masuk antrian.
 *
 * Metode yang digunakan: Graph.containsNode(String)
 *
 * ► RouteOptimizer.java (Anggota 1):
 * cariRuteTopOrder() mengintegrasikan kedua modul:
 * 1. Mengambil pesanan paling atas (getTopOrder)
 * 2. Memanggil RouteOptimizer.hitungDijkstra() dengan
 * restaurantNode → "asal" dan destinationNode → "tujuan"
 * 3. Mengembalikan List<String> rute yang dihasilkan
 *
 * Metode yang digunakan:
 * RouteOptimizer.hitungDijkstra(Graph, String asal, String tujuan)
 *
 * ► DeliveryOrder.java (Anggota 2):
 * Elemen yang disimpan di dalam PriorityQueue.
 *
 * ► MenuGUI.java (Anggota 3) — alur penggunaan yang disarankan:
 * // Tambah pesanan dengan validasi peta
 * manager.addOrder(pesanan, graph);
 *
 * // Cari rute untuk pesanan teratas (menu 4)
 * List<String> rute = manager.cariRuteTopOrder(graph);
 *
 * // Setelah kurir selesai antar:
 * manager.removeOrder();
 *
 * Modul : Manajemen Pesanan (Sorting & Priority)
 * Anggota : Anggota 2
 *
 * @author Anggota 2
 */
public class OrderManager {

    // ==================== ATRIBUT ====================

    /**
     * Antrian prioritas utama. Menggunakan natural ordering dari
     * DeliveryOrder.compareTo() → min-heap berdasarkan deadlineTime.
     * Pesanan paling mendesak (deadline terkecil) selalu di posisi poll().
     */
    private PriorityQueue<DeliveryOrder> orderQueue;

    // ==================== CONSTRUCTOR ====================

    /**
     * Konstruktor: membuat antrian pesanan kosong.
     */
    public OrderManager() {
        this.orderQueue = new PriorityQueue<>();
    }

    // ==================== METODE UTAMA ====================

    /**
     * Menambahkan pesanan baru ke antrian dengan validasi penuh ke Graph.
     *
     * Validasi yang dilakukan (urutan):
     * 1. Objek pesanan tidak boleh null
     * 2. restaurantNode HARUS ada di Graph (titik awal Dijkstra)
     * 3. destinationNode HARUS ada di Graph (titik tujuan Dijkstra)
     *
     * Jika salah satu node tidak ditemukan di peta, pesanan DITOLAK.
     * Ini mencegah error NullPointerException saat RouteOptimizer
     * nantinya dipanggil dengan node yang tidak valid.
     *
     * @param order pesanan yang akan ditambahkan
     * @param graph peta dari Anggota 1 untuk validasi kedua node
     * @return true jika berhasil, false jika ada validasi yang gagal
     */
    public boolean addOrder(DeliveryOrder order, Graph graph) {
        // Guard: pesanan tidak boleh null
        if (order == null) {
            System.out.println("[ERROR] Gagal: objek pesanan null.");
            return false;
        }

        // Validasi restaurantNode → ini akan jadi "asal" di hitungDijkstra()
        if (!graph.containsNode(order.getRestaurantNode())) {
            System.out.println("[ERROR] Node restoran '" + order.getRestaurantNode()
                    + "' tidak ditemukan di peta! Pesanan " + order.getOrderId() + " ditolak.");
            System.out.println("[INFO] Gunakan salah satu node: " + graph.getAllNodes());
            return false;
        }

        // Validasi destinationNode → ini akan jadi "tujuan" di hitungDijkstra()
        if (!graph.containsNode(order.getDestinationNode())) {
            System.out.println("[ERROR] Node tujuan '" + order.getDestinationNode()
                    + "' tidak ditemukan di peta! Pesanan " + order.getOrderId() + " ditolak.");
            System.out.println("[INFO] Gunakan salah satu node: " + graph.getAllNodes());
            return false;
        }

        // Semua validasi lolos → masuk antrian prioritas
        orderQueue.offer(order);
        System.out.println("[OK] Pesanan " + order.getOrderId()
                + " (" + order.getCustomerName() + ") berhasil ditambahkan ke antrian.");
        return true;
    }

    /**
     * Menambahkan pesanan TANPA validasi Graph.
     * Digunakan oleh MapSeeder / Anggota 3 saat seeding data dummy agar
     * tidak perlu melewati proses validasi.
     *
     * PERHATIAN: pastikan node-nya memang ada di Graph sebelum memanggil
     * cariRuteTopOrder(), atau RouteOptimizer akan mengembalikan path kosong.
     *
     * @param order pesanan yang akan ditambahkan
     * @return true jika berhasil, false jika order null
     */
    public boolean addOrderWithoutValidation(DeliveryOrder order) {
        if (order == null) {
            System.out.println("[ERROR] Gagal: objek pesanan null.");
            return false;
        }
        orderQueue.offer(order);
        return true;
    }

    /**
     * Mengambil DAN MENGHAPUS pesanan dengan prioritas tertinggi
     * (deadlineTime terkecil) dari antrian.
     *
     * Dipanggil oleh MenuGUI setelah kurir selesai mengantarkan pesanan
     * (biasanya setelah cariRuteTopOrder() berhasil).
     *
     * @return DeliveryOrder teratas, atau null jika antrian kosong
     */
    public DeliveryOrder removeOrder() {
        if (orderQueue.isEmpty()) {
            System.out.println("[INFO] Antrian kosong, tidak ada pesanan yang dihapus.");
            return null;
        }
        DeliveryOrder selesai = orderQueue.poll();
        System.out.println("[OK] Pesanan " + selesai.getOrderId()
                + " (" + selesai.getCustomerName() + ") selesai dan dihapus dari antrian.");
        return selesai;
    }

    /**
     * Mengintip pesanan paling atas TANPA menghapusnya dari antrian.
     *
     * Digunakan MenuGUI sebelum memanggil cariRuteTopOrder() untuk
     * menampilkan info pesanan yang akan diproses selanjutnya.
     *
     * @return DeliveryOrder dengan deadline terkecil, atau null jika kosong
     */
    public DeliveryOrder getTopOrder() {
        if (orderQueue.isEmpty()) {
            System.out.println("[INFO] Antrian pesanan saat ini kosong.");
            return null;
        }
        return orderQueue.peek();
    }

    /**
     * ═══════════════════════════════════════════════════════════
     * METODE INTEGRASI UTAMA dengan RouteOptimizer (Anggota 1)
     * ═══════════════════════════════════════════════════════════
     *
     * Mencari rute terpendek untuk pesanan PALING ATAS di antrian
     * dengan memanggil RouteOptimizer.hitungDijkstra().
     *
     * Alur kerja internal:
     * 1. Ambil pesanan teratas via getTopOrder() (peek, tidak dihapus)
     * 2. Ekstrak restaurantNode → sebagai parameter "asal" Dijkstra
     * 3. Ekstrak destinationNode → sebagai parameter "tujuan" Dijkstra
     * 4. Panggil RouteOptimizer.hitungDijkstra(graph, asal, tujuan)
     * 5. Kembalikan List<String> rute langkah-demi-langkah
     *
     * Catatan untuk MenuGUI (Anggota 3):
     * - Metode ini TIDAK menghapus pesanan dari antrian.
     * - Panggil removeOrder() secara terpisah setelah pengiriman selesai.
     *
     * @param graph peta dari Anggota 1 yang digunakan oleh Dijkstra
     * @return List<String> rute terpendek (kosong jika antrian kosong
     *         atau tidak ada jalur yang ditemukan)
     */
    public List<String> cariRuteTopOrder(Graph graph) {
        // Pastikan ada pesanan di antrian
        DeliveryOrder topOrder = getTopOrder();
        if (topOrder == null) {
            System.out.println("[INFO] Tidak ada pesanan untuk dicarikan rute.");
            return new ArrayList<>();
        }

        // Tampilkan info pesanan yang sedang diproses
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       MEMPROSES PESANAN PRIORITAS        ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Pesanan  : " + topOrder.getOrderId());
        System.out.println("║  Pelanggan: " + topOrder.getCustomerName());
        System.out.println("║  Dari     : " + topOrder.getRestaurantNode());
        System.out.println("║  Ke       : " + topOrder.getDestinationNode());
        System.out.println("║  Deadline : " + topOrder.getDeadlineTime() + " menit");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  Memanggil RouteOptimizer.hitungDijkstra...");
        System.out.println("╚══════════════════════════════════════════╝");

        // ─────────────────────────────────────────────────────────────
        // INTI INTEGRASI: Panggil Dijkstra dari Anggota 1
        // - restaurantNode → "asal" (titik pickup kurir)
        // - destinationNode → "tujuan" (rumah pelanggan)
        // ─────────────────────────────────────────────────────────────
        List<String> rute = RouteOptimizer.hitungDijkstra(
                graph,
                topOrder.getRestaurantNode(), // "asal" ke hitungDijkstra
                topOrder.getDestinationNode() // "tujuan" ke hitungDijkstra
        );

        return rute;
    }

    /**
     * Mengurutkan seluruh pesanan berdasarkan deadline (terkecil dulu).
     *
     * TIDAK mengubah isi PriorityQueue asli — mengembalikan salinan
     * sebagai List untuk keperluan tampilan di displayQueue().
     *
     * @return List<DeliveryOrder> terurut dari deadline terkecil
     */
    public List<DeliveryOrder> sortOrdersByDeadline() {
        List<DeliveryOrder> sortedList = new ArrayList<>(orderQueue);
        Collections.sort(sortedList); // Menggunakan DeliveryOrder.compareTo()
        return sortedList;
    }

    /**
     * Menampilkan seluruh isi antrian pesanan ke terminal,
     * diurutkan dari yang paling mendesak (deadline terkecil).
     */
    public void displayQueue() {
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║              DAFTAR ANTRIAN PESANAN PENGIRIMAN                  ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");

        if (orderQueue.isEmpty()) {
            System.out.println("║  (Tidak ada pesanan dalam antrian saat ini)                      ║");
        } else {
            // Ambil snapshot terurut — tidak mengubah antrian asli
            List<DeliveryOrder> snapshot = sortOrdersByDeadline();
            for (int i = 0; i < snapshot.size(); i++) {
                System.out.printf("║  %2d. %s%n", (i + 1), snapshot.get(i).toString());
            }
        }

        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total pesanan dalam antrian: %-35d ║%n", orderQueue.size());
        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
    }

    // ==================== METODE BANTU ====================

    /**
     * @return true jika antrian kosong, false jika masih ada pesanan
     */
    public boolean isEmpty() {
        return orderQueue.isEmpty();
    }

    /**
     * @return jumlah pesanan yang ada di antrian saat ini
     */
    public int getSize() {
        return orderQueue.size();
    }

    /**
     * Menghapus SEMUA pesanan dari antrian (reset total).
     * Berguna untuk pengujian atau skenario reset sistem.
     */
    public void clearQueue() {
        orderQueue.clear();
        System.out.println("[INFO] Seluruh pesanan telah dihapus dari antrian.");
    }
}
