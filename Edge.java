/**
 * Sisi graf berarah: menyimpan node tujuan dan bobotnya.
 * Digunakan sebagai elemen adjacency list di Graph.
 */
public class Edge {

    private String destination;
    private int weight;

    public Edge(String destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public String getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "-> " + destination + " (bobot: " + weight + ")";
    }
}
