/**
 * @author Michael Bianconi
 * @since 01/29/2019
 *
 * Inventories are simple lists of commodities with related fields such as
 * price and availability.
 */

package TMP;

import java.util.HashMap;

public class Inventory {

    public static class InventoryItem
    {
        private final Commodity commodity;
        private int onHand;
        private int buyPrice;
        private int sellPrice;

        public InventoryItem(Commodity c, int onHand, int buy, int sell) {
            this.commodity = c;
            this.onHand = onHand;
            this.buyPrice = buy;
            this.sellPrice = sell;
        }

        public Commodity getCommodity() { return this.commodity; }
        public int getOnHand() { return this.onHand; }
        public int getBuyPrice() { return this.buyPrice; }
        public int getSellPrice() { return this.sellPrice; }
        public int hashCode() { return this.commodity.hashCode(); }
        public boolean equals(Object o)
        {
            if (!(o instanceof InventoryItem)) {
                return false;
            }

            InventoryItem i = (InventoryItem) o;
            return this.commodity.equals(i.commodity)
                && this.onHand == i.onHand
                && this.buyPrice == i.buyPrice
                && this.sellPrice == i.sellPrice;
        }
        public String toString() {
            return "[INVENTORY ITEM]\n"
                 + "\t" + this.commodity + "\n"
                 + "\tOn Hand: " + this.onHand + "\n"
                 + "\tBuy Price: " + this.buyPrice + "\n"
                 + "\tSell Price: " + this.sellPrice + "\n";
        }
    }

    private HashMap<String,InventoryItem> items;

    public Inventory()
    {
        this.items= new HashMap<>();
    }

    public int getNumItems() { return this.items.size(); }
    public InventoryItem get(String c) { return this.items.get(c); }
    public void add(String s, InventoryItem c) { this.items.put(s, c); }
}
