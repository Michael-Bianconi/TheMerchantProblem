
package tmp;

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
 * travel from one to another using Routes.
 */
public class Port {

    /** Name of the Port table. */
    public static final String TABLE_NAME = "PORTS";

    /** Unique ID of the Port. */
    public final int ID;

    /** Name of the Port. */
    public final String NAME;

    /** X COORDINATE OF THE PORT. */
    public final int X;

    /** Y COORDINATE OF THE PORT. */
    public final int Y;

    /**
     * Creates the port table if it doesn't already exist.
     * @param conn The connection to the H2 database.
     * @return Returns true if and only if successful.
     */
    public static boolean createTable(Connection conn) {
        String sqlCommand =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "ID     INTEGER PRIMARY KEY NOT NULL," +
                "NAME   TEXT                NOT NULL," +
                "X      INTEGER             NOT NULL," +
                "Y      INTEGER             NOT NULL);";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Retrieves the Port data from the connection.
     * @param id The ID of the Port to retrieve.
     * @param conn The connection to the database.
     * @return Returns the Port with the given ID, or null if not found.
     */
    public static Port retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int in_id = -1;
        String name = "";
        int x = -1;
        int y = -1;
        String sqlCommand =
                "SELECT * FROM " + TABLE_NAME + " WHERE ID=" + id + ";";

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                if (numResults > 1) {return null;}

                in_id = set.getInt("ID");
                name = set.getString("NAME");
                x = set.getInt("X");
                y = set.getInt("Y");
                numResults++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // There were no rows in the table with that ID
        if (numResults == 0) { return null; }

        return new Port(in_id, name, x, y);
    }


    /**
     * Retrieves ALL Ports from the table and stores them in a HashMap.
     * @param conn Connection to the database.
     * @return Returns a Map linking each PortID to its Port.
     */
    public static HashMap<Integer, Port> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, Port> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int id = set.getInt("ID");
                String name = set.getString("NAME");
                int x = set.getInt("X");
                int y = set.getInt("Y");
                map.put(id, new Port(id, name, x, y));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Retrieves every inventory linked to this port.
     * @param conn Connection to the database.
     * @return Returns a Map linking each InventoryID to its Inventory.
     */
    public HashMap<Integer, PortInventory> retrievePortInventories(
            Connection conn){

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + PortInventory.TABLE_NAME +
                            " WHERE PORT_ID="+ID+";";
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
     * @param conn Connection to the database.
     * @return Returns a Map linking each RouteID to its Route.
     */
    public HashMap<Integer, Route> retrieveRoutesOut(
            Connection conn){

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + Route.TABLE_NAME +
                " WHERE START_PORT="+ID+";";
        HashMap<Integer, Route> map = new HashMap<>();

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


    /**
     * Stores this Port into the database. Will replace the old
     * one if one already exists with the same ID.
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME + "(ID,NAME,X,Y) KEY(ID) " +
                "VALUES("+ID+",'"+NAME+"',"+X+","+Y+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
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

    public int hashCode() { return Objects.hash(ID); }

    public boolean equals(Object o)
    {
        if (!(o instanceof Port)) { return false; }
        Port p = (Port) o;
        return p.ID == ID;
    }

    public String toString() {
        return "[PORT]\t" + ID + "\t" + NAME + "\t" + X + "\t" + Y;
    }
}
