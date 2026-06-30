import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


public class RouteOptimizer {

    private static class NodeJarak {
        String namaNode;
        int jarak;

        NodeJarak(String namaNode, int jarak) {
            this.namaNode = namaNode;
            this.jarak = jarak;
        }
    }

    // Merekam satu langkah eksplorasi Dijkstra untuk animasi
    public static class LangkahAnimasi {
        public String nodeDikunjungi;   // node yang sedang diproses
        public String dariNode;         // parent node ini di shortest-path tree
        public int jarakKumulatif;      // jarak dari start ke node ini
        public String tipe;             // "VISIT", "RELAX", "INIT", atau "FINISH"
        public String nodeDiperbarui;   // tetangga yang nilainya turun (hanya untuk RELAX)

        public LangkahAnimasi(String nodeDikunjungi, String dariNode, int jarakKumulatif, String tipe, String nodeDiperbarui) {
            this.nodeDikunjungi = nodeDikunjungi;
            this.dariNode = dariNode;
            this.jarakKumulatif = jarakKumulatif;
            this.tipe = tipe;
            this.nodeDiperbarui = nodeDiperbarui;
        }
    }

    // Hasil lengkap Dijkstra: path, jarak, dan rekaman langkah animasi
    public static class HasilAnimasi {
        public List<String> path;                       // urutan node dari asal ke tujuan
        public int totalJarak;                          // total bobot shortest path
        public List<LangkahAnimasi> langkahList;        // rekaman langkah untuk animasi
        public java.util.Map<String, String> parentMap; // parent tiap node (untuk highlight edge)

        public HasilAnimasi(List<String> path, int totalJarak, List<LangkahAnimasi> langkahList, java.util.Map<String, String> parentMap) {
            this.path = path;
            this.totalJarak = totalJarak;
            this.langkahList = langkahList;
            this.parentMap = parentMap;
        }
    }

    /**
     * Cari rute terpendek dari asal ke tujuan menggunakan Dijkstra.
     *
     * Kompleksitas: O((V + E) log V) — inisialisasi O(V), loop utama
     * memproses tiap edge dengan operasi heap O(log V), rekonstruksi
     * path O(V). Ruang O(V + E) untuk HashMap jarak/previous,
     * HashSet visited, dan PriorityQueue.
     *
     * @param graph  graf peta
     * @param asal   node awal
     * @param tujuan node akhir
     * @return urutan node dari asal ke tujuan, atau list kosong
     *         jika tidak ada jalur / node tidak ditemukan
     */
    public static List<String> hitungDijkstra(Graph graph, String asal, String tujuan) {

        // Validasi input
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

        // --- Inisialisasi ---
        HashMap<String, Integer> jarak = new HashMap<>();
        HashMap<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();

        PriorityQueue<NodeJarak> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(nj -> nj.jarak));

        for (String node : graph.getAllNodes()) {
            jarak.put(node, Integer.MAX_VALUE);
        }
        jarak.put(asal, 0);
        minHeap.offer(new NodeJarak(asal, 0));

        // --- Loop utama Dijkstra ---
        while (!minHeap.isEmpty()) {
            NodeJarak current = minHeap.poll();
            String currentNode = current.namaNode;
            int currentJarak = current.jarak;

            if (visited.contains(currentNode)) {
                continue; // lazy deletion: entri lama di heap
            }
            visited.add(currentNode);

            if (currentNode.equals(tujuan)) {
                break; // early exit — sudah sampai tujuan
            }

            for (Edge edge : graph.getNeighbors(currentNode)) {
                String neighbor = edge.getDestination();
                int bobot = edge.getWeight();

                if (visited.contains(neighbor)) {
                    continue;
                }

                int jarakBaru = currentJarak + bobot;

                if (jarakBaru < jarak.get(neighbor)) {
                    jarak.put(neighbor, jarakBaru);
                    previous.put(neighbor, currentNode);
                    minHeap.offer(new NodeJarak(neighbor, jarakBaru));
                }
            }
        }

        // --- Rekonstruksi path ---
        List<String> path = new ArrayList<>();

        if (!previous.containsKey(tujuan)) {
            System.out.println("[INFO] Tidak ada jalur dari '" + asal + "' ke '" + tujuan + "'.");
            return path;
        }

