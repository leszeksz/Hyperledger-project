package application.java;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class DistributionService {
    private static final int DEFAULT_USER_CHOICE = 0;

    public static void createDistribution(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter the id of distribution:");
        String distributionId = scanner.next();
        System.out.println("Enter owner of new distribution:");
        String owner = scanner.next();
        System.out.println("Enter the sales id:");
        String salesId = scanner.next();
        System.out.println("Enter the product id you want to ship:");
        String saledProductId = scanner.next();
        System.out.println("Enter product quantity you want to ship:");
        String quantity = scanner.next();
        System.out.println("Enter the shipper:");
        String shipper = scanner.next();
        System.out.println("Enter the location of shipment:");
        String location = scanner.next();
        System.out.println("Enter the cost of shipment:");
        String shippingCost = scanner.next();
        try {
            contract.submitTransaction("CreateDistribution", distributionId, owner, salesId, saledProductId, quantity, shipper, location, shippingCost);
            System.out.println("Distribution " + distributionId + " created");
        } catch (ContractException exception){
            System.out.println("Something went wrong\nDistribution not created");
        }
    }

    public static void readDistribution(Contract contract, Scanner scanner) {
        byte[] result;
        System.out.println("Enter id of distribution you want to retrieve:");
        String distributionId = scanner.next();
        try {
            result = contract.evaluateTransaction("ReadDistribution", distributionId);
            System.out.println("result: " + new String(result));
        } catch (ContractException exception){
            System.out.println("Something went wrong\nDistribution not retrieved");
        }
    }

    public static void updateDistribution(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter the id of distribution you want to update:");
        String distributionId = scanner.next();
        System.out.println("Enter new owner of distribution:");
        String owner = scanner.next();
        System.out.println("Enter new sales id:");
        String salesId = scanner.next();
        System.out.println("Enter new product id you want to ship:");
        String saledProductId = scanner.next();
        System.out.println("Enter new product quantity you want to ship:");
        String quantity = scanner.next();
        System.out.println("Enter new shipper:");
        String shipper = scanner.next();
        System.out.println("Enter new location of shipment:");
        String location = scanner.next();
        System.out.println("Enter new cost of shipment:");
        String shippingCost = scanner.next();
        try {
            contract.submitTransaction("UpdateDistribution", distributionId, owner, salesId, saledProductId, quantity, shipper, location, shippingCost);
            System.out.println("Distribution updated");
        } catch (ContractException exception){
            System.out.println("Something went wrong\nDistribution not updated");
        }
    }

    public static void transferDistribution(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter id of distribution you want to change owner:");
        String distributionId = scanner.next();
        System.out.println("Enter new owner:");
        String owner = scanner.next();
        try {
            contract.submitTransaction("TransferDistribution", distributionId, owner);
            System.out.println("Distribution transfered");
        } catch (ContractException exception){
            System.out.println("Something went wrong\nDistribution not transfered");
        }
    }

    public static void getAllDistribution(Contract contract) {
        byte[] result;
        try {
            result = contract.evaluateTransaction("GetAllDistributions");
            System.out.println("result: " + new String(result));
        } catch (ContractException exception){
            System.out.println("Something went wrong\nDistributions not retrieved");
        }
    }

    public static void deleteDistribution(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter id of distribution you want to delete:");
        String distributionId = scanner.next();
        try {
            contract.submitTransaction("DeleteDistribution", distributionId);
            System.out.println("Distribution deleted");
        } catch (ContractException exception){
            System.out.println("Something went wrong\nDistribution not deleted");
        }
    }
}
