package application.java;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class OrderService {
    private static final int DEFAULT_USER_CHOICE = 0;

    public static void createOrder(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        String orderId = "order3";
        String productName = "womanPurse";
        String quantity = "100";
        String deliveryDate = "2022-06-07";
        String status = "ORDERED";
        String price = "1000";
        String orderer = "orderer";
        String assembler = "";
        String leather = "0";
        String metal = "0";
        String owner = "store";
        try {
            contract.submitTransaction("CreateOrder", orderId, productName, quantity, deliveryDate, status, price, orderer, assembler, leather, metal, owner);
            System.out.println("Order " + orderId + " created");
        } catch (ContractException e){
            e.printStackTrace();
        }
    }

    public static void readOrder(Contract contract, Scanner scanner) {
        byte[] result;
        System.out.println("Enter id of order you want to retrieve:");
        String orderId = scanner.next();
        try {
            result = contract.evaluateTransaction("ReadOrder", orderId);
            System.out.println("result: " + new String(result));
        } catch (ContractException exception){
            exception.printStackTrace();
        }
    }

    public static void updateOrder(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        String orderId = "order3";
        String productName = "womanPurse";
        String quantity = "100";
        String deliveryDate = "2022-06-07";
        String status = "ORDERED";
        String price = "1000";
        String orderer = "orderer";
        String assembler = "";
        String leather = "1000";
        String metal = "0";
        String owner = "producer1";
        try {
            contract.submitTransaction("UpdateOrder", orderId, productName, quantity, deliveryDate, status, price, orderer, assembler, leather, metal, owner);
            System.out.println("Order updated");
        } catch (ContractException e){
            e.printStackTrace();
        }
    }

    public static void getAllOrders(Contract contract) {
        byte[] result;
        try {
            result = contract.evaluateTransaction("GetAllOrders");
            System.out.println("result: " + new String(result));
        } catch (ContractException e){
            e.printStackTrace();
        }
    }

}
