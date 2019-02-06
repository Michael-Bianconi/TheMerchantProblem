package tmp;

import data.TMPDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Michael Bianconi
 * @since 02/01/2019
 *
 * A Voyage is a log of the Ports a Merchant has visited and the
 * transactions he's made.
 */
public class Voyage extends TMPObject{

    /** ID of the Voyage. */
    public final int ID;

    /** ID of the Merchant. */
    public final int MERCHANT_ID;

    /** ID of the Port visited. */
    public final int PORT_ID;

    /** Timestamp of the visit (does not have to be the actual time. */
    public final int TIMESTAMP;

    /**
     * Constructs a new Voyage. Note: the timestamp does not (and maybe
     * should not) be the actual time. It only needs to be greater than
     * that of the previous Voyage.
     *
     * @param merchant ID of the Merchant.
     * @param port ID of the Port.
     * @param timestamp Voyage's timestamp.
     */
    public Voyage(int merchant, int port, int timestamp) {
        this(TMPDatabase.uniqueID(), merchant, port, timestamp);
    }

    /**
     * Constructs a new Voyage. Note: the timestamp does not (and maybe
     * should not) be the actual time. It only needs to be greater than
     * that of the previous Voyage.
     * @param id ID of the Voyage.
     * @param merchant ID of the Merchant.
     * @param port ID of the Port.
     * @param timestamp Voyage's timestamp.
     */
    public Voyage(int id, int merchant, int port, int timestamp) {
        ID = id;
        MERCHANT_ID = merchant;
        PORT_ID = port;
        TIMESTAMP = timestamp;
    }


    /**
     * Retrieves all Transaction from the table and stores them in a HashMap.
     *
     * @param db Connection to the database.
     * @return Returns a Map linking each ID to its data.
     */
    public HashMap<Integer, Transaction> retrieveAllTransactions(
            TMPDatabase db) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TMPFactory.tableName("TRANSACTION") +
                " WHERE VOYAGE_ID="+ID+";";
        HashMap<Integer, Transaction> map = new HashMap<>();
        Connection conn = db.getConnection();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                int ID = set.getInt("ID");
                int voyage = set.getInt("VOYAGE_ID");
                int com = set.getInt("COMMODITY_ID");
                int amount = set.getInt("AMOUNT");
                int price = set.getInt("PRICE");
                map.put(ID, new Transaction(ID,voyage,com,amount,price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the Merchant associated with this Voyage.
     *
     * @param db Connection to the database.
     * @return Returns the Route referenced by this PortInventory.
     */
    public Merchant retrieveMerchant(TMPDatabase db) {

        return (Merchant) db.retrieve("MERCHANT", MERCHANT_ID);
    }

    /**
     * Returns the Port associated with this Voyage.
     *
     * @param db Connection to the database.
     * @return Returns the Port referenced by this Voyage.
     */
    public Port retrievePort(TMPDatabase db) {

        return (Port) db.retrieve("PORT", PORT_ID);
    }

    // Object =================================================================
    @Override
    public int hashCode() {return ID;}

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Voyage)) { return false; }
        Voyage v = (Voyage) o;
        return v.ID == ID;
    }

    @Override
    public String toString() {
        return "[VOYAGE]\t"+ID+"\t"+MERCHANT_ID+"\t"+PORT_ID+"\t"+TIMESTAMP;
    }

    // TMPObject ==============================================================

    public int ID() {return ID;}

    public String storeString() {
        return "MERGE INTO " + TMPFactory.tableName("VOYAGE") +
                "(ID,MERCHANT_ID,PORT_ID,TIMESTAMP) " +
                "VALUES("+ID+","+MERCHANT_ID+","+PORT_ID+","+
                TIMESTAMP+");";
    }
}
