/*
SPDX-License-Identifier: Apache-2.0
*/

package application.java;

import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class RegisterUser {

	public static void main(String[] args) throws Exception {

		// Create a CA client for interacting with the CA.
		Properties props = new Properties();
		props.put(Constants.PROP_KEY,
				Constants.PROP_VALUE);
		props.put(Constants.PROP_KEY_2, Constants.PROP_VALUE_2);
		HFCAClient caClient = HFCAClient.createNewInstance(Constants.URL, props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);

		// Create a wallet for managing identities
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get(Constants.WALLET));

		// Check to see if we've already enrolled the user.
		if (wallet.get(Constants.APP_USER) != null) {
			System.out.println("An identity for the user " + Constants.APP_USER + " already exists in the wallet");
			return;
		}

		X509Identity adminIdentity = (X509Identity)wallet.get(Constants.ADMIN);
		if (adminIdentity == null) {
			System.out.println(Constants.ADMIN + " needs to be enrolled and added to the wallet first");
			return;
		}
		User admin = new User() {

			@Override
			public String getName() {
				return Constants.ADMIN;
			}

			@Override
			public Set<String> getRoles() {
				return null;
			}

			@Override
			public String getAccount() {
				return null;
			}

			@Override
			public String getAffiliation() {
				return Constants.DEPARTMENT_1;
			}

			@Override
			public Enrollment getEnrollment() {
				return new Enrollment() {

					@Override
					public PrivateKey getKey() {
						return adminIdentity.getPrivateKey();
					}

					@Override
					public String getCert() {
						return Identities.toPemString(adminIdentity.getCertificate());
					}
				};
			}

			@Override
			public String getMspId() {
				return Constants.ORG_1_MSP;
			}

		};

		// Register the user, enroll the user, and import the new identity into the wallet.
		RegistrationRequest registrationRequest = new RegistrationRequest(Constants.APP_USER);
		registrationRequest.setAffiliation(Constants.DEPARTMENT_1);
		registrationRequest.setEnrollmentID(Constants.APP_USER);
		String enrollmentSecret = caClient.register(registrationRequest, admin);
		Enrollment enrollment = caClient.enroll(Constants.APP_USER, enrollmentSecret);
		Identity user = Identities.newX509Identity(Constants.ORG_1_MSP, enrollment);
		wallet.put(Constants.APP_USER, user);
		System.out.println("Successfully enrolled user " + Constants.APP_USER + " and imported it into the wallet");
	}

}
