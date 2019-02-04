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
public class PortInventory {

    /**
     * Name of the PortInventory table.
     */
    public static final String TABLE_NAME = "PORT_INVENTORIES";

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
     * Creates the port table if it doesn't already exist.
     *
     * @param conn The connection to the H2 database.
     * @return Returns true if and only if successful.
     */
    public static boolean createTable(Connection conn) {
        String sqlCommand =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        "ID             INTEGER PRIMARY KEY NOT NULL," +
                        "PORT_ID        INTEGER             NOT NULL," +
                        "COMMODITY_ID   INTEGER             NOT NULL," +
                        "ON_HAND        INTEGER             NOT NULL," +
                        "BUY_PRICE      INTEGER             NOT NULL," +
                        "SELL_PRICE     INTEGER             NOT NULL," +
                        "FOREIGN KEY(PORT_ID) REFERENCES " +
                        Port.TABLE_NAME + "(ID)," +
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

    /**
     * Retrieves the PortInventory data from the connection.
     *
     * @param id   The ID of the PortInventory to retrieve.
     * @param conn The connection to the database.
     * @return Returns the PortInventory with the given ID,
     * or null if not found.
     */
    public static PortInventory retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int in_id = -1;
        int pID = -1;
        int cID = -1;
        int onHand = -1;
        int buy = -1;
        int sell = -1;
        String sqlCommand =
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE ID=" + id + ";";

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                if (numResults > 1) {
                    return null;
                }

                in_id = set.getInt("ID");
                pID = set.getInt("PORT_ID");
                cID = set.getInt("COMMODITY_ID");
                onHand = set.getInt("ON_HAND");
                buy = set.getInt("BUY_PRICE");
                sell = set.getInt("SELL_PRICE");
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

        return new PortInventory(in_id, pID, cID, onHand, buy, sell);
    }

    /**
     * Retrieves ALL Inventories from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each InventoryID to its Inventory.
     */
    public static HashMap<Integer, PortInventory> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, PortInventory> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int id = set.getInt("ID");
                int pID = set.getInt("PORT_ID");
                int cID = set.getInt("COMMODITY_ID");
                int onHand = set.getInt("ON_HAND");
                int buy = set.getInt("BUY_PRICE");
                int sell = set.getInt("SELL_PRICE");
                map.put(id, new PortInventory(id, pID, cID, onHand, buy, sell));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the Commodity associated with this PortInventory.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Commodity retrieveCommodity(Connection conn) {

        return Commodity.retrieve(COMMODITY_ID, conn);
    }

    /**
     * Returns the Port associated with this PortInventory.
     *
     * @param conn Connection to the database.
     * @return Returns the Port referenced by this PortInventory.
     */
    public Port retrievePort(Connection conn) {

        return Port.retrieve(PORT_ID, conn);
    }

    /**
     * Stores this Inventory into the database. Will replace the old
     * one if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME + "(ID,PORT_ID,COMMODITY_ID," +
                        "ON_HAND, BUY_PRICE, SELL_PRICE) KEY(ID) " +
                        "VALUES(" + ID + "," + PORT_ID + "," +
                        COMMODITY_ID + "," + ON_HAND + "," +
                        BUY_PRICE + "," + SELL_PRICE + ");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    public int hashCode() { return Objects.hash(ID, PORT_ID, COMMODITY_ID); }

    public boolean equals(Object o) {
        if (!(o instanceof PortInventory)) {
            return false;
        }
        PortInventory p = (PortInventory) o;
        return p.ID == ID
                && p.PORT_ID == PORT_ID
                && p.COMMODITY_ID == COMMODITY_ID;
    }

    public String toString() {
        return "[PORT INVENTORY]\t" + ID + "\t" + PORT_ID + "\t" +
                COMMODITY_ID + "\t" + ON_HAND + "\t" + BUY_PRICE +
                "\t" + SELL_PRICE;
    }
}
