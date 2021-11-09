/**
 * This class is used to define a fragment.
 */
public class Fragment {
    private int length;
    private String fragment;
    // stock the inverse of a fragment

    public Fragment(String fragment) {
        length = fragment.length();
        this.fragment = fragment;
    }

    public int getLength() {
        return length;
    }

    public String getFragment() {
        return fragment;
    }

}
