import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * ============================================================
 * RouteOptimizer.java - Utility Class Pencari Rute Terpendek
 * ============================================================
 * 
 * Kelas utilitas yang mengimplementasikan Algoritma Dijkstra
 * untuk mencari rute terpendek antara dua titik pada graph.
 * 
 * Implementasi ini menggunakan Min-Heap (PriorityQueue) untuk
 * mengoptimalkan pemilihan node dengan jarak terkecil pada
 * setiap iterasi.
 * 
 * Modul : Peta & Algoritma Rute (Core Graph)
 * Anggota : Anggota 1
 * 
 * @author Anggota 1
 */
public class RouteOptimizer {

    /**
     * Inner class untuk merepresentasikan sebuah node beserta
     * jarak kumulatifnya dari node asal di dalam PriorityQueue.
     * 
     * Digunakan secara internal oleh algoritma Dijkstra sebagai
     * elemen yang disimpan di Min-Heap.
     */
    private static class NodeJarak {
        String namaNode;
        int jarak;

        NodeJarak(String namaNode, int jarak) {
            this.namaNode = namaNode;
            this.jarak = jarak;
        }
    }

    /*
     * ===========================================================================
     * ANALISIS KOMPLEKSITAS WAKTU (TIME COMPLEXITY) - Algoritma Dijkstra
     * ===========================================================================
     *
     * Notasi:
     * V = jumlah node (vertex) dalam graph
     * E = jumlah edge (sisi) dalam graph
     *
     * ---------------------------------------------------------------------------
     * TAHAP 1: INISIALISASI
     * ---------------------------------------------------------------------------
     * - Membuat HashMap untuk jarak : O(V)
     * - Membuat HashMap untuk previous : O(V)
     * - Membuat HashSet untuk visited : O(1)
     * - Memasukkan node asal ke PQ : O(log 1) = O(1)
     * Total inisialisasi : O(V)
     *
     * ---------------------------------------------------------------------------
     * TAHAP 2: LOOP UTAMA (PROSES DIJKSTRA)
     * ---------------------------------------------------------------------------
     * Pada setiap iterasi:
     * a) Mengambil elemen terkecil dari PriorityQueue (poll):
     * - Operasi poll pada Min-Heap : O(log V)
     * - Dilakukan paling banyak V kali: total O(V log V)
     *
     * b) Memeriksa semua tetangga (neighbors) dari node terpilih:
     * - Setiap edge diperiksa tepat 1x (karena visited set)
     * - Untuk setiap edge yang meng-update jarak, dilakukan
     * operasi offer (insert) ke PriorityQueue: O(log V)
     * - Total untuk semua edge : O(E log V)
     *
     * Total loop utama: O(V log V) + O(E log V) = O((V + E) log V)
     *
     * ---------------------------------------------------------------------------
     * TAHAP 3: REKONSTRUKSI PATH
     * ---------------------------------------------------------------------------
     * - Menelusuri HashMap previous dari tujuan ke asal: O(V) worst case
     * - Membalik list (Collections.reverse) : O(V)
     * Total rekonstruksi : O(V)
     *
     * ---------------------------------------------------------------------------
     * TOTAL KOMPLEKSITAS WAKTU:
     * T(V, E) = O(V) + O((V + E) log V) + O(V)
     * = O((V + E) log V)
     *
     * Untuk graph terhubung dimana E >= V - 1:
     * T(V, E) = O(E log V)
     *
     * ---------------------------------------------------------------------------
     * KOMPLEKSITAS RUANG (SPACE COMPLEXITY):
     * - HashMap jarak : O(V)
     * - HashMap previous : O(V)
     * - HashSet visited : O(V)
     * - PriorityQueue : O(V) pada kasus terburuk (bisa sampai O(E)
     * karena lazy deletion, tapi dibatasi oleh visited)
     * Total : O(V + E)
     *
     * ===========================================================================
     * PERBANDINGAN DENGAN IMPLEMENTASI TANPA MIN-HEAP:
     * ===========================================================================
     * - Tanpa Min-Heap (array biasa) : O(V^2)
     * - Dengan Min-Heap (PriorityQueue): O((V + E) log V)
     * 
     * Untuk graph sparse (E << V^2), implementasi Min-Heap jauh lebih efisien.
     * Untuk graph dense (E ≈ V^2), keduanya sebanding.
     * ===========================================================================
     */

