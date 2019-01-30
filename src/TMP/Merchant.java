/**
 * @author Michael Bianconi
 * @since 01/29/19
 *
 * The Merchant's job is to travel from Port to Port and make as much profit
 * as possible.
 */

package TMP;

import java.util.ArrayList;
import java.util.List;

public class Merchant {

    private final String name;
    private Inventory inventory;
    private ArrayList<Port> visitedPorts;
    private ArrayList<Transaction> transactions;

    public Merchant(String name, Port home, Inventory inventory)
    {
        this.name = name;
        this.inventory = inventory;
        this.visitedPorts = new ArrayList<>();
        this.visitedPorts.add(home);
        this.transactions = new ArrayList<>();
    }

    public Inventory getInventory() { return this.inventory; }
    public Port getCurrentPort() { return visitedPorts.get(visitedPorts.size()-1); }
    public Port getHomePort() { return visitedPorts.get(0); }
    public List getVisitedPorts() { return this.visitedPorts; }
    public List getTransactions() { return this.transactions; }

    public void addVisitedPort(Port port) { this.visitedPorts.add(port); }
    public void addTransaction(Transaction t) { this.transactions.add(t); }

    public int hashCode() { return this.name.hashCode(); }
    public boolean equals(Object o) {
        if (!(o instanceof Merchant)) { return false; }
        Merchant m = (Merchant) o;
        return m.name.equals(this.name);
    }

}
