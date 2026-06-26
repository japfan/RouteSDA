Implementasi Kode: Program harus dibangun menggunakan bahasa Java murni (Pure 
Java). Mahasiswa dibebaskan menggunakan library bawaan Java (seperti Collection 
Framework), namun ketepatan pemilihan struktur data dan efisiensi algoritma tetap 
menjadi poin penilaian utama. 

TEMA 1: Route Optimization and Network-Based Systems 
● Konteks Masalah: Aplikasi Navigasi Kurir Ojek Online / Pengirim Makanan (apapun yg 
optimasi rute). 
● Deskripsi: Sistem simulator logistik untuk membantu kurir mengelola peta 
perumahan/kota, mengurutkan paket makanan yang masuk berdasarkan batas waktu 
(deadline), dan mencari rute jalan terpendek dari restoran ke beberapa rumah pelanggan 
agar efisien secara waktu dan bahan bakar. 
● Spesifikasi Minimal Fitur: 
    ○ Representasi peta jalan/gang menggunakan konsep Graph. 
    ○ Fitur pengurutan atau manajemen prioritas pengiriman makanan yang masuk. 
    ○ Fitur pencarian rute terpendek (Shortest Path) dari satu titik ke titik tujuan.


Anggota 1: Modul Peta & Algoritma Rute (Core Graph)
Tugasnya fokus pada pembangunan struktur data jaringan jalan dan perhitungan matematis rutenya.
 *Edge.java*: Kelas model dasar untuk merepresentasikan jalan. Isinya hanya atribut String destination (simpul tujuan) dan int weight (bobot/jarak/waktu tempuh) beserta *constructor* dan *getter*.
 *Graph.java*: Kelas utama struktur data yang menampung HashMap<String, List<Edge>>. Berisi fungsi-fungsi esensial seperti addNode(), addEdge(), dan getNeighbors().
  *RouteOptimizer.java*: Kelas utilitas yang berisi algoritma pencarian rute terpendek (*Shortest Path*), idealnya menggunakan Algoritma Dijkstra. Kelas ini memiliki fungsi yang menerima parameter graf, titik awal, dan titik tujuan, lalu mengembalikan nilai berupa rute langkah demi langkah dan total jaraknya.

Anggota 2: Modul Manajemen Pesanan (Sorting & Priority)
Tugasnya fokus pada struktur penyimpanan paket makanan yang masuk dan memastikan paket diurutkan secara efisien.
  *DeliveryOrder.java*: Kelas model (*POJO*) untuk menyimpan data satu pesanan. Atribut minimal: String orderId, String destinationNode (harus sinkron dengan nama simpul di Graph), dan int deadlineTime (batas waktu pengiriman).

 *OrderManager.java*: Kelas yang mengelola kumpulan DeliveryOrder. Menggunakan struktur data *Collection* (seperti PriorityQueue atau LinkedList yang diurutkan). Berisi fungsi addOrder(), removeOrder(), dan fungsi sortOrdersByDeadline() untuk mengurutkan pesanan dari waktu yang paling mendesak.
  *InputValidator.java*: Kelas pembantu (*helper*) untuk menangani *edge cases*. Berisi metode statis untuk membungkus Scanner agar program tidak *crash* jika pengguna memasukkan input huruf saat diminta input angka (menggunakan blok try-catch).

Anggota 3: Modul Antarmuka & Integrasi (GUI & Main)
Tugasnya menyatukan (*wiring*) kode dari Anggota 1 dan 2, menyiapkan *dummy data*, dan membangun menu terminal yang interaktif.
 *MapSeeder.java*: Kelas utilitas yang otomatis membangun peta perumahan (menambahkan *node* dan *edge* secara *hardcode*). Ini penting agar saat pengujian (*Live Demo*), kalian tidak perlu menginput data peta satu per satu dari awal.
 *MenuGUI.java*: Kelas yang berisi perulangan utama (while) untuk menampilkan teks menu di terminal. Mengatur alur seperti:
   1. Tampilkan Peta Jaringan
   2. Input Pesanan Makanan Baru (Memanggil OrderManager)
   3. Lihat Daftar Antrean Pesanan
   4. Cari Rute Tercepat untuk Pesanan Teratas (Memanggil RouteOptimizer)
 *Main.java*: *Entry point* program. File ini dijaga agar tetap bersih (*clean*), hanya berisi public static void main(String[] args) yang menginisialisasi MapSeeder, dan menjalankan MenuGUI.start().
*Tips Eksekusi Tim:*
Supaya Anggota 3 bisa mulai mengerjakan MenuGUI.java tanpa harus menunggu Anggota 1 dan 2 selesai, Anggota 1 dan 2 cukup membuat *kerangka fungsinya saja (Method Signature) terlebih dahulu* dengan *return value* palsu sementara (*stub*). Setelah kerangkanya ada, Anggota 3 bisa mulai mengintegrasikan menu, sementara Anggota 1 dan 2 melengkapi isi logikanya di *file* mereka masing-masing.