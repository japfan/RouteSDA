import java.util.List;

/**
 * MainDemo.java - Demo & Testing untuk Modul Peta & Algoritma Rute
 * 
 * File ini digunakan untuk menguji fungsionalitas Edge, Graph,
 * dan RouteOptimizer (Dijkstra) secara end-to-end.
 */
public class MainDemo {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  DEMO: Modul Peta & Algoritma Rute      ║");
        System.out.println("║  Anggota 1 - Core Graph Module           ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println();

        // ========================================
        // 1. BUAT GRAPH (Peta Perumahan)
        // ========================================
        Graph peta = new Graph();

        // Tambahkan edge (otomatis menambah node juga)
        peta.addEdge("Restoran", "Perumahan A", 3);
        peta.addEdge("Restoran", "Perumahan B", 7);
        peta.addEdge("Perumahan A", "Perumahan B", 2);
        peta.addEdge("Perumahan A", "Rumah 1", 5);
        peta.addEdge("Perumahan B", "Rumah 2", 4);
        peta.addEdge("Perumahan B", "Rumah 3", 6);
        peta.addEdge("Rumah 1", "Rumah 3", 1);
        peta.addEdge("Rumah 2", "Rumah 3", 3);

        // Cetak graph
        peta.printGraph();
        System.out.println();

        // ========================================
        // 2. TEST DIJKSTRA - Rute Normal
        // ========================================
        System.out.println(">>> TEST 1: Restoran -> Rumah 1");
        List<String> rute1 = RouteOptimizer.hitungDijkstra(peta, "Restoran", "Rumah 1");
        System.out.println("Path: " + rute1);
        System.out.println();

        System.out.println(">>> TEST 2: Restoran -> Rumah 3");
        List<String> rute2 = RouteOptimizer.hitungDijkstra(peta, "Restoran", "Rumah 3");
        System.out.println("Path: " + rute2);
        System.out.println();

        System.out.println(">>> TEST 3: Restoran -> Rumah 2");
        List<String> rute3 = RouteOptimizer.hitungDijkstra(peta, "Restoran", "Rumah 2");
        System.out.println("Path: " + rute3);
        System.out.println();

        // ========================================
        // 3. TEST EDGE CASES
        // ========================================
        System.out.println(">>> TEST 4: Node asal sama dengan tujuan");
        List<String> rute4 = RouteOptimizer.hitungDijkstra(peta, "Restoran", "Restoran");
        System.out.println("Path: " + rute4);
        System.out.println();

        System.out.println(">>> TEST 5: Node tidak ada di graph");
        List<String> rute5 = RouteOptimizer.hitungDijkstra(peta, "Restoran", "Bandara");
        System.out.println("Path: " + rute5);
        System.out.println();

        // Test node terisolasi (tidak terhubung)
        System.out.println(">>> TEST 6: Node terisolasi (tidak terhubung)");
        peta.addNode("Pulau Terpencil");
        List<String> rute6 = RouteOptimizer.hitungDijkstra(peta, "Restoran", "Pulau Terpencil");
        System.out.println("Path: " + rute6);

        System.out.println();
        System.out.println("=== SEMUA TEST SELESAI ===");
    }
}
