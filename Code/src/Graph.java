import java.util.*;

/**
 * This class is used to describe a graph.
 */
public class Graph {
    private List<Fragment> nodes;
    private List<Edge> edges;
    private final int numberOfEdges;
    private final int numberOfNodes;

    public Graph (Collection fragments){
        numberOfNodes = fragments.getCollection().length * 2;
        numberOfEdges = numberOfNodes * (numberOfNodes - 2);

        nodes = constructorNodes(fragments);
        edges = constructorEdges(nodes);
    }

    /**
     * This method is used to construct the nodes of a graph.
     *
     * @param fragments A collection of fragments.
     * @return A list of Fragments.
     */
    public List<Fragment> constructorNodes(Collection fragments){
        List<Fragment> nodesList = new ArrayList<>(numberOfNodes);
        nodesList.addAll(Arrays.asList(fragments.getCollection()));
        for (Fragment fragment : fragments.getCollection()){
            Fragment inverse = fragment.reversedComplementary();
            nodesList.add(inverse);
        }
        return nodesList;
    }

    /**
     * This method is used to construct the edges of a graph.
     *
     * @param nodes A collection of fragments.
     * @return A list of Edges.
     */
    public List<Edge> constructorEdges(List<Fragment> nodes) {
        List<Edge> edgesList = new ArrayList<>(numberOfEdges);
        for (Fragment source : nodes) {
            int indexSource = nodes.indexOf(source);
            for (Fragment destination : nodes) {
                int indexDestination = nodes.indexOf(destination);
                /*
                indexDestination != indexSource + (this.numberOfNodes / 2) makes sure that we do not create an edge
                and its inverse complementary.
                */
                if (indexDestination != indexSource && indexDestination != indexSource + (this.numberOfNodes / 2)) {
                    Edge edge = new Edge(source,destination);
                    int semiGlobalScore = semiGlobalAlignmentScore(source, destination);
                    edge.setWeight(semiGlobalScore);
                    edgesList.add(edge);
                }
            }
        }
        return edgesList;
    }

    public static int semiGlobalAlignmentScore(Fragment firstFragment, Fragment secondFragment) {
        /*
        the first fragment is represented vertically in the matrix
        the second fragment is represented horizontally in the matrix
        */
        int firstFragmentLength = firstFragment.getLength();
        int secondFragmentLength = secondFragment.getLength();

        int matrixVerticalLength = firstFragmentLength + 1;
        int matrixHorizontalLength = secondFragmentLength + 1;
        int[][] matrix = new int[matrixVerticalLength][matrixHorizontalLength];

        for (int i = 1; i < matrixVerticalLength; i++) {
            for (int j = 1; j < matrixHorizontalLength; j++) {
                String firstFragmentString = firstFragment.getFragment();
                String secondFragmentString = secondFragment.getFragment();
                int element1 = matrix[i - 1][j] - 2;
                char firstChar = firstFragmentString.charAt(i-1);
                char secondChar = secondFragmentString.charAt(j-1);
                boolean charsAreEquals = firstChar == secondChar;
                int element2 = charsAreEquals ? matrix[i - 1][j - 1] + 1 : matrix[i - 1][j - 1] - 1;
                int element3 = matrix[i][j - 1] - 2;
                matrix[i][j] = Math.max(Math.max(element1, element2), element3);
            }
        }

        int[] lastLine = matrix[matrixVerticalLength-1];
        int lastLineMaximum = Integer.MIN_VALUE;
        // int lastLineMaximumIndex = -1;
        for (int i = 0; i < lastLine.length - 1; i++) {
            int currentElement = lastLine[i];
            if (currentElement >= lastLineMaximum) {
                lastLineMaximum = currentElement;
                // lastLineMaximumIndex = i;
            }
        }

        int lastIndex = matrixHorizontalLength - 1;
        int lastColumnMaximum = Integer.MIN_VALUE;
        // int lastColumnMaximumIndex = -1;
        for (int i = 0; i < matrixVerticalLength; i++) {
            int currentElement = matrix[i][lastIndex];
            if (currentElement >= lastColumnMaximum) {
                lastColumnMaximum = currentElement;
                // lastColumnMaximumIndex = i;
            }
        }

        return Integer.max(lastLineMaximum, lastColumnMaximum);
    }

