/**
 * ============================================================
 * DeliveryOrder.java - Model Class (POJO) untuk Data Pesanan
 * ============================================================
 *
 * Kelas ini merepresentasikan satu pesanan makanan yang masuk.
 * Mengimplementasikan Comparable<DeliveryOrder> agar PriorityQueue
 * di OrderManager dapat mengurutkannya otomatis berdasarkan
 * deadlineTime (pesanan paling mendesak = prioritas tertinggi).
 *
 * ============================================================
 * KETERKAITAN DENGAN MODUL LAIN:
 * ============================================================
 *
 * ► Graph.java (Anggota 1):
 * Kedua field node di bawah WAJIB sinkron dengan nama node
 * yang ada di dalam Graph agar tidak terjadi error saat proses.
 *
 * ► RouteOptimizer.java (Anggota 1):
 * RouteOptimizer.hitungDijkstra(graph, asal, tujuan) membutuhkan
 * DUA titik: asal dan tujuan. Field di class ini menyuplai keduanya:
 *
 * restaurantNode → parameter "asal" di hitungDijkstra()
 * destinationNode → parameter "tujuan" di hitungDijkstra()
 *
 * Contoh pemanggilan dari OrderManager:
 * RouteOptimizer.hitungDijkstra(
 * graph,
 * order.getRestaurantNode(), // "asal"
 * order.getDestinationNode() // "tujuan"
 * );
 *
 * ► OrderManager.java (Anggota 2 - file ini sendiri):
 * Disimpan dalam PriorityQueue<DeliveryOrder> dan diurutkan
 * otomatis melalui compareTo() berdasarkan deadlineTime.
 *
 * Modul : Manajemen Pesanan (Sorting & Priority)
 * Anggota : Anggota 2
 *
 * @author Anggota 2
 */
public class DeliveryOrder implements Comparable<DeliveryOrder> {

    // ==================== ATRIBUT ====================

    /** ID unik pesanan. Format bebas, misal: "ORD-001" */
    private String orderId;

    /** Nama pelanggan pemesan */
    private String customerName;

    /**
     * Node ASAL pengambilan (restoran/dapur/titik kumpul kurir).
     * WAJIB ada di Graph (Anggota 1).
     * → Dikirim sebagai parameter "asal" ke RouteOptimizer.hitungDijkstra()
     */
    private String restaurantNode;

    /**
     * Node TUJUAN pengiriman (rumah pelanggan).
     * WAJIB ada di Graph (Anggota 1) — dicek via Graph.containsNode().
     * → Dikirim sebagai parameter "tujuan" ke RouteOptimizer.hitungDijkstra()
     */
    private String destinationNode;

    /**
     * Batas waktu pengiriman dalam satuan MENIT dari sekarang.
     * Semakin kecil nilainya → semakin mendesak → prioritas lebih tinggi
     * di PriorityQueue OrderManager.
     */
    private int deadlineTime;

    // ==================== CONSTRUCTOR ====================

    /**
     * Konstruktor lengkap untuk membuat objek DeliveryOrder.
     *
     * @param orderId         ID unik pesanan (contoh: "ORD-001")
     * @param customerName    nama pelanggan pemesan
     * @param restaurantNode  node asal/restoran (harus ada di Graph) → "asal"
     *                        Dijkstra
     * @param destinationNode node tujuan/rumah pelanggan (harus ada di Graph) →
     *                        "tujuan" Dijkstra
     * @param deadlineTime    batas waktu pengiriman dalam menit (harus > 0)
     */
    public DeliveryOrder(String orderId, String customerName,
            String restaurantNode, String destinationNode,
            int deadlineTime) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.restaurantNode = restaurantNode;
        this.destinationNode = destinationNode;
        this.deadlineTime = deadlineTime;
    }

    // ==================== GETTER ====================

    /** @return ID unik pesanan */
    public String getOrderId() {
        return orderId;
    }

    /** @return nama pelanggan */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Node asal untuk RouteOptimizer.
     * 
     * @return nama node restoran/asal (parameter "asal" di hitungDijkstra)
     */
    public String getRestaurantNode() {
        return restaurantNode;
    }

    /**
     * Node tujuan untuk RouteOptimizer.
     * 
     * @return nama node tujuan pengiriman (parameter "tujuan" di hitungDijkstra)
     */
    public String getDestinationNode() {
        return destinationNode;
    }

    /** @return batas waktu pengiriman dalam menit */
    public int getDeadlineTime() {
        return deadlineTime;
    }

    // ==================== SETTER ====================

    /** @param orderId ID pesanan yang baru */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /** @param customerName nama pelanggan yang baru */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /** @param restaurantNode node asal/restoran yang baru (harus ada di Graph) */
    public void setRestaurantNode(String restaurantNode) {
        this.restaurantNode = restaurantNode;
    }

    /** @param destinationNode node tujuan yang baru (harus ada di Graph) */
    public void setDestinationNode(String destinationNode) {
        this.destinationNode = destinationNode;
    }

    /** @param deadlineTime batas waktu pengiriman yang baru dalam menit */
    public void setDeadlineTime(int deadlineTime) {
        this.deadlineTime = deadlineTime;
    }

    // ==================== COMPARABLE ====================

    /**
     * Metode pembanding untuk PriorityQueue di OrderManager (min-heap).
     * Pesanan dengan deadlineTime LEBIH KECIL → prioritas lebih tinggi
     * → dikeluarkan lebih dulu dari antrian.
     *
     * @param other DeliveryOrder lain yang dibandingkan
     * @return negatif jika this lebih mendesak, positif jika sebaliknya
     */
    @Override
    public int compareTo(DeliveryOrder other) {
        return Integer.compare(this.deadlineTime, other.deadlineTime);
    }

    // ==================== UTILITY ====================

    /**
     * Representasi string pesanan untuk tampilan di terminal.
     *
     * Contoh output:
     * [ORD-001] Budi | Restoran -> Rumah A | Deadline: 15 menit
     */
    @Override
    public String toString() {
        return String.format("[%-7s] %-12s | %-12s -> %-15s | Deadline: %d menit",
                orderId, customerName, restaurantNode, destinationNode, deadlineTime);
    }
}
