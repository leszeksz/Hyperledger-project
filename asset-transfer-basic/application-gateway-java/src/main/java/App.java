/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.CallOption;
import org.hyperledger.fabric.client.CommitException;
import org.hyperledger.fabric.client.CommitStatusException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.EndorseException;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.Status;
import org.hyperledger.fabric.client.SubmitException;
import org.hyperledger.fabric.client.SubmittedTransaction;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.hyperledger.fabric.protos.gateway.ErrorDetail;

public final class App {
	private static final String mspID = "Org1MSP";
	private static final String channelName = "mychannel";
	private static final String chaincodeName = "basic";

	// Path to crypto materials.
	private static final Path cryptoPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations", "org1.example.com");
	// Path to user certificate.
	private static final Path certPath = cryptoPath.resolve(Paths.get("users", "User1@org1.example.com", "msp", "signcerts", "cert.pem"));
	// Path to user private key directory.
	private static final Path keyDirPath = cryptoPath.resolve(Paths.get("users", "User1@org1.example.com", "msp", "keystore"));
	// Path to peer tls certificate.
	private static final Path tlsCertPath = cryptoPath.resolve(Paths.get("peers", "peer0.org1.example.com", "tls", "ca.crt"));

	// Gateway peer end point.
	private static final String peerEndpoint = "localhost:7051";
	private static final String overrideAuth = "peer0.org1.example.com";

	private final Contract contract;
	private final String assetId = "asset" + Instant.now().toEpochMilli();
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private final int DEFAULT_USER_CHOICE = 0;

	public static void main(final String[] args) throws Exception {
		// The gRPC client connection should be shared by all Gateway connections to
		// this endpoint.
		ManagedChannel channel = newGrpcConnection();

		Gateway.Builder builder = Gateway.newInstance().identity(newIdentity()).signer(newSigner()).connection(channel)
				// Default timeouts for different gRPC calls
				.evaluateOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
				.endorseOptions(CallOption.deadlineAfter(15, TimeUnit.SECONDS))
				.submitOptions(CallOption.deadlineAfter(5, TimeUnit.SECONDS))
				.commitStatusOptions(CallOption.deadlineAfter(1, TimeUnit.MINUTES));

		try (Gateway gateway = builder.connect()) {
			new App(gateway).run();
		}
		catch (Exception e){
			System.out.println(e.getStackTrace());
		}
			finally {
			channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		}
	}

