package test;

import main.cmdline.UserIO;
import tmp.*;

import java.sql.Connection;
import java.sql.SQLException;

public class TradeDebug {

    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL =
            "jdbc:h2:~/Dev/projects/TheMerchantProblem/testdb";
    private static final String USER = "Mike";
    private static final String PASS = "";

    private static void generateDB(Connection conn)
    {
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

        Merchant cortez = new Merchant("Cortez", nassau.ID, nassau.ID, 600, 600);
        cortez.store(conn);

        MerchantInventory cortezInvSilk =
                new MerchantInventory(cortez.ID, silk.ID, 20);
        MerchantInventory cortezInvMeat =
                new MerchantInventory(cortez.ID, meat.ID, 100);
        cortezInvSilk.store(conn);
        cortezInvMeat.store(conn);
    }


    public static void main(String args[]) {
        try (TMPDatabase db = new TMPDatabase(DB_URL)) {

            Connection conn = db.getConnection();
            db.createTables();
            generateDB(conn);
            Merchant cortez = Merchant.retrieve(0,conn);
            Port current = cortez.retrieveCurrentPort(conn);

            UserIO.commandMerchantDisplay(cortez, conn);
            UserIO.commandPortDisplay(current, conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
