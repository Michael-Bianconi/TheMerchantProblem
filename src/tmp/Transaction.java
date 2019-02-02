package tmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Michael Bianconi
 * @since 02/01/2019
 * Every time a Merchant buys or sells a commodity, it's stored as
 * a transaction.
 */
public class Transaction {

    /** Name of the table. */
    public static final String TABLE_NAME = "TRANSACTIONS";

    /** Unique ID to this instance of this class. */
    public final int ID;

    /** ID of the associated Voyage. */
    public final int VOYAGE_ID;

    /** ID of the associated commodity sold to the port. */
    public final int OUT_ID;

    /** Amount transferred to the Port. */
    public final int OUT_AMOUNT;

    /** ID of the associated commodity given by the port. */
    public final int IN_ID;

    /** Amount transferred from the Port. */
    public final int IN_AMOUNT;

    /**
     * Creates the table if it doesn't already exist.
     *
     * @param conn The connection to the H2 database.
     * @return Returns true if and only if successful.
     */
    public static boolean createTable(Connection conn) {
        String sqlCommand =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        "ID             INTEGER PRIMARY KEY NOT NULL," +
                        "VOYAGE_ID      INTEGER             NOT NULL," +
                        "IN_ID          INTEGER             NOT NULL," +
                        "IN_AMOUNT      INTEGER             NOT NULL," +
                        "OUT_ID         INTEGER             NOT NULL," +
                        "OUT_AMOUNT     INTEGER             NOT NULL," +
                        "FOREIGN KEY(VOYAGE_ID) REFERENCES " +
                        Voyage.TABLE_NAME + "(ID)," +
                        "FOREIGN KEY(IN_ID) REFERENCES " +
                        Commodity.TABLE_NAME + "(ID)," +
                        "FOREIGN KEY(OUT_ID) REFERENCES " +
                        Commodity.TABLE_NAME + "(ID));";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Transaction)) { return false; }
        Transaction r = (Transaction) o;
        return r.ID == ID;
    }

    public int hashCode() {
        return Objects.hash(ID);
    }

    /**
     * Retrieves the data from the connection.
     *
     * @param id   The ID of the data to retrieve.
     * @param conn The connection to the database.
     * @return Returns the data with the given ID or null if not found.
     */
    public static Transaction retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int ID=-1;
        int voyage=-1;
        int inID=-1;
        int outID=-1;
        int inAmount=-1;
        int outAmount=-1;
        String sqlCommand =
                "SELECT * FROM " + TABLE_NAME + " WHERE ID=" + id + ";";

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                if (numResults > 1) {
                    return null;
                }

                ID = set.getInt("ID");
                voyage = set.getInt("VOYAGE_ID");
                inID = set.getInt("IN_ID");
                inAmount = set.getInt("IN_AMOUNT");
                outID = set.getInt("OUT_ID");
                outAmount = set.getInt("OUT_AMOUNT");
                numResults++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // There were no rows in the table with that ID
        if (numResults == 0) {
            return null;
        }

        return new Transaction(ID,voyage,inID,inAmount,outID,outAmount);
    }

    /**
     * Retrieves ALL data from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each ID to its data.
     */
    public static HashMap<Integer, Transaction> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, Transaction> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {


                int ID = set.getInt("ID");
                int voyage = set.getInt("VOYAGE_ID");
                int inID = set.getInt("IN_ID");
                int inAmount = set.getInt("IN_AMOUNT");
                int outID = set.getInt("OUT_ID");
                int outAmount = set.getInt("OUT_AMOUNT");
                map.put(ID, new Transaction(ID,voyage,inID,inAmount,outID,outAmount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the Voyage associated with this Transaction.
     *
     * @param conn Connection to the database.
     * @return Returns the Route referenced by this PortInventory.
     */
    public Merchant retrieveVoyage(Connection conn) {

        return Merchant.retrieve(VOYAGE_ID, conn);
    }

    /**
     * Returns the received Commodity associated with this Route.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this RouteCost.
     */
    public Commodity retrieveInCommodity(Connection conn) {

        return Commodity.retrieve(IN_ID, conn);
    }

    /**
     * Returns the given Commodity associated with this Route.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this RouteCost.
     */
    public Commodity retrieveOutCommodity(Connection conn) {

        return Commodity.retrieve(OUT_ID, conn);
    }

    /**
     * Constructs a new Route.
     * @param id ID of the Route.
     * @param voyage ID of the Voyage.
     * @param inID ID of the received Commodity.
     * @param inAmount Amount of IN received.
     * @param outID ID of the given Commodity.
     * @param outAmount Amount of OUT given.
     */
    public Transaction(
            int id, int voyage, int inID,
            int inAmount, int outID, int outAmount) {
        this.ID = id;
        this.VOYAGE_ID = voyage;
        this.IN_ID = inID;
        this.IN_AMOUNT = inAmount;
        this.OUT_ID = outID;
        this.OUT_AMOUNT = outAmount;
    }

    /**
     * Stores this MerchantInventory into the database. Will replace the old
     * one if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME + "(ID,VOYAGE_ID,IN_ID,IN_AMOUNT," +
                        "OUT_ID,OUT_AMOUNT) " +
                "VALUES("+ID+","+VOYAGE_ID+","+IN_ID+","+IN_AMOUNT+
                        ","+OUT_ID+","+OUT_AMOUNT+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return "[TRANSACTION]\t"+ID+"\t"+VOYAGE_ID+"\t"+IN_ID+"\t"+
                IN_AMOUNT+"\t"+OUT_ID+"\t"+OUT_AMOUNT;
    }
}