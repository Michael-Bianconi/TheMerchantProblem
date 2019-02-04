package data;

import tmp.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Michael Bianconi
 * @since 02-01-2019
 * The TMPDatabase class will build a database from scratch.
 */
public class TMPDatabase implements AutoCloseable {

    /** Username for accessing the database. (Default: username) */
    private String username = "username";

    /** Password for accessing the database. (Default: password) */
    private String password = "password";

    /** Location of the database. */
    private String dbURL = "";

    /**
     * A list of all possible commodity names. This list will shrink
     * as Commodities are added to the database.
     */
    private List<String> commodityNames;

    /**
     * A list of all possible port names. This list will shrink
     * as Ports are added to the database.
     */
    private List<String> portNames;

    // CONSTRUCTORS ===========================================================

    public TMPDatabase(String url) throws Exception {

        setURL(url);
        Class.forName(JDBC_DRIVER);
        this.conn = DriverManager.getConnection(dbURL, username, password);
    }

    // INIT METHODS, ACCESSORS ================================================

    /**
     * Reads in Commodity names from a .CSV file.
     *
     * @param path Path to the .CSV file.
     */
    public void readCommodityNames(String path) {
        this.commodityNames = TMPReader.read(path);
    }

    /**
     * Reads in Port names from a .CSV file.
     *
     * @param path Path to the .CSV file.
     */
    public void readPortNames(String path) {
        this.portNames = TMPReader.read(path);
    }

    /** Sets the location of this database. */
    public void setURL(String dbURL) {this.dbURL = dbURL;}

    /** Set the password for this database. */
    public void setPassword(String pass) {this.password = pass;}

    /** Set the username for this database. */
    public void setUsername(String user) {this.username = user;}


    private class IDPair {
        public int id1;
        public int id2;
        public IDPair(int id1, int id2) { this.id1=id1; this.id2=id2; }
        public String toString() {
            return Integer.toString(id1)+" "+Integer.toString(id2);
        }
    }

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String USER = "USER";
    private static final String PASS = "PASSWORD";
    private Connection conn;
    private static int UNIQUE_ID = 0;

    /**
     * Generates a unique identifier. Starts at 0. Increments by one
     * each time it's called.
     * @return Returns a guaranteed unique ID.
     */
    public static int uniqueID() {return UNIQUE_ID++;}

    @Override
    public void close() throws Exception {
        if (this.conn != null) {this.conn.close();}
    }

    /**
     * Delete the database. Do this BEFORE creating a TMPDatabase object.
     */
    public static void reset(String URL) {
        File db = new File(URL);
        System.out.println(URL);

        if (db.delete()) {System.out.println("DB RESET");}
    }

    public void createTables() {
        Commodity.createTable(conn);
        Port.createTable(conn);
        PortInventory.createTable(conn);
        Route.createTable(conn);
        RouteCost.createTable(conn);
        Merchant.createTable(conn);
        MerchantInventory.createTable(conn);
        Voyage.createTable(conn);
        Transaction.createTable(conn);
    }

