package tmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Merchant's carry Commodities with them to buy and sell at Ports.
 */
public class MerchantInventory {

    /** Name of the table. */
    public static final String TABLE_NAME = "MERCHANT_INVENTORIES";

    /** Unique ID to this instance of this class. */
    public final int ID;

    /** ID of the associated Merchant. */
    public final int MERCHANT_ID;

    /** ID of the associated commodity. */
    public final int COMMODITY_ID;

    /** Amount that must be paid. */
    public final int AMOUNT;

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
                        "MERCHANT_ID    INTEGER             NOT NULL," +
                        "COMMODITY_ID   INTEGER             NOT NULL," +
                        "AMOUNT         INTEGER             NOT NULL," +
                        "FOREIGN KEY(MERCHANT_ID) REFERENCES " +
                        Merchant.TABLE_NAME + "(ID)," +
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
        if (!(o instanceof MerchantInventory)) { return false; }
        MerchantInventory r = (MerchantInventory) o;
        return r.ID == ID
                && r.MERCHANT_ID == MERCHANT_ID
                && r.COMMODITY_ID == COMMODITY_ID
                && r.AMOUNT == AMOUNT;
    }

    public int hashCode() {
        return Objects.hash(ID,MERCHANT_ID,COMMODITY_ID,AMOUNT);
    }

    /**
     * Retrieves the data from the connection.
     *
     * @param id   The ID of the data to retrieve.
     * @param conn The connection to the database.
     * @return Returns the data with the given ID or null if not found.
     */
    public static MerchantInventory retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int costID = -1;
        int merchantID = -1;
        int commodityID = -1;
        int amount = -1;
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

                costID = set.getInt("ID");
                merchantID = set.getInt("MERCHANT_ID");
                commodityID = set.getInt("COMMODITY_ID");
                amount = set.getInt("AMOUNT");
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

        return new MerchantInventory(costID, merchantID, commodityID, amount);
    }

    /**
     * Retrieves ALL data from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each CostID to its Cost.
     */
    public static HashMap<Integer, MerchantInventory> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, MerchantInventory> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int id = set.getInt("ID");
                int merchantID = set.getInt("MERCHANT_ID");
                int commodityID = set.getInt("COMMODITY_ID");
                int amount = set.getInt("AMOUNT");
                map.put(id, new MerchantInventory(id, merchantID, commodityID, amount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the Merchant associated with this Route.
     *
     * @param conn Connection to the database.
     * @return Returns the Route referenced by this PortInventory.
     */
    public Merchant retrieveMerchant(Connection conn) {

        return Merchant.retrieve(MERCHANT_ID, conn);
    }

    /**
     * Returns the Commodity associated with this Route.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this RouteCost.
     */
    public Commodity retrieveCommodity(Connection conn) {

        return Commodity.retrieve(COMMODITY_ID, conn);
    }

    /**
     * Constructs a new Route.
     * @param id ID of the Route.
     * @param merchantID ID of the Route.
     * @param commodityID ID of the associated Commodity.
     * @param amount Amount required to travel.
     */
    public MerchantInventory(
            int id, int merchantID, int commodityID, int amount) {
        this.ID = id;
        this.MERCHANT_ID = merchantID;
        this.COMMODITY_ID = commodityID;
        this.AMOUNT = amount;
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
                "MERGE INTO " + TABLE_NAME + "(ID,MERCHANT_ID,COMMODITY_ID," +
                        "AMOUNT) " +
                "VALUES("+ID+","+MERCHANT_ID+","+COMMODITY_ID+","+AMOUNT+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return "[MERCHANT INVENTORY]\t"+ID+"\t"+MERCHANT_ID+"\t"+COMMODITY_ID+"\t"+AMOUNT;
    }
}