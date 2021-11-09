import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.Scanner;

/**
 * This class is the main class for the Fragment Assembler.
 */
public class FragmentAssembler {

    /**
     * This method is used to extract the fragments from the fasta file.
     *
     * @param path The path to the fasta file.
     * @return An ArrayList of Strings with all the fragments from the fasta file.
     */
    public static ArrayList<String> extractFragments(String path) {
        ArrayList<String> fragments = new ArrayList<>();
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
                        StringBuilder fragment = new StringBuilder();
                        data = reader.nextLine();
                        newFragment = pattern.matcher(data).find();
                        while (reader.hasNextLine() && !newFragment) {
                            fragment.append(data);
                            data = reader.nextLine();
                            newFragment = pattern.matcher(data).find();
                        }
                        fragments.add(fragment.toString());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return fragments;
    }

    public static void main(String[] args) {
        ArrayList<String> fragments = extractFragments(System.getenv("PATH_COLLECTION_1"));
    }
}
