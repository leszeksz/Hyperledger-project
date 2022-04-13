package org.hyperledger.fabric.samples.assettransfer;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderContractTest {
    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockAssetResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> assetList;

        MockAssetResultsIterator() {
            super();

            assetList = new ArrayList<>();

            assetList.add(new OrderContractTest.MockKeyValue("order1",
                    "{\"ID\":\"order1\", \"productName\":\"womanPurse\",\"quantity\":300, \"deliveryDate\":\"2022-05-06\", \"status\":\"ORDERED\",\"price\":1000, \"leatherCount\":300,\"metalCount\":0}"));
            assetList.add(new OrderContractTest.MockKeyValue("order2",
                    "{\"ID\":\"order2\", \"productName\":\"womanPurse\",\"quantity\":300, \"deliveryDate\":\"2022-05-06\", \"status\":\"ORDERED\",\"price\":1000, \"leatherCount\":300,\"metalCount\":0}"));
            assetList.add(new OrderContractTest.MockKeyValue("order3",
                    "{\"ID\":\"order3\", \"productName\":\"womanPurse\",\"quantity\":300, \"deliveryDate\":\"2022-05-06\", \"status\":\"ORDERED\",\"price\":1000, \"leatherCount\":300,\"metalCount\":0}"));
        }

        @Override
        public Iterator<KeyValue> iterator() {
            return assetList.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }

    @Test
    public void invokeUnknownTransaction() {
        OrderContract contract = new OrderContract();
        Context ctx = mock(Context.class);

        Throwable thrown = catchThrowable(() -> {
            contract.unknownTransaction(ctx);
        });

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                .hasMessage("Undefined contract method called");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);

        verifyZeroInteractions(ctx);
    }

    @Nested
    class InvokeReadOrderTransaction {

        @Test
        public void whenAssetExists() {
            OrderContract contract = new OrderContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            //TODO
            when(stub.getStringState("order1"))
                    .thenReturn("{\"iD\":\"order1\", \"productName\":\"womanPurse\",\"quantity\":300, \"deliveryDate\":\"2022-05-06\", \"status\":\"ORDERED\",\"price\":1000, \"leatherCount\":0,\"metalCount\":0}");

            Order order = contract.ReadOrder(ctx, "order1");

            assertThat(order).isEqualTo(new Order("order1", "womanPurse", 300, "2022-05-06", "ORDERED", 1000,  0, 0));
        }

        @Test
        public void whenAssetDoesNotExist() {
            OrderContract contract = new OrderContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("order1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.ReadOrder(ctx, "order1");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Order order1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ORDER_NOT_FOUND".getBytes());
        }
    }

    @Nested
    class InvokeCreateOrderTransaction {

        @Test
        public void whenAssetExists() {
            OrderContract contract = new OrderContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("order2"))
                    .thenReturn("{\"ID\":\"order2, \"productName\":300,\"quantity\":\"300\", \"deliveryDate\":\"2022-05-06, \"status\":ORDERED,\"price\":\"1000\", \"leatherCount\":300,\"metalCount\":\"0\"}");

            Throwable thrown = catchThrowable(() -> {
                contract.CreateOrder(ctx, "order2", "productName", 2, "2022-05-06", "status", 10,  0, 0);
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Order order2 already exists");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ORDER_ALREADY_EXISTS".getBytes());
        }

        @Test
        public void whenAssetDoesNotExist() {
            OrderContract contract = new OrderContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("order1")).thenReturn("");

            Order asset = contract.CreateOrder(ctx, "order1", "productName", 2, "2022-05-06", "status", 10,  0, 0);

            assertThat(asset).isEqualTo(new Order("order1", "productName", 2, "2022-05-06", "status", 10,  0, 0));
        }
    }

    @Test
    void invokeGetAllAssetsTransaction() {
        OrderContract contract = new OrderContract();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStateByRange("", "")).thenReturn(new MockAssetResultsIterator());

        String assets = contract.GetAllOrders(ctx);

