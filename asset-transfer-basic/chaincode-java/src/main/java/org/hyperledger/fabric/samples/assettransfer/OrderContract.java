package org.hyperledger.fabric.samples.assettransfer;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Contract(
        name = "basic",
        info = @Info(contact = @Contact( name = "order")))
@Default
public class OrderContract implements ContractInterface {
    private final Genson genson = new Genson();

    private enum Errors {
        ORDER_NOT_FOUND,
        ORDER_ALREADY_EXISTS
    }

    /**
     * Creates a new order on the ledger.
     *
     * @param ctx the transaction context
     * @param ID the ID of the new order
     * @param productName name of ordered product
     * @param quantity quantity of ordered product
     * @param deliveryDate date of delivery
     * @param status status of order
     * @param price price of product
     * @param orderer ordering company
     * @param assembler company assembles ordered product
     * @return the created order
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Order CreateOrder(final Context ctx, final String ID, final String productName, final int quantity, final String deliveryDate, final String status,
                             final int price, final String orderer, final String assembler) {
        ChaincodeStub stub = ctx.getStub();

        checkAssetAlreadyExists(ctx, ID, "Order %s already exists");

        Order order = new Order(ID, productName, quantity, getDeliveryDate(deliveryDate), status, price, orderer, assembler);
        serialize(stub, order, ID);
        return order;
    }

    public LocalDate getDeliveryDate(String deliveryDate) {
        String[] splittedDate = deliveryDate.split("-");
        LocalDate date = LocalDate.of(Integer.valueOf(splittedDate[0]), Integer.valueOf(splittedDate[1]), Integer.valueOf(splittedDate[2]));
        return date;
    }

    /**
     * Retrieves an order with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param ID the ID of the order
     * @return the order found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Order ReadOrder(final Context ctx, final String ID) {
        ChaincodeStub stub = ctx.getStub();
        String orderJSON = stub.getStringState(ID);

        checkIfOrderExists(orderJSON == null || orderJSON.isEmpty(), ID);

        Order order = genson.deserialize(orderJSON, Order.class);
        return order;
    }

    /**
     * Updates the properties of an order on the ledger.
     *
     * @param ctx the transaction context
     * @param ID the ID of the order being updated
     * @param productName name of ordered product
     * @param quantity quantity of ordered product
     * @param deliveryDate date of delivery
     * @param status status of the order being updated
     * @param price price of product
     * @param orderer ordering company
     * @param assembler company assembles ordered product
     * @return the updated order
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Order UpdateOrder(final Context ctx, final String ID, final String productName, final int quantity, final String deliveryDate, final String status,
                             final int price, final String orderer, final String assembler) throws Exception {
        ChaincodeStub stub = ctx.getStub();
        Order.OrderStatuses updatedStatus = null;

        //TODO tworzenie z deliveryDate obiektu typu Date

        checkIfOrderExists(!OrderExists(ctx, ID), ID);
        if(status.equals(Order.OrderStatuses.ORDERED)){
            validateOrdered(productName, quantity, getDeliveryDate(deliveryDate), price);
            updatedStatus = Order.OrderStatuses.LEATHER_COLLECTED;
//        } else if(status.equals(Order.OrderStatuses.LEATHER_COLLECTED)) {
//            //validateCollected1();
//            updatedStatus = Order.OrderStatuses.LEATHER_COLLECTED;
//        } else if(status.equals(Order.OrderStatuses.MATERIALS_COLLECTED)) {
//            //validateCollected2();
//        } else if(status.equals(Order.OrderStatuses.MATERIALS_DELIVERED)) {
//            //validateDelivered();
//            updatedStatus = Order.OrderStatuses.PRODUCED;
        } else {
            throw new Exception("Invalid status provided");
        }

        if(updatedStatus == null){
                throw new Exception("No status provided");
        }

        Order order = new Order(ID, productName, quantity, getDeliveryDate(deliveryDate), updatedStatus.toString(), price, orderer, assembler);
        serialize(stub, order, ID);
        return order;
    }

    private void validateOrdered(String productName, int quantity, LocalDate deliveryDate, int price) throws Exception {
        if(!productName.equals("Woman purse")){
            throw new Exception("Product name should be Woman Purse");
        }
        if(price != 1000){
            throw new Exception("Price per one product should be 1000$");
        }
        long daysToDelivery = LocalDate.of(deliveryDate.getYear(), deliveryDate.getMonth(), deliveryDate.getDayOfMonth()).toEpochDay() - LocalDate.now().toEpochDay();
        if(quantity / daysToDelivery > 0.01){
            throw new Exception("Quantity/days to delivery should be less than 0.01");
        }
    }

    /**
     * Checks the existence of the order on the ledger
     *
     * @param ctx the transaction context
     * @param ID the ID of the order being checked
     * @return boolean indicating the existence of the order
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean OrderExists(final Context ctx, final String ID) {
        ChaincodeStub stub = ctx.getStub();
        String orderJSON = stub.getStringState(ID);

        return (orderJSON != null && !orderJSON.isEmpty());
    }

    /**
     * Retrieves all orders from the ledger.
     *
     * @param ctx the transaction context
     * @return array of all orders found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllOrders(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Order> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Order order = genson.deserialize(result.getStringValue(), Order.class);
            System.out.println(order);
            queryResults.add(order);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    private void checkAssetAlreadyExists(Context ctx, String assetId, String format) {
        if (OrderExists(ctx, assetId)) {
            String errorMessage = String.format(format, assetId);
            throw new ChaincodeException(errorMessage, Errors.ORDER_ALREADY_EXISTS.toString());
        }
    }

    private void checkIfOrderExists(boolean ctx, String assetId) {
        if (ctx){
            String errorMessage = String.format("Order %s does not exist", assetId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, Errors.ORDER_NOT_FOUND.toString());
        }
    }

    private void serialize(ChaincodeStub stub, Object obj, String id){
        String sortedJson = genson.serialize(obj);
        stub.putStringState(id,sortedJson);
    }
}
