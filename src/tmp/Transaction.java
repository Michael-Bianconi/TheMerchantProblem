package tmp;

import data.TMPDatabase;

/**
 * @author Michael Bianconi
 * @since 02/01/2019
 * Every time a Merchant buys or sells a commodity, it's stored as
 * a transaction.
 */
public class Transaction extends TMPObject {

    /** Unique ID to this instance of this class. */
    public final int ID;

    /** ID of the associated Voyage. */
    public final int VOYAGE_ID;

    /** ID of the associated commodity traded. */
    public final int COMMODITY_ID;

    /** Amount of the Commodity traded. */
    public final int AMOUNT;

    /** Amount traded for (selling to the port will result in negative. */
    public final int PRICE;


    /**
     * Returns the Voyage associated with this Transaction.
     *
     * @param db Connection to the database.
     * @return Returns the Route referenced by this PortInventory.
     */
    public Voyage retrieveVoyage(TMPDatabase db) {

        return (Voyage) db.retrieve("VOYAGE", VOYAGE_ID);
    }

    /**
     * Returns this Transaction's Commodity.
     *
     * @param db Connection to the database.
     * @return Returns the Commodity referenced by this Transaction.
     */
    public Commodity retrieveCommodity(TMPDatabase db) {

        return (Commodity) db.retrieve("COMMODITY", COMMODITY_ID);
    }

    /**
     * Constructs a new Route.
     * @param voyage ID of the Voyage.
     * @param comID ID of the traded Commodity.
     * @param amount Amount of the Commodity traded.
     * @param price Gold transferred (negative for selling to port.)
     */
    public Transaction(
            int voyage, int comID, int amount, int price) {
        this(TMPDatabase.uniqueID(), voyage, comID, amount, price);
    }

    /**
     * Constructs a new Route.
     * @param id ID of the Route.
     * @param voyage ID of the Voyage.
     * @param comID ID of the traded Commodity.
     * @param amount Amount of the Commodity traded.
     * @param price Gold transferred (negative for selling to port.)
     */
    public Transaction(
            int id, int voyage, int comID, int amount, int price) {
        this.ID = id;
        this.VOYAGE_ID = voyage;
        this.COMMODITY_ID = comID;
        this.AMOUNT = amount;
        this.PRICE = price;
    }
    // Object =================================================================

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Transaction)) { return false; }
        Transaction r = (Transaction) o;
        return r.ID == ID;
    }

    @Override
    public int hashCode() {return ID;}

    @Override
    public String toString() {
        return "[TRANSACTION]\t"+ID+"\t"+VOYAGE_ID+"\t"+COMMODITY_ID+"\t"+
                AMOUNT+"\t"+PRICE;
    }

    // TMPObject ==============================================================

    public int ID() {return ID;}

    public String storeString() {
        return "MERGE INTO " + TMPFactory.tableName("TRANSACTION") +
                "(ID,VOYAGE_ID,COMMODITY_ID,"+
                "AMOUNT,PRICE) " +
                "VALUES("+ID+","+VOYAGE_ID+","+COMMODITY_ID+","+AMOUNT+
                ","+PRICE+");";
    }
}