package test;

import main.cmdline.UserIO;
import tmp.*;

import java.sql.Connection;

public class TravelDebug {

    private static final String DB_URL =
            "jdbc:h2:~/Dev/projects/TheMerchantProblem/traveldb";

    private static void generateDB(Connection conn) {
        Commodity silk = new Commodity("Silk", 0.1f);
        Commodity meat = new Commodity("Meat", 16f);
        silk.store(conn);
        meat.store(conn);

        Port nassau = new Port("Nassau", 0, 0);
        Port curaco = new Port("Curaco", 100, 50);
        nassau.store(conn);
        curaco.store(conn);

        Route NtoC = new Route(nassau.ID, curaco.ID);
        Route CtoN = new Route(curaco.ID, nassau.ID);
        NtoC.store(conn);
        CtoN.store(conn);

        RouteCost NtoCC = new RouteCost(NtoC.ID, silk.ID, 3);
        RouteCost CtoNC = new RouteCost(CtoN.ID, meat.ID, 4);
        NtoCC.store(conn);
        CtoNC.store(conn);

        Merchant cortez = new Merchant("Cortez", nassau.ID, nassau.ID, 600, 600);
        cortez.store(conn);

        MerchantInventory cortezInvSilk =
                new MerchantInventory(cortez.ID, silk.ID, 20);
        MerchantInventory cortezInvMeat =
                new MerchantInventory(cortez.ID, meat.ID, 10);
        cortezInvSilk.store(conn);
        cortezInvMeat.store(conn);
    }

    public static void main(String args[]) {
        try (TMPDatabase db = new TMPDatabase(DB_URL)) {

            Connection conn = db.getConnection();
            db.createTables();
            generateDB(conn);

            UserIO.commandHelp();

            Merchant cortez = Merchant.retrieve(8,conn);
            Port current = cortez.retrieveCurrentPort(conn);

            UserIO.commandMerchantDisplay(cortez, conn);
            UserIO.commandPortDisplay(current, conn);

            System.out.println("Traveling to Curaco");
            UserIO.commandTravel(cortez, Route.retrieve(4,conn), conn, true);

            UserIO.commandMerchantDisplay(cortez, conn);
            UserIO.commandPortDisplay(cortez.retrieveCurrentPort(conn), conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
