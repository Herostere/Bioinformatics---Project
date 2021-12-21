import java.sql.Array;
import java.util.*;

public class Graph {
    private List<Fragment> nodes;
    private List<Edge> edges;
    private final int m; // nbre d'arcs
    private final int n; //nbre de noeuds

    public Graph (Collection fragments){
        n = fragments.getCollection().length * 2;
        m = n * (n - 2);

        nodes = constructorNodes(fragments);
        edges = constructorEdges(nodes);// constructeur d arc
    }

    public List<Fragment> constructorNodes(Collection fragments){
        List<Fragment> nodesList = new ArrayList<>(n); //verifier si garder arraylist ou passer en tableau
        nodesList.addAll(Arrays.asList(fragments.getCollection()));
        for (Fragment fragment : Arrays.asList(fragments.getCollection())){
            Fragment inverse = fragment.reversedComplementary(); //calcul inverse d'un fragment
            nodesList.add(inverse);
        }
        return nodesList;
    }

    public List<Edge> constructorEdges(List<Fragment> nodes){
        List<Edge> edgesList = new ArrayList<>(m); //verifier si garder arraylist ou passer en tableau
        for (Fragment src : nodes){
            int indiceSrc = nodes.indexOf(src);
            for (Fragment dest : nodes){

                if (nodes.indexOf(dest) != indiceSrc && nodes.indexOf(dest) != indiceSrc + (this.n/2) ){
                    Edge edge = new Edge(src,dest);
                    edgesList.add(edge);
                }
            }
        }
        return edgesList;
    }

    public static int[][] getOverlapGraph(Fragment f, Fragment g) {
        int matriceSizeLong = f.getLength();
        int matriceSizeLarg = g.getLength();

        int[][] overlapGraph = new int[matriceSizeLong + 1][matriceSizeLarg + 1];

        for(int i = 0; i < matriceSizeLong+1; i++){
            overlapGraph[0][i] = 0;
        }

        for(int i = 0; i < matriceSizeLarg+1; i++){
            overlapGraph[i][0] = 0;
        }

        for (int i = 1; i < matriceSizeLong; i++) {
            for (int j = 1; j < matriceSizeLarg; j++) {
                int penality;
                if (f.getFragment().charAt(i) == g.getFragment().charAt(j)){
                    penality = 1;
                }
                else {penality = -1;}

                overlapGraph[i][j] = Math.max(Math.max(overlapGraph[i-1][j] -2, overlapGraph[i][j-1] -2), overlapGraph[i-1][j-1] + penality);
            }
        }

        return overlapGraph;
    }

    public List<Edge> greedy(){
        int[] in = new int[n];
        int[] out = new int[n];
        List<Edge> chemin = new ArrayList<Edge>();
        bubbleReverseSort(edges);

        for (Edge arc : edges){
            if(in[nodes.indexOf(arc.dest)] == 0 && out[nodes.indexOf(arc.src)] == 0){
                in[nodes.indexOf(arc.dest)] = 1;
                out[nodes.indexOf(arc.src)] = 1;
                if (nodes.indexOf(arc.dest) < n/2){
                    in[nodes.indexOf(arc.dest) + n] = 1;
                }
                else{
                    in[nodes.indexOf(arc.dest) - n] = 1;
                }
                if (nodes.indexOf(arc.src) < n/2){
                    out[nodes.indexOf(arc.src) + n] = 1;
                }
                else{
                    out[nodes.indexOf(arc.src) - n] = 1;
                }
                ArrayList<Fragment> newFragments = new ArrayList<>();
                newFragments.add(arc.src);
                newFragments.add(arc.dest);
                Edge edges = new Edge(arc.src,arc.dest, newFragments);
                chemin = union(chemin, edges);
            }
            if (chemin.size() == 1){
                break;
            }
        }
        return chemin;
    }

    private void bubbleReverseSort(List<Edge> edges) {
        for (int i = 0; i < this.n  -1; i++) {
            for (int j = 0; j < this.n - i - 1; j++) {
                if (edges.get(j).weight < edges.get(j + 1).weight) {
                    Edge temp = edges.get(j);
                    edges.set(j, edges.get(j + 1));
                    edges.set(j + 1, temp);
                }
            }
        }
    }

    public List<Edge> union(List<Edge> chem, Edge arc){
        List<Edge> chemin = chem;
        int flag = 0;
        if(chemin.isEmpty()){
            chemin.add(arc);
            return chemin;
        }
        for (Edge edges : chem){
            if (edges.dest == arc.src){
                edges.chemin.remove(chemin.size()-1);
                ArrayList<Fragment> newArrayList = new ArrayList<>(edges.chemin);
                newArrayList.addAll(arc.chemin);
                Edge temp = new Edge(edges.src, arc.dest, newArrayList);
                chemin.add(temp);
                flag += 1;
            }
            else if (edges.src == arc.dest){

                arc.chemin.remove(chemin.size()-1);
                ArrayList<Fragment> newArrayList = new ArrayList<>(arc.chemin);
                newArrayList.addAll(edges.chemin);

                Edge temp = new Edge(arc.src, edges.dest,newArrayList);
                chemin.add(temp);
                flag += 1;
            }

        }
        if(flag == 0){
            chemin.add(arc);
        }
        return chemin;
    }

}