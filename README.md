# TitikTemu — Pencari Rute Terpendek

Program dengan bahasa Java yang mensimulasikan sistem pencarian rute terpendek berbasis graph. Program merepresentasikan peta sebagai graph berbobot, mengelola antrean titik tujuan berdasarkan urgensi deadline, dan mencari rute tercepat dari titik asal ke tujuan menggunakan algoritma Dijkstra lengkap dengan visualisasi animasi proses pencarian rute pada peta.

Dikerjakan untuk tugas mata kuliah Struktur Data dan Algoritma, Tema 1: *Route Optimization and Network-Based Systems*.

## Anggota Kelompok

| No | Nama | NIM | Modul yang Dikerjakan |
|----|------|-----|------------------------|
| 1 | _Jaffan Arya Wirasena_ | _L0125017_ | Modul Peta & Algoritma Rute — `Edge.java`, `Graph.java`, `RouteOptimizer.java` |
| 2 | _Daffa Gathfan Chaidar_ | _L0125077_ | Modul Manajemen Pesanan — `DeliveryOrder.java`, `OrderManager.java`, `InputValidator.java` |
| 3 | _Habibi Ramadan_ | _L0125045_ | Modul Antarmuka & Integrasi — `Main.java`, `MainFrame.java`, `MapPanel.java`, `MapSeeder.java` |


## Fitur Utama

- **Visualisasi peta jaringan jalan** — peta perumahan digambarkan sebagai graph interaktif (`MapPanel`) berisi node lokasi dan edge jalan beserta bobot jaraknya.
- **Input pesanan baru** — form pada panel kiri untuk menambahkan pesanan (Order ID, nama pelanggan, lokasi asal, tujuan, deadline), dengan validasi field kosong, validasi node ke peta, dan validasi format angka.
- **Antrean pesanan terurut otomatis** — daftar pesanan selalu tampil terurut dari deadline paling mendesak, diperbarui otomatis setiap ada pesanan baru atau pesanan selesai diproses.
- **Pencarian rute tercepat (Dijkstra)** — tombol "Cari Rute Tercepat" menghitung jalur terpendek dari restoran ke tujuan pesanan paling mendesak di antrean, menampilkan urutan node dan total jarak.
- **Animasi proses pencarian rute** — tahap eksplorasi Dijkstra (node dikunjungi, edge di-relax) dan hasil rute terpendek digambar secara animasi di atas peta, tidak hanya ditampilkan sebagai teks.
- **Auto-seed data simulasi** — `MapSeeder` otomatis mengisi 7 node, 11 edge (graph tidak berarah), dan 3 pesanan dummy saat program dijalankan, sehingga demo bisa langsung dilakukan tanpa input manual dari awal.
- **Penyelesaian pesanan otomatis** — setelah animasi rute selesai diputar, pesanan yang sedang diproses otomatis dihapus dari antrean.

## Struktur Data dan Algoritma

### Struktur Data

| Struktur Data | Digunakan di | Operasi Kunci & Kompleksitas | Alasan Pemilihan |
|---|---|---|---|
| `HashMap<String, List<Edge>>` (adjacency list) | `Graph.java` | `addNode`/`containsNode`/`getNeighbors`: O(1) | Peta jalan bersifat tidak berarah dan sparse (jumlah edge jauh lebih kecil dari V²); adjacency list memakai ruang O(V+E), jauh lebih hemat dibanding adjacency matrix O(V²) |
| `PriorityQueue<DeliveryOrder>` (binary min-heap) | `OrderManager.java` | `offer`: O(log n), `poll`: O(log n), `peek`: O(1) | Kurir perlu tahu pesanan paling mendesak setiap saat; min-heap menjaga urutan prioritas otomatis tanpa sorting ulang setiap ada perubahan, lebih efisien dari ArrayList yang butuh O(n) untuk mencari elemen termendesak |
| `Comparable<DeliveryOrder>` (natural ordering) | `DeliveryOrder.java` | `compareTo`: O(1) | Mendefinisikan urutan berdasarkan `deadlineTime` agar `PriorityQueue` bisa membandingkan dua pesanan tanpa `Comparator` eksternal |
| `HashMap` / `HashSet` (jarak, previous, visited) | `RouteOptimizer.java` | get/put/contains: O(1) | Menyimpan jarak sementara tiap node, jejak node sebelumnya untuk rekonstruksi rute, dan node yang statusnya sudah final selama Dijkstra berjalan |
| `ArrayList` + `Collections.sort` (snapshot tampilan) | `OrderManager.sortOrdersByDeadline` | O(n log n) (TimSort) | Menghasilkan salinan daftar pesanan terurut untuk ditampilkan, tanpa mengubah struktur `PriorityQueue` asli yang sedang aktif |

