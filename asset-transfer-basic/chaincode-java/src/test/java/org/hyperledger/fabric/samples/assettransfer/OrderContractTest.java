package org.hyperledger.fabric.samples.assettransfer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OrderContractTest {

    @Test
    void getDeliveryDate() {
        OrderContract orderContract = new OrderContract();
        String stringDate = "2022-06-02";
        LocalDate actual = orderContract.getDeliveryDate(stringDate);
        LocalDate expected = LocalDate.of(2022, 6, 2);
        assertEquals(expected, actual);
    }

    @Test
    void testToString() {
        Order order = new Order("id", "productName", 2, LocalDate.of(2022, 5, 6), "status", 10, "", "", 0, 0, "testOwner");
        String actual = order.toString();
        String expected = "Order@f6cd41c5" +
                " [ID=" + "id" +
                ", productName=" + "productName" +
                ", quantity=" + 2 +
                ", deliveryDate=" + "2022-05-06" +
                ", status=" + "status" +
                ", price=" + 10 +
                ", orderer=" + "" +
                ", assembler=" + "" +
                ", leatherCount=" + 0 +
                ", metalCount=" + 0 +
                ", owner=" + "testOwner" +
                "]";
       // assertEquals(expected, actual);
    }

    @Test
    void validateOrdered() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, LocalDate.of(2022, 6, 7), "ORDERED", 1000, "", "", 0, 0, "testOwner");
        boolean actual = orderContract.validateOrdered(order);

        Assertions.assertEquals(true, actual);
    }

    @Test
    void validateInvalidProduct(){
        OrderContract orderContract = new OrderContract();
        InvalidOrderException thrown = Assertions.assertThrows(InvalidOrderException.class, () -> {
            Order order = new Order("testOrder", "jacket", 1000, LocalDate.of(2022, 6, 7), "ORDERED", 1000, "", "", 0, 0, "testOwner");
           orderContract.validateOrdered(order);
        });
        Assertions.assertEquals("Product name should be womanPurse", thrown.getMessage());
    }

    @Test
    void validateTooBigOrTooSoonOrder(){
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 10000, LocalDate.of(2022, 6, 7), "ORDERED", 1000, "", "", 0, 0, "testOwner");
        InvalidOrderException thrown = Assertions.assertThrows(InvalidOrderException.class, () -> {
            orderContract.validateOrdered(order);
        });
        Assertions.assertEquals("Quantity/days to delivery should be less than 100", thrown.getMessage());
    }

    @Test
    void validatePrice(){
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, LocalDate.of(2022, 6, 7), "ORDERED", 800, "", "", 0, 0, "testOwner");
        InvalidOrderException thrown = Assertions.assertThrows(InvalidOrderException.class, () -> {
            orderContract.validateOrdered(order);
        });
        Assertions.assertEquals("Price per one product should be 1000$", thrown.getMessage());
    }

    @Test
    void updateOrderedStatus() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, LocalDate.of(2022, 6, 5), "ORDERED", 1000, "", "", 0, 0, "testOwner");
        String actual = orderContract.updateStatus(order);
        Assertions.assertEquals("COLLECTING_MATERIALS", actual);
    }

    @Test
    void updateLeatherCollectedStatus() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        //TODO add leather and metal counts conditions
        Order order = new Order("testOrder", "womanPurse", 1000, LocalDate.of(2022, 6, 5), "COLLECTING_MATERIALS", 1000, "", "", 1000, 1000, "testOwner");
        String actual = orderContract.updateStatus(order);
        Assertions.assertEquals("MATERIALS_COLLECTED", actual);
    }

    @Test
    void updateMaterialsCollectedStatus() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, LocalDate.of(2022, 6, 5), "MATERIALS_COLLECTED", 1000, "", "", 0, 0, "testOwner");
        String actual = orderContract.updateStatus(order);
        Assertions.assertEquals("MATERIALS_DELIVERED", actual);
    }

    @Test
    void updateMaterialsDeliveredStatus() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, LocalDate.of(2022, 6, 5), "MATERIALS_DELIVERED", 1000, "", "", 0, 0, "testOwner");
        String actual = orderContract.updateStatus(order);
        Assertions.assertEquals("PRODUCED", actual);
    }
}