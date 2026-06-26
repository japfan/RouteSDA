/**
 * ============================================================
 * Edge.java - Model Class untuk Representasi Sisi Graph
 * ============================================================
 * 
 * Kelas ini merepresentasikan sebuah sisi (edge) dalam graph
 * yang digunakan pada sistem navigasi rute kurir.
 * 
 * Setiap edge menyimpan informasi:
 *   - destination : nama node tujuan (String)
 *   - weight      : bobot/jarak/waktu tempuh (int)
 * 
 * Modul   : Peta & Algoritma Rute (Core Graph)
 * Anggota : Anggota 1
 * 
 * @author Anggota 1
 */
public class Edge {

    // ==================== ATRIBUT ====================

    /** Nama node tujuan dari edge ini */
    private String destination;

    /** Bobot edge, merepresentasikan jarak (meter) atau waktu tempuh (detik) */
    private int weight;

    // ==================== CONSTRUCTOR ====================

    /**
     * Konstruktor untuk membuat objek Edge baru.
     *
     * @param destination nama node tujuan
     * @param weight      bobot/jarak/waktu dari edge
     */
    public Edge(String destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    // ==================== GETTER ====================

    /**
     * Mengambil nama node tujuan dari edge ini.
     *
     * @return nama node tujuan (String)
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Mengambil bobot/jarak dari edge ini.
     *
     * @return bobot edge (int)
     */
    public int getWeight() {
        return weight;
    }

    // ==================== SETTER ====================

    /**
     * Mengatur nama node tujuan dari edge ini.
     *
     * @param destination nama node tujuan yang baru
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Mengatur bobot/jarak dari edge ini.
     *
     * @param weight bobot edge yang baru
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    // ==================== UTILITY ====================

    /**
     * Representasi string dari Edge untuk keperluan debugging.
     *
     * @return string format: "-> [destination] (bobot: weight)"
     */
    @Override
    public String toString() {
        return "-> " + destination + " (bobot: " + weight + ")";
    }
}
