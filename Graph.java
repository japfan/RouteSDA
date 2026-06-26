import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * ============================================================
 * Graph.java - Struktur Data Graph (Adjacency List)
 * ============================================================
 * 
 * Kelas ini merepresentasikan peta jalan/perumahan/kota
 * menggunakan struktur data Graph dengan representasi
 * Adjacency List (HashMap<String, List<Edge>>).
 * 
 * Graph bersifat UNDIRECTED (tidak berarah), sehingga setiap
 * edge yang ditambahkan akan otomatis berlaku dua arah.
 * 
 * Contoh penggunaan:
 *   Graph peta = new Graph();
 *   peta.addNode("Restoran");
 *   peta.addNode("Rumah 1");
 *   peta.addEdge("Restoran", "Rumah 1", 5);
 * 
 * Modul   : Peta & Algoritma Rute (Core Graph)
 * Anggota : Anggota 1
 * 
 * @author Anggota 1
 */
public class Graph {

    // ==================== ATRIBUT ====================

    /**
     * Adjacency List: menyimpan mapping dari setiap node (String)
     * ke daftar edge (List<Edge>) yang terhubung dengannya.
     * 
     * Contoh isi:
     *   "Restoran"    -> [Edge("Perumahan A", 3), Edge("Perumahan B", 7)]
     *   "Perumahan A" -> [Edge("Restoran", 3), Edge("Rumah 1", 2)]
     */
    private HashMap<String, List<Edge>> adjacencyList;

    // ==================== CONSTRUCTOR ====================

    /**
     * Konstruktor: menginisialisasi graph kosong.
     */
    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    // ==================== METHODS ====================

    /**
     * Menambahkan node baru ke dalam graph.
     * Jika node sudah ada, maka tidak akan ditambahkan lagi
     * (mencegah penimpaan daftar edge yang sudah ada).
     *
     * @param nodeName nama node yang akan ditambahkan (contoh: "Restoran")
     */
    public void addNode(String nodeName) {
        // putIfAbsent memastikan node tidak ditimpa jika sudah ada
        adjacencyList.putIfAbsent(nodeName, new ArrayList<>());
    }

    /**
     * Menambahkan edge (sisi) antara dua node dengan bobot tertentu.
     * Karena graph bersifat UNDIRECTED, edge ditambahkan dua arah:
     *   source -> destination  DAN  destination -> source
     * 
     * Jika salah satu node belum ada di graph, maka node tersebut
     * akan otomatis ditambahkan terlebih dahulu.
     *
     * @param source      nama node asal
     * @param destination nama node tujuan
     * @param weight      bobot/jarak/waktu tempuh dari edge
     */
    public void addEdge(String source, String destination, int weight) {
        // Pastikan kedua node sudah ada di graph
        addNode(source);
        addNode(destination);

        // Tambahkan edge dua arah (undirected)
        adjacencyList.get(source).add(new Edge(destination, weight));
        adjacencyList.get(destination).add(new Edge(source, weight));
    }

    /**
     * Mengambil daftar tetangga (neighbors) dari sebuah node.
     * Tetangga adalah semua node yang terhubung langsung via edge.
     *
     * @param nodeName nama node yang ingin dicari tetangganya
     * @return List<Edge> daftar edge yang terhubung ke node tersebut,
     *         atau empty list jika node tidak ditemukan
     */
    public List<Edge> getNeighbors(String nodeName) {
        // Jika node tidak ditemukan, kembalikan empty list (defensive programming)
        return adjacencyList.getOrDefault(nodeName, Collections.emptyList());
    }

    /**
     * Mengambil semua nama node yang ada di dalam graph.
     *
     * @return Set<String> kumpulan semua nama node
     */
    public Set<String> getAllNodes() {
        return adjacencyList.keySet();
    }

    /**
     * Mengecek apakah sebuah node ada di dalam graph.
     *
     * @param nodeName nama node yang dicek
     * @return true jika node ditemukan, false jika tidak
     */
    public boolean containsNode(String nodeName) {
        return adjacencyList.containsKey(nodeName);
    }

    /**
     * Mencetak seluruh isi graph ke console untuk keperluan debugging.
     * Format output:
     *   Restoran -> [-> Perumahan A (bobot: 3), -> Rumah 1 (bobot: 5)]
     */
    public void printGraph() {
        System.out.println("========================================");
        System.out.println("         PETA GRAPH (Adjacency List)    ");
        System.out.println("========================================");

        for (String node : adjacencyList.keySet()) {
            System.out.print(node + " -> [");
            List<Edge> edges = adjacencyList.get(node);
            for (int i = 0; i < edges.size(); i++) {
                System.out.print(edges.get(i));
                if (i < edges.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }

        System.out.println("========================================");
    }
}
