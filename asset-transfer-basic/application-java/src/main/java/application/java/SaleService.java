package application.java;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class SaleService {
    private static final int DEFAULT_USER_CHOICE = 0;

    public static void createSaleAsset(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
        System.out.println("Enter the sale ID:");
        String saleID = scanner.next();
        System.out.println("Enter the owner:");
        String owner = scanner.next();
        System.out.println("Enter the name of the product:");
        String product = scanner.next();
        System.out.println("Enter the quantity:");
        String quantity = scanner.next();
        System.out.println("Enter the contractor:");
        String contractor = scanner.next();
        try {
            contract.submitTransaction("CreateSaleAsset",saleID,owner,product,quantity,contractor);
            System.out.println("Sale asset created.");
        }catch (ContractException exception){
            System.out.println("Something went wrong\nSale asset not created");
        }
    }

    public static void readSaleAsset(Contract contract, Scanner scanner)throws InterruptedException, TimeoutException{
        System.out.println("Enter id of the sale asset you want to check:");
        String saleAssetID = scanner.next();
        try {
            contract.submitTransaction("ReadSaleAsset", saleAssetID);
            System.out.println("Sale asset retrieved successfully");
        } catch (ContractException exception){
            System.out.println("Something went wrong\nSale asset not retrieved");
        }
    }

    public static void updateSaleAsset(Contract contract, Scanner scanner)throws InterruptedException, TimeoutException{
        System.out.println("Enter the id of the sale asset you want to update:");
        String saleID = scanner.next();
        System.out.println("Enter new owner:");
        String newOwner = scanner.next();
        System.out.println("Enter new product name:");
        String newProductName = scanner.next();
        System.out.println("Enter the quantity:");
        String newQuantity = scanner.next();
        System.out.println("Enter the new contractor:");
        String newContractor = scanner.next();
        try {
            contract.submitTransaction("UpdateSaleAsset", saleID, newOwner, newProductName,newQuantity,newContractor);
            System.out.println("Sale asset updated");
        } catch (ContractException exception){
            System.out.println("Something went wrong\nSale asset not updated");
        }
    }

    public static void transferSaleAsset(Contract contract, Scanner scanner)throws InterruptedException, TimeoutException{
        System.out.println("Enter the id of sale asset you want to transfer:");
        String saleID = scanner.next();
        System.out.println("Enter new owner:");
        String newOwner = scanner.next();
        try {
            contract.submitTransaction("TransferSaleAsset",saleID,newOwner);
        } catch (ContractException exception){
            System.out.println("Something went wrong\nSale asset not transferred.");
        }
    }

    public static void getAllSaleAssets(Contract contract)throws InterruptedException, TimeoutException{
        try{
            contract.submitTransaction("GetAllSaleAssets");
            System.out.println("Sale assets retrieved successfully");
        }catch (ContractException exception){
            System.out.println("Something went wrong\nSale assets not retrieved");
        }
    }

    public static void deleteSaleAsset(Contract contract, Scanner scanner)throws InterruptedException, TimeoutException{
        System.out.println("Enter id of sale asset you want to delete:");
        String deleteSaleAssetID = scanner.next();
        try{
            contract.submitTransaction("DeleteSaleAsset",deleteSaleAssetID);
        }catch (ContractException exception){
            System.out.println("Something went wrong\nSale asset not deleted");
        }
    }

    public static int getSaleAssetMenu(Scanner scanner){
        System.out.println("Choose what you want to do:\n1. Create sale asset\n2. Get sale asset\n3. Update sale asset\n4. Change sale asset owner\n5. Get all sale assets\n6. Delete sale asset\n0. Exit");
        int userChoice = DEFAULT_USER_CHOICE;

        try{
            userChoice = scanner.nextInt();
        }catch (InputMismatchException inputMismatchException){
            System.out.println("Your input is not an integer.");
            inputMismatchException.getStackTrace();
        }catch (NoSuchElementException noSuchElementException){
            System.out.println("You have not input any value.");
            noSuchElementException.getStackTrace();
        }catch (IllegalStateException illegalStateException){
            System.out.println("Scanner is closed.");
            illegalStateException.getStackTrace();
        }
        return userChoice;
    }

    public static void getUserInput(int userChoice, Contract contract, Scanner scanner)throws InterruptedException, TimeoutException{
        while (userChoice != DEFAULT_USER_CHOICE){
            switch (userChoice){
                case 1:
                    createSaleAsset(contract,scanner);
                    break;
                case 2:
                    readSaleAsset(contract,scanner);
                    break;
                case 3:
                    updateSaleAsset(contract, scanner);
                    break;
                case 4:
                    transferSaleAsset(contract, scanner);
                    break;
                case 5:
                    getAllSaleAssets(contract);
                case 6:
                    deleteSaleAsset(contract, scanner);
                    break;
            }
            userChoice = getSaleAssetMenu(scanner);
        }
    }
}