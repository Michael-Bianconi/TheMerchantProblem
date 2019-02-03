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

    public static void main(String args[]) {
        try (TMPDatabase db = new TMPDatabase(DB_URL)) {
            Connection conn = db.getConnection();
            db.createTables();
            db.createCommodities(2);
            db.createPorts(3);
            db.createPortInventories(5);
            db.createRoutes(6);
            db.createRouteCosts(10);
            db.createMerchants(1);
            db.createMerchantInventories(2);
            db.createVoyages(1);
            db.createTransactions(0);

        } catch(Exception e) {e.printStackTrace();}
    }
}
