package main.ai;

import data.TMPDatabase;
import data.TMPGenerator;
import tmp.Merchant;

/**
 * This is the main executable for running the TMP algorithm to find
 * the most profitable route.
 */
public class Main {

    private static final String DB_URL =
            "jdbc:h2:~/Dev/projects/TheMerchantProblem/testdb";
    private static final String USER = "username";
    private static final String PASS = "password";

    private static void solve(Merchant m, TMPDatabase db) {


    }

    public static void main(String[] args) {

        if (args.length == 2 && args[1].equals("-r")) {
            TMPDatabase.reset(DB_URL+".mv.db");
        }
        try (TMPDatabase db = new TMPDatabase(DB_URL,USER,PASS)) {

            TMPGenerator generator = new TMPGenerator(db);
            generator.setVerbose(true);
            generator.generateWorld();
            Merchant user = generator.generateMerchant(args[1]);

            solve(user, db);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
