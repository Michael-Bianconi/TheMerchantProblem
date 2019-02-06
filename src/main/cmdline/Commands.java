package main.cmdline;

import data.TMPDatabase;
import tmp.*;

import java.util.Map;

/**
 * @author Michael Bianconi
 * @since 02-02-2019
 * This class defines actions that can be performed.
 */
public class Commands {

    private static final String HELP_STRING =
            "==================================================================\n" +
                    "= Welcome to The Merchant Problem!                               =\n" +
                    "=                                                                =\n" +
                    "= Travel from Port to Port, buying and selling Commodities.      =\n" +
                    "= Make as much profit as possible and return home.               =\n" +
                    "= Commands:                                                      =\n" +
                    "= \tMERCHANT? - Displays info about your merchant.               =\n" +
                    "= \tPORT? [port] - Displays info about the given port.           =\n" +
                    "= \tTRADE [amount][id] - Trade at the Port.                      =\n" +
                    "= \tTRAVEL [route] - Moves your Merchant along the Route.        =\n" +
                    "= \tRETIRE - End your Voyage. Try to be at your Home Port.       =\n" +
                    "= \tHELP - Displays this message.                                =\n" +
                    "==================================================================\n";

    private static final String MERCHANT_INFO_STRING =
            "==================================================================\n" +
                    "= MERCHANT?\n" +
                    "= ID:         %d\n" +
                    "= NAME:       %s\n" +
                    "= HOME PORT:  %s (ID=%d)\n" +
                    "= CURRENT:    %s (ID=%d)\n" +
                    "= CAPACITY:   %.2f/%d\n" +
                    "= GOLD:       %d\n" +
                    "= INVENTORY:\n%s" +       // use buildInventoryString(Merchant)
                    "= VOYAGE:\n%s" +
                    "==================================================================\n";

    private static final String MERCHANT_INVENTORY_INFO_STRING =
            "=    " +
                    "ID: %d\t" +
                    "COMMODITY: %s(ID=%d)\t" +
                    "AMOUNT: %d\t" +
                    "WEIGHT (PER UNIT): %.2f\t" +
                    "WEIGHT (TOTAL): %.2f\t\n";

    private static final String PORT_INFO_STRING =
            "==================================================================\n" +
                    "= PORT?\n" +
                    "= ID:    %d\n" +
                    "= NAME:  %s\n" +
                    "= INVENTORY:\n%s" + // use buildInventoryString(Port)
                    "= ROUTES:\n%s" + // use buildRoutes(Port)
                    "==================================================================\n";

    private static final String PORT_INVENTORY_INFO_STRING =
            "=    " +
                    "ID: %d\t" +
                    "COMMODITY: %s(ID=%d)\t" +
                    "ON HAND: %d\t" +
                    "BUY PRICE: %d\t" +
                    "SELL PRICE: %d\t\n";

    private static final String ROUTE_INFO_STRING =
            "=    " +
                    "ID: %s\t" +
                    "START: %s(ID=%d)\t" +
                    "END: %s(ID=%d)\n" +
                    "=    Costs:\n%s"; // use buildRouteCostString(Route)

    private static final String ROUTE_COST_INFO_STRING =
            "=        " +
                    "ID: %d\t" +
                    "Route: %d\t" +
                    "COMMODITY: %s(ID=%d)\t" +
                    "Amount: %d\n";

    private static final String VOYAGE_INFO_STRING =
            "=    " +
                    "ID: %d\t" +
                    "MERCHANT: %s(ID=%d)\t" +
                    "PORT: %s(ID=%d)\t" +
                    "TIMESTAMP: %d\n" +
                    "=    TRANSACTIONS:\n%s"; // use buildTransactionsString(Voyage)

    private static final String TRANSACTION_INFO_STRING =
            "=        " +
                    "ID: %d\t" +
                    "VOYAGE: %d\t" +
                    "COMMODITY: %s(ID=%d)\t" +
                    "AMOUNT: %d\t" +
                    "PRICE: %d\n";


