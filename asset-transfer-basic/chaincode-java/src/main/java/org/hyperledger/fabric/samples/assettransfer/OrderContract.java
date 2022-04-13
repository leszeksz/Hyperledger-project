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
import java.util.List;
import java.util.Objects;

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

    public enum OrderStatuses {
        ORDERED,
        COLLECTING_MATERIALS,
        MATERIALS_COLLECTED,
        MATERIALS_DELIVERED,
        PRODUCED
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
     * @return the created order
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Order CreateOrder(final Context ctx, final String ID, final String productName, final int quantity, final String deliveryDate, final String status,
                             final int price, final int leatherCount, final int metalCount) {
        ChaincodeStub stub = ctx.getStub();

        checkAssetAlreadyExists(ctx, ID);

        Order order = new Order(ID, productName, quantity, deliveryDate, status, price, leatherCount, metalCount);
        serialize(stub, order, ID);
        return order;
    }

    public LocalDate getDeliveryDate(String deliveryDate) {
        String[] splittedDate = deliveryDate.split("-");
        return LocalDate.of(Integer.parseInt(splittedDate[0]), Integer.parseInt(splittedDate[1]), Integer.parseInt(splittedDate[2]));
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

        return genson.deserialize(Objects.requireNonNull(orderJSON), Order.class);
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
     * @return the updated order
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Order UpdateOrder(final Context ctx, final String ID, final String productName, final int quantity, final String deliveryDate, final String status,
                             final int price, final int leatherCount, final int metalCount) {
        ChaincodeStub stub = ctx.getStub();

        checkIfOrderExists(!OrderExists(ctx, ID), ID);
        String updatedStatus = null;
        try {
            updatedStatus = updateStatus(new Order(ID, productName, quantity, deliveryDate, status, price, leatherCount, metalCount));
        } catch (InvalidOrderException e) {
            e.printStackTrace();
        }

        Order order = new Order(ID, productName, quantity, deliveryDate, updatedStatus, price, leatherCount, metalCount);
        serialize(stub, order, ID);
        return order;
    }

    public String updateStatus(Order order) throws InvalidOrderException {
        String updatedStatus = null;
        if(order.getStatus().equals(OrderStatuses.ORDERED.toString())){
            validateOrdered(order);
            updatedStatus = OrderStatuses.COLLECTING_MATERIALS.toString();
        } else if(order.getStatus().equals(OrderStatuses.COLLECTING_MATERIALS.toString()) && areMaterialsCollected(order)) {
            //TODO status not updates
            validateCollecting(order);
            updatedStatus = OrderStatuses.MATERIALS_COLLECTED.toString();
        } else if(order.getStatus().equals(OrderStatuses.COLLECTING_MATERIALS.toString()) && !areMaterialsCollected(order)){
            validateCollecting(order);
        } else if(order.getStatus().equals(OrderStatuses.MATERIALS_COLLECTED.toString())) {
            validateCollected(order);
            updatedStatus = OrderStatuses.MATERIALS_DELIVERED.toString();
        } else if(order.getStatus().equals(OrderStatuses.MATERIALS_DELIVERED.toString())) {
            validateDelivered(order);
            updatedStatus = OrderStatuses.PRODUCED.toString();
        } else {
            throw new InvalidOrderException("Invalid status provided");
        }
        return updatedStatus;
    }

    private boolean areMaterialsCollected(Order order) {
        return order.getLeatherCount() == order.getQuantity() && order.getMetalCount() == order.getQuantity();
    }

    public boolean validateOrdered(Order order) throws InvalidOrderException {
        if(!order.getProductName().equals("womanPurse")){
            throw new InvalidOrderException("We do not have such product in our offer");
        }
        if(order.getPrice() != 1000){
            throw new InvalidOrderException("Price per one product should be 1000$");
        }
        long daysToDelivery = getDaysToDelivery(getDeliveryDate(order.getDeliveryDate()));
        if(order.getQuantity() / daysToDelivery > 100){
            throw new InvalidOrderException("Quantity/days to delivery should be less than 100");
        }
        return true;
    }

    private long getDaysToDelivery(LocalDate deliveryDate) {
        return LocalDate.of(deliveryDate.getYear(), deliveryDate.getMonth(), deliveryDate.getDayOfMonth()).toEpochDay() - LocalDate.now().toEpochDay();
    }

    public void validateCollecting(Order order) throws InvalidOrderException {
//        validateOrdered(order);
        if(order.getQuantity() < 200){
            throw new InvalidOrderException("Too small order");
        }
        if(order.getQuantity() / getDaysToDelivery(getDeliveryDate(order.getDeliveryDate())) > 100){
            throw new InvalidOrderException("Too big quantity or delivery date too soon");
        }
    }

    public void validateCollected(Order order) throws InvalidOrderException {
//        validateCollecting(order);
        if(getDaysToDelivery(getDeliveryDate(order.getDeliveryDate())) < 14){
            throw new InvalidOrderException("Delivery date too soon");
        }
    }

    public void validateDelivered(Order order) throws InvalidOrderException {
//        validateCollected(order);
        if(!order.getProductName().equals("womanPurse")){
            throw new InvalidOrderException("We do not have such product in our offer");
        }
        if(order.getQuantity() / getDaysToDelivery(getDeliveryDate(order.getDeliveryDate())) > 100){
            throw new InvalidOrderException("Too big quantity or delivery date too soon");
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

        for (KeyValue result : results) {
            Order order = genson.deserialize(result.getStringValue(), Order.class);
            System.out.println(order);
            queryResults.add(order);
        }

        return genson.serialize(queryResults);
    }

    private void checkAssetAlreadyExists(Context ctx, String assetId) {
        if (OrderExists(ctx, assetId)) {
            String errorMessage = String.format("Order %s already exists", assetId);
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