    /**
     * Menghitung rute terpendek dari node asal ke node tujuan
     * menggunakan Algoritma Dijkstra dengan optimasi Min-Heap.
     *
     * @param graph  objek Graph yang merepresentasikan peta
     * @param asal   nama node asal (contoh: "Restoran")
     * @param tujuan nama node tujuan (contoh: "Rumah 1")
     * @return List<String> berisi urutan path terpendek dari asal ke tujuan.
     *         Mengembalikan list kosong jika tidak ada jalur yang ditemukan
     *         atau jika node asal/tujuan tidak ada di graph.
     */
    public static List<String> hitungDijkstra(Graph graph, String asal, String tujuan) {

        // ============================================================
        // VALIDASI INPUT
        // ============================================================

        if (!graph.containsNode(asal)) {
            System.out.println("[ERROR] Node asal '" + asal + "' tidak ditemukan di graph!");
            return new ArrayList<>();
        }

        if (!graph.containsNode(tujuan)) {
            System.out.println("[ERROR] Node tujuan '" + tujuan + "' tidak ditemukan di graph!");
            return new ArrayList<>();
        }

        if (asal.equals(tujuan)) {
            System.out.println("[INFO] Node asal dan tujuan sama: " + asal);
            System.out.println("Total jarak: 0");
            List<String> result = new ArrayList<>();
            result.add(asal);
            return result;
        }

        // ============================================================
        // TAHAP 1: INISIALISASI
        // ============================================================

        // Menyimpan jarak terpendek yang diketahui dari asal ke setiap node
        HashMap<String, Integer> jarak = new HashMap<>();

        // Menyimpan node sebelumnya pada path terpendek (untuk rekonstruksi rute)
        HashMap<String, String> previous = new HashMap<>();

        // Menyimpan node-node yang sudah selesai diproses
        Set<String> visited = new HashSet<>();

        // Min-Heap: selalu mengambil node dengan jarak terkecil terlebih dahulu
        PriorityQueue<NodeJarak> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(nj -> nj.jarak));

        // Inisialisasi semua jarak ke INFINITY, kecuali node asal = 0
        for (String node : graph.getAllNodes()) {
            jarak.put(node, Integer.MAX_VALUE);
        }
        jarak.put(asal, 0);

        // Masukkan node asal ke Min-Heap dengan jarak 0
        minHeap.offer(new NodeJarak(asal, 0));

        // ============================================================
        // TAHAP 2: LOOP UTAMA ALGORITMA DIJKSTRA
        // ============================================================

        while (!minHeap.isEmpty()) {

            // Ambil node dengan jarak terkecil dari Min-Heap
            NodeJarak current = minHeap.poll();
            String currentNode = current.namaNode;
            int currentJarak = current.jarak;

            // Jika node sudah pernah diproses, skip (lazy deletion)
            if (visited.contains(currentNode)) {
                continue;
            }

            // Tandai node sebagai sudah diproses
            visited.add(currentNode);

            // Optimasi: jika sudah sampai ke tujuan, hentikan pencarian
            if (currentNode.equals(tujuan)) {
                break;
            }

            // Periksa semua tetangga dari node saat ini
            for (Edge edge : graph.getNeighbors(currentNode)) {
                String neighbor = edge.getDestination();
                int bobot = edge.getWeight();

                // Skip tetangga yang sudah diproses
                if (visited.contains(neighbor)) {
                    continue;
                }

                // Hitung jarak baru melalui node saat ini
                int jarakBaru = currentJarak + bobot;

                // Jika jarak baru lebih kecil, update (RELAXATION step)
                if (jarakBaru < jarak.get(neighbor)) {
                    jarak.put(neighbor, jarakBaru);
                    previous.put(neighbor, currentNode);

                    // Masukkan ke Min-Heap dengan jarak yang diperbarui
                    minHeap.offer(new NodeJarak(neighbor, jarakBaru));
                }
            }
        }

