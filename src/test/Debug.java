package test;

import TMP.Commodity;
import org.h2.mvstore.DataUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Random;

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
        try {
            Class.forName(JDBC_DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            debugCommodities(conn);
        } catch(Exception e) {
            e.printStackTrace();
        }
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
     * Creates and debugs Commodities.
     * @param conn Connection to the database.
     * @return Returns an array of Commodities for future use.
     */
    private static Commodity[] debugCommodities(Connection conn) {

        Commodity commodities[] = new Commodity[NUM_COMMODITIES];
        Random rand = new Random();
        for (int i = 0; i < NUM_COMMODITIES; i++) {
            float weight = rand.nextFloat() * 100;
            String name = createRandomString(5);
            Commodity c = new Commodity(i, name, weight);
            commodities[i]=c;
        }

        System.out.println("COMMODITIES:");
        for (Commodity c : commodities) { System.out.println(c); }

        for (Commodity c : commodities) { c.store(conn); }

        System.out.println("COMMODITIES STORED IN DATABASE:");
        Map<Integer,Commodity> map = Commodity.retrieveAll(conn);
        for (Map.Entry<Integer, Commodity> e : map.entrySet()) {
            System.out.println(e.getValue());
        }

        System.out.println("COMMODITIES RETRIEVED BY RANDOM ID:");
        for (int i = 0; i < NUM_COMMODITIES; i++) {
            int id = rand.nextInt(NUM_COMMODITIES);
            Commodity c = Commodity.retrieve(id, conn);
            System.out.println("ID: " + id + ", Data: " + c);
        }

        return commodities;

    }
}
