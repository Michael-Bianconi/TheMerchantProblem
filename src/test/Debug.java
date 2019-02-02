package test;

import tmp.*;

import java.sql.Connection;

/**
 * @author Michael Bianconi
 * @since 02-01-2019
 * This class was created to debug connections within the database.
 */
public class Debug {

    private Debug() {   }

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL =
            "jdbc:h2:~/Dev/projects/TheMerchantProblem/testdb";
    private static final String USER = "Mike";
    private static final String PASS = "";

    private static final int NUM_COMMODITIES = 7;
    private static final String COMMODITY_NAMES[] = {
            "GOLD", "MEAT", "FURS", "SILK", "SILVER", "LUMBER", "SLAVES"
    };

    public static void main(String args[]) {
        try (TMPDatabase db = new TMPDatabase(DB_URL)) {
            Connection conn = db.getConnection();
            db.createTables();
            db.createCommodities(7);
            db.createPorts(5);
            db.createPortInventories(27);
            db.createRoutes(20);
            db.createRouteCosts(43);
            db.createMerchants(3);
            db.createMerchantInventories(18);
            db.createVoyages(6);
            db.createTransactions(39);

            Port p = Port.retrieve(3,conn);
            System.out.println(p.retrievePortInventories(conn));
            System.out.println(p.retrieveRoutesOut(conn));

            Route r = Route.retrieve(2,conn);
            System.out.println(r.retrieveRouteCosts(conn));

            Merchant m = Merchant.retrieve(0,conn);
            System.out.println(m.retrieveAllMerchantInventories(conn));
            System.out.println(m.retrieveAllVoyages(conn));

            Voyage v = Voyage.retrieve(5,conn);
            System.out.println(v.retrieveAllTransactions(conn));

        } catch(Exception e) {e.printStackTrace();}
    }
}
