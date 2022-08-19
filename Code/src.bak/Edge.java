import java.util.ArrayList;

/**
 * This class describes an Edge.
 */
public class Edge {
    private final Fragment src;
    private final Fragment dest;
    private final int weight;
    private ArrayList<Fragment> chemin;

    public Edge(Fragment src, Fragment dest, int weight){
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

//    public Edge(Fragment src, Fragment dest, ArrayList<Fragment> chemin) {
//        this.src = src;
//        this.dest = dest;
//        this.chemin = chemin;
//    }

    /**
     * This method is used to compute the weight of an edge.
     *
     * @return An int representing the weight of the edge.
     */
//    public int weight() {
//        int[][] overlapGraph = Graph.getOverlapGraph(this.src, this.dest);
//        int i = this.src.getLength();
//        int j = this.dest.getLength();
//
//        int maximum = 0;
//
//        for (int x = 0; x < j; x++) {
//            maximum = Math.max(maximum, overlapGraph[i][x]);
//        }
//
//        for (int x = 0; x < i; x++) {
//            maximum = Math.max(maximum, overlapGraph[x][j]);
//        }
//
//        return maximum;
//    }

    public Fragment getSrc() {
        return this.src;
    }

    public Fragment getDest() {
        return this.dest;
    }

    public int getWeight() {
        return this.weight;
    }

    public ArrayList<Fragment> getChemin() {
        return chemin;
    }

//    public void setWeight(int weight) {
//        this.weight = weight;
//    }
}
