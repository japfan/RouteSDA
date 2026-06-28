import java.util.Scanner;

/**
 * ============================================================
 * InputValidator.java - Helper Class untuk Validasi Input
 * ============================================================
 *
 * Kelas pembantu (utility) dengan semua metode STATIS.
 * Tidak perlu di-instansiasi atau langsung panggil:
 * int n = InputValidator.readPositiveInt(sc, "Masukkan deadline: ");
 *
 * Tujuan utama: membungkus Scanner agar program TIDAK CRASH
 * ketika pengguna memasukkan tipe data yang salah.
 * Contoh masalah: program minta angka, pengguna ketik "abc"
 * jika tanpa validasi: NumberFormatException / InputMismatchException
 * dengan kelas ini: tampil pesan error, lalu minta input ulang
 *
 * Modul : Manajemen Pesanan (Sorting & Priority)
 * Anggota : Anggota 2
 *
 * @author Anggota 2
 */
public class InputValidator {

    // Kelas utility tidak boleh di-instansiasi
    private InputValidator() {
    }

    // ==================== INPUT INTEGER ====================

    /**
     * Membaca satu integer dari pengguna dengan aman (anti-crash).
     * Memakai nextLine() + Integer.parseInt() + try-catch agar program
     * tidak melempar exception ketika pengguna mengetik huruf.
     *
     * @param sc     Scanner yang digunakan
     * @param prompt teks pertanyaan yang ditampilkan ke pengguna
     * @return integer valid dari pengguna (diulang sampai valid)
     */
    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim(); // Selalu baca sebagai String dulu
            try {
                return Integer.parseInt(input); // Coba konversi ke int
            } catch (NumberFormatException e) {
                // Bukan angka: tampilkan error, ulangi
                System.out.println("[ERROR] \"" + input + "\" bukan angka! Masukkan angka bulat.");
            }
        }
    }

    /**
     * Membaca integer POSITIF (>= 1) dari pengguna.
     * Menolak 0, negatif, dan non-angka.
     * Contoh: deadlineTime pesanan (minimal 1 menit).
     *
     * @param sc     Scanner yang digunakan
     * @param prompt teks pertanyaan
     * @return integer >= 1
     */
    public static int readPositiveInt(Scanner sc, String prompt) {
        while (true) {
            int value = readInt(sc, prompt);
            if (value > 0)
                return value;
            System.out.println("[ERROR] Angka harus lebih dari 0! (input: " + value + ")");
        }
    }

    /**
     * Membaca integer NON-NEGATIF (>= 0) dari pengguna.
     * Menolak angka negatif. Cocok untuk bobot/jarak (bisa = 0).
     *
     * @param sc     Scanner yang digunakan
     * @param prompt teks pertanyaan
     * @return integer >= 0
     */
    public static int readNonNegativeInt(Scanner sc, String prompt) {
        while (true) {
            int value = readInt(sc, prompt);
            if (value >= 0)
                return value;
            System.out.println("[ERROR] Angka tidak boleh negatif! (input: " + value + ")");
        }
    }

    /**
     * Membaca integer dalam rentang [min, max] dari pengguna.
     * Paling sering dipakai untuk membaca pilihan menu di MenuGUI.
     *
     * Contoh: readIntInRange(sc, "Pilih (1-4): ", 1, 4)
     *
     * @param sc     Scanner yang digunakan
     * @param prompt teks pertanyaan
     * @param min    batas bawah inklusif
     * @param max    batas atas inklusif
     * @return integer dalam rentang [min, max]
     */
    public static int readIntInRange(Scanner sc, String prompt, int min, int max) {
        while (true) {
            int value = readInt(sc, prompt);
            if (value >= min && value <= max)
                return value;
            System.out.println("[ERROR] Pilihan harus antara " + min
                    + " dan " + max + "! (input: " + value + ")");
        }
    }

    // ==================== INPUT STRING ====================

    /**
     * Membaca satu baris teks dari pengguna (boleh kosong).
     *
     * @param sc     Scanner yang digunakan
     * @param prompt teks pertanyaan
     * @return string yang diinput (sudah di-trim)
     */
    public static String readString(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    /**
     * Membaca string yang TIDAK BOLEH KOSONG dari pengguna.
     * Menolak string kosong atau hanya spasi.
     *
     * @param sc     Scanner yang digunakan
     * @param prompt teks pertanyaan
     * @return string non-kosong dari pengguna
     */
    public static String readNonEmptyString(Scanner sc, String prompt) {
        while (true) {
            String input = readString(sc, prompt);
            if (!input.isEmpty())
                return input;
            System.out.println("[ERROR] Input tidak boleh kosong! Silakan ketik sesuatu.");
        }
    }

    // ==================== VALIDASI NODE GRAPH ====================

    /**
     * Membaca nama node dari pengguna dan langsung memvalidasinya
     * ke Graph (Anggota 1) via Graph.containsNode().
     *
     * Jika node tidak ada di peta, tampilkan daftar node yang valid
     * dan minta pengguna input ulang — loop sampai nama node valid.
     *
     * Menghemat pekerjaan Anggota 3: MenuGUI tidak perlu menulis
     * logika validasi node secara manual di setiap input form.
     *
     * @param sc     Scanner yang digunakan
     * @param prompt teks pertanyaan (contoh: "Masukkan node tujuan: ")
     * @param graph  Graph dari Anggota 1 sebagai referensi validasi
     * @return nama node yang valid (pasti ada di dalam Graph)
     */
    public static String readNodeName(Scanner sc, String prompt, Graph graph) {
        while (true) {
            String nodeName = readNonEmptyString(sc, prompt);
            if (graph.containsNode(nodeName))
                return nodeName; // Valid!

            System.out.println("[ERROR] Node \"" + nodeName + "\" tidak ditemukan di peta!");
            System.out.println("[INFO]  Node yang tersedia: " + graph.getAllNodes());
        }
    }

    /**
     * Validasi non-interaktif apakah sebuah string adalah node yang valid.
     * Berguna untuk validasi programatik tanpa meminta input ulang.
     *
     * @param nodeName nama node yang akan dicek
     * @param graph    Graph dari Anggota 1
     * @return true jika node ada di Graph, false jika tidak atau null/kosong
     */
    public static boolean isValidNode(String nodeName, Graph graph) {
        if (nodeName == null || nodeName.trim().isEmpty())
            return false;
        return graph.containsNode(nodeName.trim());
    }

    /**
     * ============================================================
     * METODE INTEGRASI: Baca sepasang node Asal + Tujuan
     * untuk langsung digunakan di RouteOptimizer.hitungDijkstra()
     * ============================================================
     *
     * Membaca DUA node sekaligus (asal dan tujuan), keduanya
     * divalidasi ke Graph. Hasilnya bisa langsung dipakai
     * sebagai argumen RouteOptimizer.hitungDijkstra().
     *
     * Contoh pakai di MenuGUI:
     * String[] nodes = InputValidator.readAsalDanTujuan(sc, graph);
     * RouteOptimizer.hitungDijkstra(graph, nodes[0], nodes[1]);
     *
     * @param sc    Scanner yang digunakan
     * @param graph Graph dari Anggota 1 untuk validasi kedua node
     * @return String[2] dimana [0] = asal, [1] = tujuan
     */
    public static String[] readAsalDanTujuan(Scanner sc, Graph graph) {
        System.out.println("[INFO] Node yang tersedia: " + graph.getAllNodes());
        String asal = readNodeName(sc, "Masukkan node ASAL   (restoran) : ", graph);
        String tujuan = readNodeName(sc, "Masukkan node TUJUAN (pelanggan): ", graph);
        return new String[] { asal, tujuan };
    }

    // ==================== BANGUN DELIVERY ORDER ====================

    /**
     * ============================================================
     * METODE FACTORY: Bangun DeliveryOrder dari input keyboard
     * ============================================================
     *
     * Membaca semua field yang dibutuhkan untuk membuat DeliveryOrder
     * baru dari pengguna, dengan validasi penuh di setiap field.
     *
     * Alur:
     * 1. Baca orderId (non-empty string)
     * 2. Baca customerName (non-empty string)
     * 3. Baca restaurantNode → validasi ke Graph (→ "asal" Dijkstra)
     * 4. Baca destinationNode → validasi ke Graph (→ "tujuan" Dijkstra)
     * 5. Baca deadlineTime → harus positif
     * 6. Buat dan kembalikan objek DeliveryOrder
     *
     * Contoh pakai di MenuGUI (menu 2 - Input Pesanan Baru):
     * DeliveryOrder pesanan = InputValidator.buildOrderFromInput(sc, graph);
     * orderManager.addOrderWithoutValidation(pesanan); // sudah tervalidasi di sini
     *
     * @param sc    Scanner yang digunakan
     * @param graph Graph dari Anggota 1 untuk validasi node
     * @return DeliveryOrder baru yang sudah tervalidasi penuh
     */
    public static DeliveryOrder buildOrderFromInput(Scanner sc, Graph graph) {
        System.out.println("\n--- Input Pesanan Baru ---");
        System.out.println("[INFO] Node tersedia: " + graph.getAllNodes());

        String orderId = readNonEmptyString(sc, "Order ID           : ");
        String customerName = readNonEmptyString(sc, "Nama Pelanggan     : ");

        // Kedua node ini akan jadi argumen hitungDijkstra() nanti
        String restaurantNode = readNodeName(sc,
                "Node Restoran/Asal : ", graph); // → "asal" di Dijkstra
        String destinationNode = readNodeName(sc,
                "Node Tujuan/Rumah  : ", graph); // → "tujuan" di Dijkstra

        int deadlineTime = readPositiveInt(sc, "Deadline (menit)   : ");

        return new DeliveryOrder(orderId, customerName,
                restaurantNode, destinationNode, deadlineTime);
    }

    // ==================== KONFIRMASI ====================

    /**
     * Meminta konfirmasi y/n dari pengguna.
     * Menerima: "y", "Y", "ya", "yes" → true
     * "n", "N", "tidak", "no" → false
     * Input lain akan meminta ulang.
     *
     * @param sc     Scanner yang digunakan
     * @param prompt teks pertanyaan konfirmasi (tanpa "(y/n)")
     * @return true jika ya, false jika tidak
     */
    public static boolean readConfirmation(Scanner sc, String prompt) {
        while (true) {
            String input = readNonEmptyString(sc, prompt + " (y/n): ").toLowerCase();
            if (input.equals("y") || input.equals("ya") || input.equals("yes"))
                return true;
            if (input.equals("n") || input.equals("tidak") || input.equals("no"))
                return false;
            System.out.println("[ERROR] Ketik 'y' untuk Ya atau 'n' untuk Tidak.");
        }
    }
}
