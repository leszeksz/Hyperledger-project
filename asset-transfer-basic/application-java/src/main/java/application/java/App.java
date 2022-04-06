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
				startContract(gateway);
		}
	}


	private static void startContract(Gateway gateway) throws ContractException, InterruptedException, TimeoutException {
		int DEFAULT_USER_CHOICE = 0;
		Network network = gateway.getNetwork(Constants.CHANNEL);
		Contract contract = network.getContract(Constants.CONTRACT);

		Scanner scanner = new Scanner(System.in);
		int userChoice = getUserInput(scanner);

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
				case 7:
					SaleService.createSaleAsset(contract, scanner);
					break;
				case 8:
					SaleService.getAllSaleAssets(contract);
					break;
			}
			userChoice = getUserInput(scanner);
		}
		scanner.close();
		System.out.println("Bye");
	}

	private static int getUserInput(Scanner scanner){

		System.out.println("Choose what you want to do:\n1. Create asset\n2. GetAllAssets\n3. Show one asset\n4. Update asset\n5. Delete asset\n6. Change owner\n7. Create sale asset\n8. Get all sale assets\n0. Exit");
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