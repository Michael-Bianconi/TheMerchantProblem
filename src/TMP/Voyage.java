package TMP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Michael Bianconi
 * @since 02/01/2019
 *
 * A Voyage is a log of the Ports a Merchant has visited and the
 * transactions he's made.
 */
public class Voyage {

    /** Name of the Voyage Table. */
    public static final String TABLE_NAME = "VOYAGES";

    /** ID of the Voyage. */
    public final int ID;

    /** ID of the Merchant. */
    public final int MERCHANT_ID;

    /** ID of the Port visited. */
    public final int PORT_ID;

    /** Timestamp of the visit (does not have to be the actual time. */
    public final int TIMESTAMP;

    /**
     * Constructs a new Voyage. Note: the timestamp does not (and maybe
     * should not) be the actual time. It only needs to be greater than
     * that of the previous Voyage.
     * @param id ID of the Voyage.
     * @param merchant ID of the Merchant.
     * @param port ID of the Port.
     * @param timestamp Voyage's timestamp.
     */
    public Voyage(int id, int merchant, int port, int timestamp) {
        ID = id;
        MERCHANT_ID = merchant;
        PORT_ID = port;
        TIMESTAMP = timestamp;
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
                        "MERCHANT_ID    INTEGER             NOT NULL," +
                        "PORT_ID        INTEGER             NOT NULL," +
                        "TIMESTAMP      INTEGER             NOT NULL," +
                        "FOREIGN KEY(MERCHANT_ID) REFERENCES " +
                        Merchant.TABLE_NAME + "(ID)," +
                        "FOREIGN KEY(PORT_ID) REFERENCES " +
                        Port.TABLE_NAME + "(ID));";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Retrieves the data from the connection.
     * @param id The ID to retrieve.
     * @param conn The connection to the database.
     * @return Returns the data with the given ID, or null if not found.
     */
    public static Voyage retrieve(int id, Connection conn) {
        // Initialize variables
        int numResults = 0;
        int in_id = -1;
        int merchant = -1;
        int port = -1;
        int time = -1;
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
                merchant = set.getInt("MERCHANT_ID");
                port = set.getInt("PORT_ID");
                time = set.getInt("TIMESTAMP");
                numResults++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // There were no rows in the table with that ID
        if (numResults == 0) { return null; }

        return new Voyage(in_id, merchant, port, time);
    }

    /**
     * Retrieves ALL data from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each ID to its data.
     */
    public static HashMap<Integer, Voyage> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, Voyage> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int id = set.getInt("ID");
                int merchantID = set.getInt("MERCHANT_ID");
                int portID = set.getInt("PORT_ID");
                int time = set.getInt("TIMESTAMP");
                map.put(id, new Voyage(id, merchantID, portID, time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the Merchant associated with this Voyage.
     *
     * @param conn Connection to the database.
     * @return Returns the Route referenced by this PortInventory.
     */
    public Merchant retrieveMerchant(Connection conn) {

        return Merchant.retrieve(MERCHANT_ID, conn);
    }

    /**
     * Returns the Port associated with this Voyage.
     *
     * @param conn Connection to the database.
     * @return Returns the Port referenced by this Voyage.
     */
    public Port retrievePort(Connection conn) {

        return Port.retrieve(PORT_ID, conn);
    }

    public int hashCode() { return Objects.hash(ID); }
    public boolean equals(Object o) {
        if (!(o instanceof Voyage)) { return false; }
        Voyage v = (Voyage) o;
        return v.ID == ID;
    }

    /**
     * Stores this Voyage into the database. Will replace the old
     * one if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME + "(ID,MERCHANT_ID,PORT_ID," +
                        "TIMESTAMP) " +
                "VALUES("+ID+","+MERCHANT_ID+","+PORT_ID+","+
                        TIMESTAMP+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return "[VOYAGE]\t"+ID+"\t"+MERCHANT_ID+"\t"+PORT_ID+"\t"+TIMESTAMP;
    }
}
