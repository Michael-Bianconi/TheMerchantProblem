package tmp;

import data.TMPDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Ports hold Commodities that can bought and sold by Merchants.
 * These Commodities are stored in PortInventories.
 */
public class PortInventory extends TMPObject {

    /**
     * ID of the Inventory.
     */
    public final int ID;

    /**
     * ID of the Port this Inventory is associated with.
     */
    public final int PORT_ID;

    /**
     * ID of the Commodity this Inventory is associated with.
     */
    public final int COMMODITY_ID;

    /**
     * The amount of the Commodity currently on hand.
     */
    public int ON_HAND;

    /**
     * The amount of gold it costs to buy this Commodity from the port.
     */
    public int BUY_PRICE;

    /**
     * The amount of gold the Port will give you in exchange.
     */
    public int SELL_PRICE;

    /**
     * Returns the Commodity associated with this PortInventory.
     *
     * @param db Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Commodity retrieveCommodity(TMPDatabase db) {

        return (Commodity) db.retrieve("COMMODITY", COMMODITY_ID);
    }

    /**
     * Returns the Port associated with this PortInventory.
     *
     * @param db Connection to the database.
     * @return Returns the Port referenced by this PortInventory.
     */
    public Port retrievePort(TMPDatabase db) {

        return (Port) db.retrieve("PORT", PORT_ID);
    }


    /**
     * Construct a new PortInventory.
     *
     * @param pID    ID of the Port.
     * @param cID    ID of the Commodity.
     * @param onHand Amount of the commodity available to buy.
     * @param buy    The cost of buying the commodity from the Port.
     * @param sell   The cost of selling the commodity to the Port.
     */
    public PortInventory(
            int pID, int cID, int onHand, int buy, int sell) {
        this(TMPDatabase.uniqueID(), pID, cID, onHand, buy, sell);
    }

    /**
     * Construct a new PortInventory.
     *
     * @param id     ID of the PortInventory.
     * @param pID    ID of the Port.
     * @param cID    ID of the Commodity.
     * @param onHand Amount of the commodity available to buy.
     * @param buy    The cost of buying the commodity from the Port.
     * @param sell   The cost of selling the commodity to the Port.
     */
    public PortInventory(
            int id, int pID, int cID, int onHand, int buy, int sell) {

        this.ID = id;
        this.PORT_ID = pID;
        this.COMMODITY_ID = cID;
        this.ON_HAND = onHand;
        this.BUY_PRICE = buy;
        this.SELL_PRICE = sell;
    }
    // Object =================================================================

    @Override
    public int hashCode() { return Objects.hash(ID, PORT_ID, COMMODITY_ID); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PortInventory)) {
            return false;
        }
        PortInventory p = (PortInventory) o;
        return p.ID == ID;
    }

    @Override
    public String toString() {
        return "[PORT INVENTORY]\t" + ID + "\t" + PORT_ID + "\t" +
                COMMODITY_ID + "\t" + ON_HAND + "\t" + BUY_PRICE +
                "\t" + SELL_PRICE;
    }

    // TMPObject ==============================================================
    public int ID() {return ID;}

    public String storeString() {

        return "MERGE INTO " + TMPFactory.tableName("PORT_INVENTORY") +
                "(ID,PORT_ID,COMMODITY_ID," +
                "ON_HAND, BUY_PRICE, SELL_PRICE) KEY(ID) " +
                "VALUES(" + ID + "," + PORT_ID + "," +
                COMMODITY_ID + "," + ON_HAND + "," +
                BUY_PRICE + "," + SELL_PRICE + ");";
    }
}
