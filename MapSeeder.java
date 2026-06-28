/**
 * ============================================================
 * MapSeeder.java - Data Populator Otomatis (Seeder)
 * ============================================================
 * Modul   : Modul Antarmuka & Integrasi (GUI & Main)
 * Anggota : Anggota 3
 */
public class MapSeeder {
    
    /**
     * Mengisi graf peta jalan dan antrean pesanan dengan data simulasi awal.
     * * ANALISIS KOMPLEKSITAS WAKTU:
     * - Menambahkan V node dan E rute/edge: O(V + E)
     * - Menyisipkan data pesanan awal ke Min-Heap: O(N log N), di mana N adalah jumlah pesanan.
     * Karena jumlah data yang dimasukkan bersifat konstan (hardcoded), kompleksitas operasinya adalah O(1).
     * * @param graph objek graf dari Anggota 1
     * @param orderManager objek manajemen antrean dari Anggota 2
     */
    public static void seedMap(Graph graph, OrderManager orderManager) {
        // 1. Daftarkan jalan/edge (Otomatis mendaftarkan Node lewat Graph.addEdge dua arah)
        graph.addEdge("Restoran", "Perumahan A", 4);  
        graph.addEdge("Restoran", "Perumahan B", 6);  
        graph.addEdge("Perumahan A", "Perumahan C", 3);
        graph.addEdge("Perumahan A", "Perumahan D", 8);
        graph.addEdge("Perumahan B", "Perumahan D", 2);
        graph.addEdge("Perumahan B", "Perumahan E", 5);
        graph.addEdge("Perumahan C", "Perumahan D", 1);
        graph.addEdge("Perumahan C", "Perumahan F", 7);
        graph.addEdge("Perumahan D", "Perumahan E", 3);
        graph.addEdge("Perumahan D", "Perumahan F", 4);
        graph.addEdge("Perumahan E", "Perumahan F", 2);

        // 2. Daftarkan dummy pesanan awal agar aplikasi tidak kosong saat live demo
        orderManager.addOrderWithoutValidation(new DeliveryOrder("ORD-001", "Budi", "Restoran", "Perumahan F", 25));
        orderManager.addOrderWithoutValidation(new DeliveryOrder("ORD-002", "Siti", "Restoran", "Perumahan C", 15));
        orderManager.addOrderWithoutValidation(new DeliveryOrder("ORD-003", "Andi", "Restoran", "Perumahan E", 40));
    }
}