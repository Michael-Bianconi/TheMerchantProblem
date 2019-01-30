package TMP;

/**
 * @author Michael Bianconi
 * @since 01/28/2019
 *
 * Commodities are bought and sold in the pursuit of profit.
 */
public class Commodity implements Comparable<Commodity> {

    /** The name of the Commodity. Cannot be changed. */
    private final String name;

    /** Initialize the Commodity with a final String as a name. */
    public Commodity(String name) {
        this.name = name;
    }

    /** @return Returns the name of the Commodity. */
    public String getName() { return this.name; }

    /** @return Returns the hashcode of the Commodity's name. */
    public int hashCode() { return this.name.hashCode(); }

    /** @return Returns true if the names are the same. */
    public boolean equals(Object o) {
        if (!(o instanceof Commodity)) {
            return false;
        }
        Commodity c = (Commodity) o;
        return c.name.equals(this.name);
    }

    /** @return Returns a lexicographical comparison of their names. */
    public int compareTo(Commodity c)
    {
        return this.name.compareTo(c.name);
    }

    public String toString() {
        return "[Commodity]\n\t" + this.name + "\n";
    }
}
