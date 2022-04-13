package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Order {

    private static final String ID_PROP = "iD";
    private static final String PRODUCT_NAME_PROP = "productName";
    private static final String QUANTITY_PROP = "quantity";
    private static final String DELIVERY_DATE_PROP = "deliveryDate";
    private static final String STATUS_PROP = "status";
    private static final String PRICE_PROP = "price";
    private static final String LEATHER_COUNT_PROP = "leatherCount";
    private static final String METAL_COUNT_PROP = "metalCount";

    @Property()
    private final String ID;

    @Property()
    private final String productName;

    @Property()
    private final int quantity;

    @Property()
    private final String deliveryDate;

    @Property()
    private final String status;

    @Property()
    private final int price;

    @Property()
    private final int leatherCount;

    @Property()
    private final int metalCount;

    public Order(@JsonProperty(ID_PROP) final String ID, @JsonProperty(PRODUCT_NAME_PROP) final String productName, @JsonProperty(QUANTITY_PROP) final int quantity,
                 @JsonProperty(DELIVERY_DATE_PROP) final String deliveryDate, @JsonProperty(STATUS_PROP) final String status, @JsonProperty(PRICE_PROP) final int price,
                 @JsonProperty(LEATHER_COUNT_PROP) final int leatherCount, @JsonProperty(METAL_COUNT_PROP) final int metalCount) {
        this.ID = ID;
        this.productName = productName;
        this.quantity = quantity;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.price = price;
        this.leatherCount = leatherCount;
        this.metalCount = metalCount;

    }

    public String getID() {
        return ID;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDeliveryDate() { return deliveryDate; }

    public String getStatus() {
        return status;
    }

    public int getPrice() {
        return price;
    }

    public int getLeatherCount() { return leatherCount; }

    public int getMetalCount() { return metalCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.deepEquals(
                new String[] {getID(), getProductName(), getStatus(), getDeliveryDate()},
                new String[] {order.getID(), order.getProductName(), order.getStatus(), order.getDeliveryDate()})
                &&
                Objects.deepEquals(
                        new int[] {getQuantity(), getPrice(), getLeatherCount(), getMetalCount()},
                        new int[] {order.getQuantity(), order.getPrice(), order.getLeatherCount(), order.getMetalCount()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getProductName(), getQuantity(), getDeliveryDate(), getStatus(), getPrice(), getLeatherCount(), getMetalCount());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                " [" + ID_PROP + "=" + ID +
                ", " + PRODUCT_NAME_PROP + "=" + productName +
                ", " + QUANTITY_PROP + "=" + quantity +
                ", " + DELIVERY_DATE_PROP + "=" + deliveryDate +
                ", " + STATUS_PROP + "=" + status +
                ", " + PRICE_PROP + "=" + price +
                ", " + LEATHER_COUNT_PROP + "=" + leatherCount +
                ", " + METAL_COUNT_PROP + "=" + metalCount +
                "]";
    }
}
