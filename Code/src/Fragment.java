/**
 * This class is used to define a fragment.
 */
public class Fragment {
    private int length;
    // stock the inverse of a fragment

    public Fragment(String fragment) {
        length = fragment.length();
    }

    public int getLength() {
        return length;
    }

}
