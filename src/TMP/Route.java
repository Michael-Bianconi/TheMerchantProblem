package TMP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * The Merchant travels between Ports using Routes. Routes may have
 * an associated RouteCost that must be paid before using the
 * Route.
 */
public class Route {

    /** Name of Route table. */
    public static final String TABLE_NAME = "ROUTES";

    /** ID of the Route. */
    public final int ID;

    /** ID of the starting Port. */
    public final int START_PORT;

    /** ID of the end Port. */
    public final int END_PORT;

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
                        "START_PORT     INTEGER             NOT NULL," +
                        "END_PORT       INTEGER             NOT NULL," +
                        "FOREIGN KEY(START_PORT) REFERENCES " +
                        Port.TABLE_NAME + "(ID)," +
                        "FOREIGN KEY(END_PORT) REFERENCES " +
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
        if (!(o instanceof Route)) { return false; }
        Route r = (Route) o;
        return r.ID == ID
            && r.START_PORT == START_PORT
            && r.END_PORT == END_PORT;
    }

    public int hashCode() { return Objects.hash(ID,START_PORT,END_PORT); }

    /**
     * Retrieves the Route data from the connection.
     *
     * @param id   The ID of the Route to retrieve.
     * @param conn The connection to the database.
     * @return Returns the Route with the given ID,
     *         or null if not found.
     */
    public static Route retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int routeID = -1;
        int startID = -1;
        int endID = -1;
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

                routeID = set.getInt("ID");
                startID = set.getInt("START_PORT");
                endID = set.getInt("END_PORT");
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

        return new Route(routeID, startID, endID);
    }

    /**
     * Retrieves ALL Routes from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each InventoryID to its Inventory.
     */
    public static HashMap<Integer, Route> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, Route> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int routeID = set.getInt("ID");
                int startID = set.getInt("START_PORT");
                int endID = set.getInt("END_PORT");
                map.put(routeID, new Route(routeID, startID, endID));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Retrieves all RouteCosts associated with this Route.
     * @param conn Connection to the database.
     * @return Returns a Map of all RouteCosts associated with this Route.
     */
    public HashMap<Integer, RouteCost> retrieveRouteCosts(Connection conn)
    {
        // Initialize variables
        String sqlCommand = "SELECT * FROM " + RouteCost.TABLE_NAME +
                " WHERE ROUTE_ID="+ID+";";
        HashMap<Integer, RouteCost> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int id = set.getInt("ID");
                int pID = set.getInt("ROUTE_ID");
                int cID = set.getInt("COMMODITY_ID");
                int amount = set.getInt("AMOUNT");

                map.put(id, new RouteCost(id,pID,cID,amount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the End Port associated with this Route.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveEndPort(Connection conn) {

        return Port.retrieve(END_PORT, conn);
    }

    /**
     * Returns the Start Port associated with this Route.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveStartPort(Connection conn) {

        return Port.retrieve(START_PORT, conn);
    }

    /**
     * Constructs a new Route.
     * @param id ID of the Route.
     * @param startID ID of the starting Port.
     * @param endID ID of the destination Port.
     */
    public Route(int id, int startID, int endID) {
        this.ID = id;
        this.START_PORT = startID;
        this.END_PORT = endID;
    }

    /**
     * Stores this Route into the database. Will replace the old
     * one if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME + "(ID,START_PORT,END_PORT) " +
                "VALUES("+ID+","+START_PORT+","+END_PORT+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return "[ROUTE]\t"+ID+"\t"+START_PORT+"\t"+END_PORT;
    }
}
