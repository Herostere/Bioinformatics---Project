import java.util.List;

public class EdgesAlignments {
    private final List<Edge> edges;
    private final List<Alignment> alignments;

    public EdgesAlignments(List<Edge> edges, List<Alignment> alignments) {
        this.edges = edges;
        this.alignments = alignments;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Alignment> getAlignments() {
        return alignments;
    }
}