	private static ManagedChannel newGrpcConnection() throws IOException, CertificateException {
		Reader tlsCertReader = Files.newBufferedReader(tlsCertPath);
		X509Certificate tlsCert = Identities.readX509Certificate(tlsCertReader);

		return NettyChannelBuilder.forTarget(peerEndpoint)
				.sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build()).overrideAuthority(overrideAuth)
				.build();
	}

	private static Identity newIdentity() throws IOException, CertificateException {
		Reader certReader = Files.newBufferedReader(certPath);
		X509Certificate certificate = Identities.readX509Certificate(certReader);

		return new X509Identity(mspID, certificate);
	}

	private static Signer newSigner() throws IOException, InvalidKeyException {
		Path keyPath = Files.list(keyDirPath)
				.findFirst()
				.orElseThrow(IllegalArgumentException:: new);
		Reader keyReader = Files.newBufferedReader(keyPath);
		PrivateKey privateKey = Identities.readPrivateKey(keyReader);

		return Signers.newPrivateKeySigner(privateKey);
	}

	public App(final Gateway gateway) {
		// Get a network instance representing the channel where the smart contract is
		// deployed.
		Network network = gateway.getNetwork(channelName);

		// Get the smart contract from the network.
		contract = network.getContract(chaincodeName);
	}

	public void run() throws GatewayException, CommitException {

		Scanner scanner = new Scanner(System.in);
		int userChoice = getUserInput(scanner);

		if(userChoice == DEFAULT_USER_CHOICE){
			scanner.close();
			System.out.println("You are about to exit");
		}

		while(userChoice != DEFAULT_USER_CHOICE){

			switch(userChoice){
				case 1:
					// Return all the current assets on the ledger.
					getAllAssets();
					System.out.println(Constants.GET_ALL_ASSETS);
					break;
				case 2:
					// Create a new asset on the ledger.
					createAsset();
					System.out.println(Constants.CREATE_ASSET);
					break;
				case 3:
					// Update an existing asset asynchronously.
					transferAssetAsync();
					System.out.println("transferAssetAsync");
					break;
				default:
					System.out.println("You choose value out of range \nChoose value from a menu");
					break;
			}
			if(userChoice != DEFAULT_USER_CHOICE) {
				userChoice = getUserInput(scanner);
			}
		}

		scanner.close();
		System.out.println("You are about to exit");

	}

	private int getUserInput(Scanner scanner){

		System.out.println("Choose action: \n0-exit \n1-getAllAssets \n2-createAsset \n3-transferAssetAsync");
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
	
	/**
	 * This type of transaction would typically only be run once by an application
	 * the first time it was started after its initial deployment. A new version of
	 * the chaincode deployed later would likely not need to run an "init" function.
	 */
	private void initLedger() throws EndorseException, SubmitException, CommitStatusException, CommitException {
		System.out.println("\n--> Submit Transaction: " + Constants.INIT_LEDGER + ", function creates the initial set of assets on the ledger");

		contract.submitTransaction(Constants.INIT_LEDGER);

		System.out.println("*** Transaction committed successfully");
	}

	/**
	 * Evaluate a transaction to query ledger state.
	 */
	private void getAllAssets() throws GatewayException {
		System.out.println("\n--> Evaluate Transaction: " + Constants.GET_ALL_ASSETS + ", function returns all the current assets on the ledger");
		
		byte[] result = contract.evaluateTransaction(Constants.GET_ALL_ASSETS);
		
		System.out.println("*** Result: " + prettyJson(result));
	}

	private String prettyJson(final byte[] json) {
		return prettyJson(new String(json, StandardCharsets.UTF_8));
	}

	private String prettyJson(final String json) {
		JsonElement parsedJson = JsonParser.parseString(json);
		return gson.toJson(parsedJson);
	}

	/**
	 * Submit a transaction synchronously, blocking until it has been committed to
	 * the ledger.
	 */
	private void createAsset() throws EndorseException, SubmitException, CommitStatusException, CommitException {
		System.out.println("\n--> Submit Transaction: " + Constants.CREATE_ASSET + ", creates new asset with ID, Color, Size, Owner and AppraisedValue arguments");

		contract.submitTransaction(Constants.CREATE_ASSET, assetId, "yellow", "5", "Tom", "1300");

		System.out.println("*** Transaction committed successfully");
	}

	/**
	 * Submit transaction asynchronously, allowing the application to process the
	 * smart contract response (e.g. update a UI) while waiting for the commit
	 * notification.
	 */
	private void transferAssetAsync() throws EndorseException, SubmitException, CommitStatusException {
		System.out.println("\n--> Async Submit Transaction: " + Constants.TRANSFER_ASSET + " , updates existing asset owner");
		
		SubmittedTransaction commit = contract.newProposal(Constants.TRANSFER_ASSET)
				.addArguments(assetId, "Saptha")
				.build()
				.endorse()
				.submitAsync();

		byte[] result = commit.getResult();
		String oldOwner = new String(result, StandardCharsets.UTF_8);

		System.out.println("*** Successfully submitted transaction to transfer ownership from " + oldOwner + " to Saptha");
		System.out.println("*** Waiting for transaction commit");
		
		Status status = commit.getStatus();
		if (!status.isSuccessful()) {
			throw new RuntimeException("Transaction " + status.getTransactionId() +
					" failed to commit with status code " + status.getCode());
		}
		
		System.out.println("*** Transaction committed successfully");
	}

	private void readAssetById() throws GatewayException {
		System.out.println("\n--> Evaluate Transaction: " + Constants.READ_ASSET + " , function returns asset attributes");
		
		byte[] evaluateResult = contract.evaluateTransaction(Constants.READ_ASSET, assetId);
		
		System.out.println("*** Result:" + prettyJson(evaluateResult));
	}

	/**
	 * submitTransaction() will throw an error containing details of any error
	 * responses from the smart contract.
	 */
	private void updateNonExistentAsset() {
		try {
			System.out.println("\n--> Submit Transaction: " + Constants.UPDATE_ASSET +  " asset70, asset70 does not exist and should return an error");
			
			contract.submitTransaction(Constants.UPDATE_ASSET, "asset70", "blue", "5", "Tomoko", "300");
			
			System.out.println("******** FAILED to return an error");
		} catch (EndorseException | SubmitException | CommitStatusException e) {
			System.out.println("*** Successfully caught the error: ");
			e.printStackTrace(System.out);
			System.out.println("Transaction ID: " + e.getTransactionId());

			List<ErrorDetail> details = e.getDetails();
			if (!details.isEmpty()) {
				System.out.println("Error Details:");
				for (ErrorDetail detail : details) {
					System.out.println("- address: " + detail.getAddress() + ", mspId: " + detail.getMspId()
							+ ", message: " + detail.getMessage());
				}
			}
		} catch (CommitException e) {
			System.out.println("*** Successfully caught the error: " + e);
			e.printStackTrace(System.out);
			System.out.println("Transaction ID: " + e.getTransactionId());
			System.out.println("Status code: " + e.getCode());
		}
	}
}
