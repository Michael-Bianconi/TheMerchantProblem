package test;

import TMP.*;
import TMP.Inventory.InventoryItem;

public class basictest {

    public static void main(String args[])
    {
        Commodity gold = new Commodity("Gold");
        Commodity silk = new Commodity("Silk");
        Commodity furs = new Commodity("Furs");
        Commodity meat = new Commodity("Meat");

        InventoryItem nInv_GOLD = new InventoryItem(gold, 8, 7, 5);
        InventoryItem nInv_SILK = new InventoryItem(silk, 2, 10, 19);
        InventoryItem sInv_FURS = new InventoryItem(furs, 1,34,19);
        InventoryItem mInv_GOLD = new InventoryItem(gold, 100, 0, 0);
        InventoryItem mInv_MEAT = new InventoryItem(meat, 100, 15, 10);

        Inventory nassauInv = new Inventory();
        Inventory sanJuanInv = new Inventory();
        Inventory antiguaInv = new Inventory();
        Inventory merchantInv = new Inventory();

        nassauInv.add("Gold", nInv_GOLD);
        nassauInv.add("Silk", nInv_SILK);
        sanJuanInv.add("Furs", sInv_FURS);
        merchantInv.add("Gold", mInv_GOLD);
        merchantInv.add("Meat", mInv_MEAT);

        Port nassau = new Port("Nassua", nassauInv);
        Port sanJuan = new Port( "San Juan", sanJuanInv);
        Port antigua = new Port( "Antigua", antiguaInv);

        Merchant merchant = new Merchant("Merchant", nassau, merchantInv);

        Inventory portInventory = merchant.getCurrentPort().getInventory();
        InventoryItem portItem = portInventory.get("Silk");

        System.out.printf("Nassau's Silk:\n%s\n",portItem);
    }
}
