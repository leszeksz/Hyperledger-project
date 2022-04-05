package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class SaleAsset {

    @Property()
    private final String saleID;

    @Property()
    private final String owner;

    @Property()
    private final String product;

    @Property()
    private final int quantity;

    @Property()
    private final String contractor;

    public String getSaleID() {
        return saleID;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getContractor() {
        return contractor;
    }

    public SaleAsset(@JsonProperty("saleId") final String saleID,@JsonProperty("owner") final String owner, @JsonProperty("product") final String product,@JsonProperty("quantity") final int quantity,@JsonProperty("contractor") final String contractor) {
        this.saleID = saleID;
        this.owner = owner;
        this.product = product;
        this.quantity = quantity;
        this.contractor = contractor;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        SaleAsset other = (SaleAsset) obj;

        return Objects.deepEquals(
                new String[] {getSaleID(),getOwner(), getContractor(), getProduct()},
                new String[] {other.getSaleID(),other.getOwner(), other.getContractor(), other.getProduct()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSaleID(),getOwner(), getContractor(),getProduct());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [saleID=" + saleID + ", owner=" + owner + ", product=" + product + ", quantity=" + quantity + ", contractor=" + contractor + "]";
    }

    public String getOwner() {
        return owner;
    }
}
