package test;

import TMP.Commodity;
import TMP.Port;
import TMP.PortInventory;
import TMP.Route;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class basictest {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/DEVELOPMENT/projects/TheMerchantProblem/testdb";
    static final String USER = "Mike";
    static final String PASS = "";

    public static void main(String args[])
    {
        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)) {
            Class.forName(JDBC_DRIVER);

            Commodity.createTable(conn);
            Commodity gold = new Commodity(0, "Gold", 0);
            Commodity meat = new Commodity(1, "Meat", 1);
            Commodity furs = new Commodity(2,"Furs",5.7f);
            gold.store(conn);
            meat.store(conn);
            furs.store(conn);

            Port.createTable(conn);
            Port nassau = new Port(0,"Nassau");
            Port sanJuan = new Port(1,"San Juan");
            Port antigua = new Port(2,"Antigua");
            nassau.store(conn);
            sanJuan.store(conn);
            antigua.store(conn);

            PortInventory.createTable(conn);
            PortInventory nassauMeat = new PortInventory(0,0,1,10,15,100);
            PortInventory nassauFurs = new PortInventory(1,0,2,1,222,101);
            PortInventory sanJuanGold = new PortInventory(2,1,0,122,0,0);
            PortInventory antiguaFurs = new PortInventory(3,2,2,1,2910,6);
            nassauMeat.store(conn);
            nassauFurs.store(conn);
            sanJuanGold.store(conn);
            antiguaFurs.store(conn);

            Route.createTable(conn);
            Route NtoS = new Route(0,0,1);
            Route NtoA = new Route(1,0,2);
            Route StoA = new Route(2,1,2);
            Route AtoN = new Route(3,2,0);
            NtoS.store(conn);
            NtoA.store(conn);
            StoA.store(conn);
            AtoN.store(conn);

            Map<Integer, Commodity> commodities = Commodity.retrieveAll(conn);

            for (Map.Entry<Integer, Commodity> e : commodities.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, Port> ports = Port.retrieveAll(conn);

            for (Map.Entry<Integer, Port> e : ports.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, PortInventory> invs = nassau.retrievePortInventories(conn);

            for (Map.Entry<Integer, PortInventory> e : invs.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, Route> routes = Route.retrieveAll(conn);

            for (Map.Entry<Integer, Route> e : routes.entrySet()) {
                System.out.println(e.getValue());
            }

        } catch(Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        }
    }
}
