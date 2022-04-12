package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDate;
import java.util.Objects;

@DataType()
public final class Order {

    private static final String ID_PROP = "ID";
    private static final String PRODUCT_NAME_PROP = "productName";
    private static final String QUANTITY_PROP = "quantity";
    private static final String DELIVERY_DATE_PROP = "deliveryDate";
    private static final String STATUS_PROP = "status";
    private static final String PRICE_PROP = "price";
    private static final String ORDERER_PROP = "orderer";
    private static final String ASSEMBLER_PROP = "assembler";
    private static final String LEATHER_COUNT_PROP = "leatherCount";
    private static final String METAL_COUNT_PROP = "metalCount";
    private static final String OWNER_PROP = "owner";

    public enum OrderStatuses {
        ORDERED,
        COLLECTING_MATERIALS,
        MATERIALS_COLLECTED,
        MATERIALS_DELIVERED,
        PRODUCED
    }

    @Property()
    private final String ID;

    @Property()
    private final String productName;

    @Property()
    private final int quantity;

    @Property()
    private final LocalDate deliveryDate;

    @Property()
    private final String status;

    @Property()
    private final int price;

    @Property()
    private final String orderer;

    @Property()
    private final String assembler;

    @Property()
    private final int leatherCount;

    @Property()
    private final int metalCount;

    @Property()
    private final String owner;

    public Order(@JsonProperty(ID_PROP) final String ID, @JsonProperty(PRODUCT_NAME_PROP) final String productName, @JsonProperty(QUANTITY_PROP) final int quantity,
                 @JsonProperty(DELIVERY_DATE_PROP) final LocalDate deliveryDate, @JsonProperty(STATUS_PROP) final String status, @JsonProperty(PRICE_PROP) final int price,
                 @JsonProperty(ORDERER_PROP) final String orderer, @JsonProperty(ASSEMBLER_PROP) final String assembler, @JsonProperty(LEATHER_COUNT_PROP) final int leatherCount,
                 @JsonProperty(METAL_COUNT_PROP) final int metalCount, @JsonProperty(OWNER_PROP) final String owner) {
        this.ID = ID;
        this.productName = productName;
        this.quantity = quantity;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.price = price;
        this.orderer = orderer;
        this.assembler = assembler;
        this.leatherCount = leatherCount;
        this.metalCount = metalCount;
        this.owner = owner;

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

    public LocalDate getDeliveryDate() { return deliveryDate; }

    public String getStatus() {
        return status;
    }

    public int getPrice() {
        return price;
    }

    public String getOrderer() {
        return orderer;
    }

    public String getAssembler() {
        return assembler;
    }

    public int getLeatherCount() { return leatherCount; }

    public int getMetalCount() { return metalCount; }

    public String getOwner() { return owner; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.deepEquals(
                new String[] {getID(), getProductName(), getStatus(), getOrderer(), getAssembler(), getOwner()},
                new String[] {order.getID(), order.getProductName(), order.getStatus(), order.getOrderer(), order.getAssembler(), order.getOwner()})
                &&
                Objects.deepEquals(
                        new int[] {getQuantity(), getPrice(), getLeatherCount(), getMetalCount()},
                        new int[] {order.getQuantity(), order.getPrice(), order.getLeatherCount(), order.getMetalCount()})
                &&
                Objects.deepEquals(
                        new LocalDate[] {getDeliveryDate()},
                        new LocalDate[] {order.getDeliveryDate()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getProductName(), getQuantity(), getDeliveryDate(), getStatus(), getPrice(), getOrderer(), getAssembler(), getLeatherCount(),
                getMetalCount(), getOwner());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                " [ID=" + ID +
                ", " + ID_PROP + "=" + productName +
                ", " + QUANTITY_PROP + "=" + quantity +
                ", " + DELIVERY_DATE_PROP + "=" + deliveryDate +
                ", " + STATUS_PROP + "=" + status +
                ", " + PRICE_PROP + "=" + price +
                ", " + ORDERER_PROP + "=" + orderer +
                ", " + ASSEMBLER_PROP + "=" + assembler +
                ", " + LEATHER_COUNT_PROP + "=" + leatherCount +
                ", " + METAL_COUNT_PROP + "=" + metalCount +
                ", " + OWNER_PROP + "=" + owner +
                "]";
    }
}
