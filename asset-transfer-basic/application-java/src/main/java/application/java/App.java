/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.*;


public class App {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	// helper function for getting connected to the gateway
	public static Gateway connect() throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get(Constants.WALLET);
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();

		builder.identity(wallet, Constants.APP_USER).networkConfig(networkConfigPath).discovery(true);

		return builder.connect();
	}

	private static final int DEFAULT_USER_CHOICE = 0;

	public static void main(String[] args) throws Exception {
//		 enrolls the admin and registers the user
		try {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
		} catch (Exception e) {
			System.err.println(e);
		}

		// connect to the network and invoke the smart contract
		try (Gateway gateway = connect()) {
			System.out.println("Choose what you want to do:\n1. Production\n2. Distribution\n3. Sale");

			Scanner scanner = new Scanner(System.in);
			int userChoice = scanner.nextInt();
			switch(userChoice){
				case 1:
					startContract(gateway);
					break;
				case 2:
					startDistribution(gateway);
					break;
				case 3:
					startSale(gateway);
			}
		}
	}


	private static void startContract(Gateway gateway) throws ContractException, InterruptedException, TimeoutException {
		int DEFAULT_USER_CHOICE = 0;
		Network network = gateway.getNetwork(Constants.CHANNEL);
		Contract contract = network.getContract(Constants.CONTRACT);

		Scanner scanner = new Scanner(System.in);
		int userChoice = getUserInput(scanner, "asset");

		if(userChoice == DEFAULT_USER_CHOICE){
			scanner.close();
			System.out.println("You are about to exit. Good bye :)");
			return;
		}


		while (userChoice != DEFAULT_USER_CHOICE) {
			switch (userChoice) {
				case 1:
					AssetService.createAsset(contract, scanner);
					break;
				case 2:
					AssetService.getAssets(contract);
					break;
				case 3:
					AssetService.readAsset(contract, scanner);
					break;
				case 4:
					AssetService.updateAsset(contract, scanner);
					break;
				case 5:
					AssetService.deleteAsset(contract, scanner);
					break;
				case 6:
					AssetService.transferAsset(contract, scanner);
					break;
				default:
					break;
			}
				userChoice = getUserInput(scanner, "asset");
		}
		scanner.close();
		System.out.println("Bye");
	}

	private static void startDistribution(Gateway gateway) throws InterruptedException, TimeoutException {
		int DEFAULT_USER_CHOICE = 0;
		Network network = gateway.getNetwork(Constants.CHANNEL);
		Contract contract = network.getContract(Constants.CONTRACT);

		Scanner scanner = new Scanner(System.in);
		int userChoice = getUserInput(scanner, "distribution");

		if(userChoice == DEFAULT_USER_CHOICE){
			scanner.close();
			System.out.println("You are about to exit. Good bye :)");
			return;
		}


		while (userChoice != DEFAULT_USER_CHOICE) {
			switch (userChoice) {
				case 1:
					DistributionService.createDistribution(contract, scanner);
					break;
				case 2:
					DistributionService.getAllDistribution(contract);
					break;
				case 3:
					DistributionService.readDistribution(contract, scanner);
					break;
				case 4:
					DistributionService.updateDistribution(contract, scanner);
					break;
				case 5:
					DistributionService.deleteDistribution(contract, scanner);
					break;
				case 6:
					DistributionService.transferDistribution(contract, scanner);
					break;
				default:
					break;
			}
			userChoice = getUserInput(scanner, "distribution");
		}
		scanner.close();
		System.out.println("Bye");
	}

	private static void startSale(Gateway gateway) throws ContractException, InterruptedException, TimeoutException {
		int DEFAULT_USER_CHOICE = 0;
		Network network = gateway.getNetwork(Constants.CHANNEL);
		Contract contract = network.getContract(Constants.CONTRACT);

		Scanner scanner = new Scanner(System.in);
		int userChoice = getUserInput(scanner, "sale");

		if(userChoice == DEFAULT_USER_CHOICE){
			scanner.close();
			System.out.println("You are about to exit. Good bye :)");
			return;
		}


		while (userChoice != DEFAULT_USER_CHOICE) {
			switch (userChoice) {
				case 1:
					SaleService.createSaleAsset(contract, scanner);
					break;
				case 2:
					SaleService.getAllSaleAssets(contract);
					break;
				case 3:
					SaleService.readSaleAsset(contract, scanner);
					break;
				case 4:
					SaleService.updateSaleAsset(contract, scanner);
					break;
				case 5:
					SaleService.deleteSaleAsset(contract, scanner);
					break;
				case 6:
					SaleService.transferSaleAsset(contract, scanner);
					break;
				default:
					break;
			}
			userChoice = getUserInput(scanner, "sale");
		}
		scanner.close();
		System.out.println("Bye");
	}

	private static int getUserInput(Scanner scanner, String item){

		System.out.println("Choose what you want to do:\n1. Create " + item + "\n2. Get all " + item + "s \n3. Get " + item + "\n4. Update " + item + "\n5. Delete " + item + "\n6. Change " + item + "'s owner\n0. Exit");
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

}