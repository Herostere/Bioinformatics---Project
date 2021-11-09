import java.util.ArrayList;

/**
 * This class describes a Collection. A Collection is a set of fragments.
 */
public class Collection {

    private Fragment[] collection;
    private int length;

    public Collection(ArrayList<Fragment> fragments) {

        length = fragments.size();
        collection = new Fragment[length];

        for (int i = 0; i < fragments.size(); i++) {
            collection[i] = fragments.get(i);
        }
    }

    public Fragment[] getCollection() {
        return collection;
    }
}
