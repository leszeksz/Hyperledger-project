package org.hyperledger.fabric.samples.assettransfer;

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
}