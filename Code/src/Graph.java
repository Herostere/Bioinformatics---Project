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
        nodes = constructorNodes(fragments);

        numberOfNodes = nodes.size();
        numberOfEdges = numberOfNodes * (numberOfNodes - 2);
//        edges = constructorEdges(nodes);
    }

    /**
     * This method is used to construct the nodes of a graph.
     *
     * @param fragments A collection of fragments.
     * @return A list of Fragments.
     */
    public List<Fragment> constructorNodes(Collection fragments){
        List<Fragment> nodesList = new ArrayList<>(fragments.getCollection().length);
        for (Fragment fragment : fragments.getCollection()) {
            if (!nodesList.contains(fragment) && !nodesList.contains(fragment.reversedComplementary())) {
                nodesList.add(fragment);
            }
        }
        int numberOfNodesTemp = nodesList.size();
        for (int i = 0; i < numberOfNodesTemp; i++){
            Fragment inverse = nodesList.get(i).reversedComplementary();
            nodesList.add(inverse);
        }
        return nodesList;
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
     * @return A two dimension tab representing the overlap graph.
     */
    private int[][] getOverlapGraph() {
        int[][] graph = new int[numberOfNodes][numberOfNodes];
        System.out.println("Constructing Overlap Graph:");
        for (int i = 0; i < numberOfNodes; i++) {
            int percentage = (int) (((float) i / numberOfNodes) * 100);
            System.out.print("\r");
            System.out.print("[" + "*".repeat(percentage) + "-".repeat(100 - percentage) + "]");
            for (int j = 0; j < numberOfNodes; j++) {
                if (i == j || i == j + (numberOfNodes / 2) || j == i + (numberOfNodes / 2)) {
                    graph[i][j] = -1;
                }
                else {
                    int score = semiGlobalAlignmentScore(nodes.get(i), nodes.get(j));
                    graph[i][j] = score;
                }
            }
        }
        System.out.println();
        return graph;
    }

    /**
     * This method is used to perform the greedy algorithm.
     *
     * @return The ordered list of fragments that are in the path.
     */
    public List<Fragment> greedy(){
        int[] in = new int[numberOfNodes];
        int[] out = new int[numberOfNodes];
        List<List<Fragment>> listOfSets = new ArrayList<>();
//        int[][] overlapGraph = getOverlapGraph();
//        System.out.println(Arrays.deepToString(overlapGraph));

        for (int i = 0; i < numberOfNodes; i++) {
            List<Fragment> initialList = new ArrayList<>();
            initialList.add(nodes.get(i));
            listOfSets.add(initialList);
        }

        List<Edge> edges = sortEdges();
        for (Edge edge : edges) {
            Fragment f = edge.getSrc();
            Fragment g = edge.getDest();
            int indexF = nodes.indexOf(f);
            int indexG = nodes.indexOf(g);

            List<Fragment> foundF = findSet(listOfSets, f);
            List<Fragment> foundG = findSet(listOfSets, g);
            boolean fInG = g.getFragment().contains(f.getFragment());
            boolean gInF = f.getFragment().contains(g.getFragment());
            if (fInG || gInF) {
                continue;
            }
            if (in[indexG] == 0 && out[indexF] == 0 && (foundF.size() != foundG.size() || !foundF.equals(foundG))) {
                in[indexG] = 1;
                out[indexF] = 1;
                union(listOfSets, foundF, foundG);
            }
            if (listOfSets.size() == 1) {
                break;
            }
        }
        return listOfSets.get(0);
    }

    private List<Fragment> findSet(List<List<Fragment>> listOfSets, Fragment node) {
        List<Fragment> set = new ArrayList<>(0);
        for (List<Fragment> setTemp : listOfSets) {
            if (setTemp.contains(node)) {
                set = setTemp;
            }
        }
        return set;
    }

    private void union(List<List<Fragment>> listOfSets, List<Fragment> list1, List<Fragment> list2) {
        listOfSets.remove(list1);
        listOfSets.remove(list2);
        list1.addAll(list2);
        listOfSets.add(list1);
    }

    private List<Edge> sortEdges() {
        List<Edge> edges = new ArrayList<>(numberOfNodes * (numberOfNodes - 2));
        System.out.println("Constructing edges:");
        for (int i = 0; i < numberOfNodes; i++) {
            int percentage = (int) (((float) i / numberOfNodes) * 10);
            System.out.print("\r");
            System.out.print("[" + "*".repeat(percentage) + "-".repeat(10 - percentage) + "]");
            Fragment node1 = nodes.get(i);
            for (int j = 0; j < numberOfNodes; j++) {
                Fragment node2 = nodes.get(j);
                if (i != j && i != j + (numberOfNodes / 2) && j != i + (numberOfNodes / 2)){
                    int score = semiGlobalAlignmentScore(node1, node2);
                    edges.add(new Edge(node1, node2, score));
                }
            }
        }
        System.out.print("\r");
        System.out.print("[" + "*".repeat(10) + "]");
        System.out.println();

        edges.sort(new CompareEdges());

        return edges;
    }

    /**
     * This method is used to do the union between a path and an edge.
     *
     * @param chem The path on which perform the union.
     * @param arc The edge on which perform the union.
     * @return The list representing the united path and edge.
     */
//    public List<Edge> union(List<Edge> chem, Edge arc){
//        List<Edge> chemin = chem;
//        int flag = 0;
//        if(chemin.isEmpty()){
//            chemin.add(arc);
//            return chemin;
//        }
//        for (int i = 0; i < chem.size(); i++) {
//            Edge edges = chem.get(i);
//            if (edges.getDest() == arc.getSrc()){
//                edges.getChemin().remove(edges.getChemin().size()-1);
//                ArrayList<Fragment> newArrayList = new ArrayList<>(edges.getChemin());
//                newArrayList.addAll(arc.getChemin());
//                Edge temp = new Edge(edges.getSrc(), arc.getDest(), newArrayList);
//                chemin.add(temp);
//                flag += 1;
//            }
//            else if (edges.getSrc() == arc.getDest()){
//                arc.getChemin().remove(arc.getChemin().size()-1);
//                ArrayList<Fragment> newArrayList = new ArrayList<>(arc.getChemin());
//                newArrayList.addAll(edges.getChemin());
//
//                Edge temp = new Edge(arc.getSrc(), edges.getDest(), newArrayList);
//                chemin.add(temp);
//                flag += 1;
//            }
//
//        }
//        if(flag == 0){
//            chemin.add(arc);
//        }
//        return chemin;
//    }

}