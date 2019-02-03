package main.cmdline;

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
        commands.add("MERCHANT?");
        commands.add("PORT?");
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
     * @param conn The Connection to the database.
     * @return Returns true if able to parse and execute.
     */
    public static boolean parse(
            String[] args, Merchant user, Connection conn) {

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

                case "MERCHANT?": // $ MERCHANT
                    Commands.displayMerchant(user, conn);
                    return true;

                case "PORT?": // $ PORT? [ID]
                    if (args.length == 1) {
                        Port current = user.retrieveCurrentPort(conn);
                        if (current == null) {
                            System.out.println("Somehow, you're not at a Port!");
                            return false;

                        } else {
                            Commands.displayPort(current, conn);
                            return true;
                        }

                    } else {
                        Port p = Port.retrieve(Integer.parseInt(args[1]), conn);
                        if (p == null) {
                            System.out.println("Unknown port!");
                            return false;

                        } else {
                            Commands.displayPort(p, conn);
                            return true;
                        }
                    }

                case "BUY": // $ BUY <COMMODITY> <AMOUNT>

                    if (args.length != 3) {
                        System.out.println("$ BUY <AMOUNT> <COMMODITY ID>");
                        return false;
                    }
                    Port p = user.retrieveCurrentPort(conn);

                    if (p == null) {
                        System.out.println("Somehow, you're not at a Port!");
                        return false;
                    }
                    Commodity buyCom =
                            Commodity.retrieve(Integer.parseInt(args[2]), conn);
                    int buyAmount = Integer.parseInt(args[1]);

                    // Unsuccessful trade
                    if (Commands.trade(
                            user, p, buyCom, buyAmount, conn, true) == null) {
                        System.out.println("Couldn't trade!");
                        return false;
                    }

                    return true;

                case "SELL": // $ SELL <AMOUNT> <COMMODITY ID>

                    if (args.length != 3) {
                        System.out.println("$ SELL <COMMODITY ID> <AMOUNT>");
                        return false;
                    }
                    Port port = user.retrieveCurrentPort(conn);
                    if (port == null) {
                        System.out.println("Somehow, you're not at a Port!");
                        return false;
                    }
                    Commodity sellCom =
                            Commodity.retrieve(Integer.parseInt(args[2]), conn);
                    int sellAmount = -Integer.parseInt(args[1]);

                    // Unsuccessful trade
                    if (Commands.trade(
                            user, port, sellCom, sellAmount, conn, true) == null) {
                        System.out.println("Couldn't trade!");
                        return false;
                    }

                    return true;

                case "TRAVEL": // $ TRAVEL <ROUTE>

                    if (args.length != 2) {
                        System.out.println("$ TRAVEL <ROUTE ID>");
                        return false;
                    }
                    Route r = Route.retrieve(Integer.parseInt(args[1]), conn);
                    if (Commands.travel(user, r, conn, true) == null) {
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
     * @param conn Connection to the database.
     */
    public static void inputPrompt(Merchant user, Connection conn) {

        int tokenBuffer = 100;
        Scanner s = new Scanner(System.in);
        String[] array = new String[tokenBuffer];

        while (true) {

            System.out.print("$ ");
            String[] tokens = s.nextLine().split(" ");
            parse(tokens, user, conn);
        }
    }
}