    private static String createRandomString(int length) {
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int val = rand.nextInt(25)+65;
            builder.append((char) val);
        }
        return builder.toString();
    }


    /**
     * Creates several Commodities and stores them in the database.
     * @param num
     * @return
     */
    public Commodity[] createCommodities(int num) {
        Commodity[] commodities = new Commodity[num];
        Random rand = new Random();
        for (int i = 0; i < num; i++) {
            float weight = rand.nextFloat() * 100;
            String name = createRandomString(5);
            Commodity c = new Commodity(name, weight);
            c.store(conn);
            commodities[i]=c;
        }
        return commodities;
    }

    public Port[] createPorts(int num) {
        Port[] ports = new Port[num];
        Random rand = new Random();
        for (int i = 0; i < num; i++) {
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            String name = createRandomString(7);
            Port p = new Port(name,x,y);
            p.store(conn);
            ports[i]=p;
        }
        return ports;
    }

    public PortInventory[] createPortInventories(int num) {
        ArrayList<Integer> commodityIDs =
                getColumnInt(Commodity.TABLE_NAME,"ID");
        ArrayList<Integer> portIDs =
                getColumnInt(Port.TABLE_NAME, "ID");
        Random rand = new Random();
        PortInventory[] invs = new PortInventory[num];

        for (int id = 0; id < num; id++){
            int index = rand.nextInt(commodityIDs.size());
            int comID = commodityIDs.get(index);
            index = rand.nextInt(portIDs.size());
            int portID = portIDs.get(index);
            int onHand = rand.nextInt(256);
            int buy = rand.nextInt(256);
            int sell = rand.nextInt(256);
            PortInventory inv =
                    new PortInventory(portID, comID, onHand, buy, sell);
            invs[id] = inv;
            inv.store(conn);
        }
        return invs;
    }

    /** num is restricted to N*(N-1), where N is the number of ports. */
    public Route[] createRoutes(int num) {
        ArrayList<IDPair> portPairs = _routeCartesian();
        Random rand = new Random();
        int numRoutes = (num > portPairs.size()) ? portPairs.size() : num;
        Route[] routes = new Route[numRoutes];

        for (int id = 0; id < numRoutes; id++) {

            int index;
            if (portPairs.size()==1) {index=0;}
            else {index = rand.nextInt(portPairs.size());}
            IDPair pair = portPairs.get(index);
            portPairs.remove(index);

            Route r = new Route(pair.id1, pair.id2);
            r.store(conn);
            routes[id]=r;
        }
        return routes;
    }

    public RouteCost[] createRouteCosts(int num) {
        ArrayList<IDPair> pairs = _routeCostCartesian();
        Random rand = new Random();
        int numCosts = (num > pairs.size()) ? pairs.size() : num;
        RouteCost[] costs = new RouteCost[numCosts];
        for (int id = 0; id < numCosts; id++) {
            int index = rand.nextInt(pairs.size());
            IDPair pair = pairs.get(index);
            pairs.remove(index);
            int amount = rand.nextInt(256);
            RouteCost c = new RouteCost(pair.id1, pair.id2, amount);
            c.store(conn);
            costs[id] = c;
        }
        return costs;
    }

    public Merchant[] createMerchants(int num) {
        ArrayList<Integer> portIDs = getColumnInt(Port.TABLE_NAME, "ID");
        Random random = new Random();
        Merchant[] merchants = new Merchant[num];
        for (int id = 0; id < num; id++) {
            String name = createRandomString(8);
            int home = portIDs.get(random.nextInt(portIDs.size()));
            int current = portIDs.get(random.nextInt(portIDs.size()));
            int capacity = random.nextInt(1024);
            int gold = random.nextInt(20000);
            Merchant m  = new Merchant(name, home, current, capacity,gold);
            m.store(conn);
            merchants[id]=m;
        }
        return merchants;
    }

    public MerchantInventory[] createMerchantInventories(int num) {
        ArrayList<IDPair> pairs = _merchantInventoryCartesian();
        Random rand = new Random();
        num = (num > pairs.size()) ? pairs.size() : num;
        MerchantInventory[] invs = new MerchantInventory[num];
        for (int id = 0; id < num; id++) {
            int index = rand.nextInt(pairs.size());
            IDPair pair = pairs.get(index);
            pairs.remove(index);
            int amount = rand.nextInt(256);
            MerchantInventory m =
                    new MerchantInventory(pair.id1,pair.id2,amount);
            invs[id] = m;
            m.store(conn);
        }
        return invs;
    }

    public Voyage[] createVoyages(int num) {
        ArrayList<IDPair> pairs = _voyageCartesian();
        num = (num > pairs.size()) ? pairs.size() : num;
        Random rand = new Random();
        Voyage[] voyages = new Voyage[num];
        for (int id = 0; id < num; id++) {
            int index = rand.nextInt(pairs.size());
            IDPair pair = pairs.get(index);
            pairs.remove(index);
            int time = rand.nextInt(100);
            Voyage v = new Voyage(pair.id1,pair.id2,time);
            v.store(conn);
            voyages[id] = v;
        }
        return voyages;
    }

    public Transaction[] createTransactions(int num) {
        ArrayList<Integer> voyages =
                getColumnInt(Voyage.TABLE_NAME, "ID");
        ArrayList<Integer> commodities =
                getColumnInt(Commodity.TABLE_NAME, "ID");
        System.out.println("Num commodities: " + commodities.size());
        Random rand = new Random();
        Transaction[] trans = new Transaction[num];
        for (int id = 0; id < num; id++) {
            int voyage = voyages.get(rand.nextInt(voyages.size()));
            int commodity = commodities.get(rand.nextInt(commodities.size()));
            int amount = rand.nextInt(256);
            int price = rand.nextInt(256);
            Transaction t =
                    new Transaction(voyage,commodity,amount,price);
            System.out.println(t);
            t.store(conn);
            trans[id] = t;
        }
        return trans;
    }

    public Connection getConnection() {return this.conn; }

    /** Returns ALL values in a column. Values must be of type int. */
    public ArrayList<Integer> getColumnInt(String table, String column) {
        String command =
                "SELECT " + column + " FROM " + table + ";";

        ArrayList<Integer> values = new ArrayList<>();
        int numValues = 0;

        // Execute the statement
        try (PreparedStatement stmt = conn.prepareStatement(command)) {

            // Get each field. If there's more than one row, something's wrong.
            ResultSet set = stmt.executeQuery();
            while (set.next()) {

                int val = set.getInt(column);
                values.add(val);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return values;
    }


    private ArrayList<IDPair> _routeCostCartesian() {

        ArrayList<Integer> routes =
                getColumnInt(Route.TABLE_NAME, "ID");
        ArrayList<Integer> coms =
                getColumnInt(Commodity.TABLE_NAME, "ID");
        ArrayList<IDPair> pairs = new ArrayList<>();

        for (int s = 0; s < routes.size(); s++) {
            for (int e = 0; e < coms.size(); e++) {
                pairs.add(new IDPair(routes.get(s),coms.get(e)));
            }
        }

        return pairs;
    }

    private ArrayList<IDPair> _merchantInventoryCartesian() {

        ArrayList<Integer> merchants =
                getColumnInt(Merchant.TABLE_NAME, "ID");
        ArrayList<Integer> coms =
                getColumnInt(Commodity.TABLE_NAME, "ID");
        ArrayList<IDPair> pairs = new ArrayList<>();

        for (int s = 0; s < merchants.size(); s++) {
            for (int e = 0; e < coms.size(); e++) {
                pairs.add(new IDPair(merchants.get(s),coms.get(e)));
            }
        }

        return pairs;
    }


    private ArrayList<IDPair> _routeCartesian() {

        ArrayList<Integer> startPorts =
                getColumnInt(Port.TABLE_NAME, "ID");
        ArrayList<Integer> endPorts = new ArrayList<>(startPorts);
        ArrayList<IDPair> pairs = new ArrayList<>();

        for (int s = 0; s < startPorts.size(); s++) {
            for (int e = 0; e < endPorts.size(); e++) {
                if (s == e) {continue;}
                pairs.add(new IDPair(startPorts.get(s),endPorts.get(e)));
            }
        }

        return pairs;
    }

    public ArrayList<IDPair> _voyageCartesian() {

        ArrayList<Integer> merchants =
                getColumnInt(Merchant.TABLE_NAME, "ID");
        ArrayList<Integer> ports =
                getColumnInt(Port.TABLE_NAME, "ID");
        ArrayList<IDPair> pairs = new ArrayList<>();

        for (int s = 0; s < merchants.size(); s++) {
            for (int e = 0; e < ports.size(); e++) {
                pairs.add(new IDPair(merchants.get(s), ports.get(e)));
            }
        }

        return pairs;
    }

    public ArrayList<IDPair> _transactionCartesian() {

        ArrayList<Integer> coms1 =
                getColumnInt(Commodity.TABLE_NAME, "ID");
        ArrayList<Integer> coms2 =
                getColumnInt(Commodity.TABLE_NAME, "ID");
        ArrayList<IDPair> pairs = new ArrayList<>();

        for (Integer i : coms1) {
            for (Integer e : coms2) {
                if (!i.equals(e)) {
                    System.out.println("adding");
                    pairs.add(new IDPair(i, e));
                }
            }
        }

        return pairs;
    }
}
