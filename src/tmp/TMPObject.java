package tmp;

/**
 * TMPObjects are abstract classes that allow subclasses to more
 * easily interact with the TMPDatabase.
 *
 * @author Michael Bianconi
 * @since 02-04-2019
 */
public abstract class TMPObject {

    /** @return Returns a unique ID to the TMPObject. */
    public abstract int ID();

    /**
     * Returns a String that can used to store this object in the
     * H2 database.
     *
     * Example: MERGE INTO TABLE(COL,COL) VALUES(VAL,VAL);
     *
     * @return String.
     */
    public abstract String storeString();
}
