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
            System.out.println(r);
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
}
