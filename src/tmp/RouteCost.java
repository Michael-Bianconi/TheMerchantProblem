package tmp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * Each Route incurs a RouteCost that Merchants must pay before travel.
 */
public class RouteCost {

    /** Name of RouteCost table. */
    public static final String TABLE_NAME = "ROUTE_COSTS";

    /** ID of the Route. */
    public final int ID;

    /** ID of the associated route. */
    public final int ROUTE_ID;

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
                        "ROUTE_ID        INTEGER             NOT NULL," +
                        "COMMODITY_ID   INTEGER             NOT NULL," +
                        "AMOUNT         INTEGER             NOT NULL," +
                        "FOREIGN KEY(ROUTE_ID) REFERENCES " +
                        Route.TABLE_NAME + "(ID)," +
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
        if (!(o instanceof RouteCost)) { return false; }
        RouteCost r = (RouteCost) o;
        return r.ID == ID
                && r.ROUTE_ID == ROUTE_ID
                && r.COMMODITY_ID == COMMODITY_ID
                && r.AMOUNT == AMOUNT;
    }

    public int hashCode() {
        return Objects.hash(ID,ROUTE_ID,COMMODITY_ID,AMOUNT);
    }

    /**
     * Retrieves the RouteCost data from the connection.
     *
     * @param id   The ID of the RouteCost to retrieve.
     * @param conn The connection to the database.
     * @return Returns the Route with the given ID,
     *         or null if not found.
     */
    public static RouteCost retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int costID = -1;
        int routeID = -1;
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
                routeID = set.getInt("ROUTE_ID");
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

        return new RouteCost(costID, routeID, commodityID, amount);
    }

    /**
     * Retrieves ALL RouteCosts from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each CostID to its Cost.
     */
    public static HashMap<Integer, RouteCost> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, RouteCost> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int id = set.getInt("ID");
                int routeID = set.getInt("ROUTE_ID");
                int commodityID = set.getInt("COMMODITY_ID");
                int amount = set.getInt("AMOUNT");
                map.put(id, new RouteCost(id, routeID, commodityID, amount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the Route associated with this Route.
     *
     * @param conn Connection to the database.
     * @return Returns the Route referenced by this PortInventory.
     */
    public Route retrieveRoute(Connection conn) {

        return Route.retrieve(ROUTE_ID, conn);
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
     * @param routeID ID of the Route.
     * @param commodityID ID of the associated Commodity.
     * @param amount Amount required to travel.
     */
    public RouteCost(int routeID, int commodityID, int amount) {
        this(TMPDatabase.uniqueID(), routeID, commodityID, amount);
    }

    /**
     * Constructs a new Route.
     * @param id ID of the Route.
     * @param routeID ID of the Route.
     * @param commodityID ID of the associated Commodity.
     * @param amount Amount required to travel.
     */
    public RouteCost(int id, int routeID, int commodityID, int amount) {
        this.ID = id;
        this.ROUTE_ID = routeID;
        this.COMMODITY_ID = commodityID;
        this.AMOUNT = amount;
    }

    /**
     * Stores this RouteCost into the database. Will replace the old
     * one if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME + "(ID,ROUTE_ID,COMMODITY_ID," +
                        "AMOUNT) " +
                "VALUES("+ID+","+ROUTE_ID+","+COMMODITY_ID+","+AMOUNT+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return "[ROUTE COST]\t"+ID+"\t"+ROUTE_ID+"\t"+COMMODITY_ID+"\t"+AMOUNT;
    }
}