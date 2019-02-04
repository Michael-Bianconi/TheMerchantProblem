package tmp;

import data.TMPDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Michael Bianconi
 * @since 01/31/2019
 * Merchants travel from Port to Port, trading Commodities. Their goal is
 * to make as much profit as possible before returning home.
 */
public class Merchant {

    /** Name of the Merchant table. */
    public static final String TABLE_NAME = "MERCHANTS";

    /** ID of the Merchant. */
    public final int ID;

    /** Name of the Merchant. */
    public final String NAME;

    /** ID of the Merchant's Home Port. */
    public final int HOME_PORT;

    /** ID of the Port the Merchant is currently at. */
    public int CURRENT_PORT;

    /** How much weight the Merchant is capable of carrying at once. */
    public final int CAPACITY;

    /** How much Gold this Merchant has. */
    public int GOLD;

    /**
     * Constructs a new Merchant.
     *
     * @param name Name of the Merchant.
     * @param home ID of the Merchant's Home Port.
     * @param current ID of the Merchant's Current Port.
     * @param capacity How much weight the Merchant can hold.
     * @param gold How much Gold this Merchant has.
     */
    public Merchant(
            String name, int home, int current, int capacity, int gold) {
        this(TMPDatabase.uniqueID(), name, home, current, capacity, gold);
    }

    /**
     * Constructs a new Merchant.
     *
     * @param id Merchant's ID.
     * @param name Name of the Merchant.
     * @param home ID of the Merchant's Home Port.
     * @param current ID of the Merchant's Current Port.
     * @param capacity How much weight the Merchant can hold.
     * @param gold How much Gold this Merchant has.
     */
    public Merchant(
            int id, String name, int home,
            int current, int capacity, int gold) {
        ID = id;
        NAME = name;
        HOME_PORT = home;
        CURRENT_PORT = current;
        CAPACITY = capacity;
        GOLD = gold;
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
                        "NAME           TEXT                NOT NULL," +
                        "HOME_PORT      INTEGER             NOT NULL," +
                        "CURRENT_PORT   INTEGER             NOT NULL," +
                        "CAPACITY       INTEGER             NOT NULL," +
                        "GOLD           INTEGER             NOT NULL," +
                        "FOREIGN KEY(HOME_PORT) REFERENCES " +
                        Port.TABLE_NAME + "(ID)," +
                        "FOREIGN KEY(CURRENT_PORT) REFERENCES " +
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
        if (!(o instanceof Merchant)) { return false; }
        Merchant m = (Merchant) o;
        return m.ID == ID;
    }

    public int hashCode() { return Objects.hash(ID,NAME,HOME_PORT,CAPACITY); }

