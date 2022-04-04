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
        System.out.println("Enter owner of new distribution:");
        String owner = scanner.next();
        System.out.println("Enter the sales id:");
        String salesId = scanner.next();
        System.out.println("Enter the id of distribution:");
        String distributionId = scanner.next();
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
            contract.submitTransaction("CreateDistribution", owner, salesId, distributionId, saledProductId, quantity, shipper, location, shippingCost);
            System.out.println("Distribution created");
        } catch (ContractException exception){
            System.out.println("Something went wrong\nDistribution not created");
        }
    }

    public static void readDistribution(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter id of distribution you want to retrieve:");
        String distributionId = scanner.next();
        try {
            contract.submitTransaction("ReadDistribution", distributionId);
            System.out.println("Distribution retrieved");
        } catch (ContractException exception){
            System.out.println("Something went wrong\nDistribution not retrieved");
        }
    }

    public static void updateDistribution(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter id of distribution you want to update:");
        String distributionId = scanner.next();
        System.out.println("Enter new shipper:");
        String shipper = scanner.next();
        System.out.println("Enter new location of shipment:");
        String location = scanner.next();
        try {
            contract.submitTransaction("UpdateDistribution", distributionId, shipper, location);
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

    public static void getAllDistribution(Contract contract) throws InterruptedException, TimeoutException {
        try {
            contract.submitTransaction("GetAllDistributions");
            System.out.println("Distributions retrieved");
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

    public static int getDistributionMenu(Scanner scanner){
        System.out.println("Choose what you want to do:\n1. Create distribution\n2. Get distribution\n3. Update distribution\n4. Change owner\n5. Get distributions\n6. Delete distribution\n0. Exit");
        int userChoice = DEFAULT_USER_CHOICE;

        try {
            userChoice = scanner.nextInt();
        } catch (InputMismatchException e){
            System.out.println("You have not input integer");
            e.printStackTrace();
        } catch (NoSuchElementException e){
            System.out.println("You have not input any value");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.out.println("Scanner is closed");
            e.printStackTrace();
        }
        return userChoice;
    }
    public static void getUserInput(int userChoice, Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        while (userChoice != DEFAULT_USER_CHOICE) {
            switch (userChoice) {
                case 1:
                    createDistribution(contract, scanner);
                    break;
                case 2:
                    readDistribution(contract, scanner);
                    break;
                case 3:
                    updateDistribution(contract, scanner);
                    break;
                case 4:
                    transferDistribution(contract, scanner);
                case 5:
                    getAllDistribution(contract);
                case 6:
                    deleteDistribution(contract, scanner);
            }
            userChoice = getDistributionMenu(scanner);
        }
    }
}
