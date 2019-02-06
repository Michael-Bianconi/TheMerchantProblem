
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
 * @since 01/29/2019
 *
 * Ports hold Commodities that are bought and sold to Merchants. Merchants
 * travel from one to another using Routes. Ports have a functionally
 * unlimited amount of gold.
 */
public class Port extends TMPObject {

    /** Unique ID of the Port. */
    public final int ID;

    /** Name of the Port. */
    public final String NAME;

    /** X COORDINATE OF THE PORT. */
    public final int X;

    /** Y COORDINATE OF THE PORT. */
    public final int Y;

    // Constructors ===========================================================
    /**
     * Constructs a new Port.
     * @param name Name of the Port.
     * @param x X coordinate of the Port.
     * @param y Y coordinate of the Port.
     */
    public Port(
            String name, int x, int y) {

        this(TMPDatabase.uniqueID(), name, x, y);
    }

    /**
     * Construct a new Port.
     * @param id ID of the Port.
     * @param name Name of the Port.
     * @param x X coordinate of the Port.
     * @param y Y coordinate of the Port.
     */
    public Port(int id, String name, int x, int y) {
        this.ID = id;
        this.NAME = name;
        this.X = x;
        this.Y = y;
    }

    /**
     * @return Returns true if the two Ports have a Route between them.
     */
    public static boolean areConnected(int start, int end, TMPDatabase db) {

        String command = "SELECT COUNT(*) FROM " +
                TMPFactory.tableName("ROUTE") +
                " WHERE START_PORT="+start+" AND END_PORT="+end+";";
        Connection conn = db.getConnection();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(command)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            set.next();
            return set.getInt(1) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a single PortInventory, based on its commodity.
     * @param id ID of the commodity.
     * @param db Connection to the database.
     * @return Returns the PortInventory, or null.
     */
    public PortInventory retrievePortInventoryByCommodity(
            int id, TMPDatabase db) {
        // Initialize variables
        int numResults = 0;
        int in_id = -1;
        int pID = -1;
        int cID = -1;
        int onHand = -1;
        int buy = -1;
        int sell = -1;
        String sqlCommand =
        "SELECT * FROM " + TMPFactory.tableName("PORT_INVENTORY") +
        " WHERE PORT_ID="+ID+" AND COMMODITY_ID="+id+";";
        Connection conn = db.getConnection();

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
     * Retrieves every inventory linked to this port.
     * @param db Connection to the database.
     * @return Returns a Map linking each InventoryID to its Inventory.
     */
    public HashMap<Integer, PortInventory> retrievePortInventories(
            TMPDatabase db){

        // Initialize variables
        String sqlCommand =
                "SELECT * FROM " + TMPFactory.tableName("PORT_INVENTORY") +
                            " WHERE PORT_ID="+ID+";";
        HashMap<Integer, PortInventory> map = new HashMap<>();
        Connection conn = db.getConnection();

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
                map.put(id, new PortInventory(id,pID,cID,onHand,buy,sell));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Retrieves every Route with this Port as its Start Port.
     * @param db Connection to the database.
     * @return Returns a Map linking each RouteID to its Route.
     */
    public HashMap<Integer, Route> retrieveRoutesOut(
            TMPDatabase db){

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TMPFactory.tableName("ROUTE") +
                " WHERE START_PORT="+ID+";";
        HashMap<Integer, Route> map = new HashMap<>();
        Connection conn = db.getConnection();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int id = set.getInt("ID");
                int start = set.getInt("START_PORT");
                int end = set.getInt("END_PORT");
                map.put(id, new Route(id,start,end));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    // Object =================================================================
    @Override
    public int hashCode() { return Objects.hash(ID); }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Port)) { return false; }
        Port p = (Port) o;
        return p.ID == ID;
    }

    @Override
    public String toString() {
        return "[PORT]\t" + ID + "\t" + NAME + "\t" + X + "\t" + Y;
    }

    // TMPObject ==============================================================
    public int ID() {return ID;}

    public String storeString() {
        return "MERGE INTO " + TMPFactory.tableName("PORT")
        + "(ID,NAME,X,Y) KEY(ID) VALUES("+ID+",'"+NAME+"',"+X+","+Y+");";
    }
}
