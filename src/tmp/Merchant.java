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
public class Merchant extends TMPObject {

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

    // Constructors ===========================================================

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
     * Retrieves a single MerchantInventory, based on its commodity.
     * @param id ID of the commodity.
     * @param db Connection to the database.
     * @return Returns the MerchantInventory, or null.
     */
    public MerchantInventory retrieveMerchantInventoryByCommodity(
            int id, TMPDatabase db) {

        String sqlCommand =
                "SELECT * FROM " + TMPFactory.tableName("MERCHANT_INVENTORY") +
                " WHERE MERCHANT_ID="+ID+" AND COMMODITY_ID="+id+";";
        Connection conn = db.getConnection();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            set.next();
            return (MerchantInventory)
                    TMPFactory.create("MERCHANT_INVENTORY", set);

        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Retrieves ALL Merchants from the table and stores them in a HashMap.
     *
     * @param db Connection to the database.
     * @return Returns a Map linking each MerchantID to its Merchant.
     */
    public HashMap<Integer, MerchantInventory>
        retrieveAllMerchantInventories(TMPDatabase db) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " +
                TMPFactory.tableName("MERCHANT_INVENTORY") +
                " WHERE MERCHANT_ID="+ID+";";
        HashMap<Integer, MerchantInventory> map = new HashMap<>();
        Connection conn = db.getConnection();

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(sqlCommand)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                MerchantInventory inv = (MerchantInventory) TMPFactory.create(
                        "MERCHANT_INVENTORY", set);

                map.put(inv.ID(), inv);
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
     * @param db Connection to the database.
     * @return Returns a Map linking each ID to its data.
     */
    public HashMap<Integer, Voyage> retrieveAllVoyages(TMPDatabase db) {

        // Initialize variables
        String sqlCommand = "SELECT * FROM " + TMPFactory.tableName("VOYAGE") +
                " WHERE MERCHANT_ID="+ID+
                " ORDER BY TIMESTAMP DESC";
        HashMap<Integer, Voyage> map = new HashMap<>();
        Connection conn = db.getConnection();

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
     * @param db Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveHomePort(TMPDatabase db) {

        return (Port) db.retrieve("PORT",HOME_PORT);
    }

    /**
     * Returns the Current Port associated with this Merchant.
     *
     * @param db Connection to the database.
     * @return Returns the Commodity referenced by this PortInventory.
     */
    public Port retrieveCurrentPort(TMPDatabase db) {

        return (Port) db.retrieve("PORT", CURRENT_PORT);
    }

    // Object =================================================================

    @Override
    public String toString() {
        return "[MERCHANT]\t"+ID+"\t"+NAME+"\t"+HOME_PORT+
                "\t"+CURRENT_PORT+"\t"+CAPACITY;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Merchant)) { return false; }
        Merchant m = (Merchant) o;
        return m.ID == ID;
    }

    @Override
    public int hashCode() { return ID; }

    // TMPObject ==============================================================

    public int ID() {return ID;}

    public String storeString() {
        return "MERGE INTO " + TMPFactory.tableName("MERCHANT") +
                "(ID,NAME,HOME_PORT,CURRENT_PORT,CAPACITY,GOLD) " +
                "VALUES(" + ID + ",'" + NAME + "'," + HOME_PORT + "," +
                CURRENT_PORT + "," + CAPACITY + "," + GOLD + ");";
    }


    /**
     * Returns the sum weight of all commodities this Merchant has
     * in his inventory.
     * @param db Connection to the database.
     * @return float
     */
    public float getUsedCapacity(TMPDatabase db) {
        Map<Integer, MerchantInventory> map =
                retrieveAllMerchantInventories(db);
        float total = 0;

        for (Map.Entry<Integer, MerchantInventory> e : map.entrySet()) {
            MerchantInventory i = e.getValue();
            Commodity c = i.retrieveCommodity(db);
            total += c.WEIGHT * i.AMOUNT;
        }

        return total;
    }

    /**
     * @param db Connection to the database.
     * @return Returns this Merchant's most recent Voyage (the
     * voyage with the largest timestamp).
     */
    public Voyage getLatestVoyage(TMPDatabase db) {
        String command = "SELECT ID, MAX(TIMESTAMP) FROM " +
                TMPFactory.tableName("VOYAGE")+ " WHERE MERCHANT_ID="+ID+
                " GROUP BY(ID);";

        Connection conn = db.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(command)) {
            ResultSet set = stmt.executeQuery();
            set.next();
            return (Voyage) db.retrieve("VOYAGE",set.getInt("ID"));

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
     * @param db Connection to the database.
     * @return Returns true if able to trade.
     */
    public boolean canTrade(
            Port port, Commodity com, int amount, TMPDatabase db) {

        if (port == null) {
            System.out.println("Null port!");
            return false;
        }

        if (com == null) {
            System.out.println("That commodity doesn't exist!");
            return false;
        }

        PortInventory pInv =
                port.retrievePortInventoryByCommodity(com.ID, db);

        // the port does not trade that commodity
        if (pInv == null) {
            System.out.printf("%s doesn't trade %s\n", port.NAME, com.NAME);
            return false;
        }

        // buying
        if (amount > 0) {

            // the port doesn't have enough of that commodity
            if (pInv.ON_HAND < amount) {
                System.out.printf("%s doesn't have enough %s\n", port.NAME,com.NAME);
                return false;
            }

            // the merchant doesn't have enough Gold
            if (pInv.BUY_PRICE*amount > GOLD) {
                System.out.printf("You don't have enough gold\n");
                return false;
            }

            // the merchant must have the capacity for this commodity
            if( getUsedCapacity(db)+(com.WEIGHT*amount) > CAPACITY) {
                System.out.printf("You don't have enough space for that many %s\n",com.NAME);
            }

            // selling
        } else if (amount < 0) {

            amount = -amount; // flip it to positive

            MerchantInventory mInv =
                    retrieveMerchantInventoryByCommodity(com.ID, db);

            // Merchant doesn't have this commodity.
            if (mInv == null) {
                System.out.printf("You don't own %s\n",com.NAME);
                return false;
            }

            // The Merchant doesn't have enough of this commodity
            if (mInv.AMOUNT < amount) {
                System.out.printf("You don't have enough %s\n",com.NAME);
                return false;
            }
        }

        return true;
    }

    /**
     * To travel along a Route, the Merchant must currently be at
     * the starting port and must own the required commodities.
     *
     * @param r Route to travel.
     * @param db Connection to the database.
     * @return Returns true if the Merchant can use the Route.
     */
    public boolean canTravel(Route r, TMPDatabase db) {

        if (r == null) {
            System.out.println("That route doesn't exist!");
            return false;
        }

        Port start = r.retrieveStartPort(db);

        // The Merchant is not at the starting port.
        if (start.ID != CURRENT_PORT) {return false;}

        Map<Integer, RouteCost> costs =
                r.retrieveRouteCosts(db);

        // Check each RouteCost
        for (Map.Entry<Integer, RouteCost> e : costs.entrySet()) {

            RouteCost cost = e.getValue();
            MerchantInventory commodity =
                    retrieveMerchantInventoryByCommodity(
                            cost.COMMODITY_ID, db);

            // The merchant does not own the Commodity
            if (commodity == null) {return false;}

            // The merchant does not own enough of the Commodity
            if (commodity.AMOUNT < cost.AMOUNT) {return false;}
        }

        return true;
    }
}
