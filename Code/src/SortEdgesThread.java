import java.util.List;

public class SortEdgesThread extends Thread {
    private final String name;
    private final int start;
    private final int end;
    private final List<Fragment> nodes;
    private final List<Edge> edges;

    public SortEdgesThread(List<Fragment> nodes, String name, int start, int end, List<Edge> edges) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.nodes = nodes;
        this.edges = edges;
    }

    public void run() {
        System.out.println("Starting " + name);
        int numberOfNodes = nodes.size();
        for (int i = start; i < end; i++) {
            Fragment node1 = nodes.get(i);
            for (int j = 0; j < numberOfNodes; j++) {
                Fragment node2 = nodes.get(j);
                if (i != j && i != j + (numberOfNodes / 2) && j != i + (numberOfNodes / 2)){
                    int score = Graph.semiGlobalAlignmentScore(node1, node2);
                    edges.add(new Edge(node1, node2, score));
                }
            }
        }
        System.out.println("Completed: " + name);
    }
}
