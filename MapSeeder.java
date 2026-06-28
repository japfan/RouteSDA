public class MapSeeder {
    
    public static void seedMap(Graph graph) {
        // 1. Daftarkan semua titik/simpul (Node) yang ada di peta
        // Titik "Restoran" kita anggap sebagai titik awal (Hub)
        graph.addNode("Restoran"); 
        graph.addNode("Perumahan A");
        graph.addNode("Perumahan B");
        graph.addNode("Perumahan C");
        graph.addNode("Perumahan D");
        graph.addNode("Perumahan E");
        graph.addNode("Perumahan F");

        // 2. Daftarkan jalan/rute penghubung antar titik beserta jarak/waktunya (Weight)
        // Parameter: addEdge(SimpulAsal, SimpulTujuan, Bobot)
        
        // Jalan dari Restoran ke perumahan terdekat
        graph.addEdge("Restoran", "Perumahan A", 4);  // Jarak 4 km / 4 menit
        graph.addEdge("Restoran", "Perumahan B", 6);  // Jarak 6 km / 6 menit
        
        // Hubungan jalan antar perumahan (membentuk jaringan/graf)
        graph.addEdge("Perumahan A", "Perumahan C", 3);
        graph.addEdge("Perumahan A", "Perumahan D", 8);
        
        graph.addEdge("Perumahan B", "Perumahan D", 2);
        graph.addEdge("Perumahan B", "Perumahan E", 5);
        
        graph.addEdge("Perumahan C", "Perumahan D", 1);
        graph.addEdge("Perumahan C", "Perumahan F", 7);
        
        graph.addEdge("Perumahan D", "Perumahan E", 3);
        graph.addEdge("Perumahan D", "Perumahan F", 4);
        
        graph.addEdge("Perumahan E", "Perumahan F", 2);
    }
}