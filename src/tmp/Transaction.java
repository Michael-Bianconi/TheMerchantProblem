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

    /** ID of the associated commodity traded. */
    public final int COMMODITY_ID;

    /** Amount of the Commodity traded. */
    public final int AMOUNT;

    /** Amount traded for (selling to the port will result in negative. */
    public final int PRICE;

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
                        "COMMODITY_ID   INTEGER             NOT NULL," +
                        "AMOUNT         INTEGER             NOT NULL," +
                        "PRICE          INTEGER             NOT NULL," +
                        "FOREIGN KEY(VOYAGE_ID) REFERENCES " +
                        Voyage.TABLE_NAME + "(ID)," +
                        "FOREIGN KEY(COMMODITY_ID) REFERENCES " +
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
        String sqlCommand =
                "SELECT * FROM " + TABLE_NAME + " WHERE ID=" + id + ";";

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            set.next();
            int ID = set.getInt("ID");
            int voyage = set.getInt("VOYAGE_ID");
            int com = set.getInt("COMMODITY_ID");
            int amount = set.getInt("AMOUNT");
            int price = set.getInt("PRICE");
            return new Transaction(ID,voyage, com, amount, price);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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
                int comID = set.getInt("COMMODITY_ID");
                int amount = set.getInt("AMOUNT");
                int price = set.getInt("PRICE");
                map.put(ID, new Transaction(ID,voyage,comID, amount, price));
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
     * Returns this Transaction's Commodity.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this Transaction.
     */
    public Commodity retrieveCommodity(Connection conn) {

        Commodity c = Commodity.retrieve(COMMODITY_ID, conn);
        System.out.println(COMMODITY_ID+": "+c);
        return c;
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

    /**
     * Stores this MerchantInventory into the database. Will replace the old
     * one if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME + "(ID,VOYAGE_ID,COMMODITY_ID,"+
                        "AMOUNT,PRICE) " +
                "VALUES("+ID+","+VOYAGE_ID+","+COMMODITY_ID+","+AMOUNT+
                        ","+PRICE+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return "[TRANSACTION]\t"+ID+"\t"+VOYAGE_ID+"\t"+COMMODITY_ID+"\t"+
                AMOUNT+"\t"+PRICE;
    }
}