        String step = tujuan;
        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }
        Collections.reverse(path);

        // Output hasil
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
     * Versi ringkas hitungDijkstra: hanya mengembalikan total jarak,
     * tanpa rekonstruksi path maupun output console. Digunakan oleh
     * MainFrame untuk menampilkan angka jarak langsung di GUI.
     *
     * Kompleksitas sama dengan hitungDijkstra: O((V + E) log V).
     *
     * @param graph  graf peta
     * @param asal   node awal
     * @param tujuan node akhir
     * @return total jarak terpendek, atau -1 jika tidak ada jalur
     */
    public static int hitungTotalJarak(Graph graph, String asal, String tujuan) {
        if (!graph.containsNode(asal) || !graph.containsNode(tujuan))
            return -1;
        if (asal.equals(tujuan))
            return 0;

        HashMap<String, Integer> jarak = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<NodeJarak> minHeap = new PriorityQueue<>(
                Comparator.comparingInt(nj -> nj.jarak));

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
                break;

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
        return (hasil == Integer.MAX_VALUE) ? -1 : hasil;
    }

    /**
     * Dijkstra dengan perekaman langkah demi langkah untuk animasi
     * visual di MapPanel. Tiap kali node dikunjungi atau edge di-relax,
     * satu entri LangkahAnimasi ditambahkan.
     *
     * Kompleksitas: O((V + E) log V) — identik dengan hitungDijkstra,
     * karena pencatatan langkah hanya O(1) per operasi.
     *
     * @param graph  graf peta
     * @param asal   node awal
     * @param tujuan node akhir
     * @return HasilAnimasi berisi path, total jarak, dan daftar langkah
     */
    public static HasilAnimasi hitungDijkstraDenganAnimasi(Graph graph, String asal, String tujuan) {
        List<LangkahAnimasi> langkahList = new ArrayList<>();
        java.util.Map<String, String> parentMap = new java.util.HashMap<>();

        // Validasi input
        if (!graph.containsNode(asal)) {
            System.out.println("[ERROR] Node asal '" + asal + "' tidak ditemukan di graph!");
            return new HasilAnimasi(new ArrayList<>(), -1, langkahList, parentMap);
        }
        if (!graph.containsNode(tujuan)) {
            System.out.println("[ERROR] Node tujuan '" + tujuan + "' tidak ditemukan di graph!");
            return new HasilAnimasi(new ArrayList<>(), -1, langkahList, parentMap);
        }
        if (asal.equals(tujuan)) {
            List<String> path = new ArrayList<>();
            path.add(asal);
            return new HasilAnimasi(path, 0, langkahList, parentMap);
        }

        // --- Inisialisasi ---
        HashMap<String, Integer> jarak = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<NodeJarak> minHeap = new PriorityQueue<>(Comparator.comparingInt(nj -> nj.jarak));

        for (String node : graph.getAllNodes()) {
            jarak.put(node, Integer.MAX_VALUE);
        }
        jarak.put(asal, 0);
        minHeap.offer(new NodeJarak(asal, 0));

        langkahList.add(new LangkahAnimasi(asal, null, 0, "INIT", null));

        // --- Loop utama Dijkstra ---
        while (!minHeap.isEmpty()) {
            NodeJarak current = minHeap.poll();
            String currentNode = current.namaNode;
            int currentJarak = current.jarak;

            if (visited.contains(currentNode)) continue;
            visited.add(currentNode);

            String parent = parentMap.getOrDefault(currentNode, (currentNode.equals(asal) ? null : "unknown"));
            langkahList.add(new LangkahAnimasi(currentNode, parent, currentJarak, "VISIT", null));

            if (currentNode.equals(tujuan)) break;

            for (Edge edge : graph.getNeighbors(currentNode)) {
                String neighbor = edge.getDestination();
                int bobot = edge.getWeight();

                if (visited.contains(neighbor)) continue;

                int jarakBaru = currentJarak + bobot;
                if (jarakBaru < jarak.get(neighbor)) {
                    jarak.put(neighbor, jarakBaru);
                    parentMap.put(neighbor, currentNode);
                    minHeap.offer(new NodeJarak(neighbor, jarakBaru));

                    langkahList.add(new LangkahAnimasi(currentNode, currentNode, jarakBaru, "RELAX", neighbor));
                }
            }
        }

        // --- Rekonstruksi path ---
        List<String> path = new ArrayList<>();
        if (parentMap.containsKey(tujuan) || asal.equals(tujuan)) {
            String step = tujuan;
            while (step != null) {
                path.add(step);
                step = parentMap.get(step);
            }
            Collections.reverse(path);
        }

        int totalJarak = jarak.getOrDefault(tujuan, Integer.MAX_VALUE);
        if (totalJarak == Integer.MAX_VALUE) totalJarak = -1;

        langkahList.add(new LangkahAnimasi(tujuan, null, totalJarak, "FINISH", null));

        System.out.println("========================================");
        System.out.println("   HASIL PENCARIAN RUTE TERPENDEK (ANIMASI)");
        System.out.println("========================================");
        System.out.println("Dari   : " + asal);
        System.out.println("Ke     : " + tujuan);
        System.out.println("Total jarak: " + totalJarak);
        System.out.println("Jumlah langkah animasi: " + langkahList.size());
        System.out.println("========================================");

        return new HasilAnimasi(path, totalJarak, langkahList, parentMap);
    }
}