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


    private static String consensus(List<Alignment> shifted) {
        int index = findSomething(shifted);
        StringBuilder consensused = new StringBuilder();
        char value;
        while (findSomething(shifted) != -1) {
            int aCounter = 0;
            int cCounter = 0;
            int gCounter = 0;
            int tCounter = 0;
            int gapCounter = 0;
            for (int i = index; i < shifted.size(); i++) {
                int shifts = shifted.get(i).getShifts();
                if (shifts > 0) {
                    shifted.get(i).setShifts(shifts - 1);
                }
                else if (shifted.get(i).getSource().length() > 0) {
                    String source = shifted.get(i).getSource();
                    value = source.charAt(0);
                    switch (value) {
                        case 'a' -> aCounter += 1;
                        case 'c' -> cCounter += 1;
                        case 'g' -> gCounter += 1;
                        case 't' -> tCounter += 1;
                        case '-' -> gapCounter += 1;
                    }
                    shifted.get(i).setSource(source.substring(1));
                }
                else if (shifted.get(i).getDestination().length() > 0) {
                    String destination = shifted.get(i).getDestination();
                    value = destination.charAt(0);
                    switch (value) {
                        case 'a' -> aCounter += 1;
                        case 'c' -> cCounter += 1;
                        case 'g' -> gCounter += 1;
                        case 't' -> tCounter += 1;
                        case '-' -> gapCounter += 1;
                    }
                    shifted.get(i).setDestination(destination.substring(1));
                }
            }
            String toAdd = computeToAdd(aCounter, cCounter, gCounter, tCounter, gapCounter);
            consensused.append(toAdd);
        }
        return consensused.toString();
    }

    private static String computeToAdd(int aCounter, int cCounter, int gCounter, int tCounter, int gapCounter) {
        boolean aC = aCounter >= cCounter;
        boolean aG = aCounter >= gCounter;
        boolean aT = aCounter >= tCounter;
        boolean aGap = aCounter >= gapCounter;
        boolean cA = cCounter >= aCounter;
        boolean cG = cCounter >= gCounter;
        boolean cT = cCounter >= tCounter;
        boolean cGap = cCounter >= gapCounter;
        boolean gA = gCounter >= aCounter;
        boolean gC = gCounter >= cCounter;
        boolean gT = gCounter >= tCounter;
        boolean gGap = gCounter >= gapCounter;
        boolean tA = tCounter >= aCounter;
        boolean tC = tCounter >= cCounter;
        boolean tG = tCounter >= gCounter;
        boolean tGap = tCounter >= gapCounter;
        if (aC && aG && aT && aGap) {
            return "a";
        }
        else if (cA && cG && cT && cGap) {
            return "c";
        }
        else if (gA && gC && gT && gGap) {
            return "g";
        }
        else if (tA && tC && tG && tGap) {
            return "t";
        }
        else  {
            return "-";
        }

    }

    private static int findSomething(List<Alignment> shifted) {
        for (int i = 0; i < shifted.size(); i++) {
            int shifts = shifted.get(i).getShifts();
            if (shifts > 0) {
                shifted.get(i).setShifts(shifts - 1);
            }
            else if (shifted.get(i).getSource().length() > 0 || shifted.get(i).getDestination().length() > 0) {
                return  i;
            }
        }
        return -1;
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
                case "a" -> complementary.append("t");
                case "t" -> complementary.append("a");
                case "c" -> complementary.append("g");
                case "g" -> complementary.append("c");
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
            List<Fragment> orderedEdges = graph.greedy();
            List<Alignment> alignments = Graph.alignments(orderedEdges);
            List<Alignment> alignmentsShifted = Graph.shifts(alignments);
            String finalString = consensus(alignmentsShifted);

            // 5. Print the string
            System.out.println(finalString);
            String stringIC = reversedComplementaryString(finalString);
            System.out.println(stringIC);
//            writeFile(chaine, pathOutput);
//            writeFile(chaineIC, pathOutputIC);
        }
    }
}
