package TMP;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * @author Michael Bianconi
 * @since 02-01-2019
 * The TMPDatabase class will build a database from scratch.
 */
public class TMPDatabase implements AutoCloseable {

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
    private int numCommodities;

    public TMPDatabase(String dbName) throws Exception {

        Class.forName(JDBC_DRIVER);
        this.conn = DriverManager.getConnection(dbName, USER, PASS);
    }

    @Override
    public void close() throws Exception {
        if (this.conn != null) {this.conn.close();}
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
            Commodity c = new Commodity(i, name, weight);
            c.store(conn);
            commodities[i]=c;
        }
        this.numCommodities = num;
        return commodities;
    }

    public Port[] createPorts(int num) {
        Port[] ports = new Port[num];
        Random rand = new Random();
        for (int i = 0; i < num; i++) {
            int x = rand.nextInt(100);
            int y = rand.nextInt(100);
            String name = createRandomString(7);
            Port p = new Port(i,name,x,y);
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
                    new PortInventory(id, portID, comID, onHand, buy, sell);
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
            else {index = rand.nextInt(portPairs.size()-1);}
            IDPair pair = portPairs.get(index);
            portPairs.remove(index);

            Route r = new Route(id, pair.id1, pair.id2);
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
            int index = rand.nextInt(pairs.size()-1);
            IDPair pair = pairs.get(index);
            pairs.remove(index);
            int amount = rand.nextInt(256);
            RouteCost c = new RouteCost(id, pair.id1, pair.id2, amount);
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
            int home = portIDs.get(random.nextInt(portIDs.size()-1));
            int current = portIDs.get(random.nextInt(portIDs.size()-1));
            int capacity = random.nextInt(1024);
            Merchant m  = new Merchant(id, name, home, current, capacity);
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
            int index = rand.nextInt(pairs.size()-1);
            IDPair pair = pairs.get(index);
            pairs.remove(index);
            int amount = rand.nextInt(256);
            MerchantInventory m =
                    new MerchantInventory(id,pair.id1,pair.id2,amount);
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
            int index = rand.nextInt(pairs.size()-1);
            IDPair pair = pairs.get(index);
            pairs.remove(index);
            int time = rand.nextInt();
            Voyage v = new Voyage(id,pair.id1,pair.id2,time);
            v.store(conn);
            voyages[id] = v;
        }
        return voyages;
    }

    public Transaction[] createTransactions(int num) {
        ArrayList<Integer> voyages =
                getColumnInt(Voyage.TABLE_NAME, "ID");
        ArrayList<IDPair> coms = _transactionCartesian();
        System.out.println(coms);
        Random rand = new Random();
        Transaction[] trans = new Transaction[num];
        for (int id = 0; id < num; id++) {
            int voyage = voyages.get(rand.nextInt(voyages.size()-1));
            IDPair pair = coms.get(rand.nextInt(coms.size()-1));
            int comIn = pair.id1;
            int comOut = pair.id2;
            int amountIn = rand.nextInt(256);
            int amountOut = rand.nextInt(256);
            Transaction t =
                    new Transaction(id,voyage,comIn,amountIn,comOut,amountOut);
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
