package main.cmdline;

import data.TMPDatabase;
import tmp.Commodity;
import tmp.Merchant;
import tmp.Port;
import tmp.Route;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Michael Bianconi
 * @since 02-03-2019
 * This class parses User Input.
 */
public class UserIO {

    private static Set<String> commands = new HashSet<>();

    static {
        commands.add("HELP");
        commands.add("WHOAMI");
        commands.add("WHEREAMI");
        commands.add("BUY");
        commands.add("SELL");
        commands.add("TRAVEL");
        commands.add("RETIRE");
    }

    // Static class
    private UserIO() {
    }

    /**
     * Given String arguments (similar to main(String[] args), parse and
     * execute the command. If invalid, print invalid.
     *
     * @param args Input to parse.
     * @param user The user's Merchant.
     * @param db The Connection to the database.
     * @return Returns true if able to parse and execute.
     */
    public static boolean parse(
            String[] args, Merchant user, TMPDatabase db) {

        try {

            if (args.length == 0) {
                return false;
            }
            if (!commands.contains(args[0].toUpperCase())) {
                System.out.println("Unknown command!");
                return false;
            }
            switch (args[0].toUpperCase()) {

                case "HELP": // $ HELP
                    Commands.displayHelp();
                    return true;

                case "WHOAMI": // $ MERCHANT
                    Commands.displayMerchant(user, db);
                    return true;

                case "WHEREAMI": // $ PORT? [ID]
                    if (args.length == 1) {
                        Port current = user.retrieveCurrentPort(db);
                        if (current == null) {
                            System.out.println("Somehow, you're not at a Port!");
                            return false;

                        } else {
                            Commands.displayPort(current, db);
                            return true;
                        }

                    } else {
                        Port p = (Port) db.retrieve("PORT",Integer.parseInt(args[1]));
                        if (p == null) {
                            System.out.println("Unknown port!");
                            return false;

                        } else {
                            Commands.displayPort(p, db);
                            return true;
                        }
                    }

                case "BUY": // $ BUY <COMMODITY> <AMOUNT>

                    if (args.length != 3) {
                        System.out.println("$ BUY <AMOUNT> <COMMODITY ID>");
                        return false;
                    }
                    Port p = user.retrieveCurrentPort(db);

                    if (p == null) {
                        System.out.println("Somehow, you're not at a Port!");
                        return false;
                    }
                    Commodity buyCom =
                            (Commodity) db.retrieve(
                                    "COMMODITY",Integer.parseInt(args[2]));
                    int buyAmount = Integer.parseInt(args[1]);

                    // Unsuccessful trade
                    if (Commands.trade(
                            user, p, buyCom, buyAmount, db, true) == null) {
                        System.out.println("Couldn't trade!");
                        return false;
                    }

                    return true;

                case "SELL": // $ SELL <AMOUNT> <COMMODITY ID>

                    if (args.length != 3) {
                        System.out.println("$ SELL <COMMODITY ID> <AMOUNT>");
                        return false;
                    }
                    Port port = user.retrieveCurrentPort(db);
                    if (port == null) {
                        System.out.println("Somehow, you're not at a Port!");
                        return false;
                    }
                    Commodity sellCom =
                            (Commodity) db.retrieve(
                                    "COMMODITY",Integer.parseInt(args[2]));
                    int sellAmount = -Integer.parseInt(args[1]);

                    // Unsuccessful trade
                    if (Commands.trade(
                            user, port, sellCom, sellAmount, db, true) == null) {
                        System.out.println("Couldn't trade!");
                        return false;
                    }

                    return true;

                case "TRAVEL": // $ TRAVEL <ROUTE>

                    if (args.length != 2) {
                        System.out.println("$ TRAVEL <ROUTE ID>");
                        return false;
                    }
                    Route r = (Route) db.retrieve("ROUTE",Integer.parseInt(args[1]));
                    if (Commands.travel(user, r, db, true) == null) {
                        System.out.println("Couldn't travel!");
                        return false;
                    }
                    return true;

                case "RETIRE": // $ RETIRE
                    System.exit(0);

                default:
                    System.out.println("Unknown command!");
                    return false;
            }

        } catch(NumberFormatException e) {
            System.out.println("Wrong format!");
            return false;
        }
    }

    /**
     * Continually prompts for user input.
     * @param user The Merchant the user is controlling.
     * @param db Connection to the database.
     */
    public static void inputPrompt(Merchant user, TMPDatabase db) {

        int tokenBuffer = 100;
        Scanner s = new Scanner(System.in);
        String[] array = new String[tokenBuffer];

        while (true) {

            System.out.print("$ ");
            String[] tokens = s.nextLine().split(" ");
            parse(tokens, user, db);
        }
    }
}
