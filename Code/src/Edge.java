/**
 * This class describes an Edge.
 */
public record Edge(Fragment src, Fragment dest, int weight) {

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