//        assertThat(assets).isEqualTo("[{\"ID\":\"order1\", \"productName\":\"womanPurse\",\"quantity\":300, \"deliveryDate\":\"2022-05-06\", \"status\":ORDERED\",\"price\":1000, \"leatherCount\":300,\"metalCount\":0},"
//                + "{\"ID\":\"order2\", \"productName\":\"womanPurse\",\"quantity\":300, \"deliveryDate\":\"2022-05-06\", \"status\":\"ORDERED\",\"price\":1000, \"leatherCount\":300,\"metalCount\":0},"
//                + "{\"ID\":\"order3\", \"productName\":\"womanPurse\",\"quantity\":300, \"deliveryDate\":\"2022-05-06\", \"status\":\"ORDERED\",\"price\":1000, \"leatherCount\":300,\"metalCount\":0},");
    }

    @Nested
    class UpdateOrderTransaction {

        @Test
        public void whenAssetExists() {
            OrderContract contract = new OrderContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("order1"))
                    .thenReturn("{\"ID\":\"order1\", \"productName\":\"womanPurse\",\"quantity\":300, \"deliveryDate\":\"2022-07-06\", \"status\":\"ORDERED\",\"price\":1000, \"leatherCount\":0,\"metalCount\":0}");

            Order asset = contract.UpdateOrder(ctx, "order1", "womanPurse", 300, "2022-05-06", "ORDERED", 1000,  100, 0);

            assertThat(asset).isEqualTo(new Order("order1", "womanPurse", 300, "2022-05-06", "COLLECTING_MATERIALS", 1000,  100, 0));
        }

        @Test
        public void whenAssetDoesNotExist() {
            OrderContract contract = new OrderContract();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("order1")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.UpdateOrder(ctx, "order1", "productName", 2, "2022-05-06", "status", 10,  0, 0);
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Order order1 does not exist");
            assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo("ORDER_NOT_FOUND".getBytes());
        }
    }

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
        Order order = new Order("order1", "productName", 2, "2022-05-06", "status", 10,0, 0);
        String actual = order.toString();
        String expected = "Order@f6cd41c5" +
                " [ID=" + "order1" +
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
//        assertEquals(expected, actual);
    }

    @Test
    void validateOrdered() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, "2022-06-07", "ORDERED", 1000, 0, 0);
        boolean actual = orderContract.validateOrdered(order);

        Assertions.assertEquals(true, actual);
    }

    @Test
    void validateInvalidProduct(){
        OrderContract orderContract = new OrderContract();
        InvalidOrderException thrown = Assertions.assertThrows(InvalidOrderException.class, () -> {
            Order order = new Order("testOrder", "jacket", 1000, "2022-06-07", "ORDERED", 1000, 0, 0);
           orderContract.validateOrdered(order);
        });
        Assertions.assertEquals("We do not have such product in our offer", thrown.getMessage());
    }

    @Test
    void validateTooBigOrTooSoonOrder(){
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 10000, "2022-06-07", "ORDERED", 1000, 0, 0);
        InvalidOrderException thrown = Assertions.assertThrows(InvalidOrderException.class, () -> {
            orderContract.validateOrdered(order);
        });
        Assertions.assertEquals("Quantity/days to delivery should be less than 100", thrown.getMessage());
    }

    @Test
    void validatePrice(){
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, "2022-06-07", "ORDERED", 800,  0, 0);
        InvalidOrderException thrown = Assertions.assertThrows(InvalidOrderException.class, () -> {
            orderContract.validateOrdered(order);
        });
        Assertions.assertEquals("Price per one product should be 1000$", thrown.getMessage());
    }

    @Test
    void updateOrderedStatus() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, "2022-06-05", "ORDERED", 1000, 0, 0);
        String actual = orderContract.updateStatus(order);
        Assertions.assertEquals("COLLECTING_MATERIALS", actual);
    }

    @Test
    void updateLeatherCollectedStatus() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        //TODO add leather and metal counts conditions
        Order order = new Order("testOrder", "womanPurse", 1000, "2022-06-05", "COLLECTING_MATERIALS", 1000, 1000, 1000);
        String actual = orderContract.updateStatus(order);
        Assertions.assertEquals("MATERIALS_COLLECTED", actual);
    }

    @Test
    void updateMaterialsCollectedStatus() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, "2022-06-05", "MATERIALS_COLLECTED", 1000,  0, 0);
        String actual = orderContract.updateStatus(order);
        Assertions.assertEquals("MATERIALS_DELIVERED", actual);
    }

    @Test
    void updateMaterialsDeliveredStatus() throws InvalidOrderException {
        OrderContract orderContract = new OrderContract();
        Order order = new Order("testOrder", "womanPurse", 1000, "2022-06-05", "MATERIALS_DELIVERED", 1000,  0, 0);
        String actual = orderContract.updateStatus(order);
        Assertions.assertEquals("PRODUCED", actual);
    }
}