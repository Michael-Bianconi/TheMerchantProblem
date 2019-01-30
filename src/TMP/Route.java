/**
 * @author Michael Bianconi
 * @since 01/29/19
 *
 * Routes connect Ports, and may have a cost associated with using them.
 */

package TMP;

import java.util.ArrayList;
import java.util.Objects;

public class Route {

    public static class RouteCost {

        private final Commodity commodity;
        private int cost;

        public RouteCost(Commodity commodity, int cost)
        {
            this.commodity = commodity;
            this.cost = cost;
        }

        public Commodity getCommodity() { return this.commodity; }
        public int getCost() { return this.cost; }

        public void setCost(int cost) { this.cost = cost; }

        public int hashCode() { return this.commodity.hashCode(); }
        public boolean equals(Object o) {
            if (!(o instanceof RouteCost)) { return false; }
            RouteCost rc = (RouteCost) o;
            return rc.commodity.equals(this.commodity) && this.cost == rc.cost;
        }
    }

    private final Port port1;
    private final Port port2;
    private ArrayList<RouteCost> costs;


    public Route(Port p1, Port p2) {
        this.port1 = p1;
        this.port2 = p2;
        this.costs = new ArrayList<>();
    }

    public Port getPort1() { return this.port1; }
    public Port getPort2() { return this.port2; }
    public ArrayList getCosts() { return this.costs; }
    public void addCost(RouteCost rc) { this.costs.add(rc); }

    public int hashCode() { return Objects.hash(this.port1, this.port2); }
    public boolean equals(Object o) {
        if (!(o instanceof Route)) { return false; }
        Route r = (Route) o;
        return r.port1.equals(this.port1)
            && r.port2.equals(this.port2)
            && r.costs.equals(this.costs);
    }
}
