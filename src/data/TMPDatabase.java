package data;

import tmp.*;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Bianconi
 * @since 02-01-2019
 * The TMPDatabase class can access the H2 database.
 */
public class TMPDatabase implements AutoCloseable {

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private Connection conn;
    private static int UNIQUE_ID = 0;


    // CONSTRUCTORS ===========================================================

    public TMPDatabase(String url, String user, String pass) throws Exception {

        Class.forName(JDBC_DRIVER);
        this.conn = DriverManager.getConnection(url, user, pass);
    }

    // Accessors ==============================================================


    public Connection getConnection() {return this.conn; }

    /**
     * Generates a unique identifier. Starts at 0. Increments by one
     * each time it's called.
     * @return Returns a guaranteed unique ID.
     */
    public static int uniqueID() {return UNIQUE_ID++;}

    // Autocloseable ==========================================================

    @Override
    public void close() throws Exception {
        if (this.conn != null) {this.conn.close();}
    }

    // OPERATIONS =============================================================

    /**
     * Delete the database. Do this BEFORE creating a TMPDatabase object.
     */
    public static void reset(String URL) {
        File db = new File(URL);
        System.out.println(URL);

        if (db.delete()) {System.out.println("DB RESET");}
    }

    /**
     * Retrieves a TMPObject from the database by ID.
     *
     * @param type Type of object.
     * @param id ID of the object.
     * @return Returns the TMPObject with the given type and ID.
     */
    public TMPObject retrieve(String type, int id) {
        String command = String.format(TMPFactory.selectString(type),id);


        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(command)) {

            // Compile the Commodity from entry data.
            ResultSet set = stmt.executeQuery();
            set.next();
            return TMPFactory.create(type, set);

        } catch (SQLException e) {

            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves all TMPObjects of the given type.
     *
     * @param type Type of object.
     * @return Returns a Map from ID to data.
     */
    public Map<Integer,TMPObject> retrieveAll(String type) {
        String command = TMPFactory.selectAllString(type);
        Map<Integer, TMPObject> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(command)) {

            // Compile the Commodity from entry data.
            ResultSet set = stmt.executeQuery();

            while (set.next()) {
                TMPObject obj = TMPFactory.create(type, set);
                map.put(obj.ID(), obj);
            }

        } catch (SQLException e) {

            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Stores a TMPObject into the database.
     * @param object Object to store.
     * @return Returns true if the object was successfully stored.
     */
    public boolean store(TMPObject object) {
        String command = object.storeString();

        try (PreparedStatement stmt = conn.prepareStatement(command)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Creates the Table for the type.
     * @param type Type of table to create.
     * @return Returns true if the table was created.
     */
    public boolean createTable(String type) {
        String command = TMPFactory.createTableString(type);

        try (PreparedStatement stmt = conn.prepareStatement(command)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
