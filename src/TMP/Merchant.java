package TMP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Michael Bianconi
 * @since 01/31/2019
 * Merchants travel from Port to Port, trading Commodities. Their goal is
 * to make as much profit as possible before returning home.
 */
public class Merchant {

    /** Name of the Merchant table. */
    public static final String TABLE_NAME = "MERCHANTS";

    /** ID of the Merchant. */
    public final int ID;

    /** Name of the Merchant. */
    public final String NAME;

    /** ID of the Merchant's Home Port. */
    public final int HOME_PORT;

    /** ID of the Port the Merchant is currently at. */
    public int CURRENT_PORT;

    /** How much weight the Merchant is capable of carrying at once. */
    public final int CAPACITY;

    /**
     * Constructs a new Merchant.
     *
     * @param id Merchant's ID.
     * @param name Name of the Merchant.
     * @param home ID of the Merchant's Home Port.
     * @param current ID of the Merchant's Current Port.
     * @param capacity How much weight the Merchant can hold.
     */
    public Merchant(int id, String name, int home, int current, int capacity) {
        ID = id;
        NAME = name;
        HOME_PORT = home;
        CURRENT_PORT = current;
        CAPACITY = capacity;
    }

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
                        "NAME           TEXT                NOT NULL," +
                        "HOME_PORT      INTEGER             NOT NULL," +
                        "CURRENT_PORT   INTEGER             NOT NULL," +
                        "CAPACITY       INTEGER             NOT NULL," +
                        "FOREIGN KEY(HOME_PORT) REFERENCES " +
                        Port.TABLE_NAME + "(ID)," +
                        "FOREIGN KEY(CURRENT_PORT) REFERENCES " +
                        Port.TABLE_NAME + "(ID));";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Merchant)) { return false; }
        Merchant m = (Merchant) o;
        return m.ID == ID
            && m.NAME.equals(NAME)
            && m.HOME_PORT == HOME_PORT
            && m.CAPACITY == CAPACITY;
    }

    public int hashCode() { return Objects.hash(ID,NAME,HOME_PORT,CAPACITY); }

    /**
     * Retrieves the Merchant data from the connection.
     *
     * @param id   The ID of the Merchant to retrieve.
     * @param conn The connection to the database.
     * @return Returns the Merchant with the given ID, or null if not found.
     */
    public static Merchant retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int merchantID = -1;
        String name = "";
        int home = -1;
        int current = -1;
        int capacity = -1;
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

                merchantID = set.getInt("ID");
                name = set.getString("NAME");
                home = set.getInt("HOME_PORT");
                current = set.getInt("CURRENT_PORT");
                capacity = set.getInt("CAPACITY");
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

        return new Merchant(merchantID, name, home, current, capacity);
    }

    /**
     * Retrieves ALL Merchants from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each MerchantID to its Merchant.
     */
    public static HashMap<Integer, Merchant> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, Merchant> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int merchantID = set.getInt("ID");
                String name = set.getString("NAME");
                int home = set.getInt("HOME_PORT");
                int current = set.getInt("CURRENT_PORT");
                int capacity = set.getInt("CAPACITY");
                map.put(merchantID, new Merchant(
                        merchantID,name,home,current,capacity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Retrieves ALL Merchants from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each MerchantID to its Merchant.
     */
    public HashMap<Integer, MerchantInventory>
        retrieveAllMerchantInventories(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + MerchantInventory.TABLE_NAME +
                " WHERE MERCHANT_ID="+ID+";";
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
                map.put(id, new MerchantInventory(
                        id,merchantID,commodityID,amount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Retrieves Voyages from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each ID to its data.
     */
    public HashMap<Integer, Voyage> retrieveAllVoyages(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + Voyage.TABLE_NAME +
                " WHERE MERCHANT_ID="+ID+";";
        HashMap<Integer, Voyage> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                int id = set.getInt("ID");
                int merchant = set.getInt("MERCHANT_ID");
                int port = set.getInt("PORT_ID");
                int time = set.getInt("TIMESTAMP");
                map.put(id, new Voyage(id,merchant,port,time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the Home Port associated with this Merchant.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveHomePort(Connection conn) {

        return Port.retrieve(HOME_PORT, conn);
    }

    /**
     * Returns the Current Port associated with this Merchant.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveCurrentPort(Connection conn) {

        return Port.retrieve(CURRENT_PORT, conn);
    }

    /**
     * Stores this Merchant into the database. Will replace the old
     * one if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME +
                        "(ID,NAME,HOME_PORT,CURRENT_PORT,CAPACITY) " +
                "VALUES("+ID+",'"+NAME+"',"+HOME_PORT+","+CURRENT_PORT+"," +
                        CAPACITY+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return "[MERCHANT]\t"+ID+"\t"+NAME+"\t"+HOME_PORT+
                "\t"+CURRENT_PORT+"\t"+CAPACITY;
    }
}
