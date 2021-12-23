import java.util.ArrayList;

/**
 * This class describes a Collection. A Collection is a set of fragments.
 */
public class Collection {

    private Fragment[] collection;
    private int length;

    public Collection(ArrayList<Fragment> fragments) {
        this.length = fragments.size();
        this.collection = new Fragment[length];

        for (int i = 0; i < fragments.size(); i++) {
            this.collection[i] = fragments.get(i);
        }
    }

    public Fragment[] getCollection() {
        return this.collection;
    }
}
