package application.java;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class AssetService {

    private static final int DEFAULT_USER_CHOICE = 0;

    public static void deleteAsset(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter the product id you want to delete:");
        String productIdToDelete = scanner.next();
        try {
            contract.submitTransaction("DeleteAsset", productIdToDelete);
            System.out.println("Asset updated");
        } catch (ContractException exception){
            System.out.println("Invalid product Id");
        }
    }

    public static void transferAsset(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter the product id owner you want to change:");
        String productIdToTransfer = scanner.next();
        System.out.println("Enter new owner:");
        String newOwner = scanner.next();
        try {
            contract.submitTransaction("TransferAsset", productIdToTransfer, newOwner);
            System.out.println("Asset updated");
        } catch (ContractException exception){
            System.out.println("Invalid product Id");
        }
    }

    public static void updateAsset(Contract contract, Scanner scanner) throws TimeoutException, InterruptedException {
        System.out.println("Enter the product id you want to change:");
        String productIdToChange = scanner.next();
        System.out.println("Enter new owner:");
        String ownerToChange = scanner.next();
        System.out.println("Enter new price:");
        String priceToChange = scanner.next();
        if (!validatePrice(priceToChange)){
            System.out.println("Price must be greater than zero. Enter price again:");
            priceToChange = scanner.next();
        }
        try {
            contract.submitTransaction("UpdateAsset", productIdToChange, ownerToChange, priceToChange);
            System.out.println("Asset updated");
        } catch (ContractException exception){
            System.out.println("Invalid product Id");
        }
    }

    public static void readAsset(Contract contract, Scanner scanner) {
        byte[] result;
        System.out.println("Enter the product id you want to check:");
        String productName = scanner.next();
        try {
            result = contract.evaluateTransaction("ReadAsset", productName);
            System.out.println("result: " + new String(result));
        } catch (ContractException exception){
            System.out.println("Can't find asset with id: " + productName);
        }
    }

    public static void getAssets(Contract contract) throws ContractException {
        byte[] result;
        result = contract.evaluateTransaction("GetAllAssets");
        System.out.println("Evaluate Transaction: GetAllAssets, result: " + new String(result));
    }

    public static void createAsset(Contract contract, Scanner scanner) throws TimeoutException, InterruptedException {
        System.out.println("Enter the product id:");
        String productId = scanner.next();
        System.out.println("Enter owner:");
        String owner = scanner.next();
        System.out.println("Enter price:");
        String price = scanner.next();
        if (!validatePrice(price)){
            System.out.println("Price must be greater than zero. Enter price again:");
            price = scanner.next();
        }
        try {
            byte[] result = contract.submitTransaction("CreateAsset", productId, owner, price);
            System.out.println("result: " + result);
            System.out.println("Submit Transaction: CreateAsset " + productId);
        } catch (ContractException exception){
            System.out.println("Something went wrong. Try again.");
        }
    }

    public static int getUserInput(Scanner scanner){

        System.out.println("Choose what you want to do:\n1. Create asset\n2. GetAllAssets\n3. Show one asset\n4. Update asset\n5. Delete asset\n6. Change owner\n0. Exit");
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

    private static boolean validatePrice(String price){
        int number = Integer.parseInt(price);
        if (number < 0){
            return false;
        } else return true;
    }

    public static void invokeAssetContacts(int userChoice, Contract contract, Scanner scanner) throws ContractException, InterruptedException, TimeoutException {
        while (userChoice != DEFAULT_USER_CHOICE) {
            switch (userChoice) {
                case 1:
                    createAsset(contract, scanner);
                    break;
                case 2:
                    getAssets(contract);
                    break;
                case 3:
                    readAsset(contract, scanner);
                    break;
                case 4:
                    updateAsset(contract, scanner);
				case 5:
					deleteAsset(contract, scanner);
				case 6:
					transferAsset(contract, scanner);
            }
            userChoice = getUserInput(scanner);
        }
    }
}
