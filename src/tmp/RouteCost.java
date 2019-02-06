package tmp;

import data.TMPDatabase;

/**
 * Each Route incurs a RouteCost that Merchants must pay before travel.
 */
public class RouteCost extends TMPObject {

    /** ID of the Route. */
    public final int ID;

    /** ID of the associated route. */
    public final int ROUTE_ID;

    /** ID of the associated commodity. */
    public final int COMMODITY_ID;

    /** Amount that must be paid. */
    public final int AMOUNT;

    // Constructors ===========================================================

    /**
     * Constructs a new Route.
     * @param routeID ID of the Route.
     * @param commodityID ID of the associated Commodity.
     * @param amount Amount required to travel.
     */
    public RouteCost(int routeID, int commodityID, int amount) {
        this(TMPDatabase.uniqueID(), routeID, commodityID, amount);
    }

    /**
     * Constructs a new Route.
     * @param id ID of the Route.
     * @param routeID ID of the Route.
     * @param commodityID ID of the associated Commodity.
     * @param amount Amount required to travel.
     */
    public RouteCost(int id, int routeID, int commodityID, int amount) {
        this.ID = id;
        this.ROUTE_ID = routeID;
        this.COMMODITY_ID = commodityID;
        this.AMOUNT = amount;
    }

    /**
     * Returns the Route associated with this Route.
     *
     * @param db Connection to the database.
     * @return Returns the Route referenced by this PortInventory.
     */
    public Route retrieveRoute(TMPDatabase db) {

        return (Route) db.retrieve("ROUTE", ROUTE_ID);
    }

    /**
     * Returns the Commodity associated with this Route.
     *
     * @param db Connection to the database.
     * @return Returns the Commodity referenced by this RouteCost.
     */
    public Commodity retrieveCommodity(TMPDatabase db) {

        return (Commodity) db.retrieve("COMMODITY", COMMODITY_ID);
    }


    // TMPObject ==============================================================

    public int ID() {return ID;}

    public String storeString() {

        return "MERGE INTO " + TMPFactory.tableName("ROUTE_COST") +
                "(ID,ROUTE_ID,COMMODITY_ID," +
                "AMOUNT) " +
                "VALUES("+ID+","+ROUTE_ID+","+COMMODITY_ID+","+AMOUNT+");";
    }

    // Object =================================================================
    @Override
    public String toString() {

        return "[ROUTE COST]\t"+ID+"\t"+ROUTE_ID+"\t"+COMMODITY_ID+"\t"+AMOUNT;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RouteCost)) { return false; }
        RouteCost r = (RouteCost) o;
        return r.ID == ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }
}