    /**
     * Retrieves the Merchant data from the connection.
     *
     * @param id   The ID of the Merchant to retrieve.
     * @param conn The connection to the database.
     * @return Returns the Merchant with the given ID, or null if not found.
     */
    public static Merchant retrieve(int id, Connection conn) {
        // Initialize variables
        String sqlCommand =
                "SELECT * FROM " + TABLE_NAME + " WHERE ID=" + id + ";";

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            set.next();
            int merchantID = set.getInt("ID");
            String name = set.getString("NAME");
            int home = set.getInt("HOME_PORT");
            int current = set.getInt("CURRENT_PORT");
            int capacity = set.getInt("CAPACITY");
            int gold = set.getInt("GOLD");
            return new Merchant(
                    merchantID, name, home, current, capacity, gold);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a single MerchantInventory, based on its commodity.
     * @param id ID of the commodity.
     * @param conn Connection to the database.
     * @return Returns the MerchantInventory, or null.
     */
    public MerchantInventory retrieveMerchantInventoryByCommodity(
            int id, Connection conn) {

        String sqlCommand =
                "SELECT * FROM " + MerchantInventory.TABLE_NAME +
                " WHERE MERCHANT_ID="+ID+" AND COMMODITY_ID="+id+";";

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            set.next();
            int costID = set.getInt("ID");
            int merchantID = set.getInt("MERCHANT_ID");
            int commodityID = set.getInt("COMMODITY_ID");
            int amount = set.getInt("AMOUNT");
            return new MerchantInventory(
                    costID, merchantID, commodityID, amount);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Retrieves ALL Merchants from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each MerchantID to its Merchant.
     */
    public static HashMap<Integer, Merchant> retrieveAll(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TABLE_NAME + ";";
        HashMap<Integer, Merchant> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int merchantID = set.getInt("ID");
                String name = set.getString("NAME");
                int home = set.getInt("HOME_PORT");
                int current = set.getInt("CURRENT_PORT");
                int capacity = set.getInt("CAPACITY");
                int gold = set.getInt("GOLD");
                map.put(merchantID, new Merchant(
                        merchantID,name,home,current,capacity,gold));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Retrieves ALL Merchants from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each MerchantID to its Merchant.
     */
    public HashMap<Integer, MerchantInventory>
        retrieveAllMerchantInventories(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + MerchantInventory.TABLE_NAME +
                " WHERE MERCHANT_ID="+ID+";";
        HashMap<Integer, MerchantInventory> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                int id = set.getInt("ID");
                int merchantID = set.getInt("MERCHANT_ID");
                int commodityID = set.getInt("COMMODITY_ID");
                int amount = set.getInt("AMOUNT");
                map.put(id, new MerchantInventory(
                        id,merchantID,commodityID,amount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Retrieves Voyages from the table and stores them in a HashMap.
     *
     * @param conn Connection to the database.
     * @return Returns a Map linking each ID to its data.
     */
    public HashMap<Integer, Voyage> retrieveAllVoyages(Connection conn) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + Voyage.TABLE_NAME +
                " WHERE MERCHANT_ID="+ID+
                " ORDER BY TIMESTAMP DESC";
        HashMap<Integer, Voyage> map = new HashMap<>();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                int id = set.getInt("ID");
                int merchant = set.getInt("MERCHANT_ID");
                int port = set.getInt("PORT_ID");
                int time = set.getInt("TIMESTAMP");
                map.put(id, new Voyage(id,merchant,port,time));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return map;
    }

    /**
     * Returns the Home Port associated with this Merchant.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveHomePort(Connection conn) {

        return Port.retrieve(HOME_PORT, conn);
    }

    /**
     * Returns the Current Port associated with this Merchant.
     *
     * @param conn Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveCurrentPort(Connection conn) {

        return Port.retrieve(CURRENT_PORT, conn);
    }

    /**
     * Stores this Merchant into the database. Will replace the old
     * one if one already exists with the same ID.
     *
     * @param conn The connection to the database.
     * @return True if and only if successful.
     */
    public boolean store(Connection conn) {

        String sqlCommand =
                "MERGE INTO " + TABLE_NAME +
                        "(ID,NAME,HOME_PORT,CURRENT_PORT,CAPACITY,GOLD) " +
                "VALUES("+ID+",'"+NAME+"',"+HOME_PORT+","+CURRENT_PORT+"," +
                        CAPACITY+","+GOLD+");";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String toString() {
        return "[MERCHANT]\t"+ID+"\t"+NAME+"\t"+HOME_PORT+
                "\t"+CURRENT_PORT+"\t"+CAPACITY;
    }

    /**
     * Returns the sum weight of all commodities this Merchant has
     * in his inventory.
     * @param conn Connection to the database.
     * @return float
     */
    public float getUsedCapacity(Connection conn) {
        Map<Integer, MerchantInventory> map =
                retrieveAllMerchantInventories(conn);
        float total = 0;

        for (Map.Entry<Integer, MerchantInventory> e : map.entrySet()) {
            MerchantInventory i = e.getValue();
            Commodity c = i.retrieveCommodity(conn);
            total += c.WEIGHT * i.AMOUNT;
        }

        return total;
    }

    /**
     * @param conn Connection to the database.
     * @return Returns this Merchant's most recent Voyage (the
     * voyage with the largest timestamp).
     */
    public Voyage getLatestVoyage(Connection conn) {
        String command = "SELECT ID, MAX(TIMESTAMP) FROM " +
                Voyage.TABLE_NAME + " WHERE MERCHANT_ID="+ID+
                " GROUP BY(ID);";

        try (PreparedStatement stmt = conn.prepareStatement(command)) {
            ResultSet set = stmt.executeQuery();
            set.next();
            return Voyage.retrieve(set.getInt("ID"),conn);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Runs through the necessary conditions for trading a commodity
     * with a port. They must have enough available, they must have
     * enough Gold, and the Merchant must, if buying, have
     * enough capacity to carry the new Commodities.
     * @param port Port to trade with.
     * @param com Commodity to trade.
     * @param amount Amount to trade (- for selling, + for buying).
     * @param conn Connection to the database.
     * @return Returns true if able to trade.
     */
    public boolean canTrade(
            Port port, Commodity com, int amount, Connection conn) {

        PortInventory pInv =
                port.retrievePortInventoryByCommodity(com.ID, conn);

        // the port does not trade that commodity
        if (pInv == null) {return false;}

        // buying
        if (amount > 0) {

            // the port doesn't have enough of that commodity
            if (pInv.ON_HAND < amount) {return false;}

            // the merchant doesn't have enough Gold
            if (pInv.BUY_PRICE*amount > GOLD) {return false;}

            // the merchant must have the capacity for this commodity
            return getUsedCapacity(conn)+(com.WEIGHT*amount) <= CAPACITY;

            // selling
        } else if (amount < 0) {

            amount = -amount; // flip it to positive

            MerchantInventory mInv =
                    retrieveMerchantInventoryByCommodity(com.ID, conn);

            // Merchant doesn't have this commodity.
            if (mInv == null) {return false;}

            // The Merchant doesn't have enough of this commodity
            return mInv.AMOUNT >= amount;
        }

        return true;
    }

    /**
     * To travel along a Route, the Merchant must currently be at
     * the starting port and must own the required commodities.
     *
     * @param r Route to travel.
     * @param conn Connection to the database.
     * @return Returns true if the Merchant can use the Route.
     */
    public boolean canTravel(Route r, Connection conn) {

        Port start = r.retrieveStartPort(conn);

        // The Merchant is not at the starting port.
        if (start.ID != CURRENT_PORT) {return false;}

        Map<Integer, RouteCost> costs =
                r.retrieveRouteCosts(conn);

        // Check each RouteCost
        for (Map.Entry<Integer, RouteCost> e : costs.entrySet()) {

            RouteCost cost = e.getValue();
            MerchantInventory commodity =
                    retrieveMerchantInventoryByCommodity(
                            cost.COMMODITY_ID, conn);

            // The merchant does not own the Commodity
            if (commodity == null) {return false;}

            // The merchant does not own enough of the Commodity
            if (commodity.AMOUNT < cost.AMOUNT) {return false;}
        }

        return true;
    }
}
