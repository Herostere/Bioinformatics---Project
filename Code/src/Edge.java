import java.util.ArrayList;

public class Edge {
    Fragment src;
    Fragment dest;
    int weight;
    ArrayList<Fragment> chemin;

    public Edge(Fragment src, Fragment dest){
        this.src = src;
        this.dest = dest;
        this.weight = weight();
    }

    public Edge(Fragment src, Fragment dest, int x) {
        this.src = src;
        this.dest = dest;
        this.chemin = chemin;
    }

    public Edge(Fragment src, Fragment dest, ArrayList<Fragment> chemin) {
        this.src = src;
        this.dest = dest;
        this.chemin = chemin;
    }

    public int weight() {  //calcul du poids
        int[][] overlapGraph = Graph.getOverlapGraph(this.src, this.dest);
        int i = this.src.getLength();
        int j = this.dest.getLength();

        int maximum = 0;

        for (int x = 0; x < j; x++) {
            maximum = Math.max(maximum, overlapGraph[i][x]);
        }

        for (int x = 0; x < i; x++) {
            maximum = Math.max(maximum, overlapGraph[x][j]);
        }

        return maximum;
    }
}
