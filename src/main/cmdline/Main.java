package main.cmdline;

import tmp.*;

import java.sql.Connection;

/**
 * @author Michael Bianconi
 * @since 02-02-2019
 * This class is the Command Line version of The Merchant Problem.
 * It allows users to "play" the problem.
 */
public class Main {

    private static final String DB_URL =
            "jdbc:h2:~/Dev/projects/TheMerchantProblem/testdb";

    public static void main(String[] args) {

        try (TMPDatabase db = new TMPDatabase(DB_URL)) {
            Connection conn = db.getConnection();
            Merchant m = Merchant.retrieve(1, conn);
            Port p = Port.retrieve(2, conn);


        } catch (Exception e) { e.printStackTrace(); }
    }
}

