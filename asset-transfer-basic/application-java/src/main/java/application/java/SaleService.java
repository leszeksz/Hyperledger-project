//package application.java;
//
//import org.hyperledger.fabric.gateway.Contract;
//import org.hyperledger.fabric.gateway.ContractException;
//
//import java.util.Scanner;
//import java.util.concurrent.TimeoutException;
//
//public class SaleService {
//    private static final int DEFAULT_USER_CHOICE = 0;
//
//    public static void createSaleAsset(Contract contract, Scanner scanner) throws InterruptedException, TimeoutException {
//        System.out.println("Enter the sale ID:");
//        String saleID = scanner.next();
//        System.out.println("Enter the owner:");
//        String owner = scanner.next();
//        System.out.println("Enter the name of the product:");
//        String product = scanner.next();
//        System.out.println("Enter the quantity:");
//        String quantity = scanner.next();
//        System.out.println("Enter the contractor:");
//        String contractor = scanner.next();
//        try {
//            contract.submitTransaction("CreateSaleAsset",saleID,owner,product,quantity,contractor);
//            System.out.println("Sale asset created.");
//        }catch (ContractException exception){
//            System.out.println("Something went wrong\nSale asset not created");
//        }
//    }
//
//    public static void readSaleAsset(Contract contract, Scanner scanner)throws InterruptedException, TimeoutException{
//        System.out.println("Enter id of the sale asset you want to check:");
//        String saleAssetID = scanner.next();
//        try {
//            contract.submitTransaction("ReadSaleAsset", saleAssetID);
//            System.out.println("Sale asset retrieved successfully");
//        } catch (ContractException exception){
//            System.out.println("Something went wrong\nSale asset not retrieved");
//        }
//    }
//
//    public static void updateSaleAsset(Contract contract, Scanner scanner)throws InterruptedException, TimeoutException{
//        System.out.println("Enter the id of the sale asset you want to update:");
//        String saleID = scanner.next();
//        System.out.println("Enter new owner:");
//        String newOwner = scanner.next();
//        System.out.println("Enter new product name:");
//        String newProductName = scanner.next();
//        System.out.println("Enter the quantity:");
//        String newQuantity = scanner.next();
//        System.out.println("Enter the new contractor:");
//        String newContractor = scanner.next();
//        try {
//            contract.submitTransaction("UpdateSaleAsset", saleID, newOwner, newProductName,newQuantity,newContractor);
//            System.out.println("Sale asset updated");
//        } catch (ContractException exception){
//            System.out.println("Something went wrong\nSale asset not updated");
//        }
//    }
//
//    public static void transferSaleAsset(Contract contract, Scanner scanner){
//        System.out.println("Enter the id of sale asset you want to transfer:");
//        String saleID = scanner.next();
//        System.out.println("Enter new owner:");
//        String newOwner = scanner.next();
//    }
//
//}