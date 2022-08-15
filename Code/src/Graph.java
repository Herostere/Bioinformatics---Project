import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is used to describe a graph.
 */
public class Graph {
    private final List<Fragment> nodes;
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
        // nodesList.addAll(Arrays.asList(fragments.getCollection()));
        // Todo check if better without loop

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

    public static semiGlobalScoreMatrix semiGlobalAlignmentScore(Fragment firstFragment, Fragment secondFragment) {
        /*
        the first fragment is represented vertically in the matrix
        the second fragment is represented horizontally in the matrix
        */
        int firstFragmentLength = firstFragment.getLength();
        int secondFragmentLength = secondFragment.getLength();

        int matrixVerticalLength = firstFragmentLength + 1;
        int matrixHorizontalLength = secondFragmentLength + 1;
        int[][] matrix = new int[matrixVerticalLength][matrixHorizontalLength];

        String firstFragmentString = firstFragment.getFragment();
        String secondFragmentString = secondFragment.getFragment();
        for (int i = 1; i < matrixVerticalLength; i++) {
            for (int j = 1; j < matrixHorizontalLength; j++) {

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
        int lastLineMaximumIndex = -1;
        for (int i = 0; i < lastLine.length - 1; i++) {
            int currentElement = lastLine[i];
            if (currentElement >= lastLineMaximum) {
                lastLineMaximum = currentElement;
                lastLineMaximumIndex = i;
            }
        }

        int lastIndex = matrixHorizontalLength - 1;
        int lastColumnMaximum = Integer.MIN_VALUE;
        int lastColumnMaximumIndex = -1;
        for (int i = 0; i < matrixVerticalLength; i++) {
            int currentElement = matrix[i][lastIndex];
            if (currentElement >= lastColumnMaximum) {
                lastColumnMaximum = currentElement;
                lastColumnMaximumIndex = i;
            }
        }

        if (lastLineMaximum > lastColumnMaximum) {
            return new semiGlobalScoreMatrix(lastLineMaximum, matrix, lastLineMaximumIndex, "line");
        }
        else {
            return new semiGlobalScoreMatrix(lastColumnMaximum, matrix, lastColumnMaximumIndex, "column");
        }
    }

    /**
     * This method is used to perform the greedy algorithm.
     *
     * @return The ordered list of fragments that are in the path.
     */
    public List<Fragment> greedy(){
        boolean[] in = new boolean[numberOfNodes];
        boolean[] out = new boolean[numberOfNodes];
        List<List<Fragment>> listOfSets = new ArrayList<>(numberOfNodes);

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
            if (!in[indexG] && !out[indexF] && (foundF.size() != foundG.size() || !foundF.equals(foundG))) {
                in[indexG] = true;
                out[indexF] = true;
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

    public static List<Alignment> alignments(List<Fragment> fragments) {
        List<Alignment> alignmentsList = new ArrayList<>();
        int i = 0;
        int j = 1;
        while (i < fragments.size() - 1) {
            Fragment fragment1 = fragments.get(i);
            Fragment fragment2 = fragments.get(j);
            alignmentsList.add(alignment(fragment1, fragment2, semiGlobalAlignmentScore(fragment1, fragment2)));
            i += 1;
            j += 1;
        }
        return alignmentsList;
    }

    public static Alignment alignment(Fragment fragment1, Fragment fragment2, semiGlobalScoreMatrix semiGlobal) {
        int i = 0;
        int j = 0;
        char charFrag1;
        char charFrag2;
        StringBuilder construction1 = new StringBuilder();
        StringBuilder construction2 = new StringBuilder();

        switch (semiGlobal.position()) {
            case "line" -> {
                i = semiGlobal.matrix().length-1;
                j = semiGlobal.index();
            }
            case "column" -> {
                i = semiGlobal.index();
                j = semiGlobal.matrix()[i].length-1;
            }
        }

        while(i != 0 && j != 0) {
            int currentScore = semiGlobal.matrix()[i][j];

            int scoreUp = semiGlobal.matrix()[i-1][j];
            int scoreLeft = semiGlobal.matrix()[i][j-1];
            int scoreDiagonal = semiGlobal.matrix()[i-1][j-1];

            // diagonal case
            charFrag1 = fragment1.getFragment().charAt(i-1);
            charFrag2 = fragment2.getFragment().charAt(j-1);
            if ((charFrag1 == charFrag2 && scoreDiagonal + 1 == currentScore) || (charFrag1 != charFrag2 && scoreDiagonal - 1 == currentScore)) {
                construction1.insert(0, charFrag1);
                construction2.insert(0, charFrag2);
                i -= 1;
                j -= 1;
            }
            // left case
            else if (scoreLeft - 2 == currentScore) {
                construction1.insert(0, "-");
                construction2.insert(0, charFrag2);
                j -= 1;
            }
            // up case
            else if (scoreUp - 2 == currentScore) {
                construction1.insert(0, charFrag1);
                construction2.insert(0, "-");
                i -= 1;
            }
        }
        while (i > 0) {
            charFrag1 = fragment1.getFragment().charAt(i-1);
            construction1.insert(0, charFrag1);
            construction2.insert(0, ".");
            i -= 1;
        }
        while (j > 0) {
            charFrag2 = fragment2.getFragment().charAt(j-1);
            construction2.insert(0, charFrag2);
            construction1.insert(0, ".");
            j -= 1;
        }
        if (semiGlobal.position().equals("column")) {
            for (int x = semiGlobal.index(); x < fragment1.getLength(); x++) {
                charFrag1 = fragment1.getFragment().charAt(Math.max(0, x - 1));
                construction1.insert(Math.max(0, construction1.length() - 1), charFrag1);
            }
        }
        else {
            for (int x = semiGlobal.index(); x < fragment2.getLength(); x++) {
                charFrag2 = fragment2.getFragment().charAt(Math.max(0, x - 1));
                construction2.insert(Math.max(0, construction2.length() - 1), charFrag2);
            }
        }
        return new Alignment(construction1, construction2);
    }

    public static List<Alignment> shifts(List<Alignment> alignments) {
        List<Alignment> shiftedAlignments = new ArrayList<>();
        String currentDestination = alignments.get(0).getDestination();
        String currentSource;
        shiftedAlignments.add(new Alignment(new StringBuilder(alignments.get(0).getSource()), new StringBuilder(alignments.get(0).getDestination())));
        int totalShifts = countShifts(currentDestination);
        for (int i = 1; i < alignments.size(); i++) {
            currentDestination = alignments.get(i).getDestination();
            currentSource = alignments.get(i).getSource();
            shiftedAlignments.add(new Alignment(new StringBuilder(currentSource), new StringBuilder(currentDestination), totalShifts));
            totalShifts += countShifts(currentDestination);
        }
        return shiftedAlignments;
    }

    private static int countShifts(String fragment) {
        int shifts = 0;
        for (int i = 0; i < fragment.length(); i++) {
            char currentChar = fragment.charAt(i);

            if ('.' != currentChar) {
                break;
            }
            shifts += 1;
        }
        return shifts;
    }

    private List<Edge> sortEdges() {
        List<Edge> edges = new ArrayList<>(numberOfNodes * (numberOfNodes - 2));
        int numberOfThreads = 50;
        int partitionSize = numberOfNodes / numberOfThreads;
        List<List<Integer>> partitions = new ArrayList<>();
        List<Integer> collection = IntStream.rangeClosed(0, numberOfNodes - 1).boxed().collect(Collectors.toList());
        for (int i = 0; i < collection.size(); i += partitionSize) {
            partitions.add(collection.subList(i, Math.min(i + partitionSize, collection.size())));
        }
        List<SortEdgesThread> threads = new ArrayList<>(numberOfThreads);
        for (List<Integer> partition : partitions) {
            threads.add(new SortEdgesThread(nodes, partition.get(0), partition.get(partition.size() - 1), edges));
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