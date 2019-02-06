package tmp;

import data.TMPDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Merchant's carry Commodities with them to buy and sell at Ports.
 */
public class MerchantInventory extends TMPObject {

    /** Unique ID to this instance of this class. */
    public final int ID;

    /** ID of the associated Merchant. */
    public final int MERCHANT_ID;

    /** ID of the associated commodity. */
    public final int COMMODITY_ID;

    /** Amount that must be paid. */
    public int AMOUNT;

    // Constructors ===========================================================

    /**
     * Constructs a new MerchantInventory.
     * @param id ID of the Inventory.
     * @param merchantID ID of the Merchant.
     * @param commodityID ID of the associated Commodity.
     * @param amount Amount owned.
     */
    public MerchantInventory(
            int id, int merchantID, int commodityID, int amount) {
        this.ID = id;
        this.MERCHANT_ID = merchantID;
        this.COMMODITY_ID = commodityID;
        this.AMOUNT = amount;
    }

    /**
     * Constructs a new MerchantInventory.
     * @param merchant ID of the Merchant.
     * @param commodity ID of the associated Commodity.
     * @param amount Amount owned by the Merchant.
     */
    public MerchantInventory(
            int merchant, int commodity, int amount) {
        this(TMPDatabase.uniqueID(), merchant, commodity, amount);
    }


    /**
     * Returns the Merchant associated with this Route.
     *
     * @param db Connection to the database.
     * @return Returns the Route referenced by this PortInventory.
     */
    public Merchant retrieveMerchant(TMPDatabase db) {

        return (Merchant) db.retrieve("MERCHANT", MERCHANT_ID);
    }

    /**
     * Returns the Commodity associated with this Route.
     *
     * @param db Connection to the database.
     * @return Returns the Commodity referenced by this RouteCost.
     */
    public Commodity retrieveCommodity(TMPDatabase db) {

        return (Commodity) db.retrieve("COMMODITY", COMMODITY_ID);
    }

    // Object =================================================================
    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public String toString() {
        return "[MERCHANT INVENTORY]\t"+ID+"\t"+
                MERCHANT_ID+"\t"+COMMODITY_ID+"\t"+AMOUNT;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MerchantInventory)) { return false; }
        MerchantInventory r = (MerchantInventory) o;
        return r.ID == ID;
    }

    // TMPObject ==============================================================
    public int ID() {return ID;}

    public String storeString() {
        return "MERGE INTO " + TMPFactory.tableName("MERCHANT_INVENTORY")
                + "(ID,MERCHANT_ID,COMMODITY_ID,AMOUNT) " +
                "VALUES("+ID+","+MERCHANT_ID+","+COMMODITY_ID+","+AMOUNT+");";
    }
}