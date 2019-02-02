package test;

import TMP.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class basictest {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/Dev/projects/TheMerchantProblem/testdb";
    static final String USER = "Mike";
    static final String PASS = "";

    public static void main(String args[])
    {
        try {
            Class.forName(JDBC_DRIVER);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try (Connection conn = DriverManager.getConnection(DB_URL,USER,PASS)) {

            Commodity.createTable(conn);
            Commodity commodityList[] = {
                    new Commodity(0, "Gold", 0),
                    new Commodity(1, "Meat", 1),
                    new Commodity(2,"Furs",5.7f)
            };
            for (Commodity c : commodityList) {
                c.store(conn);
            }

            Port.createTable(conn);
            Port portList[] = {
                    new Port(0,"Nassau",0,0),
                    new Port(1,"San Juan",0,1),
                    new Port(2,"Antigua",1,0)
            };
            for (Port p : portList)
            {
                p.store(conn);
            }

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

            RouteCost.createTable(conn);
            RouteCost r0 = new RouteCost(0,0,0,100);
            RouteCost r1 = new RouteCost(1,1,1,5);
            RouteCost r2 = new RouteCost(2,1,2,3);
            RouteCost r3 = new RouteCost(3,2,0,50);
            r0.store(conn);
            r1.store(conn);
            r2.store(conn);
            r3.store(conn);

            Merchant.createTable(conn);
            Merchant merchantList[] = {
                    new Merchant(0, "Mike", 0,0,100),
                    new Merchant(1, "Niamh",1,0,420),
                    new Merchant(2,"Cortez", 1,2,24)
            };
            for (Merchant m : merchantList)
            {
                m.store(conn);
            }

            MerchantInventory.createTable(conn);
            MerchantInventory mInvList[] = {
                    new MerchantInventory(0,0,0,50),
                    new MerchantInventory(1,0,1,25),
                    new MerchantInventory(2,1,0,30)
            };
            for (MerchantInventory m : mInvList)
            {
                m.store(conn);
            }

            Voyage.createTable(conn);
            Voyage voyageList[] = {
                    new Voyage(0,0,0,0),
                    new Voyage(1,0,2,1),
                    new Voyage(2,0,1,2),
                    new Voyage(3,1,2,3)
            };
            for(Voyage v : voyageList) {v.store(conn);}
            Map<Integer, Voyage> voyages = Merchant.retrieve(0,conn).retrieveAllVoyages(conn);

            for (Map.Entry<Integer, Voyage> e : voyages.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, MerchantInventory> minvs =
                    MerchantInventory.retrieveAll(conn);

            Map<Integer, Commodity> commodities = Commodity.retrieveAll(conn);

            for (Map.Entry<Integer, MerchantInventory> e : minvs.entrySet()) {
                System.out.println(e.getValue());
            }

            for (Map.Entry<Integer, Commodity> e : commodities.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, Port> ports = Port.retrieveAll(conn);

            for (Map.Entry<Integer, Port> e : ports.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, PortInventory> invs = PortInventory.retrieveAll(conn);

            for (Map.Entry<Integer, PortInventory> e : invs.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, Route> routes = Route.retrieveAll(conn);

            for (Map.Entry<Integer, Route> e : routes.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, RouteCost> costs = NtoA.retrieveRouteCosts(conn);

            for (Map.Entry<Integer, RouteCost> e : costs.entrySet()) {
                System.out.println(e.getValue());
            }

            Map<Integer, Merchant> merchant = Merchant.retrieveAll(conn);

            for (Map.Entry<Integer, Merchant> e : merchant.entrySet()) {
                System.out.println(e.getValue());
            }

        } catch(Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        }
    }
}
