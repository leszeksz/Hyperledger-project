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
//				startDistributionContract(gateway);
		}
	}


	private static void startContract(Gateway gateway) throws ContractException, InterruptedException, TimeoutException {
		int DEFAULT_USER_CHOICE = 0;
		Network network = gateway.getNetwork(Constants.CHANNEL);
		Contract contract = network.getContract(Constants.CONTRACT);

		Scanner scanner = new Scanner(System.in);
		int userChoice = AssetService.getUserInput(scanner);

		if(userChoice == DEFAULT_USER_CHOICE){
			scanner.close();
			System.out.println("You are about to exit. Good bye :)");
			return;
		}

		try {
			AssetService.invokeAssetContacts(userChoice, contract, scanner);
		} catch(ContractException e) {
			e.printStackTrace();
		}
		scanner.close();
		System.out.println("Bye");
	}

	private static void startDistributionContract(Gateway gateway) throws InterruptedException, TimeoutException {
		int DEFAULT_USER_CHOICE = 0;
		Network network = gateway.getNetwork(Constants.CHANNEL);
		Contract contract = network.getContract(Constants.CONTRACT);

		Scanner scanner = new Scanner(System.in);
		int userChoice = DistributionService.getDistributionMenu(scanner);

		if(userChoice == DEFAULT_USER_CHOICE){
			scanner.close();
			System.out.println("You are about to exit. Good bye :)");
			return;
		}

		DistributionService.getUserInput(userChoice, contract, scanner);
		scanner.close();
		System.out.println("Bye");
	}
}