### Algoritma

**Dijkstra's Algorithm** (`RouteOptimizer.hitungDijkstra` / `hitungDijkstraDenganAnimasi`) — mencari rute terpendek dari node asal ke node tujuan dengan pendekatan greedy berbasis `PriorityQueue`. Dipilih karena seluruh bobot edge pada peta (jarak/waktu tempuh) bersifat non-negatif, sehingga Dijkstra menjamin hasil optimal dengan kompleksitas **O((V + E) log V)** — lebih efisien dibanding Bellman-Ford (O(V × E)) yang sebenarnya hanya diperlukan jika ada bobot negatif.

**Lazy deletion pada min-heap** — saat sebuah node sudah dikunjungi namun entri lamanya masih tersisa di `PriorityQueue` (karena pernah di-`offer()` lebih dari sekali), entri tersebut cukup dilewati (`continue`) saat di-`poll()`, tanpa perlu dihapus eksplisit dari heap.

**Path reconstruction** — rute akhir disusun balik dari node tujuan ke node asal melalui map `previous`/`parentMap`, lalu dibalik urutannya dengan `Collections.reverse()`.

## Instalasi dan Menjalankan Program

### Prasyarat
- JDK (Java Development Kit) versi 8 atau lebih baru, direkomendasikan JDK 11/17
- Tidak ada library eksternal yang perlu diinstal (lihat bagian Library di bawah)

### Menjalankan via Terminal
```bash
# 1. Pastikan semua file .java berada dalam satu folder yang sama

# 2. Compile seluruh source code
javac *.java

# 3. Jalankan program
java Main
```

Program terbuka sebagai jendela aplikasi desktop (Swing GUI) berjudul **TitikTemu**, lengkap dengan peta, form input data, dan antrean (tidak ada interaksi via terminal/Scanner saat runtime.)

### Menjalankan via IDE
Import seluruh file `.java` ke dalam satu project baru (IntelliJ IDEA, Eclipse, NetBeans, atau VS Code dengan Extension Pack for Java), lalu jalankan `Main.java`.

## Library Eksternal

Proyek ini **tidak menggunakan library atau dependency eksternal apa pun**. Seluruh fungsionalitas mulai dari struktur data, algoritma, hingga antarmuka grafis dibangun murni menggunakan Java SE Standard Library (*Pure Java*) yang sudah tersedia di dalam JDK tanpa instalasi tambahan:

| Package Bawaan JDK | Fungsi dalam Proyek |
|---|---|
| `java.util.HashMap`, `java.util.HashSet` | Adjacency list graph; tracking visited/jarak/previous node pada Dijkstra |
| `java.util.PriorityQueue` | Antrean pesanan (min-heap by deadline) dan min-heap jarak pada Dijkstra |
| `java.util.ArrayList`, `java.util.List` | Daftar edge per node, hasil rute, snapshot antrean terurut |
| `java.util.Collections` | `sort()` untuk urutkan snapshot pesanan, `reverse()` untuk rekonstruksi rute |
| `java.util.Scanner` | Pembacaan input baris demi baris pada `InputValidator` |
| `javax.swing.*` | Komponen antarmuka grafis (`JFrame`, `JPanel`, `JButton`, `JTextField`, dll.) |
| `java.awt.*` | Rendering grafis 2D untuk visualisasi peta dan animasi (`Graphics2D`, `Color`, `Timer`, dll.) |

Karena tidak ada library eksternal, tidak diperlukan tool manajemen dependency seperti Maven atau Gradle. Kompilasi cukup dengan `javac` standar.

## Catatan Tambahan

`InputValidator.java` menyediakan utilitas validasi input berbasis `Scanner` (`readInt`, `readPositiveInt`, `readNodeName`, dll.) yang dirancang untuk mode input baris-demi-baris di terminal. Pada build GUI saat ini (`MainFrame`), validasi yang setara diterapkan langsung memakai dialog Swing (`JOptionPane`) karena input pada GUI bersifat event-driven, bukan blocking seperti `Scanner`. Kelas ini tetap relevan sebagai modul mandiri, misalnya untuk mode command-line alternatif atau pengujian logika validasi secara terpisah dari GUI.
