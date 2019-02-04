package data;

import com.opencsv.CSVReader;
import tmp.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The TMPGenerator can generate new, cohesive data for the
 * {@link TMPDatabase}.
 */
public class TMPGenerator {


    // Member Fields ==========================================================

    /** The default path to a List of Commodity names. */
    private String commodityNameFile =
            "./src/data/COMMODITY_STANDARD.csv";

    /** The default path to a List of Port names. */
    private String portNameFile =
            "./src/data/PORT_STANDARD.csv";

    /** The TMPDatabase to generate. */
    private TMPDatabase database;

    /** Higher number = more Routes between Ports. (1..10) */
    private int globality = 5;

    /** Higher number = costlier Routes. (1..10) */
    private int travelCosts = 5;

    /** Higher number = more Commodities available at Ports. (1..10) */
    private int industry = 5;

    /** If True, prints info as this is generating the database. */
    private boolean verbose = false;

    /** A map from CommodityID to its value. */
    private Map<Integer, Integer> commodityValues = new HashMap<>();

    /** A map from PortID to its wealth. */
    private Map<Integer, Integer> portWealth = new HashMap<>();

    // Constructors ===========================================================

    /** Constructs the Generator with a database. */
    public TMPGenerator(TMPDatabase database) {this.database=database;}

    // Accessors ==============================================================

    /** Set the file to use for Commodity names. */
    public void setCommodityNameFile(String path) {commodityNameFile=path;}

    /** Set the file to use for Port names. */
    public void setPortNameFile(String path) {portNameFile=path;}

    /**
     * Set how interconnected the Ports are on a scale of 1 to 10. Lower the
     * value, the fewer Routes there are between Ports. There will never be
     * a stranded Port (a Port with no way in) or a dead end (a Port with
     * no way back to a previous Port).
     */
    public void setGlobality(int amount) {
        assert(amount>=1 && amount<=10);
        globality=amount;
    }

    /**
     * Set how easy it is to use Routes on a scale of 1 to 10. Higher the
     * value, more it costs to use a Route.
     */
    public void setTravelCosts(int amount) {
        assert(amount>=1 && amount<=10);
        travelCosts=amount;
    }

    /**
     * Set how many Commodities Ports are willing to trade on a scale of
     * 1 to 10. Lower value = less Commodities.
     */
    public void setIndustry(int amount) {
        assert(amount>=1 && amount<=10);
        industry=amount;
    }

    /**
     * Sets whether or not to print out Generator details.
     */
    public void setVerbose(boolean v) {verbose=v;}

    // GENERATE! ==============================================================

