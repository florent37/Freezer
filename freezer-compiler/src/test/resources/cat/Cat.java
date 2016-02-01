package cat;

import fr.xebia.android.freezer.annotations.Model;

/**
 * Created by florentchampigny on 21/01/2016.
 */
@Model
public class Cat {
    String shortName;
    String name;
    long[] ages;
    long nana;

    public Cat() {
    }

    public Cat(String shortName) {
        this.shortName = shortName;
    }
}
