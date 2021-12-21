import java.util.*;

public class Graph{
    private List<Fragment> nodes;
    private List<int[]> edges;
    private final int m; // nbre d'arcs
    private final int n; //nbre de noeuds

    public Graph (List<Fragment> fragments){
        n = fragments.size() * 2;
        m = n * (n - 2);

        nodes = constructorNodes;
        edges = ;// constructeur d arc
    }
    public List<Fragment> constructorNodes(List<Fragment> fragments){
        List<Fragment> nodesList = new ArrayList<>(n);
        for (Fragment fragment : fragments){
            nodesList.add(fragment);
            Fragment inverse = fragment.constructInverse(); //fct ou constructeur d inverse de fragment
            nodesList.add(inverse);
            return nodesList;
        }
    }

    public List<int[]> constructorEdges(){
        List<int[]> edgesList = new ArrayList<>(m);

    }

}