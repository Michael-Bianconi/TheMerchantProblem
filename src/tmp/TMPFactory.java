package tmp;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The TMPFactory handles construction of TMPObjects.
 *
 * @author Michael Bianconi
 * @since 02-05-2019
 */
public class TMPFactory {

    /** Singleton class. */
    private TMPFactory() { /* ... */ }

    /**
     * @param type The type of TMPObject.
     * @return Returns the given TMPObject type's table name.
     */
    public static String tableName(String type) {
        switch (type) {

            case "COMMODITY": return "COMMODITIES";
            case "PORT": return "PORTS";
            case "PORT_INVENTORY": return "PORT_INVENTORIES";
            case "ROUTE": return "ROUTES";
            case "ROUTE_COST": return "ROUTE_COSTS";
            case "MERCHANT": return "MERCHANTS";
            case "MERCHANT_INVENTORY": return "MERCHANT_INVENTORIES";
            case "VOYAGE": return "VOYAGES";
            case "TRANSACTION": return "TRANSACTIONS";
            default:
                throw new IllegalArgumentException(
                        "CAN'T FIND TYPE " + type + "!");
        }
    }

    /**
     * @param type Type of the TMPObject.
     * @return Returns the SQL Command for creating the object's table.
     */
    public static String createTableString(String type) {

        switch (type) {

            case "COMMODITY":
                return "CREATE TABLE IF NOT EXISTS " + tableName(type) + "(" +
                        "ID     INTEGER PRIMARY KEY NOT NULL," +
                        "NAME   TEXT                NOT NULL," +
                        "WEIGHT REAL                NOT NULL);";

            case "PORT":
                return "CREATE TABLE IF NOT EXISTS " + tableName(type) + "(" +
                        "ID     INTEGER PRIMARY KEY NOT NULL," +
                        "NAME   TEXT                NOT NULL," +
                        "X      INTEGER             NOT NULL," +
                        "Y      INTEGER             NOT NULL);";

            case "PORT_INVENTORY":
                return "CREATE TABLE IF NOT EXISTS " + tableName(type) + "(" +
                        "ID             INTEGER PRIMARY KEY NOT NULL," +
                        "PORT_ID        INTEGER             NOT NULL," +
                        "COMMODITY_ID   INTEGER             NOT NULL," +
                        "ON_HAND        INTEGER             NOT NULL," +
                        "BUY_PRICE      INTEGER             NOT NULL," +
                        "SELL_PRICE     INTEGER             NOT NULL," +
                        "FOREIGN KEY(PORT_ID) REFERENCES " +
                        tableName("PORT") + "(ID)," +
                        "FOREIGN KEY(COMMODITY_ID) REFERENCES " +
                        tableName("COMMODITY") + "(ID));";

            case "ROUTE":
                return "CREATE TABLE IF NOT EXISTS " + tableName("ROUTE")
                        +"("+
                        "ID             INTEGER PRIMARY KEY NOT NULL," +
                        "START_PORT     INTEGER             NOT NULL," +
                        "END_PORT       INTEGER             NOT NULL," +
                        "FOREIGN KEY(START_PORT) REFERENCES " +
                        tableName("PORT") + "(ID)," +
                        "FOREIGN KEY(END_PORT) REFERENCES " +
                        tableName("PORT") + "(ID));";

            case "ROUTE_COST":
                return "CREATE TABLE IF NOT EXISTS " + tableName(type) + "(" +
                    "ID             INTEGER PRIMARY KEY NOT NULL," +
                    "ROUTE_ID        INTEGER             NOT NULL," +
                    "COMMODITY_ID   INTEGER             NOT NULL," +
                    "AMOUNT         INTEGER             NOT NULL," +
                    "FOREIGN KEY(ROUTE_ID) REFERENCES " +
                    tableName("ROUTE") + "(ID)," +
                    "FOREIGN KEY(COMMODITY_ID) REFERENCES " +
                    tableName("COMMODITY") + "(ID));";

            case "MERCHANT":

                return "CREATE TABLE IF NOT EXISTS " + tableName(type) + "(" +
                        "ID             INTEGER PRIMARY KEY NOT NULL," +
                        "NAME           TEXT                NOT NULL," +
                        "HOME_PORT      INTEGER             NOT NULL," +
                        "CURRENT_PORT   INTEGER             NOT NULL," +
                        "CAPACITY       INTEGER             NOT NULL," +
                        "GOLD           INTEGER             NOT NULL," +
                        "FOREIGN KEY(HOME_PORT) REFERENCES " +
                        tableName("PORT") + "(ID)," +
                        "FOREIGN KEY(CURRENT_PORT) REFERENCES " +
                        tableName("PORT") + "(ID));";

            case "MERCHANT_INVENTORY":

                return "CREATE TABLE IF NOT EXISTS " +
                        tableName("MERCHANT_INVENTORY") + "(" +
                    "ID             INTEGER PRIMARY KEY NOT NULL," +
                    "MERCHANT_ID    INTEGER             NOT NULL," +
                    "COMMODITY_ID   INTEGER             NOT NULL," +
                    "AMOUNT         INTEGER             NOT NULL," +
                    "FOREIGN KEY(MERCHANT_ID) REFERENCES " +
                    tableName("MERCHANT") + "(ID)," +
                    "FOREIGN KEY(COMMODITY_ID) REFERENCES " +
                    tableName("COMMODITY") + "(ID));";

            case "VOYAGE":

                return "CREATE TABLE IF NOT EXISTS " + tableName("VOYAGE")
                        + "(" +
                        "ID             INTEGER PRIMARY KEY NOT NULL," +
                        "MERCHANT_ID    INTEGER             NOT NULL," +
                        "PORT_ID        INTEGER             NOT NULL," +
                        "TIMESTAMP      INTEGER             NOT NULL," +
                        "FOREIGN KEY(MERCHANT_ID) REFERENCES " +
                        tableName("MERCHANT") + "(ID)," +
                        "FOREIGN KEY(PORT_ID) REFERENCES " +
                        tableName("PORT") + "(ID));";

            case "TRANSACTION":

                return "CREATE TABLE IF NOT EXISTS " + tableName("TRANSACTION")
                        + "(" +
                        "ID             INTEGER PRIMARY KEY NOT NULL," +
                        "VOYAGE_ID      INTEGER             NOT NULL," +
                        "COMMODITY_ID   INTEGER             NOT NULL," +
                        "AMOUNT         INTEGER             NOT NULL," +
                        "PRICE          INTEGER             NOT NULL," +
                        "FOREIGN KEY(VOYAGE_ID) REFERENCES " +
                        tableName("VOYAGE") + "(ID)," +
                        "FOREIGN KEY(COMMODITY_ID) REFERENCES " +
                        tableName("COMMODITY") + "(ID));";

            default:
                throw new IllegalArgumentException(
                        "CAN'T CREATE A TABLE FOR " + type + "!");
        }
    }
    /**
     * @param type Type of the TMPObject.
     * @return Returns the retrieve String of the TMPObject.
     */
    public static String selectString(String type) {

        return "SELECT * FROM " +tableName(type)+ " WHERE ID=%d";
    }

