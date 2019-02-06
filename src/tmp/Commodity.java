package tmp;

import data.TMPDatabase;

/**
 * Commodities, such as Silk or Emerald, can be bought and sold at
 * {@link tmp.Port Ports}. Ports will only trade Commodities that
 * exist in their {@link tmp.PortInventory inventories}.
 *
 * Each {@link tmp.Merchant} may only hold so many Commodities,
 * determined by the collective weight of his
 * {@link tmp.MerchantInventory inventory} and his Capacity.
 *
 * When traveling between Ports using {@link tmp.Route Routes},
 * Merchants must consume all {@link tmp.RouteCost Route Costs}
 * associated with that Route.
 *
 * @author Michael Bianconi
 * @since 01-28-2019
 */
public class Commodity extends TMPObject {

    /** The unique identifier of this Commodity. */
    public final int ID;

    /** The name of the Commodity. */
    public final String NAME;

    /** The weight per unit of Commodity. */
    public final float WEIGHT;

    /**
     * Construct the Commodity with a guaranteed unique ID.
     *
     * @param name Name of the ID.
     * @param weight Weight of the commodity.
     */
    public Commodity(String name, float weight) {
        this(TMPDatabase.uniqueID(), name, weight);
    }

    /**
     * Construct the Commodity. This method should only be called
     * when retrieving from the database, to ensure the ID's uniqueness.
     *
     * @param id The ID of this commodity.
     * @param name The NAME of this commodity.
     * @param weight The WEIGHT of this commodity, per unit.
     */
    public Commodity(int id, String name, float weight) {
        this.ID = id;
        this.NAME = name;
        this.WEIGHT = weight;
    }


    // Object =================================================================
    /** @return Returns this Commodity's ID. */
    @Override
    public int hashCode() {return ID;}

    /** @return Compares the IDs of the Commodities. */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Commodity)) {return false;}
        Commodity c = (Commodity) o;
        return c.ID == this.ID;
    }

    /** @return NAME(ID) */
    @Override
    public String toString() {

        return NAME + "(ID=" + ID + ")";
    }

    // TMPObject ==============================================================
    public String storeString() {
        return  "MERGE INTO " + TMPFactory.tableName("COMMODITY") +
                "(ID,NAME,WEIGHT) KEY(ID) " +
                "VALUES("+ID+",'"+NAME+"',"+WEIGHT+");";
    }

    public int ID() {return ID;}
}
