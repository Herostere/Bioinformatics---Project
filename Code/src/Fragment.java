/**
 * This class is used to define a fragment.
 */

import java.util.HashSet;
import java.util.Set;

public class Fragment {

    private int length;
    private String fragment;
    // private SuffixTree suffixTree;
    // stock the inverse of a fragment


    public Fragment(String fragment) {
        this.length = fragment.length();
        this.fragment = fragment;
    }

    public int getLength() {
        return this.length;
    }

    public String getFragment() {
        return this.fragment;
    }

    public Fragment reversedComplementary() {
        String[] fragmentArray = this.fragment.split("");
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
        return new Fragment(reverseComplementary);
    }
}