    /**
     * @param type Type of the TMPObject.
     * @return Returns the retrieveAll String for the TMPObject.
     */
    public static String selectAllString(String type) {
        return "SELECT * FROM " +tableName(type)+ ";";
    }

    /**
     * Constructs a new TMPObject.
     */
    public static TMPObject create(String type, ResultSet data)
            throws SQLException {

        switch (type) {

            case "COMMODITY":

                int comID = data.getInt("ID");
                String comName = data.getString("NAME");
                float comWeight = data.getFloat("WEIGHT");
                return new Commodity(comID,comName,comWeight);

            case "PORT":

                int portID = data.getInt("ID");
                String portName = data.getString("NAME");
                int portX = data.getInt("X");
                int portY = data.getInt("Y");
                return new Port(portID, portName, portX, portY);

            case "PORT_INVENTORY":

                int pInvID = data.getInt("ID");
                int pInvPortID = data.getInt("PORT_ID");
                int pInvComID = data.getInt("COMMODITY_ID");
                int pInvOnHand = data.getInt("ON_HAND");
                int pInvBuy = data.getInt("BUY_PRICE");
                int pInvSell = data.getInt("SELL_PRICE");
                return new PortInventory(
                    pInvID, pInvPortID, pInvComID,
                    pInvOnHand, pInvBuy, pInvSell);

            case "ROUTE":

                int routeID = data.getInt("ID");
                int routeStartID = data.getInt("START_PORT");
                int routeEndID = data.getInt("END_PORT");
                return new Route(routeID, routeStartID, routeEndID);

            case "ROUTE_COST":

                int routeCostID = data.getInt("ID");
                int routeCostRouteID = data.getInt("ROUTE_ID");
                int routeCostComID = data.getInt("COMMODITY_ID");
                int routeCostAmount = data.getInt("AMOUNT");
                return new RouteCost(
                        routeCostID,
                        routeCostRouteID,
                        routeCostComID,
                        routeCostAmount
                );

            case "MERCHANT":

                int merchantID = data.getInt("ID");
                String merchantName = data.getString("NAME");
                int merchantHome = data.getInt("HOME_PORT");
                int merchantCurrent = data.getInt("CURRENT_PORT");
                int merchantCapacity = data.getInt("CAPACITY");
                int merchantGold = data.getInt("GOLD");
                return new Merchant(
                        merchantID,
                        merchantName,
                        merchantHome,
                        merchantCurrent,
                        merchantCapacity,
                        merchantGold
                );

            case "MERCHANT_INVENTORY":

                int mInvID = data.getInt("ID");
                int mInvMerchantID = data.getInt("MERCHANT_ID");
                int mInvCommodityID = data.getInt("COMMODITY_ID");
                int mInvAmount = data.getInt("AMOUNT");
                return new MerchantInventory(
                        mInvID,
                        mInvMerchantID,
                        mInvCommodityID,
                        mInvAmount
                );

            case "VOYAGE":

                int voyageID = data.getInt("ID");
                int voyageMerchantID = data.getInt("MERCHANT_ID");
                int voyagePortID = data.getInt("PORT_ID");
                int voyageTime = data.getInt("TIMESTAMP");
                return new Voyage(
                        voyageID,
                        voyageMerchantID,
                        voyagePortID,
                        voyageTime
                );

            case "TRANSACTION":

                int transID = data.getInt("ID");
                int transVoyage = data.getInt("VOYAGE_ID");
                int transComID = data.getInt("COMMODITY_ID");
                int transAmount = data.getInt("AMOUNT");
                int transPrice = data.getInt("PRICE");
                return new Transaction(
                        transID,
                        transVoyage,
                        transComID,
                        transAmount,
                        transPrice
                );

            default:

                throw new IllegalArgumentException(
                        "CAN'T CREATE THIS " + type + "!");
        }
    }
}
