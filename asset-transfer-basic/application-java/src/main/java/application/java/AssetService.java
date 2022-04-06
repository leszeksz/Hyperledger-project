package application.java;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class AssetService {
    public static void createAsset(Contract contract, Scanner scanner) throws TimeoutException, InterruptedException {
        System.out.println("Enter the product id:");
        String productId = scanner.next();
        System.out.println("Enter owner:");
        String owner = scanner.next();
        System.out.println("Enter price:");
        String price = scanner.next();
        if (!validate(price)){
            System.out.println("Price must be greater than zero. Enter price again:");
            price = scanner.next();
        }
        try {
            contract.submitTransaction("CreateAsset", productId, owner, price);
            System.out.println("Submit Transaction: CreateAsset " + productId);
        } catch (ContractException exception){
            System.out.println("Something went wrong. Try again.");
        }
    }

    public static void getAssets(Contract contract) throws ContractException {
        byte[] result;
        result = contract.evaluateTransaction("GetAllAssets");
        System.out.println("Evaluate Transaction: GetAllAssets, result: " + new String(result));
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

    public static void updateAsset(Contract contract, Scanner scanner) throws TimeoutException, InterruptedException {
        System.out.println("Enter the product id you want to change:");
        String productIdToChange = scanner.next();
        System.out.println("Enter new owner:");
        String ownerToChange = scanner.next();
        System.out.println("Enter new price:");
        String priceToChange = scanner.next();
        if (!validate(priceToChange)){
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
    private static boolean validate(String price){
        int number = Integer.parseInt(price);
        if (number < 0){
            return false;
        } else return true;
    }
}
