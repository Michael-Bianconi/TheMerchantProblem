package main.cmdline;

import tmp.*;

import java.sql.Connection;
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
                    "= \tTRADE [in][amount] [out][amount] - Trade at the Port.        =\n" +
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
     * @param conn Connection to the database.
     * @return String
     */
    private static String buildInventoryString(Merchant m, Connection conn) {
        Map<Integer, MerchantInventory> map =
                m.retrieveAllMerchantInventories(conn);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, MerchantInventory> e : map.entrySet()) {
            MerchantInventory i = e.getValue();
            Commodity c = Commodity.retrieve(i.COMMODITY_ID, conn);
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
     * @param conn Connection to the database.
     * @return String
     */
    private static String buildInventoryString(Port p, Connection conn) {
        Map<Integer, PortInventory> map =
                p.retrievePortInventories(conn);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, PortInventory> e : map.entrySet()) {
            PortInventory i = e.getValue();
            Commodity c = i.retrieveCommodity(conn);
            builder.append(
                    String.format(
                            PORT_INVENTORY_INFO_STRING,
                            i.ID, c.NAME, c.ID, i.ON_HAND, i.BUY_PRICE, i.SELL_PRICE
                    )
            );
        }
        return builder.toString();
    }


    private static String buildRouteCostsString(Route r, Connection conn) {

        Map<Integer, RouteCost> map =
                r.retrieveRouteCosts(conn);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, RouteCost> e : map.entrySet()) {
            RouteCost cost = e.getValue();
            Commodity com = cost.retrieveCommodity(conn);

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

    private static String buildRoutesString(Port p, Connection conn) {
        Map<Integer, Route> map =
                p.retrieveRoutesOut(conn);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, Route> e : map.entrySet()) {
            Route r = e.getValue();
            Port start = r.retrieveStartPort(conn);
            Port end = r.retrieveEndPort(conn);

            builder.append(
                    String.format(
                            ROUTE_INFO_STRING,
                            r.ID,
                            start.NAME, start.ID,
                            end.NAME, end.ID,
                            buildRouteCostsString(r, conn)
                    )
            );
        }
        return builder.toString();
    }


    private static String buildTransactionsString(Voyage v, Connection conn) {
        Map<Integer, Transaction> map =
                v.retrieveAllTransactions(conn);
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, Transaction> e : map.entrySet()) {
            Transaction t = e.getValue();
            Commodity com = t.retrieveCommodity(conn);

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

    private static String buildVoyageString(Merchant m, Connection conn) {

        Map<Integer, Voyage> map =
                m.retrieveAllVoyages(conn);

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<Integer, Voyage> e : map.entrySet()) {

            Voyage v = e.getValue();
            Port p = v.retrievePort(conn);

            builder.append(
                    String.format(
                            VOYAGE_INFO_STRING,
                            v.ID,
                            m.NAME, m.ID,
                            p.NAME, p.ID,
                            v.TIMESTAMP,
                            buildTransactionsString(v, conn)
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
     * @param conn Connection to the database.
     */
    public static void displayMerchant(Merchant m, Connection conn) {

        Port h = m.retrieveHomePort(conn);
        Port c = m.retrieveCurrentPort(conn);
        float used = m.getUsedCapacity(conn);
        String out = String.format(
                MERCHANT_INFO_STRING,
                m.ID, m.NAME,
                h.NAME, h.ID,
                c.NAME, c.ID,
                used, m.CAPACITY, m.GOLD,
                buildInventoryString(m, conn),
                buildVoyageString(m, conn)
        );

        System.out.println(out);
    }

    /**
     * Displays the Port's info and inventory.
     *
     * @param p    Port to display.
     * @param conn Connection to the database.
     */
    public static void displayPort(Port p, Connection conn) {
        String out = String.format(
                PORT_INFO_STRING,
                p.ID, p.NAME,
                buildInventoryString(p, conn),
                buildRoutesString(p, conn)
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
     * @param conn   Connection to the database.
     * @param update If true, update the database.
     * @return Returns a Transaction if successful, or null.
     */
    public static Transaction trade(
            Merchant m, Port p, Commodity com,
            int amount, Connection conn, boolean update) {

        if (!m.canTrade(p, com, amount, conn)) {
            return null;
        }

        PortInventory pInv =
                p.retrievePortInventoryByCommodity(com.ID, conn);

        MerchantInventory mInv =
                m.retrieveMerchantInventoryByCommodity(com.ID, conn);

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
                m.getLatestVoyage(conn);

        if (voyage == null) {
            voyage = new Voyage(m.ID, p.ID, TMPDatabase.uniqueID());
        }

        Transaction t =
                new Transaction(voyage.ID, com.ID, amount, totalPrice);

        // update the database
        if (update) {
            m.store(conn);
            mInv.store(conn);
            pInv.store(conn);
            voyage.store(conn);
            t.store(conn);
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
     * @param conn   Connection to the database.
     * @param update If true, will store the new information in
     *               the database.
     * @return Returns a generated Voyage object, or null.
     */
    public static Voyage travel(
            Merchant m, Route r, Connection conn, boolean update) {

        if (!m.canTravel(r,conn)) {return null;}

        Map<Integer, RouteCost> costs =
                r.retrieveRouteCosts(conn);

        // Check each RouteCost
        for (Map.Entry<Integer, RouteCost> e : costs.entrySet()) {

            RouteCost cost = e.getValue();
            MerchantInventory commodity =
                    m.retrieveMerchantInventoryByCommodity(
                            cost.COMMODITY_ID, conn);


            // Pay the cost
            commodity.AMOUNT -= cost.AMOUNT;
            if (update) {commodity.store(conn);}
        }

        // Go to end port
        m.CURRENT_PORT = r.END_PORT;
        if (update) {m.store(conn);}

        // Generate Voyage
        Voyage voyage = new Voyage(m.ID,m.CURRENT_PORT,TMPDatabase.uniqueID());
        if (update) {voyage.store(conn);}

        return voyage;
    }


    /** Prints the Help message. */
    public static void displayHelp() {
        System.out.println(HELP_STRING);
    }
}

