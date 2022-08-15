import java.util.Comparator;

public class CompareEdges implements Comparator<Edge> {

    @Override
    public int compare(Edge edge1, Edge edge2) {
        return Integer.compare(edge2.getWeight(), edge1.getWeight());
    }
}