    /**
     * Builds a String containing information about all of a Merchant's
     * inventories.
     *
     * @param m    Gets this Merchant's inventory info.
     * @param db dbection to the database.
     * @return String
     */
    private static String buildInventoryString(Merchant m, TMPDatabase db) {
        Map<Integer, MerchantInventory> map =
                m.retrieveAllMerchantInventories(db);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, MerchantInventory> e : map.entrySet()) {
            MerchantInventory i = e.getValue();
            Commodity c = (Commodity) db.retrieve("COMMODITY",i.COMMODITY_ID);
            builder.append(
                    String.format(
                            MERCHANT_INVENTORY_INFO_STRING,
                            i.ID, c.NAME, c.ID, i.AMOUNT, c.WEIGHT, c.WEIGHT * i.AMOUNT
                    )
            );
        }
        return builder.toString();
    }


    /**
     * Builds a String containing information about all of a Port's
     * inventories.
     *
     * @param p    Gets this Port's inventory info.
     * @param db dbection to the database.
     * @return String
     */
    private static String buildInventoryString(Port p, TMPDatabase db) {
        Map<Integer, PortInventory> map =
                p.retrievePortInventories(db);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, PortInventory> e : map.entrySet()) {
            PortInventory i = e.getValue();
            Commodity c = (Commodity) db.retrieve("COMMODITY", i.COMMODITY_ID);
            builder.append(
                    String.format(
                            PORT_INVENTORY_INFO_STRING,
                            i.ID, c.NAME, c.ID, i.ON_HAND, i.BUY_PRICE, i.SELL_PRICE
                    )
            );
        }
        return builder.toString();
    }


    private static String buildRouteCostsString(Route r, TMPDatabase db) {

        Map<Integer, RouteCost> map =
                r.retrieveRouteCosts(db);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, RouteCost> e : map.entrySet()) {
            RouteCost cost = e.getValue();
            Commodity com = cost.retrieveCommodity(db);

            builder.append(
                    String.format(
                            ROUTE_COST_INFO_STRING,
                            cost.ID,
                            r.ID,
                            com.NAME, com.ID,
                            cost.AMOUNT
                    )
            );
        }
        return builder.toString();
    }

    private static String buildRoutesString(Port p, TMPDatabase db) {
        Map<Integer, Route> map =
                p.retrieveRoutesOut(db);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, Route> e : map.entrySet()) {
            Route r = e.getValue();
            Port start = r.retrieveStartPort(db);
            Port end = r.retrieveEndPort(db);

            builder.append(
                    String.format(
                            ROUTE_INFO_STRING,
                            r.ID,
                            start.NAME, start.ID,
                            end.NAME, end.ID,
                            buildRouteCostsString(r, db)
                    )
            );
        }
        return builder.toString();
    }


    private static String buildTransactionsString(Voyage v, TMPDatabase db) {
        Map<Integer, Transaction> map =
                v.retrieveAllTransactions(db);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, Transaction> e : map.entrySet()) {
            Transaction t = e.getValue();
            Commodity com = t.retrieveCommodity(db);

            //if (com!=null) {
            builder.append(
                    String.format(
                            TRANSACTION_INFO_STRING,
                            t.ID,
                            v.ID,
                            com.NAME, com.ID,
                            t.AMOUNT,
                            t.PRICE
                    )
            );

            // }
        }
        return builder.toString();
    }

    private static String buildVoyageString(Merchant m, TMPDatabase db) {

        Map<Integer, Voyage> map =
                m.retrieveAllVoyages(db);

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, Voyage> e : map.entrySet()) {

            Voyage v = e.getValue();
            Port p = v.retrievePort(db);

            builder.append(
                    String.format(
                            VOYAGE_INFO_STRING,
                            v.ID,
                            m.NAME, m.ID,
                            p.NAME, p.ID,
                            v.TIMESTAMP,
                            buildTransactionsString(v, db)
                    )
            );
        }

        return builder.toString();
    }


    /**
     * Displays the given Merchant's associated fields, along with his or her
     * inventory.
     *
     * @param m    Merchant to display.
     * @param db dbection to the database.
     */
    public static void displayMerchant(Merchant m, TMPDatabase db) {

        Port h = m.retrieveHomePort(db);
        Port c = m.retrieveCurrentPort(db);
        float used = m.getUsedCapacity(db);
        String out = String.format(
                MERCHANT_INFO_STRING,
                m.ID, m.NAME,
                h.NAME, h.ID,
                c.NAME, c.ID,
                used, m.CAPACITY, m.GOLD,
                buildInventoryString(m, db),
                buildVoyageString(m, db)
        );

        System.out.println(out);
    }

    /**
     * Displays the Port's info and inventory.
     *
     * @param p    Port to display.
     * @param db dbection to the database.
     */
    public static void displayPort(Port p, TMPDatabase db) {
        String out = String.format(
                PORT_INFO_STRING,
                p.ID, p.NAME,
                buildInventoryString(p, db),
                buildRoutesString(p, db)
        );
        System.out.println(out);
    }

    /**
     * The Merchant trades Commodities with the Port. Does NOT generate
     * a Transaction object. For the transaction to be successful, the
     * Merchant and Port must have the required Commodities on hand.
     * Additionally, the new weight of the Merchant's inventory cannot
     * exceed his or her capacity.
     *
     * @param m      Merchant.
     * @param p      Port.
     * @param com    Commodity bought from the Port.
     * @param amount Amount to buy or sell (negative = sell).
     * @param db   dbection to the database.
     * @param update If true, update the database.
     * @return Returns a Transaction if successful, or null.
     */
    public static Transaction trade(
            Merchant m, Port p, Commodity com,
            int amount, TMPDatabase db, boolean update) {

        if (!canTrade(m,p, com, amount, db)) {
            return null;
        }

        PortInventory pInv =
                p.retrievePortInventoryByCommodity(com.ID, db);

        if (pInv == null) {
            System.out.printf("%s doesn't have %s",p.NAME,com.NAME);
        }

        MerchantInventory mInv =
                m.retrieveMerchantInventoryByCommodity(com.ID, db);

        if (mInv == null) {
            mInv = new MerchantInventory(m.ID,com.ID,0);
        }

        int totalPrice = 0;

        // selling
        if (amount < 0) {

            totalPrice = pInv.SELL_PRICE * amount;
            m.GOLD -= totalPrice;
            mInv.AMOUNT += amount;
            pInv.ON_HAND -= amount;
        }

        // buying
        else {

            totalPrice = pInv.BUY_PRICE * amount;
            m.GOLD -= totalPrice;
            mInv.AMOUNT += amount;
            pInv.ON_HAND -= amount;
        }

        // create Transaction
        Voyage voyage =
                m.getLatestVoyage(db);

        if (voyage == null) {
            voyage = new Voyage(m.ID, p.ID, TMPDatabase.uniqueID());
        }

        Transaction t =
                new Transaction(voyage.ID, com.ID, amount, totalPrice);

        // update the database
        if (update) {
            db.store(m);
            db.store(mInv);
            db.store(pInv);
            db.store(voyage);
            db.store(t);
        }

        return t;

    }


    /**
     * Merchants can travel from Port to Port using Routes. They can
     * do so by specifying the Route to take. However, they must be
     * able to pay the associated costs.
     *
     * @param m      Merchant traveling.
     * @param r      Route the Merchant is using.
     * @param db   dbection to the database.
     * @param update If true, will store the new information in
     *               the database.
     * @return Returns a generated Voyage object, or null.
     */
    public static Voyage travel(
            Merchant m, Route r, TMPDatabase db, boolean update) {

        if (!canTravel(m,r,db)) {return null;}

        Map<Integer, RouteCost> costs =
                r.retrieveRouteCosts(db);

        // Check each RouteCost
        for (Map.Entry<Integer, RouteCost> e : costs.entrySet()) {

            RouteCost cost = e.getValue();
            MerchantInventory commodity =
                    m.retrieveMerchantInventoryByCommodity(
                            cost.COMMODITY_ID, db);


            // Pay the cost
            commodity.AMOUNT -= cost.AMOUNT;
            if (update) {db.store(commodity);}
        }

        // Go to end port
        m.CURRENT_PORT = r.END_PORT;
        if (update) {db.store(m);}

        // Generate Voyage
        Voyage voyage = new Voyage(m.ID,m.CURRENT_PORT,TMPDatabase.uniqueID());
        if (update) {db.store(voyage);}

        return voyage;
    }

    /**
     * Determine whether the Given Merchant is able to travel along the
     * given Route. The Merchant must be at the starting Port
     * and must have the Commodities required for the RouteCosts.
     * @param merchant Merchant traveling.
     * @param route Port being traveled to.
     * @param db Connection to the database.
     * @return Returns true if the Merchant can travel.
     */
    public static boolean canTravel(
            Merchant merchant, Route route, TMPDatabase db) {

        if (merchant == null) {
            System.out.println("That merchant doesn't exist!");
            return false;
        }

        if (route == null) {
            System.out.println("That route doesn't exist!");
            return false;
        }

        Port start = route.retrieveStartPort(db);

        // The Merchant is not at the starting port.
        if (start.ID != merchant.CURRENT_PORT) {
            System.out.println("That route isn't available here!");
            return false;
        }

        Map<Integer, RouteCost> costs =
                route.retrieveRouteCosts(db);

        // Check each RouteCost
        for (Map.Entry<Integer, RouteCost> e : costs.entrySet()) {

            RouteCost cost = e.getValue();
            MerchantInventory mInv =
                    merchant.retrieveMerchantInventoryByCommodity(
                            cost.COMMODITY_ID, db);

            // The merchant does not own enough of the Commodity
            if (mInv == null || mInv.AMOUNT < cost.AMOUNT) {
                Commodity com = mInv.retrieveCommodity(db);
                if (com == null) {
                    System.out.println("RouteCost Commodity not found!");
                    return false;
                }
                System.out.printf("You don't have enough %s!\n",com.NAME);
                return false;
            }
        }

        return true;
    }

    /**
     * Runs through the necessary conditions for trading a commodity
     * with a port. They must have enough available, they must have
     * enough Gold, and the Merchant must, if buying, have
     * enough capacity to carry the new Commodities.
     * @param merchant Merchant trading.
     * @param port Port to trade with.
     * @param com Commodity to trade.
     * @param amount Amount to trade (- for selling, + for buying).
     * @param db Connection to the database.
     * @return Returns true if able to trade.
     */
    public static boolean canTrade(
            Merchant merchant, Port port, Commodity com,
            int amount, TMPDatabase db) {

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
            if (pInv.BUY_PRICE*amount > merchant.GOLD) {
                System.out.printf("You don't have enough gold\n");
                return false;
            }

            // the merchant must have the capacity for this commodity
            if( merchant.getUsedCapacity(db)+(com.WEIGHT*amount) > merchant.CAPACITY) {
                System.out.printf("You don't have enough space for that many %s\n",com.NAME);
            }

            // selling
        } else if (amount < 0) {

            amount = -amount; // flip it to positive

            MerchantInventory mInv =
                    merchant.retrieveMerchantInventoryByCommodity(com.ID, db);

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


    /** Prints the Help message. */
    public static void displayHelp() {
        System.out.println(HELP_STRING);
    }
}