    /**
     * This method is used to get the overlap Graph.
     *
     * @param f A first fragment
     * @param g A second fragment
     *
     * @return A two dimension tab representing the overlap graph.
     */
    public static int[][] getOverlapGraph(Fragment f, Fragment g) {
        int matrixSizeLong = f.getLength();
        int matrixSizeLarg = g.getLength();

        int[][] overlapGraph = new int[matrixSizeLong + 1][matrixSizeLarg + 1];


        for(int i = 0; i < matrixSizeLong; i++){
            overlapGraph[i][0] = 0;
        }

        for(int i = 0; i < matrixSizeLarg; i++){
            overlapGraph[0][i] = 0;
        }

        for (int i = 1; i < matrixSizeLong; i++) {
            for (int j = 1; j < matrixSizeLarg; j++) {
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

    /**
     * This method is used to perform the greedy algorithm.
     *
     * @return The list of edges that are in the path.
     */
    public List<Edge> greedy(){
        byte[] in = new byte[numberOfNodes];
        byte[] out = new byte[numberOfNodes];
        List<Edge> chemin = new ArrayList<>();
        bubbleReverseSort(edges); //TODO changer tri

        for (Edge arc : edges){
            if(in[nodes.indexOf(arc.getDest())] == 0 && out[nodes.indexOf(arc.getSrc())] == 0){
                in[nodes.indexOf(arc.getDest())] = 1;
                out[nodes.indexOf(arc.getSrc())] = 1;
                if (nodes.indexOf(arc.getDest()) < numberOfNodes /2){
                    in[nodes.indexOf(arc.getDest()) + numberOfNodes /2] = 1;
                }
                else{
                    in[nodes.indexOf(arc.getDest()) - numberOfNodes /2] = 1;
                }
                if (nodes.indexOf(arc.getSrc()) < numberOfNodes /2){
                    out[nodes.indexOf(arc.getSrc()) + numberOfNodes /2] = 1;
                }
                else{
                    out[nodes.indexOf(arc.getSrc()) - numberOfNodes /2] = 1;
                }

                ArrayList<Fragment> newFragments = new ArrayList<>();
                newFragments.add(arc.getSrc());
                newFragments.add(arc.getDest());
                Edge edges = new Edge(arc.getSrc(),arc.getDest(), newFragments);

                chemin = union(chemin, edges);
            }
        }
        return chemin;
    }

    /**
     * This method is used to sort (from max to low) the edges on a List using the Bubble sort algorithm.
     *
     * @param edges The list to must be sorted.
     */
    private void bubbleReverseSort(List<Edge> edges) {
        for (int i = 0; i < this.numberOfNodes -1; i++) {
            for (int j = 0; j < this.numberOfNodes - i - 1; j++) {
                if (edges.get(j).getWeight() < edges.get(j + 1).getWeight()) {
                    Edge temp = edges.get(j);
                    edges.set(j, edges.get(j + 1));
                    edges.set(j + 1, temp);
                }
            }
        }
    }

    /**
     * This method is used to do the union between a path and an edge.
     *
     * @param chem The path on which perform the union.
     * @param arc The edge on which perform the union.
     * @return The list representing the united path and edge.
     */
    public List<Edge> union(List<Edge> chem, Edge arc){
        List<Edge> chemin = chem;
        int flag = 0;
        if(chemin.isEmpty()){
            chemin.add(arc);
            return chemin;
        }
        for (int i = 0; i < chem.size(); i++) {
            Edge edges = chem.get(i);
            if (edges.getDest() == arc.getSrc()){
                edges.getChemin().remove(edges.getChemin().size()-1);
                ArrayList<Fragment> newArrayList = new ArrayList<>(edges.getChemin());
                newArrayList.addAll(arc.getChemin());
                Edge temp = new Edge(edges.getSrc(), arc.getDest(), newArrayList);
                chemin.add(temp);
                flag += 1;
            }
            else if (edges.getSrc() == arc.getDest()){
                arc.getChemin().remove(arc.getChemin().size()-1);
                ArrayList<Fragment> newArrayList = new ArrayList<>(arc.getChemin());
                newArrayList.addAll(edges.getChemin());

                Edge temp = new Edge(arc.getSrc(), edges.getDest(), newArrayList);
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