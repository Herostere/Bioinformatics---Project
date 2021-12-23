/**
 * This class is used to define a fragment.
 */
public class Fragment {

    private int length;
    private String fragment;
    private int shift;

    public Fragment(String fragment) {
        this.length = fragment.length();
        this.fragment = fragment;
        this.shift = 0; // representing the best shifting for the alignment
    }

    public int getLength() {
        return this.length;
    }

    public String getFragment() {
        return this.fragment;
    }

    public int getShift() {
        return this.shift;
    }

    public void setShift(int x) {
        this.shift = x;
    }

    /**
     * This function is used to compute the reversed complementary of a nucleotide.
     *
     * @return A fragment representing the reversed complementary of another fragment.
     */
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