    /**
     * Generates several commodities and stores them in the database.
     * @see #setCommodityNameFile(String)
     */
    public void generateCommodities() {

        Commodity.createTable(database.getConnection());

        try {
            String path = commodityNameFile;
            File f = new File(path);
            System.out.println(f.getAbsolutePath());
            CSVReader reader = new CSVReader(new FileReader(path));
            reader.skip(1); // skip header
            String[] line;

            // For each line in the file
            while ((line = reader.readNext()) != null) {

                String name = line[0];
                float weight = Float.parseFloat(line[1]);
                int value = Integer.parseInt(line[2]);
                Commodity in = new Commodity(name,weight);
                commodityValues.put(in.ID, value);
                in.store(database.getConnection());

                if (verbose) {
                    System.out.printf(
                            "Stored Commodity \"%s\" (ID=%d) with value %d\n",
                            name, in.ID, value);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates several commodities and stores them in the database.
     * @see #setCommodityNameFile(String)
     */
    public void generatePorts() {

        Port.createTable(database.getConnection());

        try {
            String path = portNameFile;
            File f = new File(path);
            System.out.println(f.getAbsolutePath());
            CSVReader reader = new CSVReader(new FileReader(path));
            reader.skip(1); // skip header
            String[] line;

            // For each line in the file
            while ((line = reader.readNext()) != null) {

                String name = line[0];
                int x = Integer.parseInt(line[1]);
                int y = Integer.parseInt(line[2]);
                int wealth = Integer.parseInt(line[3]);
                Port in = new Port(name,x,y);
                portWealth.put(in.ID, wealth);
                in.store(database.getConnection());

                if (verbose) {
                    System.out.printf(
                            "Stored Port \"%s\" (ID=%d) with wealth %d\n",
                            name, in.ID, wealth);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * PortInventories are assigned to Ports and contain availability and
     * price. The number of PortInventories per Port is determined by the
     * "industry" variable. The availability of a Commodity is based on its
     * value (lower values and higher wealths mean more availability).
     * Commodity prices are based on wealth, value.
     */
    public void generatePortInventories() {

        PortInventory.createTable(database.getConnection());

        Random rand = new Random();
        int maxInventories = commodityValues.size();

        // Produce only (industry/10) of the commodities.
        int numInventories = (int) ((float) industry/10 * maxInventories);

        // Go through each port
        for (Map.Entry<Integer,Integer> e : portWealth.entrySet()) {

            int portID = e.getKey();
            int wealth = e.getValue();

            // Produce a working copy of the commodity map
            Map<Integer,Integer> working = new HashMap<>(commodityValues);
            ArrayList<Integer> keys = new ArrayList<>(working.keySet());

            // Produce several Inventories for each Port
            for (int i = 0; i < numInventories; i++) {

                int comID = keys.remove(rand.nextInt(keys.size()));
                int value = working.get(comID);

                // Determine the availability
                int onHand = wealth * (11 - value);

                // Determine the buy price
                int buy = wealth * value;

                // Determine the sell price
                int sell = wealth * value;

                // Create the inventory
                PortInventory inv =
                        new PortInventory(portID, comID, onHand, buy, sell);
                inv.store(database.getConnection());

                if (verbose) {
                    System.out.printf(
                            "Stored PortInventory at %s with " +
                                    "Commodity %s, %d on hand, going for %d/%d\n",
                            Port.retrieve(portID, database.getConnection()).NAME,
                            Commodity.retrieve(comID, database.getConnection()).NAME,
                            onHand, buy, sell);
                }
            }
        }
    }


    /**
     * Routes connect Ports together. The number of Routes is determined be
     * the globality variable. The higher the value, the more Routes that
     * exist. For every Route into a Port, there is a respective Route
     * back, to prevent dead ends. However, these may not have the same
     * costs.
     *
     * The algorithm used to generate these Routes is a bastardized
     * insertion sort. Two Port Lists are generated. The first List,
     * called visited, begins with a single Port (the first one in the
     * list). The other List, unvisited, holds all the other Ports.
     *
     * Randomly select Ports in the unvisited List. Once selected,
     * create a Route between that Port, the <i>last></i> Port
     * in the visited List. Then, create a different Route
     * with a random Port in the visited List. Finally, move the
     * unvisited Port into the visited List. Repeat until all Ports have
     * been visited.
     *
     * Finally, randomly place Routes between the Ports (don't create
     * duplicates), until a satisfactory number have been created.
     *
     * A minimum number of Routes will always be created in order to
     * bi-directionally connect every single Port.
     */
    public void generateRoutes() {

        Route.createTable(database.getConnection());
        Map<Integer,Port> map = Port.retrieveAll(database.getConnection());
        Random rand = new Random();

        // Maximum number of routes possible
        int maxRoutes = map.size() * (map.size()-1);

        // Number of routes we'll generate
        int numRoutes = (int) ((float) globality * map.size() * 2);
        numRoutes = (numRoutes>maxRoutes) ? maxRoutes: numRoutes;

        // Number of routes we've already generated
        int routeCount = 0;

        // Create the two lists
        ArrayList<Integer> unvisited = new ArrayList<>(map.keySet());
        ArrayList<Integer> visited = new ArrayList<>();
        visited.add(unvisited.remove(0));
        System.out.println(unvisited);
        System.out.println(visited);

        while (unvisited.size() > 0) {

            int index = rand.nextInt(unvisited.size());
            int start = visited.get(visited.size()-1);
            int end = unvisited.remove(index);
            Route r1 = new Route(start,end);
            Route r2 = new Route(end, start);
            r1.store(database.getConnection());
            r2.store(database.getConnection());
            routeCount+=2;
            System.out.printf(
                "Stored Route between %s and %s\n",
                Port.retrieve(start,database.getConnection()).NAME,
                Port.retrieve(end,database.getConnection()).NAME
            );

            if (visited.size()>1) {
                int randomVisited = rand.nextInt(visited.size()-1);
                int randP = visited.get(randomVisited);
                Route r3 = new Route(end, randP);
                Route r4 = new Route(randP, end);
                routeCount+=2;
                r3.store(database.getConnection());
                r4.store(database.getConnection());
                System.out.printf(
                        "Stored Route between %s and %s\n",
                        Port.retrieve(end,database.getConnection()).NAME,
                        Port.retrieve(randP,database.getConnection()).NAME
                );
            }

            visited.add(end);

        }

        // Finally, generate Routes until globality is reached.
        while (routeCount < numRoutes) {
            int port1 = visited.get(rand.nextInt(visited.size()));
            int port2 = visited.get(rand.nextInt(visited.size()));
            if (!Port.areConnected(port1,port2,database.getConnection())
            &&  !Port.areConnected(port2,port1,database.getConnection())) {
                Route r1 = new Route(port1, port2);
                Route r2 = new Route(port2, port1);
                routeCount += 2;
                r1.store(database.getConnection());
                r2.store(database.getConnection());

                if (verbose) {
                    System.out.printf(
                            "Stored Route between %s and %s\n",
                            Port.retrieve(port1, database.getConnection()).NAME,
                            Port.retrieve(port2, database.getConnection()).NAME
                    );
                }
            }
        }
    }


    /**
     * Generate RouteCosts for each Route. RouteCosts are based on several
     * factors. First and foremost, the travelCost variable will determine
     * how many Commodities are required. At 1, that number is 1, at 5,
     * it's between 1 and 3, and at 10 it's between 4 and 6.
     *
     * The amount required is based on the wealth of destination Port.
     * Wealthier Port, higher cost.
     *
     * Finally, the commodity required is <i>always</i> selected from
     * one of the Commodities carried by the starting port. This is to
     * prevent nearly-unavoidable "game-overs" but traveling to a Port
     * without enough of some certain Commodity in stock. Additionally,
     * the amount required will never be greater than the amount
     * <i>originally</i> carried by the Port. This is to allow first-
     * timers a way out, before they know what they need to leave.
     */
    public void generateRouteCosts() {

        RouteCost.createTable(database.getConnection());

        int minCosts = travelCosts/2 - 1;
        int maxCosts = travelCosts/2 + 1;
        if (minCosts <= 0) {minCosts = 1;}
        if (maxCosts >  6) {maxCosts = 6;}

        Map<Integer,Route> routes = Route.retrieveAll(database.getConnection());
        Random rand = new Random();

        for (Map.Entry<Integer, Route> e : routes.entrySet()) {

            // Get a random number of costs
            int numCosts = rand.nextInt(maxCosts-minCosts) + minCosts;

            // Get a list of the starting port's inventories
            Port start =
                    e.getValue().retrieveStartPort(database.getConnection());
            ArrayList<Integer> invs = new ArrayList<Integer>(
                    start.retrievePortInventories(database.getConnection()).keySet());

            for (int cost = 0; cost <= numCosts; cost++) {
                int index = rand.nextInt(invs.size());
                PortInventory inv =
                        PortInventory.retrieve(invs.remove(index),database.getConnection());

                int commodity = inv.COMMODITY_ID;
                int destWealth = portWealth.get(e.getValue().END_PORT);
                int amount = destWealth - (commodityValues.get(commodity)/2);
                if (amount <= 0) {amount = destWealth;}
                RouteCost rc = new RouteCost(e.getKey(),commodity, amount);
                rc.store(database.getConnection());

                if (verbose) {

                    System.out.printf("Stored RouteCost for %s to %s, %d %s\n",
                            start.NAME, Port.retrieve(e.getValue().END_PORT,database.getConnection()).NAME,
                            amount, Commodity.retrieve(commodity, database.getConnection()));
                }
            }
        }
    }
}
