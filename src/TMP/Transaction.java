/**
 * @author Michael Bianconi
 * @since 01/29/19
 *
 * Transactions are a simply log of what was sold where, to whom,
 * how much and for what price.
 */

package TMP;

import java.util.Objects;

public class Transaction {

    public enum Type {
        BUY,
        SELL,
        OTHER
    }

    private final Commodity commodity;
    private final Port port;
    private final Merchant merchant;
    private final Type type;
    private final int amount;
    private final int price;

    public Transaction(Commodity c, Port p, Merchant m, Type t, int a, int r) {
        this.commodity = c;
        this.port = p;
        this.merchant = m;
        this.type = t;
        this.amount = a;
        this.price = r;
    }

    public Commodity getCommodity() { return this.commodity; }
    public Port getPort() { return this.port; }
    public Merchant getMerchant() { return this.merchant; }
    public Type getType() { return this.type; }
    public int getAmount() { return this.amount; }
    public int getPrice() { return this.price; }
    public int hashCode() {
        return Objects.hash(commodity, port, merchant, type, amount, price);
    }
    public boolean equals(Object o)
    {
        if (!(o instanceof Transaction)) { return false; }
        Transaction t = (Transaction) o;
        return t.commodity.equals(this.commodity)
            && t.port.equals(this.port)
            && t.merchant.equals(this.merchant)
            && t.type == this.type
            && t.amount == this.amount
            && t.price == this.price;
    }

}
