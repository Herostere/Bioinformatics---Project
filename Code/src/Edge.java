import java.util.ArrayList;

/**
 * This class describes an Edge.
 */
public class Edge {
    private final Fragment src;
    private final Fragment dest;
    private final int weight;

    public Edge(Fragment src, Fragment dest, int weight){
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }

    public Fragment getSrc() {
        return this.src;
    }

    public Fragment getDest() {
        return this.dest;
    }

    public int getWeight() {
        return this.weight;
    }

}
