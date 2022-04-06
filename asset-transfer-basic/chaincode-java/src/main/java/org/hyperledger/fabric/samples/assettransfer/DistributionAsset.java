package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public class DistributionAsset {

    @Property()
    private final String owner;

    @Property()
    private final String salesId;

    @Property()
    private final String distributionId;

    @Property()
    private final String productId;

    @Property()
    private final int quantity;

    @Property()
    private final String shipper;

    @Property()
    private final String location;

    @Property()
    private final int shippingCost;

    public String getDistributionId() {
        return distributionId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public int getShippingCost() {
        return shippingCost;
    }

    public String getShipper() {
        return shipper;
    }

    public String getSalesId() {
        return salesId;
    }

    public String getOwner() {
        return owner;
    }

    public DistributionAsset(@JsonProperty("owner") final String owner, @JsonProperty("salesId") final String salesId,
                             @JsonProperty("distributionId") final String distributionId, @JsonProperty("productId") final String productId,
                             @JsonProperty("quantity") final int quantity, @JsonProperty("shipper") final String shipper,
                             @JsonProperty("location") final String location,
                             @JsonProperty("shippingCost") final int shippingCost) {
        this.owner = owner;
        this.salesId = salesId;
        this.distributionId = distributionId;
        this.productId = productId;
        this.quantity = quantity;
        this.shipper = shipper;
        this.location = location;
        this.shippingCost = shippingCost;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                "[owner=" + owner +
                ", salesId=" + salesId +
                ", distributionId=" + distributionId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", shipper=" + shipper +
                ", location=" + location +
                ", shippingCost=" + shippingCost +
                "]";
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DistributionAsset other = (DistributionAsset) o;

        return Objects.deepEquals(
                new String[] {getOwner(), getSalesId(), getDistributionId(), getProductId(), getShipper(), getLocation()},
                new String[] {other.getOwner(), other.getSalesId(), other.getDistributionId(), other.getProductId(), other.getShipper(), other.getLocation()})
                &&
                Objects.deepEquals(
                new int[] {getQuantity(), getShippingCost()},
                new int[] {other.getQuantity(), other.getShippingCost()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOwner(), getSalesId(), getDistributionId(), getProductId(), getQuantity(), getShipper(), getLocation(), getShippingCost());
    }
}
