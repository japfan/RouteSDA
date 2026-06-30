import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Graf tidak berarah berbasis adjacency list (HashMap).
 *
 * Setiap edge berlaku dua arah — menambah edge A→B otomatis
 * menambah B→A dengan bobot yang sama. 
 *
 * Operasi dasar: O(1) untuk addNode/containsNode/getNeighbors
 * (HashMap lookup), O(degree) untuk iterasi tetangga.
 */
public class Graph {

    // node → daftar edge yang terhubung langsung
    // contoh: "Restoran" → [Edge("Perumahan A", 3), Edge("Rumah 1", 5)]
    private HashMap<String, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Tambah node jika belum ada. Aman dipanggil berkali-kali —
     * tidak akan menimpa daftar edge yang sudah ada.
     */
    public void addNode(String nodeName) {
        adjacencyList.putIfAbsent(nodeName, new ArrayList<>());
    }
    
    /**
     * Tambah edge berbobot antara dua node. Karena graf tidak berarah,
     * edge ditambahkan di kedua sisi. Node dibuat otomatis jika belum ada.
     */
    public void addEdge(String source, String destination, int weight) {
        addNode(source);
        addNode(destination);

        adjacencyList.get(source).add(new Edge(destination, weight));
        adjacencyList.get(destination).add(new Edge(source, weight));
    }

    /**
     * Kembalikan daftar tetangga langsung dari suatu node,
     * atau list kosong jika node tidak ditemukan.
     */
    public List<Edge> getNeighbors(String nodeName) {
        return adjacencyList.getOrDefault(nodeName, Collections.emptyList());
    }

    /**
     * Kembalikan semua nama node dalam graf.
     */
    public Set<String> getAllNodes() {
        return adjacencyList.keySet();
    }

    /**
     * Cek apakah node ada dalam graf.
     */
    public boolean containsNode(String nodeName) {
        return adjacencyList.containsKey(nodeName);
    }
}
