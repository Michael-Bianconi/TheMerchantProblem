package TMP;

import java.sql.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Michael Bianconi
 * @since 01/28/2019
 * Commodities are bought and sold by the Merchant at ports. Each one has
 * a weight, and the sum total of the weight of all of a merchant's
 * commodities cannot exceed the merchant's capacity.
 */
public class Commodity {

    /** The name of the table that the database will store commodities in. */
    public static final String TABLE_NAME = "COMMODITIES";

    /** The commodity's ID, the <i>only</i> thing used to identify it. */
    public final int ID;

    /** The name of the Commodity. Cannot be changed. */
    public final String NAME;

    /** The weight per unit of Commodity. Cannot be changed. */
    public final float WEIGHT;

    /**
     * Creates the commodity table if it doesn't already exist.
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
     * @param id The ID of the Commodity to retrieve.
     * @param conn The connection to the database.
     * @return Returns the Commodity with the given ID, or null if not found.
     */
    public static Commodity retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int in_id = -1;
        String name="";
        float weight=-1;
        String sqlCommand =
                "SELECT * FROM " + TABLE_NAME +
                " WHERE ID=" + id + ";";

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                if (numResults > 1) {return null;}

                in_id = set.getInt("ID");
                name = set.getString("NAME");
                weight = set.getFloat("WEIGHT");
                numResults++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // There were no rows in the table with that ID
        if (numResults == 0) { return null; }

        return new Commodity(in_id, name, weight);
    }


    /**
     * Retrieves ALL Commodities from the table and stores them in a HashMap.
     * @param conn Connection to the database.
     * @return Returns a Map linking each CommodityID to its Commodity.
     */
    public static HashMap<Integer, Commodity> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, Commodity> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
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
     * Initialize the Cosmmodity. Once constructed, its fields can never
     * be changed.
     * @param id The ID of this commodity.
     * @param name The NAME of this commodity.
     * @param weight The WEIGHT of this commodity, per unit.
     */
    public Commodity(int id, String name, float weight) {
        this.ID = id;
        this.NAME = name;
        this.WEIGHT = weight;
    }

    /** Hashes all of this Commodity's fields together. */
    public int hashCode() {return Objects.hash(ID,NAME,WEIGHT);}

    /** @return Returns true if all values are the same. */
    public boolean equals(Object o) {
        if (!(o instanceof Commodity)) {return false;}
        Commodity c = (Commodity) o;
        return c.NAME.equals(this.NAME)
            && c.WEIGHT == this.WEIGHT
            && c.ID == this.ID;
    }

    /** @return Returns this Commodity's fields concatenated together. */
    public String toString() {
        return "[Commodity]\t" + ID + "\t" + NAME + "\t" + WEIGHT;
    }
}
