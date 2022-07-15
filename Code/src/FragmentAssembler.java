import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is the main class for the Fragment Assembler.
 */
public class FragmentAssembler {

    private static ArrayList<Fragment> fragments;
    private static String collectionNumber = "";

    /**
     * This method is used to extract the fragments from the fasta file.
     *
     * @param path The path to the fasta file.
     * @return An ArrayList of Strings with all the fragments from the fasta file.
     */
    public static ArrayList<Fragment> extractFragments(String path) {

        fragments = new ArrayList<>();
        String data;

        try {
            File file = new File(path);
            Scanner reader = new Scanner(file);
            Pattern pattern = Pattern.compile("fragment");

            if (reader.hasNextLine()) {
                data = reader.nextLine();
                boolean newFragment = pattern.matcher(data).find();

                while (reader.hasNextLine()) {
                    if (newFragment) {
                        if (Objects.equals(collectionNumber, "")) {
                            collectionNumber = data.substring(data.indexOf('c'));
                            char first = Character.toUpperCase(collectionNumber.charAt(0));
                            collectionNumber = first + collectionNumber.substring(1);
                        }
                        StringBuilder fragment = new StringBuilder();
                        data = reader.nextLine();
                        newFragment = pattern.matcher(data).find();

                        while (reader.hasNextLine() && !newFragment) {
                            fragment.append(data);
                            data = reader.nextLine();
                            newFragment = pattern.matcher(data).find();
                        }

                        fragments.add(new Fragment(fragment.toString()));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return fragments;
    }

    /**
     * This method is used to compute the alignment score between two fragments.
     *
     * @param frag1 The first fragment to compare.
     * @param frag2 The second fragment to compare.
     * @return An int representing the score of the best alignment.
     */
    private static int computeScore(String frag1, String frag2) {
        int score = 0;
        for (int i=0; i < Math.min(frag1.length(), frag2.length()); i++) {
            char a = frag1.charAt(i);
            char b = frag2.charAt(i);
            if (a == '-' || b == '-') {
                continue;
            }
            else if (a != b) {
                score--;
            }
            else {
                score++;
            }
        }
        return score;
    }

    /**
     * This method adds the gaps in the "next" fragment in the path, for all fragment.
     *
     * @param path The selected path.
     */
    public static void gaps(List<Edge> path) {
        Edge firstElem = path.get(0);
        ArrayList<Fragment> chemin = firstElem.getChemin();

        for (int i=0; i < chemin.size()-1; i++) {
            int score = 0;
            int shift = 0;
            Fragment firstFrag = chemin.get(i);
            Fragment secondFrag = chemin.get(i+1);
            String firstFragString = firstFrag.getFragment();
            String secondFragString = secondFrag.getFragment();
            for(int j=0; j < firstFragString.length()-1; j++){
                int tempScore = computeScore(firstFragString, secondFragString);
                if(tempScore > score){
                    score = tempScore;
                    shift = j;
                }
                secondFragString = "-" + secondFragString;
            }
            secondFrag.setShift(shift);
        }
    }

    /**
     * This method is used to compute the consensus on a path.
     *
     * @param path The path on which we want to check the consensus.
     * @return The resulting string of the consensus.
     */
    private static String consensus(List<Edge> path) {
        Edge firstElem = path.get(0);
        ArrayList<Fragment> chemin = firstElem.getChemin();
        ArrayList<String> consensusedStrings = new ArrayList<>();

        int startAt;
        int previousLength = 0;

        // iterate on all fragments of the path.
        for (int i=0; i < chemin.size(); i++) {
            Fragment frag = chemin.get(i);
            if (i != 0) {
                previousLength = chemin.get(i-1).getLength();
            }
            else {
                previousLength = chemin.get(0).getLength();

            }
            int fragLength = frag.getLength();
            ArrayList<Fragment> superpos = new ArrayList<>();

            // find fragments that overlap.
            for (int j=i+1; j < chemin.size(); j++){
                if (fragLength - chemin.get(j).getShift() > 0) {
                    fragLength -= chemin.get(j).getShift();
                    superpos.add(chemin.get(j));
                }
            }

            String consensused = "";
            ArrayList<String> superposStrings = new ArrayList<>();
            superposStrings.add(frag.getFragment());
            int zeroToAdd = 0;
            for (Fragment frag2 : superpos) {
                char[] gaps = new char[frag2.getShift()];
                Arrays.fill(gaps, '-');
                String newFrag;
                zeroToAdd += frag2.getShift();
                char[] zero = new char[zeroToAdd];
                Arrays.fill(zero, '0');
                String zeros = new String(zero);
                newFrag = zeros + frag2.getFragment();
                superposStrings.add(newFrag);
            }

            int remain = frag.getLength() + frag.getShift() - previousLength;
            if (remain < 0) {
                if (i+1 < chemin.size()) {
                    Fragment fragB = chemin.get(i+1);
                    fragB.setShift(fragB.getShift() + frag.getShift());
                    chemin.remove(i);
                    i--;
                    continue;
                }
                break;
            }

            startAt = frag.getLength() - remain;
            if (remain == 0) {
                startAt = 0;
            }
            for (int j=startAt; j < frag.getFragment().length(); j++) {
                int countA = 0;
                int countC = 0;
                int countT = 0;
                int countG = 0;
                int count_ = 0;

                for(String fragA : superposStrings) {
                    if (fragA.length() < j) {
                        continue;
                    }
                    if (fragA.charAt(j) == 'a') {
                        countA++;
                    }
                    else if (fragA.charAt(j) == 'c') {
                        countC++;
                    }
                    else if (fragA.charAt(j) == 'g') {
                        countG++;
                    }
                    else if (fragA.charAt(j) == 't') {
                        countT++;
                    }
                    else if (fragA.charAt(j) == '-') {
                        count_++;
                    }
                }

                if (countA > countC && countA > countT && countA > countG && countA > count_) {
                    consensused += "a";
                }
                else if (countC > countA && countC > countT && countC > countG && countC > count_) {
                    consensused += "c";
                }
                else if (countT > countA && countT > countC && countT > countG && countT > count_) {
                    consensused += "t";
                }
                else if (countG > countA && countG > countC && countG > countT && countG > count_) {
                    consensused += "g";
                }
                else if (count_ > countA && count_ > countC && count_ > countT && count_ > countG) {
                    consensused += "-";
                }
                else if (countA == countC || countA == countT || countA == countG || countA == count_) {
                    consensused += "a";
                }
                else if (countC == countG || countC == countT || countC == count_) {
                    consensused += "c";
                }
                else if (countT == countG || countT == count_) {
                    consensused += "t";
                }
                else if (countG == count_){
                    consensused += "g";
                }
            }
            consensusedStrings.add(consensused);
        }
        String chaine = consensusedStrings.stream().collect(Collectors.joining(""));
        return chaine;
    }

    public static void writeFile(String chaine, String pathOutput) {
        try {
            FileWriter myWriter = new FileWriter(pathOutput);
            int splits;
            if (chaine.length() % 80 == 0){
                splits = chaine.length() / 80;
            }
            else {
                splits = (chaine.length() / 80) + 1;
            }
            String[] stringSplits = new String[splits];
            int currentIndex = 0;
            for (int i = 0; i < splits; i++) {
                int endIndex;
                if ((currentIndex + 80) < chaine.length()) {
                    endIndex = currentIndex + 80;
                }
                else {
                    endIndex = chaine.length();
                }
                stringSplits[i] = chaine.substring(currentIndex, endIndex);
                currentIndex += 80;
            }

            myWriter.write("> Groupe-1 " + collectionNumber + " Longueur " + chaine.length());
            for (String strings : stringSplits) {
                myWriter.write(strings);
            }
            myWriter.close();
        }
        catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    private static String reversedComplementaryString(String string) {
        String[] fragmentArray = string.split("");
        StringBuilder complementary = new StringBuilder();
        String reverseComplementary;

        for (String str : fragmentArray) {
            switch (str) {
                case "A" -> complementary.append("T");
                case "T" -> complementary.append("A");
                case "C" -> complementary.append("G");
                case "G" -> complementary.append("C");
                default -> complementary.append("-");
            }
        }

        reverseComplementary = new StringBuilder(complementary).reverse().toString();
        return reverseComplementary;
    }


    public static void main(String[] args) {
        String pathInput = "";
        String pathOutput = "";
        String pathOutputIC = "";

        if (args.length==5) {
            pathInput = args[0];
            if (args[1].equals("-out")) {
                pathOutput = args[2];
            }
            else {
                System.exit(1);
            }
            if (args[3].equals("-out-ic")) {
                pathOutputIC = args[4];
            }
            else {
                System.exit(1);
            }
            // 1. Extract fragments
            Collection collection1 = new Collection(extractFragments(pathInput));
            // 2. Construction of the graph
            Graph graph = new Graph(collection1);
            // 3. Perform "semi-global" alignment (computation of Hamiltonian path?)
            List<Edge> path = graph.greedy();
            // 3. Gaps
            gaps(path);
            // 4. Consensus
            String chaine = consensus(path);
            // 5. Print the string
            System.out.println(chaine);
            String chaineIC = reversedComplementaryString(chaine);
            writeFile(chaine, pathOutput);
            writeFile(chaineIC, pathOutputIC);
        }
    }
}
