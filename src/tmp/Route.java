package tmp;

import data.TMPDatabase;

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
public class Route extends TMPObject {

    /** ID of the Route. */
    public final int ID;

    /** ID of the starting Port. */
    public final int START_PORT;

    /** ID of the end Port. */
    public final int END_PORT;

    /**
     * Constructs a new Route.
     * @param startID ID of the starting Port.
     * @param endID ID of the destination Port.
     */
    public Route(int startID, int endID) {
        this(TMPDatabase.uniqueID(), startID, endID);
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
     * Retrieves all RouteCosts associated with this Route.
     * @param db Connection to the database.
     * @return Returns a Map of all RouteCosts associated with this Route.
     */
    public HashMap<Integer, RouteCost> retrieveRouteCosts(TMPDatabase db)
    {
        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TMPFactory.tableName("ROUTE_COST") +
                " WHERE ROUTE_ID="+ID+";";
        HashMap<Integer, RouteCost> map = new HashMap<>();
        Connection conn = db.getConnection();

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
     * @param db Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveEndPort(TMPDatabase db) {

        return (Port) db.retrieve("PORT", END_PORT);
    }

    /**
     * Returns the Start Port associated with this Route.
     *
     * @param db Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveStartPort(TMPDatabase db) {

        return (Port) db.retrieve("PORT", START_PORT);
    }

    // Object =================================================================

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Route)) { return false; }
        Route r = (Route) o;
        return r.ID == ID;
    }

    @Override
    public int hashCode() {return ID;}

    @Override
    public String toString() {
        return "[ROUTE]\t"+ID+"\t"+START_PORT+"\t"+END_PORT;
    }

    // TMPObject ==============================================================

    public int ID() {return ID;}

    public String storeString() {

        return "MERGE INTO " + TMPFactory.tableName("ROUTE")+
                "(ID,START_PORT,END_PORT) " +
                "VALUES("+ID+","+START_PORT+","+END_PORT+");";
    }
}
