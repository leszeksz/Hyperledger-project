package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.LocalDate;
import java.util.Objects;

@DataType()
public final class Order {

    public enum OrderStatuses {
        ORDERED,
        LEATHER_COLLECTED,
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

    public Order(@JsonProperty("ID") final String ID, @JsonProperty("productName") final String productName, @JsonProperty("quantity") final int quantity,
                 @JsonProperty("deliveryDate") final LocalDate deliveryDate, @JsonProperty("status") final String status, @JsonProperty("price") final int price,
                 @JsonProperty("orderer") final String orderer, @JsonProperty("assembler") final String assembler) {
        this.ID = ID;
        this.productName = productName;
        this.quantity = quantity;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.price = price;
        this.orderer = orderer;
        this.assembler = assembler;
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

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.deepEquals(
                new String[] {getID(), getProductName(), getStatus(), getOrderer(), getAssembler()},
                new String[] {order.getID(), order.getProductName(), order.getStatus(), order.getOrderer(), order.getAssembler()})
                &&
                Objects.deepEquals(
                        new int[] {getQuantity(), getPrice()},
                        new int[] {order.getQuantity(), order.getPrice()})
                &&
                Objects.deepEquals(
                        new LocalDate[] {getDeliveryDate()},
                        new LocalDate[] {order.getDeliveryDate()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getProductName(), getQuantity(), getDeliveryDate(), getStatus(), getPrice(), getOrderer(), getAssembler());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                " [productID=" + ID +
                ", productName=" + productName +
                ", quantity=" + quantity +
                ", deliveryDate=" + deliveryDate +
                ", status=" + status +
                ", price=" + price +
                ", orderer=" + orderer +
                ", assembler=" + assembler +
                "]";
    }
}