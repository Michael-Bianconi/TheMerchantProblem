/**
 * @author Michael Bianconi
 * @since 01/29/2019
 *
 * Ports hold commodities that are bought and sold to Merchants.
 */

package TMP;

import java.util.Objects;

public class Port {

    private final String name;
    private Inventory inventory;

    public Port(String name, Inventory inventory) {
        this.name = name;
        this.inventory = inventory;
    }

    public String getName() { return this.name; }
    public Inventory getInventory() { return this.inventory; }
    public int hashCode() { return Objects.hash(this.name, this.inventory); }
    public boolean equals(Object o)
    {
        if (!(o instanceof Port)) { return false; }
        Port p = (Port) o;
        return p.name.equals(this.name) && p.inventory.equals(this.inventory);
    }

}
