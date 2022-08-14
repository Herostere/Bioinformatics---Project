import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is used to describe a graph.
 */
public class Graph {
    private List<Fragment> nodes;
    private final int numberOfNodes;

    public Graph (Collection fragments){
        nodes = constructorNodes(fragments);

        numberOfNodes = nodes.size();
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
//         List<Edge> edges = new ArrayList<>(numberOfNodes * (numberOfNodes - 2));
//         System.out.println("Constructing edges:");
//         for (int i = 0; i < numberOfNodes; i++) {
//            int percentage = (int) (((float) i / numberOfNodes) * 10);
//            System.out.print("\r");
//            System.out.print("[" + "*".repeat(percentage) + "-".repeat(10 - percentage) + "]");
//            Fragment node1 = nodes.get(i);
//            for (int j = 0; j < numberOfNodes; j++) {
//                Fragment node2 = nodes.get(j);
//                if (i != j && i != j + (numberOfNodes / 2) && j != i + (numberOfNodes / 2)){
//                    int score = semiGlobalAlignmentScore(node1, node2);
//                    edges.add(new Edge(node1, node2, score));
//                }
//            }
//        }
//        System.out.print("\r");
//        System.out.print("[" + "*".repeat(10) + "]");
//        System.out.println();
        List<Edge> edges = new ArrayList<>(numberOfNodes * (numberOfNodes - 2));
        int numberOfThreads = 20;
        int partitionSize = numberOfNodes / numberOfThreads;
        List<Integer> range = IntStream.rangeClosed(0, numberOfNodes - 1).boxed().toList();
        List<List<Integer>> partitions = new ArrayList<>();
        for (int i = 0; i < range.size(); i += partitionSize) {
            partitions.add(range.subList(i, Math.min(i + partitionSize, range.size())));
        }
        List<SortEdgesThread> threads = new ArrayList<>(numberOfThreads);
        int i = 0;
        for (List<Integer> list : partitions) {
            threads.add(new SortEdgesThread(nodes, "Thread " + i, list.get(0), list.get(list.size() - 1), edges));
            i += 1;
        }
        for (SortEdgesThread thread : threads) {
            thread.start();
        }
        for (SortEdgesThread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        edges.sort(new CompareEdges());

        return edges;
    }
}