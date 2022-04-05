package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public class DistributionAsset {

    @Property()
    private final String distributionOwner;

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
    private final String deliveryLocation;

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

    public String getDeliveryLocation() {
        return deliveryLocation;
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

    public String getDistributionOwner() {
        return distributionOwner;
    }

    public DistributionAsset(@JsonProperty("distributionOwner") final String distributionOwner, @JsonProperty("salesId") final String salesId, @JsonProperty("distributionId") final String distributionId, @JsonProperty("productId") final String productId,
                             @JsonProperty("quantity") final int quantity, @JsonProperty("shipper") final String shipper,
                             @JsonProperty("deliveryLocation") final String deliveryLocation,
                             @JsonProperty("shippingCost") final int shippingCost) {
        this.distributionOwner = distributionOwner;
        this.salesId = salesId;
        this.distributionId = distributionId;
        this.productId = productId;
        this.quantity = quantity;
        this.shipper = shipper;
        this.deliveryLocation = deliveryLocation;
        this.shippingCost = shippingCost;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                "[distributionOwner='" + distributionOwner +
                ", salesId='" + salesId +
                ", distributionId='" + distributionId +
                ", productId='" + productId +
                ", quantity=" + quantity +
                ", shipper='" + shipper +
                ", deliveryLocation='" + deliveryLocation +
                ", shippingCost=" + shippingCost +
                "]";
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DistributionAsset other = (DistributionAsset) o;

        return Objects.deepEquals(
                new String[] {getDistributionOwner(), getSalesId(), getDistributionId(), getProductId(), String.valueOf(getQuantity()), getShipper(), getDeliveryLocation(), String.valueOf(getShippingCost())},
                new String[] {other.getDistributionOwner(), other.getSalesId(), other.getDistributionId(), other.getProductId(), String.valueOf(other.getQuantity()), other.getShipper(), other.getDeliveryLocation(), String.valueOf(other.getShippingCost())});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDistributionOwner(), getSalesId(), getDistributionId(), getProductId(), getQuantity(), getShipper(), getDeliveryLocation(), getShippingCost());
    }
}
