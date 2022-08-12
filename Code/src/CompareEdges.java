import java.util.Comparator;

public class CompareEdges implements Comparator<Edge> {

    @Override
    public int compare(Edge edge1, Edge edge2) {
        if (edge2.getWeight() < edge1.getWeight()) {
            return -1;
        }
        else if (edge2.getWeight() == edge1.getWeight()) {
            return 0;
        }
        else {
            return 1;
        }
    }
}
