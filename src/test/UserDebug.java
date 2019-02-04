package test;

import data.TMPDatabase;
import main.cmdline.Commands;
import main.cmdline.UserIO;
import tmp.*;

import java.sql.Connection;

public class UserDebug {

    private static final String DB_URL =
            "jdbc:h2:~/Dev/projects/TheMerchantProblem/userdb";

    private static int generateDB(Connection conn) {
        Commodity silk = new Commodity("Silk", 0.1f);
        Commodity meat = new Commodity("Meat", 16f);
        silk.store(conn);
        meat.store(conn);

        Port nassau = new Port("Nassau", 0, 0);
        Port curaco = new Port("Curaco", 100, 50);
        nassau.store(conn);
        curaco.store(conn);

        PortInventory nassauInv = new PortInventory(nassau.ID, silk.ID, 15, 10,11);
        PortInventory curacoInv = new PortInventory(curaco.ID, meat.ID, 5, 126,13);
        nassauInv.store(conn);
        curacoInv.store(conn);

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

        Voyage voyage = new Voyage(cortez.ID, cortez.CURRENT_PORT, TMPDatabase.uniqueID());
        voyage.store(conn);

        MerchantInventory cortezInvSilk =
                new MerchantInventory(cortez.ID, silk.ID, 20);
        MerchantInventory cortezInvMeat =
                new MerchantInventory(cortez.ID, meat.ID, 10);
        cortezInvSilk.store(conn);
        cortezInvMeat.store(conn);

        return cortez.ID;
    }

    public static void main(String[] args) {
        TMPDatabase.reset(DB_URL+".mv.db");
        try (TMPDatabase db = new TMPDatabase(DB_URL)) {

            Connection conn = db.getConnection();
            db.createTables();
            int userID = generateDB(conn);

            Commands.displayHelp();

            Merchant cortez = Merchant.retrieve(userID, conn);
            UserIO.inputPrompt(cortez, conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
