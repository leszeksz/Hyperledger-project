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
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;


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

	public static void main(String[] args) throws Exception {
		// enrolls the admin and registers the user
		try {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
		} catch (Exception e) {
			System.err.println(e);
		}

		// connect to the network and invoke the smart contract
		try (Gateway gateway = connect()) {

			// get the network and contract
			Network network = gateway.getNetwork(Constants.CHANNEL);
			Contract contract = network.getContract(Constants.CONTRACT);

			byte[] result;

			System.out.println("Submit Transaction: " + Constants.INIT_LEDGER + " creates the initial set of assets on the ledger.");
			contract.submitTransaction(Constants.INIT_LEDGER);

			System.out.println("\n");
			result = contract.evaluateTransaction(Constants.GET_ALL_ASSETS);
			System.out.println("Evaluate Transaction: " + Constants.GET_ALL_ASSETS + ", result: " + new String(result));

			System.out.println("\n");
			System.out.println("Submit Transaction: " + Constants.CREATE_ASSET + " asset13");
			//CreateAsset creates an asset with ID asset13, color yellow, owner Tom, size 5 and appraisedValue of 1300
			contract.submitTransaction(Constants.CREATE_ASSET, "asset13", "yellow", "5", "Tom", "1300");

			System.out.println("\n");
			System.out.println("Evaluate Transaction: " + Constants.READ_ASSET + " asset13");
			// ReadAsset returns an asset with given assetID
			result = contract.evaluateTransaction(Constants.READ_ASSET, "asset13");
			System.out.println("result: " + new String(result));

			System.out.println("\n");
			System.out.println("Evaluate Transaction: " + Constants.ASSET_EXISTS + " asset1");
			// AssetExists returns "true" if an asset with given assetID exist
			result = contract.evaluateTransaction(Constants.ASSET_EXISTS, "asset1");
			System.out.println("result: " + new String(result));

			System.out.println("\n");
			System.out.println("Submit Transaction: " + Constants.UPDATE_ASSET + " asset1, new Price : 350");
			// UpdateAsset updates an existing asset with new properties. Same args as CreateAsset
			contract.submitTransaction(Constants.UPDATE_ASSET, "asset1", "blue", "5", "Tomoko", "350");

			System.out.println("\n");
			System.out.println("Evaluate Transaction: " + Constants.READ_ASSET + " asset1");
			result = contract.evaluateTransaction(Constants.READ_ASSET, "asset1");
			System.out.println("result: " + new String(result));

			try {
				System.out.println("\n");
				System.out.println("Submit Transaction: " + Constants.UPDATE_ASSET + " asset1");
				//Non existing asset asset70 should throw Error
				contract.submitTransaction(Constants.UPDATE_ASSET, "asset1", "blue", "5", "Tomoko", "300");
			} catch (Exception e) {
				System.err.println("Expected an error on " + Constants.UPDATE_ASSET + " of non-existing Asset: " + e);
			}

			System.out.println("\n");
			System.out.println("Submit Transaction: " + Constants.TRANSFER_ASSET + " asset1 from owner Tomoko > owner Tom");
			// TransferAsset transfers an asset with given ID to new owner Tom
			contract.submitTransaction(Constants.TRANSFER_ASSET, "asset1", "Tom");

			System.out.println("\n");
			System.out.println("Evaluate Transaction: " + Constants.READ_ASSET + " asset1");
			result = contract.evaluateTransaction(Constants.READ_ASSET, "asset1");
			System.out.println("result: " + new String(result));
		}
		catch(Exception e){
			System.err.println(e);
		}

	}
}
