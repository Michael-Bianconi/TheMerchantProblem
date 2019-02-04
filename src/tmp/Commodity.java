package tmp;

import data.TMPDatabase;

import java.sql.*;
import java.util.HashMap;

/**
 * Commodities, such as Silk or Emerald, can be bought and sold at
 * {@link tmp.Port Ports}. Ports will only trade Commodities that
 * exist in their {@link tmp.PortInventory inventories}.
 *
 * Each {@link tmp.Merchant} may only hold so many Commodities,
 * determined by the collective weight of his
 * {@link tmp.MerchantInventory inventory} and his Capacity.
 *
 * When traveling between Ports using {@link tmp.Route Routes},
 * Merchants must consume all {@link tmp.RouteCost Route Costs}
 * associated with that Route.
 *
 * @author Michael Bianconi
 * @since 01-28-2019
 */
public class Commodity {

    /** The name of the Commodity table in the database. */
    public static final String TABLE_NAME = "COMMODITIES";

    /** The unique identifier of this Commodity. */
    public final int ID;

    /** The name of the Commodity. */
    public final String NAME;

    /** The weight per unit of Commodity. */
    public final float WEIGHT;

    /**
     * Creates the commodity table if it doesn't already exist.
     *
     * @param conn The connection to the H2 database.
     * @return Returns true if and only if successful.
     */
    public static boolean createTable(Connection conn) {
        String sqlCommand =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "ID     INTEGER PRIMARY KEY NOT NULL," +
                "NAME   TEXT                NOT NULL," +
                "WEIGHT REAL                NOT NULL);";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Retrieves the Commodity data from the connection.
     *
     * @param id The ID of the Commodity to retrieve.
     * @param conn The Connection to the database.
     * @return Returns the Commodity with the given ID, or null if not found.
     */
    public static Commodity retrieve(int id, Connection conn) {
        String sqlCommand =
                "SELECT * FROM " + TABLE_NAME + " WHERE ID=" + id + ";";

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Compile the Commodity from entry data.
            ResultSet set = stmt.executeQuery();
            set.next();

            int in_id = set.getInt("ID");
            String name = set.getString("NAME");
            float weight = set.getFloat("WEIGHT");

            return new Commodity(in_id, name, weight);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Retrieves ALL Commodities from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each CommodityID to its Commodity.
     */
    public static HashMap<Integer, Commodity> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, Commodity> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Compile and store each entry in the table
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int id = set.getInt("ID");
                String name = set.getString("NAME");
                float weight = set.getFloat("WEIGHT");
                map.put(id, new Commodity(id, name, weight));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }


    /**
     * Stores this Commodity into the database. Will replace the old
     * Commodity if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME + "(ID,NAME,WEIGHT) KEY(ID) " +
                "VALUES("+ID+",'"+NAME+"',"+WEIGHT+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * Construct the Commodity with a guaranteed unique ID.
     *
     * @param name Name of the ID.
     * @param weight Weight of the commodity.
     */
    public Commodity(String name, float weight) {
        this(TMPDatabase.uniqueID(), name, weight);
    }

    /**
     * Construct the Commodity. This method should only be called
     * when retrieving from the database, to ensure the ID's uniqueness.
     *
     * @param id The ID of this commodity.
     * @param name The NAME of this commodity.
     * @param weight The WEIGHT of this commodity, per unit.
     */
    public Commodity(int id, String name, float weight) {
        this.ID = id;
        this.NAME = name;
        this.WEIGHT = weight;
    }


    /** @return Returns this Commodity's ID. */
    public int hashCode() {return ID;}

    /** @return Compares the IDs of the Commodities. */
    public boolean equals(Object o) {
        if (!(o instanceof Commodity)) {return false;}
        Commodity c = (Commodity) o;
        return c.ID == this.ID;
    }

    /** @return NAME(ID) */
    public String toString() {

        return NAME + "(ID=" + ID;
    }
}