        // ============================================================
        // TAHAP 3: REKONSTRUKSI PATH
        // ============================================================

        List<String> path = new ArrayList<>();

        // Cek apakah tujuan dapat dicapai
        if (!previous.containsKey(tujuan)) {
            System.out.println("[INFO] Tidak ada jalur dari '" + asal + "' ke '" + tujuan + "'.");
            return path; // Kembalikan list kosong
        }

        // Telusuri path dari tujuan ke asal menggunakan HashMap previous
        String step = tujuan;
        while (step != null) {
            path.add(step);
            step = previous.get(step); // null ketika sampai di node asal
        }

        // Balik urutan path: dari [tujuan...asal] menjadi [asal...tujuan]
        Collections.reverse(path);

        // ============================================================
        // OUTPUT HASIL
        // ============================================================

        System.out.println("========================================");
        System.out.println("   HASIL PENCARIAN RUTE TERPENDEK");
        System.out.println("========================================");
        System.out.println("Dari   : " + asal);
        System.out.println("Ke     : " + tujuan);
        System.out.println("----------------------------------------");
        System.out.print("Rute   : ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("Total jarak: " + jarak.get(tujuan));
        System.out.println("========================================");

        return path;
    }

    /**
     * Menghitung TOTAL JARAK terpendek dari node asal ke node tujuan.
     *
     * Dipisah dari hitungDijkstra() agar MainFrame (GUI) dapat menampilkan
     * angka jarak langsung di layar tanpa hanya mengandalkan output console.
     *
     * Menggunakan logika Dijkstra yang sama namun tanpa rekonstruksi path
     * dan tanpa output console — murni mengembalikan nilai integer.
     *
     * @param graph  objek Graph peta
     * @param asal   nama node asal
     * @param tujuan nama node tujuan
     * @return total jarak terpendek (int), atau -1 jika tidak ada jalur
     */
    public static int hitungTotalJarak(Graph graph, String asal, String tujuan) {
        // Validasi node
        if (!graph.containsNode(asal) || !graph.containsNode(tujuan))
            return -1;
        if (asal.equals(tujuan))
            return 0;

        HashMap<String, Integer> jarak = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<NodeJarak> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(nj -> nj.jarak));

        // Inisialisasi semua jarak ke INFINITY
        for (String node : graph.getAllNodes())
            jarak.put(node, Integer.MAX_VALUE);
        jarak.put(asal, 0);
        minHeap.offer(new NodeJarak(asal, 0));

        while (!minHeap.isEmpty()) {
            NodeJarak current = minHeap.poll();
            if (visited.contains(current.namaNode))
                continue;
            visited.add(current.namaNode);
            if (current.namaNode.equals(tujuan))
                break; // Early exit

            for (Edge edge : graph.getNeighbors(current.namaNode)) {
                if (visited.contains(edge.getDestination()))
                    continue;
                int jarakBaru = current.jarak + edge.getWeight();
                if (jarakBaru < jarak.getOrDefault(edge.getDestination(), Integer.MAX_VALUE)) {
                    jarak.put(edge.getDestination(), jarakBaru);
                    minHeap.offer(new NodeJarak(edge.getDestination(), jarakBaru));
                }
            }
        }

        int hasil = jarak.getOrDefault(tujuan, Integer.MAX_VALUE);
        return (hasil == Integer.MAX_VALUE) ? -1 : hasil; // -1 = tidak ada jalur
    }
}