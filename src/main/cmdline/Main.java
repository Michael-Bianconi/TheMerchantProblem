package main.cmdline;

import data.TMPDatabase;
import data.TMPGenerator;
import tmp.*;

/**
 * @author Michael Bianconi
 * @since 02-02-2019
 * This class is the Command Line version of The Merchant Problem.
 * It allows users to "play" the problem.
 */
public class Main {

    private static final String DB_URL =
            "jdbc:h2:~/Dev/projects/TheMerchantProblem/testdb";
    private static final String USER = "username";
    private static final String PASS = "password";

    public static void main(String[] args) {

        if (args.length == 2 && args[1].equals("-r")) {
            TMPDatabase.reset(DB_URL+".mv.db");
        }
        try (TMPDatabase db = new TMPDatabase(DB_URL,USER,PASS)) {

            TMPGenerator generator = new TMPGenerator(db);
            if (args.length == 2 && args[1].equals("-r")) {
                System.out.println("Resetting database");
                generator.setVerbose(true);
                generator.generateWorld();
            }
            Merchant user = generator.generateMerchant(args[1]);

            Commands.displayHelp();

            UserIO.inputPrompt(user, db);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

